/*Copyright (c) 2010, Mathieu Bordas
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

package qualify.doc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.ProcessingInstruction;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.joda.time.Duration;

import qualify.TestCase;
import qualify.TestResult;
import qualify.tools.TestToolDates;

public class TestCasesTable {

	static Logger logger = Logger.getLogger(TestCasesTable.class);

	public static void generateTestCasesTable(File outputFile, List<TestCase> testCases) {

		File outputFolder = outputFile.getParentFile();
		if(outputFolder == null) {
			outputFolder = new File(".");
		}

		Element root = new Element("test_cases");
		Duration elapsedTime = Duration.ZERO;
		for(TestCase tc : testCases) {
			if(tc.getElapsedTime() != null) {
				elapsedTime = elapsedTime.plus(tc.getElapsedTime());
			}
		}
		root.setAttribute("elapsed_time", TestToolDates.toString(elapsedTime));
		Document doc = new Document(root);

		// Attaching the XSL
		HashMap<String, String> piMap = new HashMap<String, String>(2);
		piMap.put( "type", "text/xsl" );
		piMap.put( "href", "style/test_cases_table.xsl" );
		ProcessingInstruction pi = new ProcessingInstruction("xml-stylesheet", piMap);
		doc.getContent().add(0, pi);
		
		// Copying XSL, CSS, JS and PNG from jar's resources next to the new XML
		try {
			DocumentUtils.generateStyleFolder(outputFolder);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		logger.debug("Exporting " + testCases.size() + " test case(s)...");
		for(TestCase tc : testCases) {
			String testCaseLocalName = tc.getLocalName();
			Element testCaseElement = new Element("test_case");
			testCaseElement.setAttribute("name", testCaseLocalName);

			if(tc.getElapsedTime() != null) {
				// Test case has been run
				testCaseElement.setAttribute("elapsed_time", TestToolDates.toString(tc.getElapsedTime()));

				if(tc.isSuccessful()) {
					testCaseElement.setAttribute("result", TestResult.RESULT_OK_VALUE);
				} else {
					testCaseElement.setAttribute("result", TestResult.RESULT_NOK_VALUE);
				}

				List<TestResult> testResults = tc.getResults();
				for(TestResult testResult : testResults) {
					if(testResult.getTestSource() != null) {
						Element testResultElement = new Element("test_result");
						testResultElement.setAttribute("result", TestResult.resultStringValue(testResult.isSuccessful()));
						testResultElement.setAttribute("test_source", testCaseLocalName + "/" + testCaseLocalName + ".xml");
						testResultElement.setAttribute("test_source_line", "" + testResult.getTestSourceLine());
						testCaseElement.addContent(testResultElement);
					}
				}
			}

			File testSourceFile = new File(outputFolder.getAbsolutePath() + "/" + testCaseLocalName + "/" + testCaseLocalName + ".xml");
			logger.debug("Generating test source " + tc.getName() + " into file '"
					+ testSourceFile.getAbsolutePath());
			try {
				if(tc.getReport() != null) {
					tc.getReport().generateXML(testSourceFile);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			testCaseElement.setAttribute("test_source",testCaseLocalName + "/" + testCaseLocalName + ".xml");

			root.addContent(testCaseElement);
		}

		try {
			XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
			FileOutputStream fos = new FileOutputStream(outputFile);
			serializer.output(doc, fos);
			fos.close();
		}
		catch (IOException e) {
			System.err.println(e);
		}

		logger.info("Test cases table successfully exported: " + outputFile.getAbsolutePath());
	}

}
