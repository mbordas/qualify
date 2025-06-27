/*Copyright (c) 2010-2012, Mathieu Bordas
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

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;
import qualify.doc.Attachment;
import qualify.doc.Base64Image;
import qualify.doc.DocList;
import qualify.doc.DocString;
import qualify.doc.DomElementAble;
import qualify.doc.Field;
import qualify.doc.Table;
import qualify.doc.TestReport;
import qualify.doc.TestSource;
import qualify.tools.StackTraceTool;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * Extend TestCase to create a new testing scenario. You must extend TestCase and override the main method run()
 */
public abstract class TestCase {

	private static Logger logger = Logger.getLogger(TestCase.class);

	public static final String ELEMENT_NAME = "test_case", ATTRIBUTE_NAME = "name";

	private String name = "no name";
	private TestReport report = null;

	public static boolean enableTestToolsLogs = true;

	private String requirementTarget = null;

	private boolean hasRun = false;
	private Duration elapsedTime = null;
	private LinkedList<TestResult> results = null;
	private LinkedList<TestComment> comments = null;
	private LinkedList<String> keywords = null;

	private static LinkedList<String> warnings = new LinkedList<>();
	private static LinkedList<String> errors = new LinkedList<>();

	private static final String TAG_NAME = "test_case", NAME_ATTRIBUTE_NAME = "name";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean hasRun() {
		return hasRun;
	}

	public void hasRun(boolean hasRun) {
		this.hasRun = hasRun;
	}

	public String getLocalName() {
		String[] words = name.split("\\.");
		return words[words.length - 1];
	}

	public void setSource(TestSource source) {
		report = new TestReport(source);
	}

	public void setReport(TestReport report) {
		this.report = report;
	}

	public TestReport getReport() {
		return report;
	}

	public LinkedList<TestResult> getResults() {
		return results;
	}

	public TestCase() {
		this.name = this.getClass().getName();
		results = new LinkedList<>();
		comments = new LinkedList<>();
		keywords = new LinkedList<>();
	}

	public static void activateTestToolsLog(boolean shouldActivate) {
		enableTestToolsLogs = shouldActivate;
	}

	public static boolean deactivateTestToolsLog() {
		boolean result = enableTestToolsLogs;
		enableTestToolsLogs = false;
		return result;
	}

	public boolean isTestToolsLogActivated() {
		return enableTestToolsLogs;
	}

	public boolean isSuccessful() {
		if((results.size() < 1) || (getNbNOK() > 0)) {
			return false;
		} else {
			return true;
		}
	}

	public int getNbNOK() {
		int result = 0;
		for(TestResult tr : results) {
			if(!tr.isSuccessful()) {
				result++;
			}
		}
		return result;
	}

	public void addKeyword(String keyword) {
		keywords.add(keyword);
	}

	public void addKeywords(String[] keywords) {
		for(String keyword : keywords) {
			this.keywords.add(keyword);
		}
	}

	public boolean hasKeyword(String keyword) {
		for(String kw : keywords) {
			if(kw.equalsIgnoreCase(keyword)) {
				return true;
			}
		}
		return false;
	}

	public void step(String title) {
		comment("STEP: " + title);
		StackTraceElement trace = StackTraceTool.getTestCaseCall();
		if(trace != null) {
			report.addStep(trace.getLineNumber(), title);
		} else {
			addWarning("Cannot add step to report: unable to process stack trace.");
		}
	}

	public void addTestComment(TestComment tc) {
		if(tc.getTestSource() == null) {
			StackTraceElement trace = StackTraceTool.getTestCaseCall();
			if(trace != null) {
				String fileName = trace.getFileName();
				if(report != null) {
					report.addTestComment(tc);
				} else {
					addWarning("Cannot add TestComment to source because TestSource '" + fileName + "' doesn't exist.");
				}
			} else {
				addWarning("Cannot add TestComment to source because TestSource is not found from StackTrace.");
			}
		} else {
			if(report != null) {
				report.addTestComment(tc);
			} else {
				addWarning("Cannot add TestComment to source because TestSource '" + tc.getTestSource() + "' doesn't exist.");
			}
		}
		comments.add(tc);
		logger.info("comment added line " + tc.getLineNumber() + " : \n" + tc.toStringForConsole());
	}

	public void addComment(DomElementAble comment, Class<?> calledClass) {
		StackTraceElement trace = StackTraceTool.getTestCaseCall();
		String fileName = trace.getFileName();
		TestComment testComment = null;
		if(getReport() == null) {
			addWarning("Cannot add TestComment to source because TestSource '" + fileName + "' doesn't exist.");
		} else {
			testComment = new TestComment(comment, fileName, trace.getLineNumber(), this);
			report.addTestComment(testComment);
		}
		comments.add(testComment);
	}

