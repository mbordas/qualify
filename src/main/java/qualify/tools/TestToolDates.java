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

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;

import qualify.TestCase;

public class TestToolDates {

	private TestCase testCase = null;

	public static final String DEFAULT_SIMPLE_FORMAT = "yyyyMMdd",
	DEFAULT_TIMESTAMP_FORMAT = "yyyyMMddHHmmSSsss";

	public TestToolDates(TestCase tc) {
		testCase = tc;
	}

	/**
	 * Checks the expected date. Equality is verified respecting the given date format precision.
	 * @param expectedDateString The expected date as a formatted string (respecting simpleDateFormat)
	 * @param simpleDateFormat The string that specifies the date format (see java.text.SimpleDateFormat for details)
	 * @param testedDate The expected date
	 */
	public void checkEquality(String expectedDateString, String simpleDateFormat, Date testedDate) {
		if(simpleDateFormat == null) {
			simpleDateFormat = DEFAULT_SIMPLE_FORMAT;
		}
		if(testedDate == null) {
			if(expectedDateString == null) {
				testCase.addTestResult(true, "expected date is null | tested date is null", TestToolDates.class);
			} else {
				testCase.addTestResult(false, "expected='" + expectedDateString + "' [" + simpleDateFormat + "] | tested date is null", TestToolDates.class);
			}
		} else {
			if(expectedDateString == null) {
				testCase.addTestResult(false, "expected date is null | tested='" + toString(testedDate, simpleDateFormat) + "' [" + simpleDateFormat + "]", TestToolDates.class);
			} else {
				String testedDateString = toString(testedDate, simpleDateFormat);
				if(testedDateString.equals(expectedDateString)) {
					testCase.addTestResult(true, "expected='" + expectedDateString + "' [" + simpleDateFormat + "] | tested='" + testedDateString + "' [" + simpleDateFormat + "]", TestToolDates.class);
				} else {
					testCase.addTestResult(false, "expected='" + expectedDateString + "' [" + simpleDateFormat + "] | tested='" + testedDateString + "' [" + simpleDateFormat + "]", TestToolDates.class);
				}
			}
		}
	}

	/**
	 * Checks the expected date in regard of the default date format: DEFAULT_SIMPLE_FORMAT = "yyyyMMdd"
	 * @param expectedDate The expected date object
	 * @param testedDate The verified date object
	 */
	public void checkEquality(Date expectedDate, Date testedDate) {
		if(testedDate == null) {
			if(expectedDate == null) {
				testCase.addTestResult(true, "expected date is null | tested date is null", TestToolDates.class);
			} else {
				testCase.addTestResult(false, "expected='" + toString(expectedDate, DEFAULT_SIMPLE_FORMAT) + "' [" + DEFAULT_SIMPLE_FORMAT + "] | tested date is null", TestToolDates.class);
			}
		} else {
			if(expectedDate == null) {
				testCase.addTestResult(false, "expected date is null | tested='" + toString(testedDate, DEFAULT_SIMPLE_FORMAT) + "' [" + DEFAULT_SIMPLE_FORMAT + "]", TestToolDates.class);
			} else {
				String testedDateString = toString(testedDate, DEFAULT_SIMPLE_FORMAT);
				if(testedDateString.equals(toString(expectedDate, DEFAULT_SIMPLE_FORMAT))) {
					testCase.addTestResult(true, "expected='" + toString(expectedDate, DEFAULT_SIMPLE_FORMAT) + "' [" + DEFAULT_SIMPLE_FORMAT + "] | tested='" + testedDateString + "' [" + DEFAULT_SIMPLE_FORMAT + "]", TestToolDates.class);
				} else {
					testCase.addTestResult(false, "expected='" + toString(expectedDate, DEFAULT_SIMPLE_FORMAT) + "' [" + DEFAULT_SIMPLE_FORMAT + "] | tested='" + testedDateString + "' [" + DEFAULT_SIMPLE_FORMAT + "]", TestToolDates.class);
				}
			}
		}
	}
	
	public void checkAfter(DateTime limitDate, DateTime testedDate) {
		if(testedDate == null) {
			if(limitDate == null) {
				testCase.addTestResult(true, "limit date is null | tested date is null", TestToolDates.class);
			} else {
				testCase.addTestResult(false, "limit='" + toString(limitDate, DEFAULT_SIMPLE_FORMAT) + "' [" + DEFAULT_SIMPLE_FORMAT + "] | tested date is null", TestToolDates.class);
			}
		} else {
			if(limitDate == null) {
				testCase.addTestResult(false, "limit date is null | tested='" + toString(testedDate, DEFAULT_SIMPLE_FORMAT) + "' [" + DEFAULT_SIMPLE_FORMAT + "]", TestToolDates.class);
			} else {
				String testedDateString = toString(testedDate, DEFAULT_SIMPLE_FORMAT);
				if(testedDate.isAfter(limitDate)) {
					testCase.addTestResult(true, "limit='" + toString(limitDate, DEFAULT_SIMPLE_FORMAT) + "' [" + DEFAULT_SIMPLE_FORMAT + "] | tested='" + testedDateString + "' [" + DEFAULT_SIMPLE_FORMAT + "]", TestToolDates.class);
				} else {
					testCase.addTestResult(false, "limit='" + toString(limitDate, DEFAULT_SIMPLE_FORMAT) + "' [" + DEFAULT_SIMPLE_FORMAT + "] | tested='" + testedDateString + "' [" + DEFAULT_SIMPLE_FORMAT + "]", TestToolDates.class);
				}
			}
		}
	}

