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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.ProcessingInstruction;
import org.jdom.output.XMLOutputter;

import qualify.ErrorsAndWarnings;
import qualify.TestComment;
import qualify.TestResult;
import qualify.doc.Attachment.Type;
import qualify.tools.StackTraceTool;
import qualify.tools.TestToolDates;

/**
 * TestSource is used for registering check results of one test case (written in Java). It can be exported as XML.
 */
public class TestReport {

	static Logger logger = Logger.getLogger(TestReport.class);

	String absolutePath = null;

	public String getAbsolutePath() {
		return absolutePath;
	}

	private File outputFile = null;

	private String testName = null;

	/**
	 * Here the steps's separators are registered, indexed by line number
	 */
	private HashMap<Integer, String> steps = null;

	private LinkedList<TestComment> comments = null;

	private HashMap<Integer, TestResult> results = null;

	private HashMap<Throwable, String> exceptions = null;

	private LinkedList<Attachment> attachments = null;

	private TestSource source = null;

	public static TestReport createTestReport(String relativeFileName) {
		return createTestReport(new File(relativeFileName));
	}

	public static TestReport createTestReport(File originalFile) {
		if(originalFile.exists()) {
			if(originalFile.isFile()) {
				return new TestReport(originalFile.getAbsolutePath(), originalFile);
			} else {
				logger.info("test source is not a file");
				return null;
			}
		} else {
			logger.info("test source does not exist: " + originalFile.getAbsolutePath());
			return null;
		}
	}

	public static TestReport createTestReportWithoutFile(String testName) {
		return new TestReport(testName);
	}

	public TestReport(String absolutePath, File file) {
		this.absolutePath = absolutePath;
		testName = file.getName();
		results = new HashMap<Integer, TestResult>();
		comments = new LinkedList<TestComment>();
		attachments = new LinkedList<Attachment>();
		exceptions = new HashMap<Throwable, String>();
		steps = new HashMap<Integer, String>();
		source = new TestSource(file);
	}

	public TestReport(String testName) {
		this.absolutePath = null;
		this.testName = testName;
		results = new HashMap<Integer, TestResult>();
		comments = new LinkedList<TestComment>();
		attachments = new LinkedList<Attachment>();
		exceptions = new HashMap<Throwable, String>();
		steps = new HashMap<Integer, String>();
		source = null;
	}

	public TestReport(TestSource testSource) {
		this.absolutePath = null;
		this.testName = testSource.getOriginalFile().getName();
		results = new HashMap<Integer, TestResult>();
		comments = new LinkedList<TestComment>();
		attachments = new LinkedList<Attachment>();
		exceptions = new HashMap<Throwable, String>();
		steps = new HashMap<Integer, String>();
		source = testSource;
	}

	public TestSource getSource() {
		return source;
	}

	public String getTestName() {
		return testName;
	}

	public File getOutputFile() {
		return this.outputFile;
	}

	public void addTestComment(TestComment tc) {
		if(tc != null) {
			if(source != null) {
				tc.setSourceLineNumber(source.getMappedLineNumber(tc.getLineNumber()));
			}
			comments.add(tc);
		} else {
			ErrorsAndWarnings.addWarning("cannot add null comment");
		}
	}

	public void addStep(int lineNumber, String title) {
		int mappedLineNumber = source.getMappedLineNumber(lineNumber);
		steps.put(mappedLineNumber, title);
	}

	private LinkedList<TestComment> getComments(int lineNumber) {
		LinkedList<TestComment> commentList = new LinkedList<TestComment>();
		for(TestComment comment : comments) {
			if(comment.getLineNumber() == lineNumber) {
				commentList.add(comment);
			}
		}
		return commentList;
	}