	public void addComment(String text, Class<?> calledClass) {
		StackTraceElement trace = StackTraceTool.getTestCaseCall();
		String fileName = trace.getFileName();
		TestComment comment = new TestComment(text, fileName, trace.getLineNumber(), this);
		if(getReport() == null) {
			addWarning("Cannot add TestComment to source because TestSource '" + fileName + "' doesn't exist.");
		} else {
			report.addTestComment(comment);
		}
		comments.add(comment);
		logger.info("comment added line " + comment.getLineNumber() + " : \n" + comment.toStringForConsole());
	}

	public void addTestResult(TestResult tr) {
		if(tr.getTestSource() == null) {
			StackTraceElement trace = StackTraceTool.getTestCaseCall();
			if(trace != null) {
				String fileName = trace.getFileName();
				if(report != null) {
					report.addTestResult(tr);
				} else {
					addWarning("Cannot add TestResult to source because TestSource '" + fileName + "' doesn't exist.");
				}
			} else {
				addWarning("Cannot add TestResult to source because TestSource is not found from StackTrace.");
			}
		} else {
			if(report != null) {
				report.addTestResult(tr);
			} else {
				addWarning("Cannot add TestResult to source because TestSource '" + tr.getTestSource() + "' doesn't exist.");
			}
		}
		results.add(tr);
	}

	/**
	 * Use that method for TestCase checks. It registers a TestResult with values: success/failure, current requirement if set, test case
	 * source file and line.
	 */
	public boolean check(boolean condition) {
		if(requirementTarget == null) {
			setRequirementTarget(Requirement.EMPTY_REQUIREMENT_ID);
		}
		return check(condition, requirementTarget, null);
	}

	protected boolean checkNotNull(Object value) {
		boolean result = false;
		if(value == null) {
			addTestResult(false, "expected is not null | tested is null");
			result = false;
		} else {
			addTestResult(true, "expected is not null | tested=" + value);
			result = true;
		}
		return result;
	}

	protected boolean checkNull(Object value) {
		boolean result = false;
		if(value == null) {
			addTestResult(true, "expected is null | tested is null");
			result = true;
		} else {
			addTestResult(false, "expected is null | tested=" + value);
			result = false;
		}
		return result;
	}

	protected boolean checkEquality(Object expectedObject, Object testedObject) {
		boolean result = false;
		if(testedObject == null) {
			if(expectedObject == null) {
				addTestResult(true, "expected is null | tested is null");
				result = true;
			} else {
				addTestResult(false, "expected=" + expectedObject + " | tested is null");
				result = false;
			}
		} else {
			if(expectedObject == null) {
				addTestResult(false, "expected is null | tested=" + testedObject);
				result = false;
			} else {
				result = expectedObject.equals(testedObject);
				addTestResult(result, "expected=" + expectedObject + " | tested=" + testedObject);
			}
		}
		return result;
	}

	/**
	 * Main check method.
	 */
	private boolean check(boolean condition, String requirementId, String comment) {
		// Searching java file and line number of test file
		String testSource = null;
		int testSourceLine = 0;

		StackTraceElement se = StackTraceTool.getTestCaseCall();
		testSource = se.getFileName();
		testSourceLine = Integer.valueOf(se.getLineNumber()).intValue();

		TestResult testResult = new TestResult(condition, requirementId, comment, testSource, testSourceLine, this);
		addTestResult(testResult);

		return condition;
	}

	/**
	 * The need() method interrupts the run() execution if 'condition' is false. If condition is true, need() behaves like a check(). If
	 * condition is false, need() simulates a check(false) and throws an exception. Such exception is catched by the TestHarness.
	 */
	protected boolean need(boolean condition) {
		if(requirementTarget == null) {
			setRequirementTarget(Requirement.EMPTY_REQUIREMENT_ID);
		}
		if(condition == true) {
			return check(condition, getRequirementTarget(), null);
		} else {
			throw new RuntimeException("Needed condition is 'false'.");
		}

	}

	protected void comment(String text) {
		// Searching java file and line number of test file
		String testSource = null;
		int testSourceLine = 0;

		StackTraceElement se = StackTraceTool.getTestCaseCall();
		testSource = se.getFileName();
		testSourceLine = Integer.valueOf(se.getLineNumber()).intValue();

		TestComment testComment = new TestComment(text, testSource, testSourceLine, this);
		addTestComment(testComment);
	}

	protected void comment(int i) {
		comment("" + i);
	}

	protected void comment(float f) {
		comment("" + f);
	}

	protected void comment(double d) {
		comment("" + d);
	}

	protected void comment(boolean b) {
		comment("" + b);
	}

	protected void commentAll(Collection<? extends Object> objects) {
		for(Object object : objects) {
			comment(object.toString());
		}
	}

