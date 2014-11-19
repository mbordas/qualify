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

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import qualify.TestCase;
import qualify.doc.DocList;
import qualify.doc.DocString;
import qualify.doc.DomElementAble;
import qualify.doc.Field;
import qualify.doc.Span;

/**
 * TestToolStrings provides tools for checking String objects.
 */
public class TestToolStrings {

	private TestCase m_testCase = null;

	public TestToolStrings(TestCase tc) {
		m_testCase = tc;
	}

	/**
	 * Checks that the two strings given through parameters are equal. Returns true if both are null. Use checkEquality(expectedString,
	 * testedString, false) if you want to check equality without case sensitive.
	 * 
	 * @param expectedString
	 * @param testedString
	 */
	public boolean checkEquality(String expectedString, String testedString) {
		return checkEquality(expectedString, testedString, true);
	}

	/**
	 * Returns true if both strings are equal. Set caseSensitive to 'false' in order to ignore characters' case for comparison.
	 * 
	 * @param expectedString
	 *            The expected value.
	 * @param testedString
	 *            The value to compare.
	 * @param caseSensitive
	 *            Set 'false' in order to ignore characters' case. Set 'true' otherwise.
	 * @return 'true' if both strings are equal. 'false' otherwise.
	 */
	public static boolean equals(String expectedString, String testedString, boolean caseSensitive) {
		boolean equality = false;
		if(caseSensitive) {
			equality = testedString.equals(expectedString);
		} else {
			equality = testedString.toUpperCase().equals(expectedString.toUpperCase());
		}
		return equality;
	}

	/**
	 * Checks that the two strings given through parameters are equal. Returns true if both are null.
	 * 
	 * @param expectedString
	 * @param testedString
	 * @param caseSensitive
	 *            must comparison care about case sensitive?
	 */
	public boolean checkEquality(String expectedString, String testedString, boolean caseSensitive) {
		return checkEquality(m_testCase, expectedString, testedString, caseSensitive);
	}

	public static boolean checkEquality(TestCase testCase, String expectedString, String testedString, boolean caseSensitive) {
		boolean result = false;
		DocList comment = getDocListComparison(expectedString, testedString);
		if(testedString == null) {
			if(expectedString == null) {
				result = true;
				testCase.addTestResult(result, comment);
			} else {
				result = false;
				testCase.addTestResult(result, comment);
			}
		} else {
			result = equals(expectedString, testedString, caseSensitive);
			testCase.addTestResult(result, comment);
		}
		return result;
	}

	/**
	 * Return a Span object (see package qualify.doc) corresponding to the compared string: differing characters are highlighted. Use that
	 * Span object to add comment to your TestCase.
	 * 
	 * @param referenceString
	 *            The string that is used as referenced.
	 * @param stringToCompare
	 *            The string to compare. That string will be reported into the returned Span.
	 * @return
	 */
	public static Span getComparedDocString(String referenceString, String stringToCompare) {
		diff_match_patch dmp = new diff_match_patch();
		List<Diff> diffs = dmp.diff_main(referenceString, stringToCompare);

		Span secondString = new Span(null, null);
		for(Diff diff : diffs) {
			if(diff.operation == diff_match_patch.Operation.EQUAL) {
				secondString.add(new Span(diff.text, null));
			} else if(diff.operation == diff_match_patch.Operation.INSERT) {
				secondString.add(new Span(diff.text, "insert"));
			}
		}

		return secondString;
	}

