/*Copyright (c) 2010-2013, Mathieu Bordas
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1- Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
2- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
3- Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package qualify;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.codehaus.groovy.control.CompilationFailedException;
import org.joda.time.Duration;
import qualify.doc.DocumentUtils;
import qualify.doc.ReleaseNote;
import qualify.doc.TestCasesTable;
import qualify.doc.TestReport;
import qualify.doc.TestSource;
import qualify.server.HTTPServer;
import qualify.tools.CommandLineTool;
import qualify.tools.StackTraceTool;
import qualify.tools.TestObject;
import qualify.tools.TestToolFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Create a {@link TestHarness} to create the beginning point of your testing application. From your main(String[]), call the static method:
 * runTestHarness(<i>args</i>, <i>testHarness</i>) Options are:
 * <ul>
 * <li>sut_name the Software Under Test that will be displayed at the top of the release note</li>
 * <li>version the version displayed at the top of the release note</li>
 * <li>date the date displayed at the top of the release note</li>
 * <li>source_dirs the relative directory to use to recursively find the TestCase java source files</li>
 * <li>release_note the name for the main release note XML file</li>
 * </ul>
 */
public abstract class TestHarness {

	static Logger logger = Logger.getLogger(TestHarness.class);
	static final int DEFAULT_TEST_CASES_MAX_ATTEMPT = 1;

	private Duration elapsedTime = null;

	private Map<String, TestCase> testCases = new LinkedHashMap<String, TestCase>();
	private Map<String, TestCase> sanityTests = new LinkedHashMap<String, TestCase>();

	private List<File> testCasesSourceDirs = new LinkedList<File>();
	private List<String> testCasesScriptDirs = new LinkedList<String>();

	private List<TestSpy> spies = new LinkedList<TestSpy>();

	private SRD srd = null;

	private TestHarnessHandler handler = null;

	private List<Class<? extends TestCase>> reloadableTestCases = new LinkedList<Class<? extends TestCase>>();
	private int testCasesMaxAttemptNumber = DEFAULT_TEST_CASES_MAX_ATTEMPT;

	public List<TestCase> getTestCases() {
		List<TestCase> result = new LinkedList<TestCase>();
		result.addAll(sanityTests.values());
		result.addAll(testCases.values());
		return result;
	}

	public SRD getSrd() {
		if(srd == null) {
			srd = new SRD();
		}
		return srd;
	}

	public static void main(String[] args) {
		TestHarness harness = new TestHarness() {
			protected void registerTestCases() {
			}
		};
		int exitCode = runTestHarness(args, harness);
		System.exit(exitCode);
	}

	public void setHandler(TestHarnessHandler handler) {
		this.handler = handler;
	}

	public TestHarnessHandler getHandler() {
		return handler;
	}

	private static void setReleaseMetaData(CommandLineTool cmd) {
		if(cmd.isOptionInCommandLine(Qualify.OPTION_RELEASE_NOTE_FILE_NAME)) {
			if(!cmd.isOptionInCommandLine(Qualify.OPTION_SUT_NAME)) {
				ErrorsAndWarnings.addError("SUT name must be passed through option '-" + Qualify.OPTION_SUT_NAME + "'");
			}
			if(!cmd.isOptionInCommandLine(Qualify.OPTION_SUT_VERSION)) {
				ErrorsAndWarnings.addWarning("Version of SUT should be passed through option '-" + Qualify.OPTION_SUT_VERSION + "'");
			}
			if(!cmd.isOptionInCommandLine(Qualify.OPTION_SUT_DATE)) {
				ErrorsAndWarnings.addWarning("Date of SUT should be passed through option '-" + Qualify.OPTION_SUT_DATE + "'");
			}
		}
	}

	private static void loadSRD(CommandLineTool cmd, TestHarness th) {
		if(cmd.isOptionInCommandLine(Qualify.OPTION_SRD_FILE)) {
			th.srd = SRD.loadSRDFromXMLFile(new File(cmd.getOptionValue(Qualify.OPTION_SRD_FILE)));
		}
	}

	private static void setSourceDirs(CommandLineTool cmd, TestHarness th) {
		if(cmd.isOptionInCommandLine(Qualify.OPTION_SOURCE_DIRS)) {
			logger.info("Reading source dirs...");
			for(String sourceDir : cmd.getOptionValues(Qualify.OPTION_SOURCE_DIRS)) {
				File dir = new File(sourceDir);
				logger.info("Adding source dir: " + dir.getAbsolutePath());
				th.testCasesSourceDirs.add(dir);
			}
		}
	}

