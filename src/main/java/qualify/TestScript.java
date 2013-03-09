/*Copyright (c) 2011-2012, Mathieu Bordas
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

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import groovy.lang.MissingPropertyException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.log4j.Logger;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;

import qualify.doc.Preprocessor;
import qualify.doc.TestReport;
import qualify.doc.TestSource;
import qualify.server.HTTPHandler;
import qualify.server.apps.WebApp;
import qualify.tools.TestToolFile;
import qualify.tools.TestToolStrings;

public class TestScript {

	static Logger logger = Logger.getLogger(TestScript.class);

	private static GroovyShell shell = null;
	private static GroovyClassLoader gcl = null;
	private static CompilerConfiguration conf = null;
	private static final String TEMPORARY_PREPROCESSED_FOLDER = "./tmp_preproc/";

	private static void init() {
		if(shell == null) {
			Qualify.initLogs();
			Binding binding = new Binding();
			shell = new GroovyShell(Thread.currentThread().getContextClassLoader(), binding);
		}
	}
	
	public static void loadDependencies(File dependencyFolder) {
		init();
		// Configure
		conf = new CompilerConfiguration();
		File binDir = new File(dependencyFolder.getAbsolutePath() + "/bin");
		try {
			TestToolFile.createDir(binDir);
		} catch (IOException e) {
			ErrorsAndWarnings.addException(e);
			e.printStackTrace();
		}
		conf.setTargetDirectory(binDir.getAbsolutePath());
		CompilationUnit cu = new CompilationUnit(gcl);
		cu.configure(conf);
		// Add more all files to the compilation unit
		cu.addSources(TestToolFile.listFiles(dependencyFolder, ".*\\.groovy", true).toArray(new File[]{}));
		// Compile…
		cu.compile();
		shell.getClassLoader().addClasspath(binDir.getAbsolutePath());
	}
	
	public static void loadScripts(File scriptsFolder) throws MalformedURLException {
		URL[] urls = new URL[] { scriptsFolder.toURI().toURL() };
		URLClassLoader ucl = new URLClassLoader(urls, shell.getClassLoader());
		gcl = new GroovyClassLoader(ucl, conf);
	}

	/**
	 * Reads a script file. Preprocesses the file if one of the registered preprocessors matches the file's name.
	 * Compile the preprocessed source to create a TestCase and returns it.
	 * @param scriptFile The original file to load as a script
	 * @return the script, preprocessed (if Preprocessors) and compiled as TestCase
	 * @throws CompilationFailedException
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static TestCase loadTestCaseFromScript(File scriptFile) throws CompilationFailedException, IOException, InstantiationException, IllegalAccessException, MissingPropertyException {
		init();
		
		TestCase result = null;
		TestSource testSource = TestSource.createTestSource(scriptFile);
		File fileToCompile = scriptFile;

		boolean scriptCanBeCompiled = false;

		if(Preprocessor.runPreprocessor(testSource) // Pre-processing the source file with custom preprocessors
				|| TestToolStrings.matches(".*\\.groovy", scriptFile.getName())) {
			scriptCanBeCompiled = true;
		}

		if(scriptCanBeCompiled) {
			logger.info("loading script: " + scriptFile.getAbsolutePath());
			
			// Default preprocessing
			Preprocessor.defaultPreprocessor.process(testSource);

			// Reading keywords
			String[] keywords = Preprocessor.defaultPreprocessor.getKeywords(testSource);

			// Creating the preprocessed file
			TestToolFile.createDir(new File(TEMPORARY_PREPROCESSED_FOLDER));
			File processedFile = new File(TEMPORARY_PREPROCESSED_FOLDER + scriptFile.getName() + "_preproc");
			TestToolFile.deleteFile(processedFile);
			testSource.exportProcessedFile(processedFile);
			fileToCompile = processedFile;

			// Compiling the preprocessed file
			Class groovyClass = shell.getClassLoader().parseClass(fileToCompile);

			try {
			// Instantiating the class as a new TestCase
				result = (TestCase) groovyClass.newInstance();
			} catch (MissingPropertyException e) {
				throw e;
			}

			// Adding TestCase information
			String testName = scriptFile.getName().split("\\.")[0];
			result.setName(testName);
			result.setReport(new TestReport(testSource));
			result.addKeywords(keywords);
		}
		return result;
	}

	public static void loadScript(File scriptFile) throws CompilationFailedException, IOException, ClassNotFoundException {
		init();
		
		TestSource testSource = TestSource.createTestSource(scriptFile);
		File fileToCompile = scriptFile;

		boolean scriptCanBeCompiled = false;

		if(Preprocessor.runPreprocessor(testSource) // Pre-processing the source file with custom preprocessors
				|| TestToolStrings.matches(".*\\.groovy", scriptFile.getName())) {
			scriptCanBeCompiled = true;
		}

		if(scriptCanBeCompiled) {
			logger.info("loading dependency script: " + scriptFile.getAbsolutePath());
			
			// Default preprocessing
			Preprocessor.defaultPreprocessor.process(testSource);

			// Creating the preprocessed file
			TestToolFile.createDir(new File(TEMPORARY_PREPROCESSED_FOLDER));
			File processedFile = new File(TEMPORARY_PREPROCESSED_FOLDER + scriptFile.getName() + "_preproc");
			TestToolFile.deleteFile(processedFile);
			testSource.exportProcessedFile(processedFile);
			fileToCompile = processedFile;

			// Compiling the preprocessed file
			shell.getClassLoader().getResourceLoader().loadGroovySource(fileToCompile.getAbsolutePath());
		}
	}

	public static Preprocessor loadPreprocessorFromScript(File scriptFile) throws CompilationFailedException, IOException, InstantiationException, IllegalAccessException {
		init();
		logger.info("registering preprocessor: " + scriptFile.getAbsolutePath());

		Preprocessor result = null;

		Class<?> groovyClass = shell.getClassLoader().parseClass(scriptFile);

		result = (Preprocessor) groovyClass.newInstance();
		Preprocessor.registerPreprocessor(result);
		return result;
	}
	
	public static WebApp loadAppFromScript(File scriptFile) throws CompilationFailedException, IOException, InstantiationException, IllegalAccessException {
		init();
		logger.info("compiling app: " + scriptFile.getAbsolutePath());

		WebApp result = null;

		Class<?> groovyClass = shell.getClassLoader().parseClass(scriptFile);

		result = (WebApp) groovyClass.newInstance();
		result.setAppDir(scriptFile.getParentFile());
		HTTPHandler.registerApp(result);
		return result;
	}

}
