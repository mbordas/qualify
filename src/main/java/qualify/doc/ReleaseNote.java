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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.ProcessingInstruction;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import qualify.ErrorsAndWarnings;
import qualify.Qualify;
import qualify.Requirement;
import qualify.TestCase;
import qualify.TestHarness;
import qualify.TestResult;
import qualify.tools.TestToolStrings;


public class ReleaseNote {

	static Logger logger = Logger.getLogger(ReleaseNote.class);

	public static final String
	ELEMENT_ROOT_NAME = "release_note",
	ATTRIBUTE_SUT_NAME = "sut_name",
	ATTRIBUTE_SUT_DATE = "date",
	ATTRIBUTE_SUT_VERSION = "version";

	private String
	sutName = null,
	sutDate = null,
	sutVersion = null;

	private List<Requirement> requirements = null;

	public ReleaseNote(File xmlFile) {
		// Warnings are suppressed during Release note parsing. If not, multiple checks at same line are raised
		ErrorsAndWarnings.suppressWarnings();
		if(xmlFile.exists()) {
			if(xmlFile.isFile()) {
				SAXBuilder parser = new SAXBuilder();
				Document releaseNoteDocument = null;
				try {
					logger.debug("Parsing Release note file '" + xmlFile.getAbsolutePath() + "'");
					releaseNoteDocument = parser.build(xmlFile);

					sutName = releaseNoteDocument.getRootElement().getAttribute(ATTRIBUTE_SUT_NAME).getValue();
					sutDate = releaseNoteDocument.getRootElement().getAttribute(ATTRIBUTE_SUT_DATE).getValue();
					sutVersion = releaseNoteDocument.getRootElement().getAttribute(ATTRIBUTE_SUT_VERSION).getValue();

					requirements = new ArrayList<Requirement>();

					for(Object requirementTagObject : releaseNoteDocument.getRootElement().getChildren("requirement")) {
						Element requirementTag = (Element)requirementTagObject;
						requirements.add(Requirement.createRequirementFromDomElement(requirementTag));
					}
				} catch (JDOMException e) {
					e.printStackTrace();
					ErrorsAndWarnings.addError("Release note reading Exception : " + e.getMessage());
				} catch (IOException e) {
					e.printStackTrace();
					ErrorsAndWarnings.addError("Release note reading Exception : " + e.getMessage());
				}
			} else {
				ErrorsAndWarnings.addError("Release note '" + xmlFile + "' is not a file.");
			}
		} else {
			ErrorsAndWarnings.addError("Release note '" + xmlFile + "' does not exist.");
		}
		ErrorsAndWarnings.activateWarnings();
	}

