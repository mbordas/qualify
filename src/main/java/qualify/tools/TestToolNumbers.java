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

import qualify.TestCase;

public class TestToolNumbers {

	private TestCase testCase = null;

	public TestToolNumbers(TestCase tc) {
		testCase = tc;
	}

	public boolean checkEquality(int expectedInteger, int testedInteger) {
		boolean result = expectedInteger == testedInteger;
		testCase.addTestResult(result, "expected=" + expectedInteger + " | tested=" + testedInteger);
		return result;
	}

	public void checkEquality(long expectedLong, long testedLong) {
		testCase.addTestResult(expectedLong == testedLong, "expected=" + expectedLong + " | tested=" + testedLong);
	}

	public boolean checkEquality(Double expectedDouble, Double testedDouble, Double epsilon) {
		boolean result = false;
		if(testedDouble == null) {
			if(expectedDouble == null) {
				result = true;
				testCase.addTestResult(result, "expected is null (epsilon=" + epsilon + ") | tested is null");
			} else {
				result = false;
				testCase.addTestResult(result, "expected=" + expectedDouble + " (epsilon=" + epsilon + ") | tested is null");
			}
		} else {
			if(expectedDouble == null) {
				result = false;
				testCase.addTestResult(result, "expected is null (epsilon=" + epsilon + ") | tested=" + testedDouble);
			} else {
				result = Math.abs(expectedDouble - testedDouble) <= epsilon;
				testCase.addTestResult(result, "expected=" + expectedDouble + " (epsilon=" + epsilon + ") | tested=" + testedDouble);
			}
		}

		return result;
	}

	public void checkGreater(int limitInteger, int testedInteger, boolean allowEquality) {
		if(allowEquality) {
			testCase.addTestResult(testedInteger >= limitInteger, "limit=" + limitInteger + " >= tested=" + testedInteger);
		} else {
			testCase.addTestResult(testedInteger > limitInteger, "limit=" + limitInteger + " > tested=" + testedInteger);
		}
	}

	public void checkGreater(long limitLong, long testedLong, boolean allowEquality) {
		if(allowEquality) {
			testCase.addTestResult(testedLong >= limitLong, "limit=" + limitLong + " =< tested=" + testedLong);
		} else {
			testCase.addTestResult(testedLong > limitLong, "limit=" + limitLong + " < tested=" + testedLong);
		}
	}

	public boolean checkGreater(double limitDouble, double testedDouble, boolean allowEquality) {
		boolean success = false;
		if(allowEquality) {
			success = testCase.addTestResult(testedDouble >= limitDouble, "limit=" + limitDouble + " >= tested=" + testedDouble);

		} else {
			success = testCase.addTestResult(testedDouble > limitDouble, "limit=" + limitDouble + " > tested=" + testedDouble);
		}
		return success;
	}

	public void checkLesser(int limitInteger, int testedInteger, boolean allowEquality) {
		if(allowEquality) {
			testCase.addTestResult(testedInteger <= limitInteger, "limit=" + limitInteger + " <= tested=" + testedInteger);
		} else {
			testCase.addTestResult(testedInteger < limitInteger, "limit=" + limitInteger + " < tested=" + testedInteger);
		}
	}

	public void checkLesser(long limitLong, long testedLong, boolean allowEquality) {
		if(allowEquality) {
			testCase.addTestResult(testedLong <= limitLong, "limit=" + limitLong + " <= tested=" + testedLong);
		} else {
			testCase.addTestResult(testedLong < limitLong, "limit=" + limitLong + " < tested=" + testedLong);
		}
	}

	public void checkLesser(double limitDouble, double testedDouble, boolean allowEquality) {
		if(allowEquality) {
			testCase.addTestResult(testedDouble <= limitDouble, "limit=" + limitDouble + " <= tested=" + testedDouble);
		} else {
			testCase.addTestResult(testedDouble < limitDouble, "limit=" + limitDouble + " < tested=" + testedDouble);
		}
	}

	public void checkNear(int expectedInteger, int testedInteger, int maxDistanceAllowed) {
		testCase.addTestResult(Math.abs(testedInteger - expectedInteger) <= Math.abs(maxDistanceAllowed), "expected=" + expectedInteger
				+ " ~= (+/- " + maxDistanceAllowed + ") tested=" + testedInteger);
	}

	public void checkNear(long expectedLong, long testedLong, long maxDistanceAllowed) {
		testCase.addTestResult(Math.abs(testedLong - expectedLong) <= Math.abs(maxDistanceAllowed), "expected=" + expectedLong + " ~= (+/- "
				+ maxDistanceAllowed + ") tested=" + testedLong);
	}

	public void checkNear(double expectedDouble, double testedDouble, double maxDistanceAllowed) {
		testCase.addTestResult(Math.abs(testedDouble - expectedDouble) <= Math.abs(maxDistanceAllowed), "expected=" + expectedDouble
				+ " ~= (+/- " + maxDistanceAllowed + ") tested=" + testedDouble);
	}

	public boolean sameValues(int[] expectedInts, int[] testedInts) {
		boolean sameNumberOfValues = false;
		boolean sameValues = true;

		if((testedInts == null) || (expectedInts == null)) {
			// Both arrays are null
			if((testedInts == null) && (expectedInts == null)) {
				sameNumberOfValues = true;
				sameValues = true;
			} else if(testedInts == null) {
				sameNumberOfValues = false;
				sameValues = false;
			} else {
				sameNumberOfValues = false;
				sameValues = false;
			}
		} else {
			sameNumberOfValues = (expectedInts.length == testedInts.length);
			sameValues = true;

			for(int expectedValue : expectedInts) {
				boolean expectedValueIsPresent = false;
				for(int testedValue : testedInts) {
					if(expectedValue == testedValue) {
						expectedValueIsPresent = true;
					}
				}
				sameValues = sameValues && expectedValueIsPresent;
			}
		}

		return (sameNumberOfValues && sameValues);
	}

	public void checkSameValues(int[] expectedInts, int[] testedInts) {
		if((testedInts == null) || (expectedInts == null)) {
			// Both arrays are null
			if((testedInts == null) && (expectedInts == null)) {
				testCase.addTestResult(true, "expected array is null | tested int array is null");
			} else if(testedInts == null) {
				testCase.addTestResult(false, "expected array is not null (" + expectedInts.length + " values) | tested int array is null");
			} else {
				testCase.addTestResult(false, "expected array is null | tested int array is not null (" + testedInts.length + " values)");
			}
		} else {
			boolean sameNumberOfValues = (expectedInts.length == testedInts.length);
			boolean sameValues = true;

			int expectedValuesNotFoundCount = 0;
			for(int expectedValue : expectedInts) {
				boolean expectedValueIsPresent = false;
				for(int testedValue : testedInts) {
					if(expectedValue == testedValue) {
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
					testCase.addTestResult(true, "expected int array (" + expectedInts.length + " values) | " + "tested int array ("
							+ expectedInts.length + " values)");
				} else {
					testCase.addTestResult(false, "" + expectedValuesNotFoundCount + " expected values not found in tested int array");
				}
			} else {
				testCase.addTestResult(false, "expected array has " + expectedInts.length + " values | tested int array has "
						+ testedInts.length + " values");
			}
		}
	}

}