	/**
	 * Returns a DocList (see package qualify.doc) that shows the differences between two strings. Differing characters are highlighted. Use
	 * that DocList object to add comment to your TestCase.
	 * 
	 * @param expectedString
	 * @param testedString
	 * @return
	 */
	public static DocList getDocListComparison(String expectedString, String testedString) {
		DomElementAble firstLine = null, secondLine = null;
		if(expectedString != null) {
			if(testedString != null) {
				diff_match_patch dmp = new diff_match_patch();
				List<Diff> diffs = dmp.diff_main(expectedString, testedString);
				Span firstString = new Span(null, null);
				for(Diff diff : diffs) {
					if(diff.operation == diff_match_patch.Operation.DELETE) {
						firstString.add(new Span(diff.text, "delete"));
					} else if(diff.operation == diff_match_patch.Operation.EQUAL) {
						firstString.add(new Span(diff.text, null));
					}
				}
				firstLine = new Field("expected", firstString);
				Span secondString = new Span(null, null);
				for(Diff diff : diffs) {
					if(diff.operation == diff_match_patch.Operation.EQUAL) {
						secondString.add(new Span(diff.text, null));
					} else if(diff.operation == diff_match_patch.Operation.INSERT) {
						secondString.add(new Span(diff.text, "insert"));
					}
				}
				secondLine = new Field("tested", secondString);
			} else {
				firstLine = new Field("expected", expectedString);
				secondLine = new DocString("tested string is null");
			}
		} else {
			firstLine = new DocString("expected string is null");
			if(testedString != null) {
				secondLine = new Field("tested", testedString);
			} else {
				secondLine = new DocString("tested string is null");
			}
		}

		DocList comment = new DocList();
		comment.addItem(firstLine);
		comment.addItem(secondLine);
		return comment;
	}

	/**
	 * Returns 'true' if the expected string is contained into the tested string.
	 * 
	 * @param expectedContainedString
	 *            The string that is expected.
	 * @param testedString
	 *            The string into witch the expected string should be contained.
	 * @param caseSensitive
	 *            Set to 'true' if you do care about the case for string comparison.
	 * @return 'true' if the expected string is contained into the tested string. Returns 'false' otherwise.
	 */
	public boolean contains(String expectedContainedString, String testedString, boolean caseSensitive) {
		boolean inclusion = false;
		if(caseSensitive) {
			inclusion = testedString.contains(expectedContainedString);
		} else {
			inclusion = testedString.toUpperCase().contains(expectedContainedString.toUpperCase());
		}
		return inclusion;
	}

	public boolean checkContains(String expectedContainedString, String testedString, boolean caseSensitive) {
		boolean result = false;
		if(testedString == null) {
			if(expectedContainedString == null) {
				result = true;
				m_testCase.addTestResult(result, "expected string is null | tested string is null");
			} else {
				result = true;
				m_testCase.addTestResult(result, "expected='" + expectedContainedString + "' | tested string is null too");
			}
		} else {
			result = contains(expectedContainedString, testedString, caseSensitive);

			m_testCase.addTestResult(result, "expected='" + expectedContainedString + "' | tested='" + testedString + "'");
		}
		return result;
	}

	public void checkContains(String expectedContainedString, String[] testedStrings, boolean caseSensitive) {
		boolean result = false;
		for(String testedString : testedStrings) {
			if(contains(expectedContainedString, testedString, caseSensitive)) {
				result = true;
				break;
			}
		}
		if(result) {
			m_testCase.addTestResult(true, "expected string '" + expectedContainedString + "' is contained in strings array");
		} else {
			m_testCase.addTestResult(false, "expected string '" + expectedContainedString + "' is not contained in strings array");
		}
	}

	public void checkContains(String expectedContainedString, List<String> testedStrings, boolean caseSensitive) {
		checkContains(expectedContainedString, toArray(testedStrings), caseSensitive);
	}

	public void checkStartsWith(String expectedStart, String testedString) {
		boolean result = testedString.startsWith(expectedStart);
		if(result) {
			m_testCase.addTestResult(true, "tested string '" + testedString + "' starts with '" + expectedStart + "'");
		} else {
			m_testCase.addTestResult(false, "tested string '" + testedString + "' does not start with '" + expectedStart + "'");
		}
	}

	/**
	 * Checks that the Levenshtein's distance between strings a and b is less or equal to maxExpectedDistance.
	 * 
	 * @param a
	 * @param b
	 * @param maxExpectedDistance
	 */
	public void checkLevenshteinDistance(String a, String b, int maxExpectedDistance) {
		int d = getLevenshteinDistance(a, b);
		m_testCase.addTestResult(d <= maxExpectedDistance, "max expected distance=" + maxExpectedDistance + " | tested distance=" + d);
	}