	private static void loadPreprocessors(CommandLineTool cmd) {
		if(cmd.isOptionInCommandLine(Qualify.OPTION_SCRIPT_PREPROCESSORS_DIRS)) {
			logger.info("Loading preprocessors...");
			for(String scriptDirPath : cmd.getOptionValues(Qualify.OPTION_SCRIPT_PREPROCESSORS_DIRS)) {
				File scriptDir = new File(scriptDirPath);
				if(scriptDir.exists() && scriptDir.isDirectory()) {
					List<File> scriptFiles = TestToolFile.listFiles(scriptDir, ".*\\.groovy", true);
					if(scriptFiles != null) {
						for(File scriptFile : scriptFiles) {
							try {
								TestScript.loadPreprocessorFromScript(scriptFile);
							} catch(CompilationFailedException e) {
								ErrorsAndWarnings.addError(e.getMessage());
								e.printStackTrace();
							} catch(IOException e) {
								ErrorsAndWarnings.addError(e.getMessage());
								e.printStackTrace();
							} catch(InstantiationException e) {
								ErrorsAndWarnings.addError(e.getMessage());
								e.printStackTrace();
							} catch(IllegalAccessException e) {
								ErrorsAndWarnings.addError(e.getMessage());
								e.printStackTrace();
							}
						}
					} else {
						ErrorsAndWarnings.addWarning("No script file found in folder '" + scriptDirPath + "'");
					}
				} else {
					ErrorsAndWarnings.addError("Folder '" + scriptDirPath + "' does not exist or is not a directory.");
				}
			}
		}
	}

	private static void loadDependencies(CommandLineTool cmd) {
		if(cmd.isOptionInCommandLine(Qualify.OPTION_SCRIPT_DEPENDENCIES_DIRS)) {
			logger.info("Loading script dependencies...");
			TestScript.loadDependencies(new File(cmd.getOptionValue(Qualify.OPTION_SCRIPT_DEPENDENCIES_DIRS)));
		}
	}

	private static void loadScripts(CommandLineTool cmd, TestHarness th) {
		if(cmd.isOptionInCommandLine(Qualify.OPTION_SCRIPT_DIRS)) {
			logger.info("Loading scripts...");
			for(String scriptDirPath : cmd.getOptionValues(Qualify.OPTION_SCRIPT_DIRS)) {
				th.testCasesScriptDirs.add(scriptDirPath);
				File scriptDir = new File(scriptDirPath);
				if(scriptDir.exists() && scriptDir.isDirectory()) {
					List<File> scriptFiles = TestToolFile.listFiles(scriptDir, ".*", true);
					for(File scriptFile : scriptFiles) {
						logger.info("loading test script: " + scriptFile.getAbsolutePath());
						try {
							TestCase newTestCase = TestScript.loadTestCaseFromScript(scriptFile);
							if(newTestCase != null) {
								if(newTestCase.hasKeyword("SANITY_TEST")) {
									th.addSanityTest(newTestCase);
								} else {
									th.register(newTestCase);
								}
							}
						} catch(Exception e) {
							ErrorsAndWarnings.addException(e);
							String testCaseName = scriptFile.getName().split("\\.")[0];
							TestCase newTestCase = TestCase.createNotRunningTestCase();
							newTestCase.setSource(TestSource.createTestSource(scriptFile));
							newTestCase.setName(testCaseName);
							th.register(newTestCase);
							newTestCase.getReport().addException(
									"Exception raised during compilation at " + StackTraceTool.getStackTraceElementLocation(e,
											".*" + testCaseName + ".*"), e);
						}
					}
				} else {
					ErrorsAndWarnings.addError("Script file '" + scriptDirPath + "' does not exist or is not a file.");
				}
			}
		}
	}

	private static void loadObjectRepositories(CommandLineTool cmd) {
		if(cmd.isOptionInCommandLine(Qualify.OPTION_OBJECT_REPOSITORIES_DIRS)) {
			logger.info("Loading objects repositories...");
			for(String repositoryDirPath : cmd.getOptionValues(Qualify.OPTION_OBJECT_REPOSITORIES_DIRS)) {
				File repositoryDir = new File(repositoryDirPath);
				if(repositoryDir.exists()) {
					if(repositoryDir.isDirectory()) {
						List<File> repositoryFiles = TestToolFile.listFiles(repositoryDir, ".*\\.qor", true);
						for(File repositoryFile : repositoryFiles) {
							TestObject.loadObjectRepository(repositoryFile);
						}
					} else {
						ErrorsAndWarnings.addError(
								"Object repository file '" + repositoryDir.getAbsolutePath() + "' does not exist or is not a file.");
					}
				} else {
					repositoryDir.mkdirs();
				}
			}
		}
	}

	private static HTTPServer startHTTPServer(CommandLineTool cmd) {
		HTTPServer result = null;
		if(cmd.isOptionInCommandLine(Qualify.OPTION_HTTP_SERVER)) {
			int serverPort = Integer.valueOf(cmd.getOptionValue(Qualify.OPTION_HTTP_SERVER));
			logger.info("*** Starting HTTP Server using port: " + serverPort);
			try {
				result = new HTTPServer(serverPort);
			} catch(Exception e) {
				e.printStackTrace();
				ErrorsAndWarnings.addError("Launching HTTP Server failed.");
			}
		}
		return result;
	}