	public void addTestResult(TestResult tr) {
		if(source != null) {
			tr.setSourceLineNumber(source.getMappedLineNumber(tr.getTestSourceLine()));
		}
		if(results.get(tr.getTestSourceLine()) == null) {
			results.put(tr.getTestSourceLine(), tr);
		} else {
			ErrorsAndWarnings.addWarning("multiple test checks at same line");
			if(!tr.isSuccessful()) {
				results.put(tr.getTestSourceLine(), tr);
			}
		}
	}

	public void attachFile(File file, Type type, String fileName, int lineNumber) {
		if(source != null) {
			lineNumber = source.getMappedLineNumber(lineNumber);
		}
		Attachment attachment = new Attachment(file, fileName, lineNumber, type);
		logger.info("attaching: line " + lineNumber + ", " + fileName);
		if(attachment != null) {
			attachments.add(attachment);
		}
	}

	public void attachFile(File f, Type type, Class<?> calledClass) {
		int lineNumber = StackTraceTool.getTestCaseCall().getLineNumber();
		String fileName = StackTraceTool.getTestCaseCall().getFileName();
		attachFile(f, type, fileName, lineNumber);
	}

	public void attachFile(File f, Type type, String fileName, Class<?> calledClass) {
		int lineNumber = StackTraceTool.getTestCaseCall().getLineNumber();
		attachFile(f, type, fileName, lineNumber);
	}

	private LinkedList<Attachment> getFiles(int lineNumber) {
		LinkedList<Attachment> files = new LinkedList<Attachment>();
		for(Attachment attachment : attachments) {
			if(attachment.getLineNumber() == lineNumber) {
				files.add(attachment);
			}
		}
		return files;
	}

	public void generateXML(File outputFile) throws IOException {

		this.outputFile = outputFile;
		// Initializing the JDOM document
		Element root = new Element("source");
		Document doc = new Document(root);

		root.setAttribute("test_case_file", getTestName());
		if((getNumberOfOKs() == 0) || (getNumberOfNOKs() > 0)) {
			root.setAttribute("test_case_result", "NOK");
		} else {
			root.setAttribute("test_case_result", "OK");
		}
		root.setAttribute("number_of_oks", "" + getNumberOfOKs());
		root.setAttribute("number_of_noks", "" + getNumberOfNOKs());

		// Attaching the XSL
		HashMap<String, String> piMap = new HashMap<String, String>(2);
		piMap.put("type", "text/xsl");
		piMap.put("href", "../style/source.xsl");
		ProcessingInstruction pi = new ProcessingInstruction("xml-stylesheet", piMap);
		doc.getContent().add(0, pi);

		// Attaching the exceptions if raised
		for(Throwable e : exceptions.keySet()) {
			Element exceptionElement = new Element("exception");
			exceptionElement.setText(e.getMessage());
			exceptionElement.setAttribute("label", exceptions.get(e));
			root.addContent(exceptionElement);
		}

		if(source != null) {
			// ** The source code is available
			InputStream ips = new FileInputStream(source.getOriginalFile());
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String line;
			int lineNumber = 1;
			Element stepElement = new Element("step");
			if(steps.size() > 0) {
				stepElement.setAttribute("title", "start");
			} else {
				stepElement.setAttribute("title", "main");
			}
			while((line = br.readLine()) != null) {
				if(steps.containsKey(lineNumber)) {
					root.addContent(stepElement);
					stepElement = new Element("step");
					stepElement.setAttribute("title", steps.get(lineNumber));
				}

				Element lineElement = new Element("source_line");
				Element codeElement = new Element("code");
				codeElement.setText(line);
				codeElement.setAttribute("padding-left", "padding-left:" + 30 * getBeginningTabsCount(line) + "px;");
				lineElement.addContent(codeElement);
				lineElement.setAttribute("line_number", "" + lineNumber);
				reportResultCommentAndAttachment(lineElement, lineNumber);

				stepElement.addContent(lineElement);

				lineNumber++;
			}
			root.addContent(stepElement);
			br.close();
		} else {
			// ** The source code is not available

			// Computing the number of lines
			int reportLinesCount = 0;
			for(TestComment comment : comments) {
				reportLinesCount = Math.max(reportLinesCount, comment.getLineNumber());
			}
			for(Attachment attachment : attachments) {
				reportLinesCount = Math.max(reportLinesCount, attachment.getLineNumber());
			}
			for(int lineNumber : results.keySet()) {
				reportLinesCount = Math.max(reportLinesCount, lineNumber);
			}

			Element stepElement = new Element("step");
			if(steps.size() > 0) {
				stepElement.setAttribute("title", "start");
			} else {
				stepElement.setAttribute("title", "main");
			}
			// For 1 to number of lines, we export result, comment and attachment if exist
			// If no result, comment or attachment, the line is not exported
			for(int lineNumber = 1; lineNumber <= reportLinesCount; lineNumber++) {
				if(steps.containsKey(lineNumber)) {
					root.addContent(stepElement);
					stepElement = new Element("step");
					stepElement.setAttribute("title", steps.get(lineNumber));
				}

				Element lineElement = new Element("source_line");

				lineElement.setAttribute("line_number", "" + lineNumber);
				lineElement.setAttribute("padding-left", "padding-left:0px;");

				if(reportResultCommentAndAttachment(lineElement, lineNumber)) {
					stepElement.addContent(lineElement);
				}
			}
			root.addContent(stepElement);
		}

		// Copying the attachments next to the XML
		File parentDir = outputFile.getParentFile();
		Attachment.copyAttachedFiles(root, parentDir);

		// Writing the source XML output file
		XMLOutputter serializer = new XMLOutputter(); // Format.getPrettyFormat()
		outputFile.getParentFile().mkdirs();
		FileOutputStream os = new FileOutputStream(outputFile);
		serializer.output(doc, os);
		os.close();
	}

