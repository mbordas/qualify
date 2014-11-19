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

package qualify.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import qualify.TestCase;
import qualify.doc.DocList;
import qualify.doc.DocString;

public class TestToolCSV {

	private Logger logger = Logger.getLogger(TestToolCSV.class);

	private TestCase testCase = null;

	private String separator = ",";

	public TestToolCSV(TestCase tc) {
		testCase = tc;
	}

	public TestToolCSV(TestCase tc, String separator) {
		testCase = tc;
		this.separator = separator;
	}

	/**
	 * Returns the complete line found from a column number and a key value.
	 * 
	 * @param inputFile
	 *            The file to parse
	 * @param keyColumnIndex
	 *            The index of the column where the key searchedValue has to be found. Starts with 1.
	 * @param searchedValue
	 *            The key value used to find the line
	 * @return the first line from inputFile where searchedValue is found in columnIndex (starting with '1').
	 * @throws IOException
	 */
	public String getCSVLine(File inputFile, int keyColumnIndex, String searchedValue) throws IOException {
		InputStream ips = new FileInputStream(inputFile);
		InputStreamReader ipsr = new InputStreamReader(ips);
		BufferedReader br = new BufferedReader(ipsr);
		String line;
		String result = null;
		while((line = br.readLine()) != null) {
			if(line.indexOf(separator) != -1) {
				int columnCount = line.split(separator).length;
				if(keyColumnIndex - 1 < columnCount) {
					if(line.split(separator)[keyColumnIndex - 1].equals(searchedValue)) {
						result = line;
					}
				}
			}
		}
		br.close();
		ipsr.close();
		ips.close();
		return result;
	}

	public String getCSVLine(String content, int keyColumnIndex, String searchedValue) {
		String result = null;
		for(String line : TestToolStrings.toLines(content)) {
			if(line.indexOf(separator) != -1) {
				int columnCount = line.split(separator).length;
				if(keyColumnIndex - 1 < columnCount) {
					if(line.split(separator)[keyColumnIndex - 1].equals(searchedValue)) {
						result = line;
					}
				}
			}
		}
		return result;
	}

	/**
	 * Returns the line's number found from a column number and a key value.
	 * 
	 * @param inputFile
	 *            The file to parse
	 * @param keyColumnIndex
	 *            The index of the column where the key searchedValue has to be found. Starts with 1.
	 * @param searchedValue
	 *            The key value used to find the line
	 * @return the first line's number from inputFile where searchedValue is found in columnIndex (starting with '1').
	 * @throws IOException
	 */
	public int getCSVLineNumber(File inputFile, int keyColumnIndex, String searchedValue) throws IOException {
		int result = 0;
		InputStream ips = new FileInputStream(inputFile);
		InputStreamReader ipsr = new InputStreamReader(ips);
		BufferedReader br = new BufferedReader(ipsr);
		String ligne;
		int lineNumber = 0;
		while((ligne = br.readLine()) != null) {
			lineNumber++;
			if(ligne.indexOf(separator) != -1) {
				int columnCount = ligne.split(separator).length;
				if(keyColumnIndex - 1 < columnCount) {
					if(ligne.split(separator)[keyColumnIndex - 1].equals(searchedValue)) {
						result = lineNumber;
					}
				}
			}
		}
		br.close();
		ipsr.close();
		ips.close();
		return result;
	}

	/**
	 * @param inputFile
	 *            The file to parse
	 * @param keyColumnIndex
	 *            The index of the column where the key searchedValue has to be found. Starts with 1.
	 * @param searchedValue
	 *            The key value used to find the line
	 * @param columnIndex
	 *            The index of the row from which the returned value is read (starting with '1')
	 * @return the string value of the indexRow from the first line found in inputFile where searchedValue is found in columnIndex
	 * @throws IOException
	 */
	public String getCSVCell(File inputFile, int keyColumnIndex, String searchedValue, int columnIndex) throws IOException {
		return getCSVCell(TestToolFile.getContentAsString(inputFile), keyColumnIndex, searchedValue, columnIndex);
	}

	public String getCSVCell(String content, int keyColumnIndex, String searchedValue, int columnIndex) {
		String line = getCSVLine(content, keyColumnIndex, searchedValue);
		if(line == null) {
			logger.debug("Cell not found: line not found");
			return null;
		} else {
			if(line.indexOf(separator) != -1) {
				int columnCount = line.split(";").length;
				if((keyColumnIndex - 1 < columnCount) && (columnIndex - 1 < columnCount)) {
					if(line.split(separator)[keyColumnIndex - 1].equals(searchedValue)) {
						return line.split(separator)[columnIndex - 1];
					} else {
						logger.debug("Cell not found: searched value not found");
						return null;
					}
				} else {
					logger.debug("Cell not found: keyvalue value not found");
					return null;
				}
			} else {
				logger.debug("Cell not found: not enough separated values");
				return null;
			}
		}
	}

	/**
	 * Checks the value of one CSV cell.
	 * 
	 * @param inputFile
	 *            The file to parse
	 * @param keyColumnIndex
	 *            The index of the column where the key searchedValue has to be found. Starts with 1.
	 * @param searchedValue
	 *            The key value used to find the line
	 * @param columnIndex
	 *            The index of the row from which the returned value is read (starting with '1')
	 * @param expectedValue
	 *            The expected value
	 * @throws IOException
	 */
	public void checkCSVCell(File inputFile, int keyColumnIndex, String searchedValue, int columnIndex, String expectedValue) {
		try {
			checkCSVCell(TestToolFile.getContentAsString(inputFile), keyColumnIndex, searchedValue, columnIndex, expectedValue);
		} catch(IOException e) {
			e.printStackTrace();
			String comment = "inputFile: " + inputFile.getAbsolutePath() + ", keyColumnIndex: " + keyColumnIndex + ", searchedValue: "
					+ searchedValue + ", columnIndex: " + columnIndex + ", expected value: '" + expectedValue + "'"
					+ ", Unexpected Exception: " + e.getMessage();
			testCase.addTestResult(false, comment);
		}
	}

	public void checkCSVCell(String content, int keyColumnIndex, String searchedValue, int columnIndex, String expectedValue) {
		String foundCell;

		foundCell = getCSVCell(content, keyColumnIndex, searchedValue, columnIndex);

		if(foundCell == null) {
			String comment = "CSV cell not found: " + "keyColumnIndex: " + keyColumnIndex + ", searchedValue: " + searchedValue
					+ ", columnIndex: " + columnIndex + ", expected value: '" + expectedValue + "'";
			testCase.addTestResult(false, comment);

		} else {
			String testedValue = getCSVCell(content, keyColumnIndex, searchedValue, columnIndex);
			String searchDetails = "keyColumnIndex: " + keyColumnIndex + ", searchedValue: " + searchedValue + ", columnIndex: "
					+ columnIndex;
			DocList comment = new DocList();
			comment.addItem(new DocString(searchDetails));
			comment.addItem(TestToolStrings.getDocListComparison(expectedValue, testedValue));
			if(testedValue.equals(expectedValue)) {
				testCase.addTestResult(true, comment);
			} else {
				testCase.addTestResult(false, comment);
			}
		}

	}

}
