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

package qualify;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.jdom.Element;

public class Requirement {

	static Logger logger = Logger.getLogger(Requirement.class);

	private String id = "";
	private String description = "";
	private LinkedList<Requirement> subRequirements = null;
	private Requirement parent = null;
	private int localIndex = 0;

	public int getLocalIndex() {
		return localIndex;
	}

	public void setLocalIndex(int localIndex) {
		this.localIndex = localIndex;
	}

	public static final String EMPTY_REQUIREMENT_ID = "EMPTY_REQUIREMENT", ELEMENT_NAME = "requirement", ATTRIBUTE_ID = "id";

	private Vector<TestResult> results = null;

	public Requirement(String id) {
		this.id = id;
		results = new Vector<TestResult>();
		subRequirements = new LinkedList<Requirement>();
	}

	private void setParent(Requirement requirement) {
		parent = requirement;
	}

	private Requirement getParent() {
		return parent;
	}

	public void addSubRequirement(Requirement req) {
		if(req.getParent() == null) {
			subRequirements.add(req);
			req.setParent(this);
			req.setLocalIndex(subRequirements.size());
		} else {
			ErrorsAndWarnings.addError("Requirement with id='" + req.getId() + "' cannot be added as sub requirement of '" + this.getId()
					+ "' CAUSE: it is already added as sub requirement of '" + req.getParent().getId() + "'");
		}
	}

	public Requirement getSubRequirement(String id) {
		Requirement result = null;
		for(Requirement subRequirement : getSubRequirements()) {
			if(subRequirement.getId().equals(id)) {
				result = subRequirement;
				break;
			} else {
				Requirement subsubRequirement = subRequirement.getSubRequirement(id);
				if(subsubRequirement != null) {
					result = subsubRequirement;
					break;
				}
			}
		}
		return result;
	}

	public LinkedList<Requirement> getSubRequirements() {
		return subRequirements;
	}

	public void addTestResult(TestResult testResult) {
		results.add(testResult);
	}

	public Vector<TestResult> getTestResults() {
		return results;
	}

	public boolean isSuccessful() {
		if(results.size() < 1) {
			if(getSubRequirements().size() == 0) {
				return false;
			} else {
				boolean subRequirementsAreSuccessful = true;
				for(Requirement subReq : getSubRequirements()) {
					subRequirementsAreSuccessful = subRequirementsAreSuccessful && subReq.isSuccessful();
				}
				return subRequirementsAreSuccessful;
			}
		} else {
			boolean isSuccessful = true;
			for(TestResult result : results) {
				if(!result.isSuccessful()) {
					isSuccessful = false;
				}
			}
			for(Requirement subReq : getSubRequirements()) {
				isSuccessful = isSuccessful && subReq.isSuccessful();
			}
			return isSuccessful;
		}
	}

	public int getOKs() {
		int numberOfOKs = 0;
		for(TestResult result : results) {
			if(result.isSuccessful()) {
				numberOfOKs++;
			}
		}
		for(Requirement subReq : getSubRequirements()) {
			numberOfOKs = numberOfOKs + subReq.getOKs();
		}
		return numberOfOKs;
	}

	public int getNOKs() {
		int numberOfNOKs = 0;
		for(TestResult result : results) {
			if(!result.isSuccessful()) {
				numberOfNOKs++;
			}
		}
		for(Requirement subReq : getSubRequirements()) {
			numberOfNOKs = numberOfNOKs + subReq.getNOKs();
		}
		return numberOfNOKs;
	}