	public void comment(DomElementAble element) {
		// Searching java file and line number of test file
		String testSource = null;
		int testSourceLine = 0;

		StackTraceElement se = StackTraceTool.getTestCaseCall();
		testSource = se.getFileName();
		testSourceLine = Integer.valueOf(se.getLineNumber()).intValue();

		TestComment testComment = new TestComment(element, testSource, testSourceLine, this);
		addTestComment(testComment);
	}

	protected void comment(Throwable e) {
		DocList list = new DocList();
		list.addItem(new Field("Message", e.getMessage()));
		list.addItem(new Field("LocalizedMessage", e.getLocalizedMessage()));
		if(e.getCause() != null) {
			list.addItem(new Field("Cause", e.getCause().toString()));
		}
		for(StackTraceElement element : e.getStackTrace()) {
			String traceLine =
					element.getClassName() + ":" + element.getMethodName() + " (" + element.getFileName() + ":" + element.getLineNumber()
							+ ")";
			list.addItem(new DocString(traceLine));
		}
		comment(list);
	}

	protected void comment(Properties properties) {
		Table table = new Table(new String[] { "Key", "Value" });

		Set<String> keys = new TreeSet<>();

		for(Object key : properties.keySet()) {
			keys.add(key.toString());
		}

		for(String key : keys) {
			String value = properties.getProperty(key);
			table.addEntry(new String[] { key, value });
		}

		comment(table);
	}

	protected void commentBase64Image(String image) {
		Base64Image domImage = new Base64Image(image);
		comment(domImage);
	}

	/**
	 * Creates a TestResult. The source file and line will correspond to the Class that calls the 'calledClass'.
	 */
	public boolean addTestResult(boolean condition, String comment) {
		StackTraceElement se = StackTraceTool.getTestCaseCall();
		if(requirementTarget == null) {
			requirementTarget = Requirement.EMPTY_REQUIREMENT_ID;
		}
		addTestResult(new TestResult(condition, requirementTarget, comment, se.getFileName(), se.getLineNumber(), this));
		return condition;
	}

	public void addTestResult(boolean condition, DomElementAble comment) {
		StackTraceElement se = StackTraceTool.getTestCaseCall();
		if(requirementTarget == null) {
			requirementTarget = Requirement.EMPTY_REQUIREMENT_ID;
		}
		addTestResult(new TestResult(condition, requirementTarget, comment, se.getFileName(), se.getLineNumber(), this));
	}

	public void attachFile(File f, Attachment.Type type) {
		StackTraceElement se = StackTraceTool.getTestCaseCall();
		if(report != null) {
			report.attachFile(f, type, se.getFileName(), se.getLineNumber());
		} else {
			addWarning("Cannot attach file '" + f.getAbsolutePath() + "': no source is available");
		}
	}

	public void attachFile(File f) {
		if(report != null) {
			attachFile(f, Attachment.Type.OTHER);
		} else {
			addWarning("Cannot attach file '" + f.getAbsolutePath() + "': no source is available");
		}
	}

	public void attachFile(File f, String fileName) {
		if(report != null) {
			this.report.attachFile(f, Attachment.Type.OTHER, fileName, TestCase.class);
		} else {
			addWarning("Cannot attach file '" + f.getAbsolutePath() + "': no source is available");
		}
	}

	public File getRelativeFile(String relativePathFromTestSource) {
		if(getReport() == null) {
			throw new TestException("Cannot find relative file because source path is unknown.");
		} else {
			String path = getReport().getSource().getOriginalFile().getParent() + relativePathFromTestSource;
			return new File(path);
		}
	}

	/**
	 * Sets the current requirement. Call that method before performing checks about this requirement.
	 *
	 * @param requirementId The same id as in the SRD
	 */
	protected void setRequirementTarget(String requirementId) {
		if(requirementId != null) {
			this.requirementTarget = requirementId;
		} else {
			StackTraceElement callingTestCase = StackTraceTool.getTestCaseCall();
			addWarning("Requirement target '" + requirementId + "' does not exist (" + callingTestCase.getFileName() + " : "
					+ callingTestCase.getLineNumber() + ")");
			this.requirementTarget = Requirement.EMPTY_REQUIREMENT_ID;
		}
	}

	protected void resetRequirementTarget() {
		this.requirementTarget = Requirement.EMPTY_REQUIREMENT_ID;
	}

	/**
	 * Returns the current target Requirement. That method is used to process Exceptions thrown during run()
	 */
	public String getRequirementTarget() {
		return requirementTarget;
	}

	/**
	 * Sets the current requirement when requirement is written in test case.
	 *
	 * @param requirementId A new id, that cannot be found in SRD
	 */
	protected void setInlineRequirement(String requirementId) {
		setRequirementTarget(requirementId);
	}

