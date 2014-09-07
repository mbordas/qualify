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

import java.io.File;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.joda.time.DateTime;

import qualify.doc.DocString;
import qualify.doc.DomElementAble;
import qualify.tools.TestToolDates;

public class TestResult implements DomElementAble {

	static Logger logger = Logger.getLogger(TestResult.class);

	private boolean isSuccessful = false;
	private String requirementId = null;
	private DomElementAble comment = null;
	private String testSource = null;
	private int testSourceLine = 0;
	private TestCase testCase = null;
	private DateTime creationTime = null;

	public static final String TAG_NAME = "test_result",
	TIME_ATTRIBUTE_NAME = "time",
	REQUIREMENT_ID_ATTRIBUTE_NAME = "requirement",
	SOURCE_ATTRIBUTE_NAME = "source",
	SOURCE_LINE_ATTRIBUTE_NAME = "source_line",
	RESULT_ATTRIBUTE_NAME = "result",
	RESULT_OK_VALUE = "OK",
	RESULT_NOK_VALUE = "NOK",
	EMPTY_COMMENT_VALUE = "no comment available",
	EMPTY_SOURCE_LINE_VALUE = "0",
	EMPTY_SOURCE_VALUE = "no_source";

	public TestResult(boolean isSuccessful, String requirementId, String comment, String testSource, int testSourceLine, TestCase testCase) {
		this.isSuccessful = isSuccessful;
		this.requirementId = requirementId;
		if(comment != null) {
			this.comment = new DocString(comment);
		}
		this.testSource = testSource;
		this.testSourceLine = testSourceLine;
		this.testCase = testCase;
		setCreationTime(new DateTime(new Date()));
		if(requirementId == null) {
			this.requirementId = Requirement.EMPTY_REQUIREMENT_ID;
		}
		logger.debug("new TestResult: isSuccesfull=" + isSuccessful + ", requirement.getId=" + requirementId);
	}

	public TestResult(boolean isSuccessful, String requirementId, DomElementAble comment, String testSource, int testSourceLine, TestCase testCase) {
		this.isSuccessful = isSuccessful;
		this.requirementId = requirementId;
		this.comment = comment;
		this.testSource = testSource;
		this.testSourceLine = testSourceLine;
		this.testCase = testCase;
		setCreationTime(new DateTime(new Date()));
		if(requirementId == null) {
			this.requirementId = Requirement.EMPTY_REQUIREMENT_ID;
		}
		logger.debug("new TestResult: isSuccesfull=" + isSuccessful + ", requirement.getId=" + requirementId);
	}

	public void setCreationTime(DateTime t) {
		this.creationTime = t;
	}
	
	public DateTime getCreationTime() {
		return creationTime;
	}

	public boolean isSuccessful() {
		return isSuccessful;
	}

	public String getRequirementId() {
		return requirementId;
	}

	public DomElementAble getComment() {
		return comment;
	}

	public TestCase getTestCase() {
		return testCase;
	}

	public String getTestSource() {
		return testSource;
	}
	
	public void setSourceLineNumber(int number) {
		this.testSourceLine = number;
	}

	public int getTestSourceLine() {
		return testSourceLine;
	}

	public Element toDomElement() {
		Element element = new Element(TAG_NAME);
		if(requirementId != null) {
			element.setAttribute(REQUIREMENT_ID_ATTRIBUTE_NAME, requirementId);
		} else {
			element.setAttribute(REQUIREMENT_ID_ATTRIBUTE_NAME, Requirement.EMPTY_REQUIREMENT_ID);
		}
		if(comment != null) {
			element.addContent(comment.toDomElement());
		}
		if(isSuccessful()) {
			element.setAttribute(RESULT_ATTRIBUTE_NAME, RESULT_OK_VALUE);
		} else {
			element.setAttribute(RESULT_ATTRIBUTE_NAME, RESULT_NOK_VALUE);
		}
		if(testSource != null) {
			element.setAttribute(SOURCE_ATTRIBUTE_NAME, testSource);
		} else {
			element.setAttribute(SOURCE_ATTRIBUTE_NAME, EMPTY_SOURCE_VALUE);
		}

		element.setAttribute(TIME_ATTRIBUTE_NAME, "" + TestToolDates.toString(this.creationTime, TestToolDates.DEFAULT_TIMESTAMP_FORMAT));
		element.setAttribute(SOURCE_LINE_ATTRIBUTE_NAME, "" + testSourceLine);

		return element;
	}

	public static String resultStringValue(boolean isSuccessful) {
		if(isSuccessful) {
			return RESULT_OK_VALUE;
		} else {
			return RESULT_NOK_VALUE;
		}
	}
	
	public static TestResult createTestResultFromDomElement(Element testResultTag) {
		TestResult result = null;
		boolean success = testResultTag.getAttributeValue("result").equals("OK");
		result = new TestResult(
				success,
				"",
				"",
				testResultTag.getAttributeValue("test_source"),
				Integer.valueOf(testResultTag.getAttributeValue("test_source_line")).intValue(),
				null);
		return result;
	}

	public String toStringForConsole() {
		return null;
	}

	public Map<File, String> getAttachedFiles() {
		return null;
	}

}
