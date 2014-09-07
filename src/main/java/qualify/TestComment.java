/*Copyright (c) 2011, Mathieu Bordas
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

public class TestComment implements DomElementAble {

	static Logger logger = Logger.getLogger(TestComment.class);

	private String testSource = null;
	private int testSourceLine = 0;
	private TestCase testCase = null;
	private DateTime creationTime = null;

	public static final String TAG_NAME = "comment",
	TIME_ATTRIBUTE_NAME = "time",
	TEXT_ATTRIBUTE_NAME = "text",
	SOURCE_ATTRIBUTE_NAME = "source",
	SOURCE_LINE_ATTRIBUTE_NAME = "source_line",
	EMPTY_SOURCE_LINE_VALUE = "0",
	EMPTY_SOURCE_VALUE = "no_source";

	private DomElementAble content = null;

	public TestComment(String text, String testSource, int testSourceLine, TestCase testCase) {
		text = text.replace('\u0000',' ');
		text = text.replace('\u0001',' ');
		text = text.replace('\u0002',' ');
		text = text.replace('\u0003',' ');
		text = text.replace('\u0004',' ');
		text = text.replace('\u0005',' ');
		text = text.replace('\u0013',' ');
		text = text.replace('\u0014',' ');
		text = text.replace('\u0015',' ');
		text = text.replace('\u0016',' ');
		text = text.replace('\u001c',' ');
		text = text.replace('\u001b',' ');
		content = new DocString(text);
		this.testSource = testSource;
		this.testSourceLine = testSourceLine;
		this.testCase = testCase;
		setCreationTime(new DateTime(new Date()));
	}

	public TestComment(DomElementAble content, String testSource, int testSourceLine, TestCase testCase) {
		this.content = content;
		this.testSource = testSource;
		this.testSourceLine = testSourceLine;
		this.testCase = testCase;
		setCreationTime(new DateTime(new Date()));
	}

	public void setCreationTime(DateTime t) {
		this.creationTime = t;
	}

	public DateTime getCreationTime() {
		return creationTime;
	}

	public void setSourceLineNumber(int number) {
		this.testSourceLine = number;
	}

	public TestCase getTestCase() {
		return testCase;
	}

	public String getTestSource() {
		return testSource;
	}

	public int getTestSourceLine() {
		return testSourceLine;
	}

	public int getLineNumber() {
		return testSourceLine;
	}

	public Element toDomElement() {
		Element element = new Element(TAG_NAME);

		if(testSource != null) {
			element.setAttribute(SOURCE_ATTRIBUTE_NAME, testSource);
		} else {
			element.setAttribute(SOURCE_ATTRIBUTE_NAME, EMPTY_SOURCE_VALUE);
		}

		element.setAttribute(SOURCE_LINE_ATTRIBUTE_NAME, "" + testSourceLine);
		element.setAttribute(TIME_ATTRIBUTE_NAME, "" + TestToolDates.toString(this.creationTime, TestToolDates.DEFAULT_TIMESTAMP_FORMAT));
		if(content != null) {
			element.addContent(content.toDomElement());
		}

		return element;
	}

	public String toStringForConsole() {
		return content.toStringForConsole();
	}

	public Map<File, String> getAttachedFiles() {
		return null;
	}

}