	public static void generateReleaseNote(TestHarness testHarness, File outputFile) throws IOException {
		// Computing stats for test cases and checks' results
		int numberOfSuccessfulTestCases = 0;
		int numberOfTestCases = 0;
		int numberOfSuccessfulChecks = 0;
		int numberOfChecks = 0;
		for(TestCase tc : testHarness.getTestCases()) {
			numberOfTestCases++;
			for(TestResult tr : tc.getResults()) {
				numberOfChecks++;
				if(tr.isSuccessful()) {
					numberOfSuccessfulChecks++;
				}
			}
			if(tc.isSuccessful()) {
				numberOfSuccessfulTestCases++;
			}
		}
		// Computing stats for requirements
		int numberOfSuccessfulRequirements = 0;
		int numberOfRequirements = 0;
		for(Requirement req : testHarness.getSrd().getRequirements()) {
			numberOfRequirements++;
			numberOfRequirements += req.getAllSubrequirementsCount();
			if(req.isSuccessful()) {
				numberOfSuccessfulRequirements++;
				numberOfSuccessfulRequirements += req.getSuccessfulSubrequirements();
			}
		}

		double successfulChecksPercent = 100 * numberOfSuccessfulChecks / Math.max(numberOfChecks, 0.001);
		double successfulTestCasesPercent = 100 * numberOfSuccessfulTestCases / Math.max(numberOfTestCases, 0.001);
		double successfulRequirementsPercent = 100 * numberOfSuccessfulRequirements / Math.max(numberOfRequirements, 0.001);
		logger.debug("Number of requirements: " + numberOfRequirements +", Successful requirements: " + numberOfSuccessfulRequirements);

		Element root = new Element(ELEMENT_ROOT_NAME);
		root.setAttribute("sut_name", Qualify.getOptionParameter("sut_name"));
		root.setAttribute("version", Qualify.getOptionParameter("sut_version"));
		root.setAttribute("date", Qualify.getOptionParameter("sut_date"));
		Document doc = new Document(root);

		root.setAttribute("successful_checks_percent", "" + (int) Math.floor(successfulChecksPercent));
		root.setAttribute("successful_test_cases_percent", "" + (int) Math.floor(successfulTestCasesPercent));
		root.setAttribute("successful_requirements_percent", "" + (int) Math.floor(successfulRequirementsPercent));

		// Attaching the XSL
		HashMap<String, String> piMap = new HashMap<String, String>(2);
		piMap.put( "type", "text/xsl" );
		piMap.put( "href", "style/release_note.xsl" );
		ProcessingInstruction pi = new ProcessingInstruction("xml-stylesheet", piMap);
		doc.getContent().add(0, pi);

		// Copying XSL, CSS, JS and PNG from jar's resources next to the new XML
		DocumentUtils.generateStyleFolder(outputFile.getParentFile());
		
		for(String errMessage : ErrorsAndWarnings.getErrors()) {
			Element errorElement = new Element("error");
			errorElement.setText(errMessage);
			root.addContent(errorElement);
		}

		for(String warnMessage : ErrorsAndWarnings.getWarnings()) {
			Element errorElement = new Element("warning");
			errorElement.setText(warnMessage);
			root.addContent(errorElement);
		}

		File outputFolder = outputFile.getParentFile();

		logger.debug("Exporting " + testHarness.getTestCases().size() + " test case(s)...");
		for(TestCase tc : testHarness.getTestCases()) {
			File testSourceFile = new File(outputFolder.getAbsolutePath() + "/" + tc.getLocalName() + "/" + tc.getLocalName() + ".xml");
			if(tc.getReport() != null) {
				tc.getReport().generateXML(testSourceFile);
			}
		}

		logger.debug("Exporting " + testHarness.getSrd().getRequirements().size() + " requirement(s)...");
		for(Requirement req : testHarness.getSrd().getRequirements()) {
			if(outputFolder == null) {
				outputFolder = new File("./");
			}
			root.addContent(Requirement.toDomElement(req, outputFolder.getAbsolutePath()));
		}

		String testCaseTableFileName = outputFile.getParent() + "/test_cases_table.xml";
		if(Qualify.isOption(Qualify.OPTION_TEST_CASE_TABLE_FILE_NAME)) {
			testCaseTableFileName = Qualify.getOptionValue(Qualify.OPTION_TEST_CASE_TABLE_FILE_NAME);
		}
		TestCasesTable.generateTestCasesTable(new File(testCaseTableFileName), testHarness.getTestCases());

		try {
			XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
			FileOutputStream fos = new FileOutputStream(outputFile);
			serializer.output(doc, fos);
			fos.close();
		}
		catch (IOException e) {
			System.err.println(e);
		}

		logger.info("Release note successfully exported: " + outputFile.getAbsolutePath());
	}

	public static List<String> getFailedTestCases(File releaseNote) {
		List<String> result = new ArrayList<String>();
		if(releaseNote.exists()) {
			if(releaseNote.isFile()) {
				SAXBuilder parser = new SAXBuilder();
				Document releaseNoteDocument = null;
				try {
					logger.debug("Parsing Release Note file '" + releaseNote.getAbsolutePath() + "' and looking for failed tests");
					releaseNoteDocument = parser.build(releaseNote);

					for(Object requirementTagObject : releaseNoteDocument.getRootElement().getChildren(Requirement.ELEMENT_NAME)) {
						Element requirementTag = (Element)requirementTagObject;
						if(requirementTag != null) {
							logger.debug("Parsing requirement '" + requirementTag.getAttributeValue("id"));
							result.addAll(getFailedTestCasesFromRequirementTag(requirementTag));
						}
					}
				} catch (JDOMException e) {
					e.printStackTrace();
					ErrorsAndWarnings.addError("Release Note reading Exception : " + e.getMessage());
				} catch (IOException e) {
					e.printStackTrace();
					ErrorsAndWarnings.addError("Release Note reading Exception : " + e.getMessage());
				}
			} else {
				ErrorsAndWarnings.addError("Release Note '" + releaseNote.getAbsolutePath() + "' is not a file.");
			}
		} else {
			ErrorsAndWarnings.addError("Release Note '" + releaseNote.getAbsolutePath() + "' does not exist.");
		}
		return TestToolStrings.cleanDoubles(result);
	}