	private Collection<TestCase> getTestCasesFromRequirement(CommandLineTool cmd) {
		Collection<TestCase> result = new LinkedList<TestCase>();
		if(cmd.isOptionInCommandLine(Qualify.OPTION_REFERENCE_RELEASE_NOTE)) {
			String requirementName = cmd.getOptionValue(Qualify.OPTION_REQUIREMENT_TO_TEST);
			File referenceReleaseNote = new File(cmd.getOptionValue(Qualify.OPTION_REFERENCE_RELEASE_NOTE));
			if(referenceReleaseNote.isFile() && referenceReleaseNote.exists()) {
				for(String tcName : ReleaseNote.getTestCasesFromRequirement(referenceReleaseNote, requirementName)) {
					result.add(getTestCase(tcName));
				}
				logger.info("*** TEST CASES FOR REQUIREMENT " + requirementName + " (" + result.size() + ") :");
				for(TestCase testCase : result) {
					logger.info("** " + testCase.getLocalName());
				}
			} else {
				ErrorsAndWarnings.addError(
						"Cannot find reference release note '" + cmd.getOptionValue(Qualify.OPTION_REFERENCE_RELEASE_NOTE)
								+ "'. File does not exist or is a directory.");
			}
		} else {
			ErrorsAndWarnings.addError("Cannot find test cases for requirement '" + cmd.getOptionValue(Qualify.OPTION_REQUIREMENT_TO_TEST)
					+ "'. Reference release note must be passed through option '-" + Qualify.OPTION_REFERENCE_RELEASE_NOTE + "'");
		}
		return result;
	}

	private Collection<TestCase> getTestCasesFromKeyword(CommandLineTool cmd) {
		Collection<TestCase> result = new LinkedList<TestCase>();
		String keywordToTest = cmd.getOptionValue(Qualify.OPTION_KEYWORD_TO_TEST);
		for(TestCase tc : getTestCases()) {
			if(tc.hasKeyword(keywordToTest)) {
				result.add(tc);
			}
		}
		return result;
	}

	private Collection<TestCase> getLastFailedTestCases(CommandLineTool cmd) {
		Collection<TestCase> result = new LinkedList<TestCase>();
		if(cmd.isOptionInCommandLine(Qualify.OPTION_RELEASE_NOTE_FILE_NAME)) {
			File releaseNote = new File(cmd.getOptionValue(Qualify.OPTION_RELEASE_NOTE_FILE_NAME));
			if(releaseNote.exists() && releaseNote.isFile()) {
				for(String tcName : ReleaseNote.getFailedTestCases(releaseNote)) {
					result.add(getTestCase(tcName));
				}
				logger.info("*** LAST FAILED TEST CASES (" + result.size() + ") :");
				for(TestCase testCase : result) {
					logger.info("** " + testCase.getLocalName());
				}
			} else {
				ErrorsAndWarnings.addError("Cannot run last failed because release note '" + releaseNote.getAbsolutePath()
						+ "' does not exist or is not a file.");
			}
		} else {
			ErrorsAndWarnings.addError("Cannot run last failed tests without a generated release note.");
		}
		return result;
	}