	/**
	 * Returns the Levenshtein's distance between strings a and b.
	 * 
	 * @param a
	 * @param b
	 * @return The Levenshtein's distance between strings a and b.
	 */
	public static int getLevenshteinDistance(String a, String b) {
		return StringUtils.getLevenshteinDistance(a, b);
	}

	/**
	 * Returns true if the tested String is contained in at least one of the referenced Strings.
	 * 
	 * @param referencedStrings
	 *            The Strings into where tested string should be contained
	 * @param testedStrings
	 *            The String to look for
	 * @param caseSensitive
	 *            Set true if characters'case is needed.
	 * @return true if the tested String is contained in at least one of the referenced String. False otherwise.
	 */
	public boolean isContained(String[] referencedStrings, String testedString, boolean caseSensitive) {
		boolean result = false;
		for(String referencedString : referencedStrings) {
			if(contains(testedString, referencedString, caseSensitive)) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * Returns true if the expected String is contained in at least one of the Strings of the tested array.
	 * 
	 * @param expectedContainedString
	 *            The String to look for into the string array
	 * @param testedStrings
	 *            The array of Strings where expected String is searched.
	 * @param caseSensitive
	 *            Set true if characters'case is needed.
	 * @return true if the expected String is contained in at least one of the Strings of the tested array. False otherwise.
	 */
	public boolean contains(String expectedContainedString, String[] testedStrings, boolean caseSensitive) {
		boolean result = false;
		for(String testedString : testedStrings) {
			if(contains(expectedContainedString, testedString, caseSensitive)) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * Checks that the expected String is contained into at least one of the strings of the array. (See contains(String
	 * expectedContainedString, String[] testedStrings, boolean caseSensitive) for details.
	 * 
	 * @param expectedContainedString
	 *            The String to look for into the string array
	 * @param testedStrings
	 *            The array of Strings where expected String is searched.
	 * @param caseSensitive
	 *            Set true if characters'case is needed.
	 */
	public void checkArrayContains(String expectedContainedString, String[] testedStrings, boolean caseSensitive) {
		m_testCase.addTestResult(contains(expectedContainedString, testedStrings, caseSensitive), "expected contained string = '"
				+ expectedContainedString + "' | tested strings array contains " + testedStrings.length + " string(s)");
	}

	/**
	 * Checks that the expected String is not contained into at least one of the strings of the array. (See contains(String
	 * expectedContainedString, String[] testedStrings, boolean caseSensitive) for details.
	 * 
	 * @param expectedContainedString
	 *            The String to look for into the string array
	 * @param testedStrings
	 *            The array of Strings where expected String is searched.
	 * @param caseSensitive
	 *            Set true if characters'case is needed.
	 */
	public void checkDoesntContain(String expectedContainedString, String[] testedStrings, boolean caseSensitive) {
		m_testCase.addTestResult(!contains(expectedContainedString, testedStrings, caseSensitive), "expected contained string = '"
				+ expectedContainedString + "' | tested strings array contains " + testedStrings.length + " string(s)");
	}

	/**
	 * Returns true if both strings arrays contain the same values, regardless of the order.
	 * 
	 * @param expectedStrings
	 *            The expected strings array.
	 * @param testedStrings
	 *            The strings array to compare.
	 * @param caseSensitive
	 *            Set true if characters'case is needed.
	 * @return True if both strings arrays contain the same values, regardless of the order. False otherwise.
	 */
	public boolean sameValues(String[] expectedStrings, String[] testedStrings, boolean caseSensitive) {
		boolean sameNumberOfValues = false;
		boolean sameValues = true;

		if((testedStrings == null) || (expectedStrings == null)) {
			// Both arrays are null
			if((testedStrings == null) && (expectedStrings == null)) {
				sameNumberOfValues = true;
				sameValues = true;
			} else if(testedStrings == null) {
				sameNumberOfValues = false;
				sameValues = false;
			} else {
				sameNumberOfValues = false;
				sameValues = false;
			}
		} else {
			sameNumberOfValues = (expectedStrings.length == testedStrings.length);
			sameValues = true;

			for(String expectedValue : expectedStrings) {
				boolean expectedValueIsPresent = false;
				for(String testedValue : testedStrings) {
					if(equals(expectedValue, testedValue, caseSensitive)) {
						expectedValueIsPresent = true;
					}
				}
				sameValues = sameValues && expectedValueIsPresent;
			}
		}

		return (sameNumberOfValues && sameValues);
	}

	/**
	 * Checks that the tested string array has the same values as the expected one, that means: same number, same values. That check does
	 * not verify the order of the values.
	 * 
	 * @param expectedStrings
	 *            Expected strings as an array.
	 * @param testedStrings
	 *            The string array to check.
	 * @param caseSensitive
	 *            Set to 'true' if you do care about the case for string comparison.
	 */
	public void checkSameValues(String[] expectedStrings, String[] testedStrings, boolean caseSensitive) {
		if((testedStrings == null) || (expectedStrings == null)) {
			// Both arrays are null
			if((testedStrings == null) && (expectedStrings == null)) {
				m_testCase.addTestResult(true, "expected array is null | tested strings array is null");
			} else if(testedStrings == null) {
				m_testCase.addTestResult(false, "expected array is not null (" + expectedStrings.length
						+ " values) | tested strings array is null");
			} else {
				m_testCase.addTestResult(false, "expected array is null | tested strings array is not null (" + testedStrings.length
						+ " values)");
			}
		} else {
			boolean sameNumberOfValues = (expectedStrings.length == testedStrings.length);
			boolean sameValues = true;

			int expectedValuesNotFoundCount = 0;
			for(String expectedValue : expectedStrings) {
				boolean expectedValueIsPresent = false;
				for(String testedValue : testedStrings) {
					if(equals(expectedValue, testedValue, caseSensitive)) {
						expectedValueIsPresent = true;
					}
				}
				if(!expectedValueIsPresent) {
					expectedValuesNotFoundCount++;
				}
				sameValues = sameValues && expectedValueIsPresent;
			}

			if(sameNumberOfValues) {
				if(sameValues) {
					m_testCase.addTestResult(true, "expected string array (" + expectedStrings.length + " values | "
							+ "tested string array (" + expectedStrings.length + " values");
				} else {
					m_testCase.addTestResult(false, "" + expectedValuesNotFoundCount + " expected values not found in tested string array");
				}
			} else {
				m_testCase.addTestResult(false, "expected array has " + expectedStrings.length + " values | tested strings array has "
						+ testedStrings.length + " values");
			}
		}
	}

	/**
	 * Converts an Enumeration to a List.
	 * 
	 * @param strings
	 * @return
	 */
	public static List<String> toList(Enumeration<String> strings) {
		List<String> result = new ArrayList<String>();
		if(strings == null) {
			result = null;
		} else {
			while(strings.hasMoreElements()) {
				result.add(strings.nextElement());
			}
		}
		return result;
	}

	public static List<String> toList(String[] strings) {
		List<String> result = new ArrayList<String>();
		if(strings == null) {
			result = null;
		} else {
			for(String s : strings) {
				result.add(s);
			}
		}
		return result;
	}

	public static String[] toArray(List<String> strings) {
		return strings.toArray(new String[strings.size()]);
	}

	/**
	 * Creates a DocList from a HashMap composed of String elements. Use that DocList as comments.
	 * 
	 * @param strings
	 * @return
	 */
	public static DocList toDocList(HashMap<String, String> strings) {
		DocList result = new DocList();
		if(strings == null) {
			result = null;
		} else {
			for(String key : strings.keySet()) {
				result.addItem(new Field(key, strings.get(key)));
			}
		}
		return result;
	}

	/**
	 * Returns a list containing the each original string one single time, even if these values are contained several times in the original
	 * list.
	 * 
	 * @param originalList
	 *            The list of strings to clean (remove doubles).
	 * @return A list containing the each original string one single time
	 */
	public static List<String> cleanDoubles(List<String> originalList) {
		List<String> result = new ArrayList<String>();
		for(String value : originalList) {
			if(!result.contains(value)) {
				result.add(value);
			}
		}
		return result;
	}

	/**
	 * Indicates if the string does match the regular expression
	 * 
	 * @param regex
	 * @param string
	 * @return
	 */
	public static boolean matches(String regex, String testedString) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(testedString);
		return m.matches();
	}

	public boolean checkMatchesRegex(String regex, String testedString) {
		boolean result = false;

		if(testedString == null) {
			if(regex == null) {
				m_testCase.addTestResult(false, "regex is null | tested string is null");
			} else {
				m_testCase.addTestResult(false, "regex=" + regex + " | tested string is null");
			}
		} else {
			if(regex == null) {
				m_testCase.addTestResult(false, "regex is null | tested=" + testedString);
			} else {
				result = matches(regex, testedString);
				m_testCase.addTestResult(result, "regex=" + regex + " | tested=" + testedString);
			}
		}
		return result;
	}

	/**
	 * Returns the groups catched by the regular expression into the string. Group 0 is composed by the whole string. So the first extracted
	 * group is group with index 1.
	 * 
	 * @param regex
	 * @param string
	 * @return
	 */
	public static String[] getGroups(String regex, String string) {
		String[] result = null;
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(string);
		if(m.matches()) {
			int count = m.groupCount() + 1;
			result = new String[count];
			for(int i = 0; i < count; i++) {
				result[i] = m.group(i);
			}
		}
		return result;
	}

	/**
	 * Concatenate too String arrays.
	 * 
	 * @param a
	 * @param b
	 * @return a + b
	 */
	public static String[] concat(String[] a, String[] b) {
		if(a == null) {
			a = new String[0];
		}
		if(b == null) {
			b = new String[0];
		}
		return concat(a, b, 0, b.length - 1);
	}

	/**
	 * Concatenate too String arrays. Only the part of 'b' from startIndex to endIndex is added at the end of 'a'
	 * 
	 * @param a
	 * @param b
	 * @param startIndex
	 * @param endIndex
	 * @return a + b[startIndex -> endIndex]
	 */
	public static String[] concat(String[] a, String[] b, int startIndex, int endIndex) {
		if(a == null) {
			a = new String[0];
		}
		if((b == null)) {
			b = new String[0];
		}

		startIndex = Math.min(startIndex, b.length);
		endIndex = Math.min(endIndex, b.length - 1);
		String[] result = new String[Math.max(0, a.length) + 1 + endIndex - startIndex];
		for(int i = 0; i < a.length; i++) {
			result[i] = a[i];
		}
		for(int i = startIndex; i <= endIndex; i++) {
			result[a.length + i - startIndex] = b[i];
		}
		return result;

	}

	public static DocList toDocList(String[] strings) {
		DocList result = new DocList();
		for(String s : strings) {
			result.addItem(new DocString(s));
		}
		return result;
	}

	private static String toString(byte[] bytes) {
		StringBuilder result = new StringBuilder();
		for(int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i]);
			if(hex.length() == 1) {
				result.append('0');
				result.append(hex.charAt(hex.length() - 1));
			} else {
				result.append(hex.substring(hex.length() - 2));
			}
		}

		return result.toString();
	}

	public static String[] toLines(String input) {
		return input.replaceAll("\\r\\n", "\n").split("[\\r\\n]");
	}

	public static String md5(String inputString) throws NoSuchAlgorithmException {
		byte[] inputAsBytes = inputString.getBytes();
		byte[] hash = md5(inputAsBytes);
		return toString(hash);
	}

	public static String md5(InputStream is) throws NoSuchAlgorithmException, IOException {
		byte[] bytes = IOUtils.toByteArray(is);
		byte[] hash = md5(bytes);
		return toString(hash);
	}

	public static byte[] md5(byte[] bytes) throws NoSuchAlgorithmException {
		byte[] hash = null;
		hash = MessageDigest.getInstance("MD5").digest(bytes);
		return hash;
	}
}
