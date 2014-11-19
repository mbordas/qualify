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

package qualify.tools;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.codehaus.groovy.control.CompilationFailedException;

import qualify.TestCase;

public class TestToolGroovy {

	private static Logger logger = Logger.getLogger(TestToolGroovy.class);

	private TestCase testCase = null;

	public TestToolGroovy(TestCase tc) {
		testCase = tc;
	}

	/**
	 * Evaluates the Groovy script into a new GroovyShell (new class loader) and forwards the standard output and error output to the
	 * logFile.
	 * 
	 * @param groovyScript
	 *            The script to evaluate.
	 * @param logFile
	 *            The file where std out and err out will be forwarded
	 * @return
	 * @throws CompilationFailedException
	 *             Error thrown by the GroovyShell
	 * @throws IOException
	 */
	public static Object evaluate(String groovyScript, File logFile) throws CompilationFailedException, IOException {
		Object result = null;

		Binding binding = new Binding();
		PrintStream myStreamOut;
		FileOutputStream fos = new FileOutputStream(logFile);
		myStreamOut = new PrintStream(fos);

		binding.setProperty("out", myStreamOut);

		GroovyShell shell = new GroovyShell(Thread.currentThread().getContextClassLoader(), binding);

		result = shell.evaluate(groovyScript);

		myStreamOut.close();
		fos.close();

		TestToolFile.deleteFile(logFile);

		return result;
	}

	public String evaluateTemplate(String templateScript, HashMap<String, Object> map) {
		SimpleTemplateEngine templateEngine = new SimpleTemplateEngine();
		Template template;
		String result = null;
		try {
			template = templateEngine.createTemplate(templateScript);
			result = template.make(map).toString();
		} catch(CompilationFailedException e) {
			testCase.addTestResult(false, "CompilationFailedException raised: " + e.getMessage());
			e.printStackTrace();
		} catch(ClassNotFoundException e) {
			testCase.addTestResult(false, "ClassNotFoundException raised: " + e.getMessage());
			e.printStackTrace();
		} catch(IOException e) {
			testCase.addTestResult(false, "IOException raised: " + e.getMessage());
			e.printStackTrace();
		}

		return result;
	}

	public static String evaluateTemplate(File templateScript, HashMap<String, Object> map) throws CompilationFailedException,
			ClassNotFoundException, IOException {
		SimpleTemplateEngine templateEngine = new SimpleTemplateEngine();
		Template template;
		String result = null;
		template = templateEngine.createTemplate(templateScript);
		result = template.make(map).toString();
		return result;
	}

	public static String evaluateTemplate(InputStream is, HashMap<String, Object> map) throws CompilationFailedException, IOException {
		logger.debug("Evaluating template from InputStream");
		SimpleTemplateEngine templateEngine = new SimpleTemplateEngine();
		Template template;
		String result = null;
		template = templateEngine.createTemplate(new InputStreamReader(is));
		result = template.make(map).toString();
		return result;
	}
}