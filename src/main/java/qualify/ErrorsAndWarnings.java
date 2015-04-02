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

import java.util.LinkedList;

import org.apache.log4j.Logger;

import qualify.tools.StackTraceTool;

public class ErrorsAndWarnings {

	static Logger logger = Logger.getLogger(ErrorsAndWarnings.class);

	public static final int CONFIGURATION_FILE_IS_NEEDED = 1, CONFIGURATION_FILE_IS_NOT_A_VALID_XML = 2,
			CONFIGURATION_FILE_DOESNT_EXIST = 3, TEST_RESULT_FILE_IS_NOT_A_VALID_XML = 4, TEST_RESULT_FILE_DOESNT_EXIST = 5;

	private static boolean SUPPRESS_WARNINGS_ON = false;

	private static LinkedList<String> warnings = new LinkedList<String>();
	private static LinkedList<String> errors = new LinkedList<String>();

	public static String getTestCaseOriginPrefix() {
		if(StackTraceTool.getTestCaseCall() != null) {
			String testCaseName = StackTraceTool.getTestCaseCall().getFileName();
			int testCaseLine = StackTraceTool.getTestCaseCall().getLineNumber();
			return testCaseName + " line " + testCaseLine + " : ";
		} else {
			return "";
		}
	}

	public static int getErrorsCount() {
		return errors.size();
	}

	public static LinkedList<String> getWarnings() {
		return warnings;
	}

	public static LinkedList<String> getErrors() {
		return errors;
	}

	public static void addWarningsAndErrors(TestCase testCase) {
		for(String warning : testCase.getWarnings()) {
			addWarning(warning);
		}
		for(String error : testCase.getErrors()) {
			addError(error);
		}
	}

	public static void addWarning(String warnMessage) {
		if(!SUPPRESS_WARNINGS_ON) {
			logger.warn(getTestCaseOriginPrefix() + warnMessage);
			if(!warnings.contains(getTestCaseOriginPrefix() + warnMessage)) {
				warnings.add(getTestCaseOriginPrefix() + warnMessage);
			}
		}
	}

	public static void addError(String errMessage) {
		logger.error(getTestCaseOriginPrefix() + errMessage);
		errors.add(getTestCaseOriginPrefix() + errMessage);
	}

	public static void addException(Throwable e) {
		logger.error(getTestCaseOriginPrefix() + e.getMessage(), e);
		errors.add(getTestCaseOriginPrefix() + e.getMessage());
	}

	public static void activateWarnings() {
		SUPPRESS_WARNINGS_ON = false;
	}

	public static void suppressWarnings() {
		SUPPRESS_WARNINGS_ON = true;
	}
}