	public static int runTestHarness(String[] args, TestHarness th) {

		CommandLineTool cmd = Qualify.loadOptions(args);

		setReleaseMetaData(cmd);
		loadSRD(cmd, th);
		setSourceDirs(cmd, th);
		loadPreprocessors(cmd);
		loadDependencies(cmd);
		loadScripts(cmd, th);
		loadObjectRepositories(cmd);

		th.registerTestCases();

		th.prepareReloadableTestCases();
		if(cmd.isOptionInCommandLine(Qualify.OPTION_TEST_CASE_MAX_ATTEMPT)) {
			th.setTestCasesMaxAttempt(cmd);
		}

		HTTPServer server = startHTTPServer(cmd);

		Collection<TestCase> testCasesToRun = new LinkedList<TestCase>();

		if((ErrorsAndWarnings.getErrorsCount() == 0) || cmd.isOptionInCommandLine(Qualify.OPTION_RUN_ON_ERROR)) {

			File continuousReleaseNoteFile = null;
			if(cmd.isOptionInCommandLine(Qualify.OPTION_CONTINUOUS_RELEASE_NOTE)
					&& cmd.getOptionValue(Qualify.OPTION_RELEASE_NOTE_FILE_NAME) != null) {
				continuousReleaseNoteFile = new File(cmd.getOptionValue(Qualify.OPTION_RELEASE_NOTE_FILE_NAME));

				try {
					ReleaseNote.generateReleaseNote(th, continuousReleaseNoteFile);
				} catch(IOException e) {
					e.printStackTrace();
				}
			}

			boolean sanityPassed = false;

			if(cmd.isOptionInCommandLine(Qualify.OPTION_NO_SANITY)) {
				sanityPassed = true;
			} else {
				th.runAllSanityTests(continuousReleaseNoteFile);
				sanityPassed = th.areSanityTestsSuccessful();
			}

			if(sanityPassed) {
				if(cmd.isOptionInCommandLine(Qualify.OPTION_TEST_TO_RUN)) {
					String[] testCasesNames = cmd.getOptionValues(Qualify.OPTION_TEST_TO_RUN);
					for(String testCaseName : testCasesNames) {
						testCasesToRun.add(th.getTestCase(testCaseName));
					}
				} else if(cmd.isOptionInCommandLine(Qualify.OPTION_REQUIREMENT_TO_TEST)) {
					testCasesToRun = th.getTestCasesFromRequirement(cmd);
				} else if(cmd.isOptionInCommandLine(Qualify.OPTION_KEYWORD_TO_TEST)) {
					testCasesToRun = th.getTestCasesFromKeyword(cmd);
				} else if(cmd.isOptionInCommandLine(Qualify.OPTION_RUN_LAST_FAILED)) {
					testCasesToRun = th.getLastFailedTestCases(cmd);
				} else {
					testCasesToRun = new LinkedHashMap<String, TestCase>(th.testCases).values();
				}
			} else {
				ErrorsAndWarnings.addError("Sanity tests have failed");
			}

			if(cmd.isOptionInCommandLine(Qualify.OPTION_TEST_CASE_LIST_NAME)) {
				th.printTestCaseList();
			} else {
				th.runTestCases(testCasesToRun, continuousReleaseNoteFile);

				logger.info("**********************");
				logger.info("* TEST CASES SUMMARY *");
				logger.info("**********************");
			}

			exportReports(th);

			Collection<String> testCasesNames = new LinkedList<String>();
			for(TestCase testCase : testCasesToRun) {
				testCasesNames.add(testCase.getLocalName());
			}
			th.printTestCasesSynthesis(testCasesNames);

			logger.info("********************");
			logger.info("* TEST HARNESS END *");
			logger.info("********************");
		} else {
			logger.info("******************************************");
			logger.info("* TEST HARNESS STOPPED BECAUSE OF ERRORS *");
			logger.info("******************************************");
			logger.info("* To continue execution even with errors, use the option: " + Qualify.OPTION_RUN_ON_ERROR);
		}

		if(server != null) {
			try {
				logger.info("*** HTTP Server is still running on port: " + server.getPort());
				server.join();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}

		Collection<String> warnings = ErrorsAndWarnings.getWarnings();
		Collection<String> errors = ErrorsAndWarnings.getErrors();
		StringBuilder errorsAndWarnings = new StringBuilder("" + warnings.size() + " Warning(s) and " + errors.size() + " error(s)\n");
		for(String warning : warnings) {
			errorsAndWarnings.append("WARNING\t" + warning + "\n");
		}
		for(String error : errors) {
			errorsAndWarnings.append("ERROR\t" + error + "\n");
		}
		logger.info(errorsAndWarnings.toString());

		return ErrorsAndWarnings.getErrorsCount();
	}

	private TestCase getTestCase(String name) {
		TestCase result = testCases.get(name);
		if(result == null) {
			result = sanityTests.get(name);
		}
		return result;
	}

	private void setTestCasesMaxAttempt(CommandLineTool cmd) {
		try {
			int tcMaxAttempt = Integer.valueOf(cmd.getOptionValue(Qualify.OPTION_TEST_CASE_MAX_ATTEMPT));
			if(tcMaxAttempt < 1) {
				throw new IllegalArgumentException("Must be greater or equals to 1");
			}
			testCasesMaxAttemptNumber = tcMaxAttempt;
		} catch(Throwable t) {
			ErrorsAndWarnings.addError(String.format("Wrong value for option %s: %s. Must be an integer greater or equal to 1.",
					Qualify.OPTION_TEST_CASE_MAX_ATTEMPT, cmd.getOptionValue(Qualify.OPTION_TEST_CASE_MAX_ATTEMPT)));
		}
	}

	private void prepareReloadableTestCases() {
		try {
			for(Class<? extends TestCase> clazz : reloadableTestCases) {
				TestCase tc = clazz.newInstance();
				register(tc);
			}
		} catch(Throwable t) {
			throw new RuntimeException("Unable to prepare reloadable test cases", t);
		}
	}

	/**
	 * Registers the test case in the harness' test cases list.
	 *
	 * @param tc
	 */
	protected final void register(TestCase tc) {
		String testCaseShortClassName = tc.getLocalName();
		if(testCases.get(testCaseShortClassName) == null) {
			testCases.put(testCaseShortClassName, tc);

			// If no TestSource is set for the TestCase, then it is automatically searched.
			// For all test cases made from Groovy scripts, TestSource is already attached.
			if(tc.getReport() == null) {
				boolean sourceFileFound = false;
				for(File sourceDir : testCasesSourceDirs) {
					String sourceRelativeFileName = getRelativeFileName(testCaseShortClassName + ".java", sourceDir);
					if(sourceRelativeFileName != null) {
						tc.setReport(TestReport.createTestReport(sourceRelativeFileName));
						sourceFileFound = true;
						break;
					} else {
						logger.error("Test source file '" + sourceRelativeFileName + "' not found in: " + sourceDir.getAbsolutePath());
					}
				}
				if(!sourceFileFound) {
					tc.setReport(TestReport.createTestReportWithoutFile(tc.getName()));
				}
			}
		}
	}

	protected final void register(Class<? extends TestCase> clazz) {
		logger.info(String.format("TEST CASE %s registered as reloadable", clazz.getName()));
		reloadableTestCases.add(clazz);
	}

	public final void register(TestSpy spy) {
		spies.add(spy);
	}

	protected final void addSanityTest(TestCase tc) {
		String testCaseShortClassName = tc.getLocalName();
		if(sanityTests.get(testCaseShortClassName) == null) {
			sanityTests.put(testCaseShortClassName, tc);

			for(File sourceDir : testCasesSourceDirs) {
				String sourceRelativeFileName = getRelativeFileName(testCaseShortClassName + ".java", sourceDir);
				if(sourceRelativeFileName != null) {
					tc.setSource(TestSource.createTestSource(new File(sourceRelativeFileName)));
					break;
				}
			}
		}
	}

	/**
	 * Returns the relative path to java source file with shortFileName, using the source directory given through parameters.
	 *
	 * @param shortFileName The short name of the searched source file
	 * @param directory     The top directory where the file is searched recursively
	 * @return The file path, relative to JVM working directory.
	 */
	public static String getRelativeFileName(String shortFileName, File directory) {
		if(directory.exists()) {
			for(String fileOrDir : directory.list()) {
				File f = new File(directory, fileOrDir);
				if(f.isFile() && f.getName().equals(shortFileName)) {
					return directory + "/" + f.getName();
				}
				if(f.isDirectory()) {
					String fileNameFound = getRelativeFileName(shortFileName, new File(directory, f.getName()));
					if(fileNameFound != null) {
						return fileNameFound;
					}
				}
			}
		} else {
			ErrorsAndWarnings.addError("Source dir '" + directory.getAbsolutePath() + "' does not exist");
		}
		return null;
	}

	/**
	 * You must implement that method to register all the test cases you want to run.
	 */
	protected abstract void registerTestCases();

	/**
	 * Returns a fresh instance of <code>testCase</code>. That means that if registered with its {@link Class}, a new instance is created.
	 * If registered with instance, the same instance is returned (bypass).
	 *
	 * @param testCase
	 * @return
	 */
	private TestCase getRefreshedTestCase(TestCase testCase) {
		TestCase result = testCase;
		boolean isAReloadableTestCase = reloadableTestCases.contains(testCase.getClass());
		if(isAReloadableTestCase) {
			logger.info(String.format("TEST CASE %s is a reloadable (max retry: %s)", testCase.getName(), testCasesMaxAttemptNumber));
			try {
				testCases.remove(testCase.getLocalName());

				try {
					testCase = testCase.getClass().newInstance();
					register(testCase);
					result = testCase;
				} catch(Throwable t) {
					ErrorsAndWarnings.addError("Test case '" + testCase.getLocalName() + "' is not reloadable: " + t.getMessage());
					throw t;
				}
			} catch(Throwable t) {
				ErrorsAndWarnings.addError("Test case '" + testCase.getLocalName() + "' is not reloadable: " + t.getMessage());
			}
		}

		return result;
	}

	/**
	 * Runs one single test case.
	 *
	 * @param tc
	 * @return success
	 */
	protected final boolean runSingleTestCase(TestCase tc) {
		if(tc != null) {
			if(!tc.hasRun()) {
				for(TestSpy testSpy : spies) {
					testSpy.beforeTest(tc);
				}

				OutputStream logOutput = null;
				Appender logAppender = null;
				try {
					logOutput = getLogOutputStream(tc);
					logAppender = prepareLogAppender(tc, logOutput);
					tc.resetRequirementTarget();
					ErrorsAndWarnings.activateWarnings();
					tc.beforeRun();
					if(tc.getNbNOK() == 0) {
						try {
							tc.hasRun(true);
							tc.run();
						} catch(Throwable e) {
							try {
								attachExceptionToTestCase(
										"Exception raised during run() at " + StackTraceTool.getCall(e.getStackTrace(), tc.getName()), e,
										tc);
							} catch(Exception e2) {
								e.printStackTrace();
							}
						}
						try {
							logger.info("CLOSING TEST CASE " + tc.getName());
							tc.afterRun();

							for(TestSpy testSpy : spies) {
								testSpy.afterTest(tc);
							}

						} catch(Exception e) {
							try {
								attachExceptionToTestCase(
										"Exception raised during afterRun() at " + StackTraceTool.getComingStackTraceElementLocation(e,
												TestHarness.class), e, tc);
							} catch(Exception e2) {
								e.printStackTrace();
							}

						}
					}
				} catch(Throwable e) {
					attachExceptionToTestCase(
							"Exception raised during beforeRun() at " + StackTraceTool.getComingStackTraceElementLocation(e,
									TestHarness.class), e, tc);

				} finally {
					try {
						releaseLogAppender(logAppender, logOutput);
					} catch(IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return tc.isSuccessful();
	}

	OutputStream getLogOutputStream(TestCase tc) throws IOException {
		String logFileName = String.format("target/qualify-reports/%s/%s.log", tc.getLocalName(), tc.getLocalName());
		File logFile = new File(logFileName);
		logFile.getParentFile().mkdirs();
		logFile.createNewFile();

		return new FileOutputStream(logFile);
	}

	Appender prepareLogAppender(TestCase tc, OutputStream out) {
		Appender result = new WriterAppender(new PatternLayout(Qualify.LOGGER_PATTERN), out);
		Logger.getRootLogger().addAppender(result);

		return result;
	}

	void releaseLogAppender(Appender appender, OutputStream out) throws IOException {
		if(appender != null) {
			Logger.getRootLogger().removeAppender(appender);
		}
		if(out != null) {
			out.close();
		}
	}

	private void attachExceptionToTestCase(String label, Throwable e, TestCase tc) {
		logger.error(label, e);

		String testCaseName = tc.getName();

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		e.printStackTrace(pw);
		pw.flush();
		sw.flush();

		// Adding failed check to current test case
		String className = testCaseName.split("\\.")[testCaseName.split("\\.").length - 1];
		String testSource = "className";
		int testSourceLine = 0;
		try {
			StackTraceElement se = StackTraceTool.getCall(e.getStackTrace(), className);
			testSourceLine = Integer.valueOf(se.getLineNumber()).intValue();
			testSource = se.getFileName();
		} catch(Exception ex) {
			logger.debug("Exception raised when analyzing stacktrace");
		}
		tc.getReport().addException(label, e);

		TestResult tr = new TestResult(false, tc.getRequirementTarget(), sw.toString().split("\\n")[0], testSource, testSourceLine, tc);
		tc.addTestResult(tr);
	}

	/**
	 * Runs all the Sanity tests
	 */
	protected final void runAllSanityTests(File continuousReleaseNoteFile) {
		if(sanityTests.size() > 0) {
			int testIndex = 1;
			long startTime = System.currentTimeMillis();
			for(TestCase testCase : sanityTests.values()) {
				logger.info("*************************");
				logger.info("* SANITY TEST STARTS: " + testCase.getLocalName());
				logger.info("* SANITY PROGRESS: " + testIndex + " / " + sanityTests.size());
				logger.info("*************************");

				long tcStartTime = System.currentTimeMillis();

				//
				runSingleTestCase(testCase);
				//

				long tcEndTime = System.currentTimeMillis();

				testCase.setElapsedTime(new Duration(tcStartTime, tcEndTime));

				// Attaching test result to requirements
				for(TestResult tr : testCase.getResults()) {
					String requirementId = tr.getRequirementId();
					getSrd().createRequirementIfNeeded(requirementId); // add the requirement if not existing
					Requirement target = getSrd().getRequirement(requirementId);
					target.addTestResult(tr);
				}

				this.elapsedTime = new Duration(startTime, System.currentTimeMillis());

				printTestCaseResults(testCase);
				printTestCaseSynthesis(testCase);

				// WRITING RELEASE NOTE
				if(continuousReleaseNoteFile != null) {
					try {
						ReleaseNote.generateReleaseNote(this, continuousReleaseNoteFile);
					} catch(IOException e) {
						e.printStackTrace();
					}
				}

				testIndex++;
			}
			logger.info("*******************************");
			if(areSanityTestsSuccessful()) {
				logger.info("* SANITY TESTS ARE SUCCESSFUL *");
			} else {
				logger.info("* SANITY TESTS HAVE FAILED    *");
			}
			logger.info("*******************************");
		}
	}

	private boolean areSanityTestsSuccessful() {
		boolean result = true;
		for(TestCase tc : sanityTests.values()) {
			result = result && tc.isSuccessful();
		}
		return result;
	}

	protected final void runTestCases(final Collection<TestCase> testCasesToRun, File continuousReleaseNoteFile) {

		long startTime = System.currentTimeMillis();

		for(TestSpy testSpy : spies) {
			testSpy.beforeHarness(this);
		}

		// Preparing storage for test cases to retry at the end.
		Collection<TestCase> remainingTestCases = new LinkedList<TestCase>();

		// FIRST RUN
		{
			int testIndex = 1;
			for(TestCase testCase : testCasesToRun) {
				logger.info("*************************");
				logger.info("* TEST CASE STARTS: " + testCase.getLocalName());
				logger.info("* HARNESS PROGRESS: " + testIndex + " / " + testCasesToRun.size());
				logger.info("*************************");

				long tcStartTime = System.currentTimeMillis();

				//
				boolean isSuccessful = runSingleTestCase(testCase);
				//

				long tcEndTime = System.currentTimeMillis();

				testCase.setElapsedTime(new Duration(tcStartTime, tcEndTime));

				printTestCaseResults(testCase);
				printTestCaseSynthesis(testCase);

				if(!isSuccessful) {
					remainingTestCases.add(testCase);
				}

				if(isSuccessful || testCasesMaxAttemptNumber <= 1) {
					// Attaching test result to requirements
					for(TestResult tr : testCase.getResults()) {
						String requirementId = tr.getRequirementId();
						getSrd().createRequirementIfNeeded(requirementId); // add the requirement if not existing
						Requirement target = getSrd().getRequirement(requirementId);
						target.addTestResult(tr);
					}
				}

				if(testCasesMaxAttemptNumber <= 1) {
					ErrorsAndWarnings.addWarningsAndErrors(testCase);
				}

				// WRITING RELEASE NOTE
				if(continuousReleaseNoteFile != null) {
					try {
						ReleaseNote.generateReleaseNote(this, continuousReleaseNoteFile);
					} catch(IOException e) {
						e.printStackTrace();
					}
				}

				testIndex++;
			}
		}

		// RETRIES
		for(int retry = 1; retry <= testCasesMaxAttemptNumber - 1; retry++) {
			Collection<TestCase> failedTestCases = new LinkedList<TestCase>();

			int testIndex = 1;
			for(TestCase testCase : remainingTestCases) {
				logger.info("*************************");
				logger.info("* TEST CASE RETRY (" + retry + "/" + (testCasesMaxAttemptNumber - 1) + "): " + testCase.getLocalName());
				logger.info("* HARNESS RETRY PROGRESS: " + testIndex + " / " + remainingTestCases.size());
				logger.info("*************************");

				long tcStartTime = System.currentTimeMillis();

				testCase = getRefreshedTestCase(testCase);

				//
				boolean isSuccessful = runSingleTestCase(testCase);
				//

				long tcEndTime = System.currentTimeMillis();

				testCase.setElapsedTime(new Duration(tcStartTime, tcEndTime));

				printTestCaseResults(testCase);
				printTestCaseSynthesis(testCase);

				if(isSuccessful || retry == (testCasesMaxAttemptNumber - 1)) {
					// Attaching test result to requirements
					for(TestResult tr : testCase.getResults()) {
						String requirementId = tr.getRequirementId();
						getSrd().createRequirementIfNeeded(requirementId); // add the requirement if not existing
						Requirement target = getSrd().getRequirement(requirementId);
						target.addTestResult(tr);
					}
				}

				if(!isSuccessful) {
					failedTestCases.add(testCase);
					if(retry == (testCasesMaxAttemptNumber - 1)) {
						ErrorsAndWarnings.addWarningsAndErrors(testCase);
					}
				}

				// WRITING RELEASE NOTE
				if(continuousReleaseNoteFile != null) {
					try {
						ReleaseNote.generateReleaseNote(this, continuousReleaseNoteFile);
					} catch(IOException e) {
						e.printStackTrace();
					}
				}

				testIndex++;
			}

			remainingTestCases = failedTestCases;

			if(remainingTestCases.size() == 0) {
				break;
			}
		}

		this.elapsedTime = new Duration(startTime, System.currentTimeMillis());

		for(TestSpy testSpy : spies) {
			testSpy.afterHarness(this);
		}
	}

	protected static void exportReports(TestHarness th) {
		boolean overrideReferenceReleaseNote = false;

		if(Qualify.isOption(Qualify.OPTION_REFERENCE_RELEASE_NOTE)) {
			File referenceReleaseNote = new File(Qualify.getOptionValue(Qualify.OPTION_REFERENCE_RELEASE_NOTE));
			if(referenceReleaseNote.exists()) {
				boolean hasRegressions = ReleaseNote.computeRegressionsFromReference(th, referenceReleaseNote);
				if(!hasRegressions) {
					overrideReferenceReleaseNote = true;
				}
			} else {
				ErrorsAndWarnings.addWarning(
						"Reference release note '" + referenceReleaseNote.getAbsolutePath() + "' does not exist, so it is generated.");
				overrideReferenceReleaseNote = true;
			}
		}

		if(Qualify.isOption(Qualify.OPTION_RELEASE_NOTE_FILE_NAME)) {
			try {
				File releaseNoteFile = new File(Qualify.getOptionValue(Qualify.OPTION_RELEASE_NOTE_FILE_NAME));
				ReleaseNote.generateReleaseNote(th, releaseNoteFile);
				logAttachment(releaseNoteFile);
				if(overrideReferenceReleaseNote) {
					TestToolFile.copyFile(new File(Qualify.getOptionValue(Qualify.OPTION_RELEASE_NOTE_FILE_NAME)),
							new File(Qualify.getOptionValue(Qualify.OPTION_REFERENCE_RELEASE_NOTE)));
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		} else if(Qualify.isOption(Qualify.OPTION_TEST_CASE_TABLE_FILE_NAME)) {
			File testCaseTableOutputFile = new File(Qualify.getOptionValue(Qualify.OPTION_TEST_CASE_TABLE_FILE_NAME));
			TestCasesTable.generateTestCasesTable(testCaseTableOutputFile, th.getTestCases());
			logAttachment(testCaseTableOutputFile);
			String resultFolderPath = testCaseTableOutputFile.getParent();
			for(TestCase tc : th.getTestCases()) {
				File testResultFile = new File(resultFolderPath + "/" + tc.getName() + "/" + tc.getName() + ".xml");
				try {
					tc.getReport().generateXML(testResultFile);
				} catch(IOException e) {
					ErrorsAndWarnings.addError(e.getLocalizedMessage());
					e.printStackTrace();
				}
			}
		} else if(Qualify.isOption(Qualify.OPTION_RESULT_DIR)) {
			String resultFolderPath = Qualify.getOptionValue(Qualify.OPTION_RESULT_DIR);
			// Copying XSL, CSS, JS and PNG from jar's resources next to the new XML
			try {
				DocumentUtils.generateStyleFolder(new File(resultFolderPath));
			} catch(IOException e1) {
				ErrorsAndWarnings.addException(e1);
			}

			for(TestCase tc : th.getTestCases()) {
				File testResultFile = new File(resultFolderPath + "/" + tc.getName() + "/" + tc.getName() + ".xml");
				try {
					tc.getReport().generateXML(testResultFile);
				} catch(IOException e) {
					ErrorsAndWarnings.addError(e.getLocalizedMessage());
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Logs the names of all registered test cases
	 */
	protected final void printTestCaseList() {
		logger.info("* TEST CASES LIST:");
		for(TestCase testCase : getTestCases()) {
			logger.info(testCase.getLocalName());
		}
		logger.info("* END OF LIST");
	}

	protected final void printTestCaseSynthesis(String testCaseName) {
		printTestCaseResults(getTestCase(testCaseName));
	}

	/**
	 * Logs a synthesis for one test case
	 *
	 * @param tc
	 */
	protected final void printTestCaseSynthesis(TestCase tc) {
		if(tc != null) {
			String info = "TEST CASE [" + tc.getName() + "] GLOBAL:";
			if(tc.isSuccessful()) {
				info += " " + TestResult.RESULT_OK_VALUE;
			} else {
				info += " " + TestResult.RESULT_NOK_VALUE;
			}
			logger.info(info);
		}
	}

	/**
	 * Logs the results of one test case
	 *
	 * @param tc
	 */
	protected final void printTestCaseResults(TestCase tc) {
		for(TestResult result : tc.getResults()) {
			String info = "TEST CASE [" + tc.getName() + ":" + result.getTestSourceLine() + "]";
			if(result.isSuccessful()) {
				info += " " + TestResult.RESULT_OK_VALUE;
			} else {
				info += " " + TestResult.RESULT_NOK_VALUE;
			}
			info += " : " + result.getRequirementId();
			logger.info(info);
		}
	}

	protected final void printTestCasesSynthesis(Collection<String> testCasesToRun) {
		StringBuilder runFailedOption = null;
		for(String testCaseName : testCasesToRun) {
			TestCase testCase = getTestCase(testCaseName);
			printTestCaseSynthesis(testCase);

			if(!testCase.isSuccessful()) {
				if(runFailedOption == null) {
					runFailedOption = new StringBuilder(
							"-D" + CommandLineTool.OPTION_SYSTEM_PROPERTIES_PREFIX + Qualify.OPTION_TEST_TO_RUN + "=" + testCaseName);
				} else {
					runFailedOption.append("," + testCaseName);
				}
			}
		}

		if(runFailedOption != null) {
			logger.info("Run last failed test using following option: " + runFailedOption.toString());
		}
	}

	protected final void printAllTestCasesResults() {
		for(TestCase testCase : getTestCases()) {
			printTestCaseResults(testCase);
		}
	}

	public Duration getElapsedTime() {
		return elapsedTime;
	}

	static void logAttachment(File file) {
		System.out.println("[[ATTACHMENT|" + file.getAbsolutePath() + "]]");
	}
}