	/**
	 * Updates the Dom Element representing the line with available results, comments and attachments
	 * 
	 * @param lineElement
	 *            the Element to update.
	 * @param lineNumber
	 *            The line number. Used to find related results, comments and attachments.
	 * @return True if at least one result, comment or attachment is found. False elsewhere
	 */
	private boolean reportResultCommentAndAttachment(Element lineElement, int lineNumber) {
		boolean hasResultCommentOrAttachment = false;
		TestResult tr = results.get(lineNumber);
		if(tr != null) {
			hasResultCommentOrAttachment = true;
			lineElement.addContent(tr.toDomElement());
			if(tr.isSuccessful()) {
				lineElement.setAttribute("type", "result_ok");
			} else {
				lineElement.setAttribute("type", "result_nok");
			}
			if(tr.getRequirementId() != null) {
				lineElement.setAttribute("requirement", tr.getRequirementId());
			}
			lineElement.setAttribute("time", TestToolDates.toString(tr.getCreationTime(), TestToolDates.DEFAULT_TIMESTAMP_FORMAT));
		}

		for(TestComment comment : getComments(lineNumber)) {
			lineElement.addContent(comment.toDomElement());
			hasResultCommentOrAttachment = true;
		}

		for(Attachment attachment : getFiles(lineNumber)) {
			lineElement.addContent(attachment.toDomElement());
			hasResultCommentOrAttachment = true;
		}
		return hasResultCommentOrAttachment;
	}

	public static int getBeginningTabsCount(String line) {
		Pattern p = Pattern.compile("^(\\t*).*");
		Matcher m = p.matcher(line);
		m.find();
		return m.group(1).length();
	}

	public int getNumberOfOKs() {
		int result = 0;
		for(TestResult tr : this.results.values()) {
			if(tr.isSuccessful()) {
				result++;
			}
		}
		return result;
	}

	public int getNumberOfNOKs() {
		int result = 0;
		for(TestResult tr : this.results.values()) {
			if(!tr.isSuccessful()) {
				result++;
			}
		}
		return result;
	}

	public void addException(String label, Throwable e) {
		exceptions.put(e, label);
	}

}