	/**
	 * Stops the execution of the test case during expected time.
	 *
	 * @param timeInSeconds The time to pause in seconds. Precision is millisecond.
	 */
	public void pause(double timeInSeconds) {
		DateTime start = new DateTime();
		try {
			Thread.sleep((long) (timeInSeconds * DateTimeConstants.MILLIS_PER_SECOND));
			DateTime end = new DateTime();
			long duration_ms = new Duration(start, end).getMillis();
			String text = String.format("Duration %d ms from %s to %s", duration_ms, start.toString("HH:mm:ss", Locale.getDefault()),
					end.toString("HH:mm:ss", Locale.getDefault()));
			comment(text);
		} catch(InterruptedException e) {
			e.printStackTrace();
			addError("An Exception occured during Thread.sleep");
		}
	}

	/**
	 * Stops the execution of the test case during expected time.
	 *
	 * @param timeInSeconds The time to pause in seconds.
	 */
	protected void pause(int timeInSeconds) {
		pause((double) timeInSeconds);
	}

	protected void pause(DateTime until) {
		long delay_ms = until.getMillis() - System.currentTimeMillis();
		if(delay_ms > 0) {
			pause(delay_ms * 1d / DateTimeConstants.MILLIS_PER_SECOND);
		}
	}

	protected boolean waitUntil(WaitCondition waitCondition, final int timeout_s) {
		final long defaultSleep_ms = 500L;
		return waitUntil(waitCondition, timeout_s, defaultSleep_ms);
	}

	/**
	 * Wait until a specific condition is respected
	 * <p>
	 * Note: for waits relative to DOM elements, use TestToolSelenium.wait* methods
	 * </p>
	 *
	 * @param waitCondition Condition to check
	 * @param timeout_s     Timeout in seconds before wait fails
	 * @param sleep_ms      Time to sleep in ms between each checks
	 * @return Condition is successfull
	 */
	protected boolean waitUntil(WaitCondition waitCondition, final int timeout_s, final long sleep_ms) {
		final long start = System.currentTimeMillis();
		boolean success = false;
		Throwable throwable = null;
		while(!success && throwable == null && System.currentTimeMillis() < start + (timeout_s * DateTimeConstants.MILLIS_PER_SECOND)) {
			try {
				success = waitCondition.check();
				if(!success) {
					pause(sleep_ms * 1d / DateTimeConstants.MILLIS_PER_SECOND);
				}
			} catch(Throwable t) {
				throwable = t;
			}
		}
		return success;
	}

	protected void suppressWarnings() {
		ErrorsAndWarnings.suppressWarnings();
	}

	protected void activateWarnings() {
		ErrorsAndWarnings.activateWarnings();
	}

	/**
	 * That method is automatically called by TestHarness one time per test case. You have to describe the scenario of your test case here.
	 */
	public abstract void run() throws Throwable;

	/**
	 * That method is automatically called by TestHarness just before calling run(). run() will be called only if beforeRun() returns TRUE
	 */
	public void beforeRun() throws Throwable {
	}

	/**
	 * That method is automatically called by TestHarness just after calling run().
	 */
	public void afterRun() throws Throwable {
	}

	public static TestCase createTestCaseFromDomElement(Element testCaseTag) {
		TestCase result = new TestCase() {
			public void run() {
			}
		};
		result.setName(testCaseTag.getAttributeValue(ATTRIBUTE_NAME));

		String testSourceName = testCaseTag.getAttributeValue("test_source");
		if(testSourceName != null) {
			File testSource = new File(testSourceName);
			result.setSource(new TestSource(testSource));
		}

		for(Object testResultObject : testCaseTag.getChildren("test_result")) {
			Element testResultTag = (Element) testResultObject;
			TestResult tr = TestResult.createTestResultFromDomElement(testResultTag);
			result.addTestResult(tr);
		}
		return result;
	}

	public static List<String> getLocalNames(Collection<TestCase> testCases) {
		List<String> result = new LinkedList<>();
		for(TestCase tc : testCases) {
			result.add(tc.getLocalName());
		}
		return result;
	}

	public void setElapsedTime(Duration elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public Duration getElapsedTime() {
		return elapsedTime;
	}

	public static TestCase createNotRunningTestCase() {
		return new TestCase() {
			public void beforeRun() {
			}

			public void run() {
			}

			public void afterRun() {
			}
		};
	}

	public static void addWarning(String warnMessage) {
		logger.warn(warnMessage);
		if(!warnings.contains(warnMessage)) {
			warnings.add(warnMessage);
		}
	}

	public void addError(String errMessage) {
		logger.error(errMessage);
		errors.add(errMessage);
	}

	public void addException(Throwable e) {
		logger.error(e.getMessage(), e);
		errors.add(e.getMessage());
	}

	public Collection<String> getWarnings() {
		return warnings;
	}

	public Collection<String> getErrors() {
		return errors;
	}

	public interface WaitCondition {
		boolean check() throws Throwable;
	}
}