	/**
	 * 
	 * @param dateString The date's string respecting DEFAULT_SIMPLE_FORMAT
	 * @return
	 */
	public Date getDate(String dateString) {
		return getDate(dateString, DEFAULT_SIMPLE_FORMAT);
	}

	/**
	 * 
	 * @param dateString The date's string
	 * @param simpleDateFormat The format used to create the Date
	 * @return
	 */
	public Date getDate(String dateString, String simpleDateFormat) {
		return new SimpleDateFormat(simpleDateFormat).parse(dateString, new ParsePosition(0));
	}

	/**
	 * 
	 * @param date The date to convert into string
	 * @param simpleDateFormat The simple format used for conversion (see java.text.SimpleDateFormat for details)
	 * @return
	 */
	public String toString(Date date, String simpleDateFormat) {
		return new SimpleDateFormat(simpleDateFormat).format(date);
	}

	public static String toString(DateTime date, String simpleDateFormat) {
		return date.toString(DateTimeFormat.forPattern(simpleDateFormat));
	}
	
	public static String toString(DateTime date) {
		return date.toString(DateTimeFormat.forPattern(DEFAULT_SIMPLE_FORMAT));
	}
	
	/**
	 * Returns a formatted string that represents the period.
	 * The format is: hh:mm:ss.SSS
	 * The precision is day to millisecond
	 * @param duration
	 * @return
	 */
	public static String toString(Period period) {
		int hours = period.getHours();
		period = period.minusHours(hours);
		int minutes = period.getMinutes();
		period = period.minusMinutes(minutes);
		int seconds = period.getSeconds();
		period = period.minusSeconds(seconds);
		int millis = period.getMillis();
		DecimalFormat format2digits = new DecimalFormat();
		format2digits.setMinimumIntegerDigits(2);
		DecimalFormat format3digits = new DecimalFormat();
		format3digits.setMinimumIntegerDigits(3);
		return "" + format2digits.format(hours) +
		":" + format2digits.format(minutes) +
		":" + format2digits.format(seconds) +
		"." + format3digits.format(millis);
	}

	/**
	 * Returns a formatted string that represents the duration.
	 * The format is: hh:mm:ss.SSS
	 * The precision is day to millisecond
	 * @param duration
	 * @return
	 */
	public static String toString(Duration duration) {
		Duration tmpDuration = new Duration(duration);
		long SECOND = 1000;
		long MINUTE = 60 * SECOND;
		long HOUR = 60 * MINUTE;
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
		int millis = 0;
		if(tmpDuration.isLongerThan(Duration.standardHours(1))) {
			hours = (int) Math.floor(tmpDuration.getMillis() / HOUR);
			tmpDuration = tmpDuration.minus(hours * HOUR);
		}
		if(tmpDuration.isLongerThan(Duration.standardMinutes(1))) {
			minutes = (int) Math.floor(tmpDuration.getMillis() / MINUTE);
			tmpDuration = tmpDuration.minus(minutes * MINUTE);
		}
		if(tmpDuration.isLongerThan(Duration.standardSeconds(1))) {
			seconds = (int) Math.floor(tmpDuration.getMillis() / SECOND);
			tmpDuration = tmpDuration.minus(seconds * SECOND);
		}
		millis = (int) tmpDuration.getMillis();
		DecimalFormat format2digits = new DecimalFormat();
		format2digits.setMinimumIntegerDigits(2);
		DecimalFormat format3digits = new DecimalFormat();
		format3digits.setMinimumIntegerDigits(3);
		return "" + format2digits.format(hours) +
		":" + format2digits.format(minutes) +
		":" + format2digits.format(seconds) +
		"." + format3digits.format(millis);
	}

	public boolean after(Date limitDate, Date testedDate, boolean allowEquality) {
		boolean result = false;
		if(testedDate.after(limitDate)) {
			result = true;
		}
		if(allowEquality && limitDate.compareTo(testedDate) == 0) {
			result = true;
		}
		return result;
	}

	public boolean after(DateTime limitDate, DateTime testedDate, boolean allowEquality) {
		boolean result = false;
		if(testedDate.isAfter(limitDate)) {
			result = true;
		}
		if(allowEquality && limitDate.isEqual(testedDate)) {
			result = true;
		}
		return result;
	}

}