	public boolean same(Requirement requirement) {
		if(getId().equals(requirement.getId())) {
			return true;
		} else {
			return false;
		}
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public static Requirement createRequirementFromDomElement(Element requirementTag) {
		String id = requirementTag.getAttributeValue(ATTRIBUTE_ID);
		Requirement result = null;
		if(id != null) {
			result = new Requirement(id);
			result.description = requirementTag.getText();

			for(Object testCaseTagObject : requirementTag.getChildren(TestCase.ELEMENT_NAME)) {
				Element testCaseTag = (Element) testCaseTagObject;
				TestCase tc = TestCase.createTestCaseFromDomElement(testCaseTag);
				for(TestResult tr : tc.getResults()) {
					result.addTestResult(tr);
				}
				logger.debug("TestCase created from DOM: " + tc.getName());
			}

			for(Object requirementTagObject : requirementTag.getChildren("requirement")) {
				Element subRequirementTag = (Element) requirementTagObject;
				result.addSubRequirement(createRequirementFromDomElement(subRequirementTag));
			}
		} else {
			ErrorsAndWarnings.addError("Unexpected requirement without 'id' attribute");
		}

		return result;
	}

	public String getPathIndex() {
		String result = "";
		Requirement currentRequirement = getParent();
		while(currentRequirement != null) {
			result = "" + currentRequirement.getLocalIndex() + result;
			if(currentRequirement.getParent() != null) {
				result = "-" + result;
			}
			currentRequirement = currentRequirement.getParent();
		}
		return result;
	}

	public static Element toDomElement(Requirement requirement, String mainOutputFolder) {
		logger.debug("Exporting requirement '" + requirement.getId() + "'...");
		Element requirementElement = new Element("requirement");
		requirementElement.setAttribute(ATTRIBUTE_ID, requirement.getId());
		requirementElement.setAttribute("result", TestResult.resultStringValue(requirement.isSuccessful()));
		requirementElement.setAttribute("nbOKs", "" + requirement.getOKs());
		requirementElement.setAttribute("nbNOKs", "" + requirement.getNOKs());
		requirementElement.setAttribute("path_index", requirement.getPathIndex());
		requirementElement.setAttribute("local_index", "" + requirement.getLocalIndex());

		logger.debug("Exporting " + requirement.getTestResults().size() + " test results...");
		// grouping test results by test cases
		HashMap<String, Vector<TestResult>> testResults = new HashMap<String, Vector<TestResult>>();
		for(TestResult testResult : requirement.getTestResults()) {
			String testCaseName = testResult.getTestSource();
			if(testResults.get(testCaseName) == null) {
				Vector<TestResult> v = new Vector<TestResult>();
				v.add(testResult);
				testResults.put(testCaseName, v);
			} else {
				testResults.get(testCaseName).add(testResult);
			}
		}

		for(String testSource : testResults.keySet()) {

			Element testCaseElement = new Element(TestCase.ELEMENT_NAME);
			testCaseElement.setAttribute("name", testSource);

			for(TestResult testResult : testResults.get(testSource)) {
				if(testResult.getTestSource() != null) {
					Element testResultElement = new Element("test_result");
					testCaseElement.setAttribute("name", testResult.getTestCase().getLocalName());
					testResultElement.setAttribute("result", TestResult.resultStringValue(testResult.isSuccessful()));
					if(testResult.getTestCase().getReport() != null) {
						String testName = testResult.getTestCase().getLocalName();
						testCaseElement.setAttribute("test_source", testName + "/" + testName + ".xml");
						testResultElement.setAttribute("test_source", testName + "/" + testName + ".xml");
					} else {
						logger.debug("test source unavailable for test case '" + testResult.getTestCase().getName() + "'");
					}
					testResultElement.setAttribute("test_case_name", testResult.getTestCase().getLocalName());
					testResultElement.setAttribute("test_source_line", "" + testResult.getTestSourceLine());
					testCaseElement.addContent(testResultElement);
				}
			}

			requirementElement.addContent(testCaseElement);
		}

		for(Requirement subRequirement : requirement.getSubRequirements()) {
			requirementElement.addContent(toDomElement(subRequirement, mainOutputFolder));

		}

		return requirementElement;
	}

	public int getAllSubrequirementsCount() {
		int result = 0;
		for(Requirement subreq : getSubRequirements()) {
			result++;
			result += subreq.getAllSubrequirementsCount();
		}
		return result;
	}

	public int getSuccessfulSubrequirements() {
		int result = 0;
		for(Requirement subreq : getSubRequirements()) {
			if(subreq.isSuccessful()) {
				result++;
			}
			result += subreq.getSuccessfulSubrequirements();
		}
		return result;
	}

}
