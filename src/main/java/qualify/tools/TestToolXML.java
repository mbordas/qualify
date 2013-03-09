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

package qualify.tools;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import qualify.TestCase;

/**
 * Provides tools to easily browse XML files using xPath.
 * Before browsing a file, you must parse it with: parseFile(String filePath, String fileKey)
 * That way, the whole file is kept into memory, so that the calls of further browsing methods
 * go faster.
 *
 */
public class TestToolXML {

	private TestCase testCase = null;

	private HashMap<String, Document> documents = new HashMap<String, Document>();
	private Document lastDocument = null;

	private XPathFactory factory = XPathFactory.newInstance();

	public TestToolXML(TestCase tc) {
		testCase = tc;
	}

	public boolean parseFile(String filePath, String fileKey) {
		return parseFile(new File(filePath), fileKey);
	}

	public boolean parseFile(File xmlFile, String fileKey) {
		boolean result = false;
		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(true);
			DocumentBuilder builder;
			builder = domFactory.newDocumentBuilder();
			Document document = builder.parse(xmlFile);

			if(fileKey != null) {
				documents.put(fileKey, document);
			}
			lastDocument = document;

			result = true;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public boolean parseFile(File xmlFile) {
		return parseFile(xmlFile, null);
	}

	public void parseString(String content, String key) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder;
		builder = domFactory.newDocumentBuilder();

		Document document = builder.parse(new InputSource(new StringReader(content)));

		if(key != null) {
			documents.put(key, document);
		}
		lastDocument = document;
	}

	public void parseString(String content) throws ParserConfigurationException, SAXException, IOException {
		parseString(content, null);
	}

	public void closeFile(String fileKey) {
		documents.remove(fileKey);
	}
	
	public String getAttributeValueAsString(String xPath) {
		return getAttributeValueAsString(null, xPath);
	}

	public String getAttributeValueAsString(String key, String xPath) {
		String result = null;
		Document document = getDocument(key);
		if(document != null) {
			try {
				XPath xpath = factory.newXPath();
				XPathExpression expr = xpath.compile(xPath);
				result = (String) expr.evaluate(document, XPathConstants.STRING);
			} catch (XPathExpressionException e) {
				e.printStackTrace();
				testCase.addTestResult(
						false,
						"An Exception occured during getAttributeValue('" + key + "', '" + xPath + "')",
						TestToolXML.class);
			}
		}
		return result;
	}
	
	public Node getNode(String xPath) {
		return getNode(null, xPath);
	}

	public Node getNode(String fileKey, String xPath) {
		Node result = null;

		NodeList nodes = (NodeList) getNodeList(fileKey, xPath);
		if(nodes != null) {
			if(nodes.getLength() >= 1) {
				result = nodes.item(0);
			}
			for (int i = 0; i < nodes.getLength(); i++) {
				System.out.println(nodes.item(i).getNodeValue()); 
			}
		}

		return result;
	}
	
	public NodeList getNodeList(String xPath) {
		return getNodeList(null, xPath);
	}

	public NodeList getNodeList(String fileKey, String xPath) {
		NodeList result = null;

		Document document = getDocument(fileKey);
		if(document != null) {
			try {
				XPath xpath = factory.newXPath();
				XPathExpression expr = xpath.compile(xPath);
				result = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

			} catch (XPathExpressionException e) {
				e.printStackTrace();
				testCase.addTestResult(
						false,
						"An Exception occured during getNodeList('" + fileKey + "', '" + xPath + "')",
						TestToolXML.class);
			}
		}

		return result;
	}

	private Document getDocument(String fileKey) {
		Document result = lastDocument;
		if(fileKey != null) {
			result = documents.get(fileKey);
		}
		if(result == null) {
			testCase.addTestResult(
					false,
					"No document available for fileKey='" + fileKey + "'",
					TestToolXML.class);
		}
		return result;
	}
	
	public boolean checkNodeExists(String xPath) {
		return checkNodeExists(null, xPath);
	}

	public boolean checkNodeExists(String fileKey, String xPath) {
		boolean result = getNode(fileKey, xPath) != null;
		testCase.addTestResult(result, "xpath=" + xPath, TestToolXML.class);
		return result;
	}
	
	public boolean checkNodeDoesntExists(String xPath) {
		return checkNodeDoesntExists(null, xPath);
	}

	public boolean checkNodeDoesntExists(String fileKey, String xPath) {
		boolean result = getNode(fileKey, xPath) == null;
		testCase.addTestResult(result, "xpath=" + xPath, TestToolXML.class);
		return result;
	}
	
	public boolean checkTagCount(String xPath, int expectedCount) {
		return checkTagCount(null, xPath, expectedCount);
	}

	public boolean checkTagCount(String fileKey, String xPath, int expectedCount) {
		int testedCount = 0;
		NodeList nodes = getNodeList(fileKey, xPath);
		if(nodes != null) {
			testedCount = nodes.getLength();
		}

		boolean result = expectedCount == testedCount;
		testCase.addTestResult(result,
				"expected=" + expectedCount + " | tested=" + testedCount,
				TestToolXML.class);

		return result;
	}

}