	public static List<String> getTestCasesFromRequirement(File referenceReleaseNote, String requirement) {
		List<String> result = new ArrayList<String>();
		if(referenceReleaseNote.exists()) {
			if(referenceReleaseNote.isFile()) {
				SAXBuilder parser = new SAXBuilder();
				Document releaseNoteDocument = null;
				try {
					logger.debug("Parsing reference Release Note file '" + referenceReleaseNote.getAbsolutePath() + "'");
					releaseNoteDocument = parser.build(referenceReleaseNote);

					for(Object requirementTagObject : releaseNoteDocument.getRootElement().getChildren(Requirement.ELEMENT_NAME)) {
						Element requirementTag = (Element)requirementTagObject;
						if(requirementTag != null) {
							if(requirement.equals(requirementTag.getAttributeValue("id"))) {
								logger.debug("Parsing requirement '" + requirementTag.getAttributeValue("id"));
								result.addAll(getTestCasesFromRequirementTag(requirementTag));
							}
						}
					}
				} catch (JDOMException e) {
					e.printStackTrace();
					ErrorsAndWarnings.addError("reference Release Note reading Exception : " + e.getMessage());
				} catch (IOException e) {
					e.printStackTrace();
					ErrorsAndWarnings.addError("reference Release Note reading Exception : " + e.getMessage());
				}
			} else {
				ErrorsAndWarnings.addError("reference Release Note '" + referenceReleaseNote.getAbsolutePath() + "' is not a file.");
			}
		} else {
			ErrorsAndWarnings.addError("reference Release Note '" + referenceReleaseNote.getAbsolutePath() + "' does not exist.");
		}
		return TestToolStrings.cleanDoubles(result);
	}

	private static List<String> getTestCasesFromRequirementTag(Element requirementTag) {
		List<String> result = new ArrayList<String>();
		// Adding direct children
		for(Object testCaseElement : requirementTag.getChildren(TestCase.ELEMENT_NAME)) {
			Element testCaseTag = (Element)testCaseElement;
			result.add(testCaseTag.getAttributeValue("name").replaceAll("\\.java", "").replaceAll("\\.groovy", ""));
		}
		// Adding sub-requirements' children recursively
		for(Object requirementElement : requirementTag.getChildren(Requirement.ELEMENT_NAME)) {
			Element subRequirementTag = (Element)requirementElement;
			result.addAll(getTestCasesFromRequirementTag(subRequirementTag));
		}
		return result;
	}

	/**
	 * Lists the names of the failed test cases
	 * @param requirementTag
	 * @return
	 */
	private static List<String> getFailedTestCasesFromRequirementTag(Element requirementTag) {
		List<String> result = new LinkedList<String>();
		for(Object testCaseElement : requirementTag.getChildren(TestCase.ELEMENT_NAME)) {
			Element testCaseTag = (Element)testCaseElement;
			boolean isFailed = false;
			for(Object testResultObject : testCaseTag.getChildren(TestResult.TAG_NAME)) {
				Element testResultElement = (Element)testResultObject;
				String value = testResultElement.getAttributeValue(TestResult.RESULT_ATTRIBUTE_NAME).toString();
				if(TestResult.RESULT_NOK_VALUE.equals(value)) {
					isFailed = true;
					break;
				}
			}
			if(isFailed) {
				String testCaseName = testCaseTag.getAttributeValue(TestCase.ATTRIBUTE_NAME).toString();
				if(! result.contains(testCaseName)) {
					result.add(testCaseName);
				}
			}
		}
		for(Object subRequirementObject : requirementTag.getChildren(Requirement.ELEMENT_NAME)) {
			Element subRequirementTag = (Element)subRequirementObject;
			result.addAll(getFailedTestCasesFromRequirementTag(subRequirementTag));
		}
		return result;
	}

	/**
	 * Compares results of current tests campaign to the results from the referenced release note.
	 * If Regressions are detected, Errors are generated.
	 * @param referenceReleaseNote
	 * @return true if regressions are detected from reference's release note
	 */
	public static boolean computeRegressionsFromReference(TestHarness testHarness, File referenceReleaseNote) {
		boolean regressionsDetected = false;
		ReleaseNote reference = new ReleaseNote(referenceReleaseNote);
		logger.debug("Reference release note loaded");
		for(Requirement reqFromReference : reference.requirements) {
			Requirement reqFromCurrentCampaign = testHarness.getSrd().getRequirement(reqFromReference.getId());

			// checking that the requirement from reference file still exists
			if(reqFromCurrentCampaign == null) {
				ErrorsAndWarnings.addError("Regression found : Requirement '" + reqFromReference.getId() + "' not found.");
				regressionsDetected = true;
			} else
				// checking that the requirement's result is not going from OK to NOK
				if(reqFromReference.isSuccessful() && !reqFromCurrentCampaign.isSuccessful()) {
					ErrorsAndWarnings.addError("Regression found : Requirement '" + reqFromReference.getId() + "' is no more successful");
					regressionsDetected = true;
				}
			logger.debug("REQ: " + reqFromReference.getId() + " : " + reqFromReference.isSuccessful());
		}

		return regressionsDetected;
	}

	private Requirement getRequirement(String requirementId) {
		Requirement result = null;
		for(Requirement req : requirements) {
			if(req.getId().equals(requirementId)) {
				result = req;
			}
		}
		return result;
	}
}
