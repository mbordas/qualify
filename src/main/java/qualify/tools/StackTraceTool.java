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

import org.apache.log4j.Logger;

import qualify.TestCase;

public class StackTraceTool {

	static Logger logger = Logger.getLogger(StackTraceTool.class);

	public static StackTraceElement getTestCaseCall() {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();

		// Looking for the first call under TestHarness.java
		for(int i = 0; i < stack.length; i++) {
			if(stack[i] != null) {
				for(int j = 0; j < stack.length; j++) {
					if(stack[j] != null) {
						logger.debug("alternative stack[" + j + "]: " + stack[j].getFileName() + ":" + stack[j].getLineNumber());
						if(stack[j].getFileName() != null) {
							if(stack[j].getFileName().equals("TestHarness.java")) {
								return stack[j - 1];
							}
						}
					}
				}
			}
		}

		// if no result => Looking for last call of TestCase daughter class
		StackTraceElement result = null;
		for(int i = 0; i < stack.length; i++) {
			for(int j = 0; j < stack.length; j++) {
				if(stack[i] != null) {
					try {
						Class<?> superClazz = Class.forName(stack[i].getClassName()).getSuperclass();
						while(superClazz != null) {
							if(TestCase.class == superClazz) {
								result = stack[i];
								break;
							}
							superClazz = superClazz.getSuperclass();
						}
					} catch(ClassNotFoundException e) {
						logger.debug("alternative stack length: " + stack.length);
					}
				}
			}
		}
		return result;
	}

	public static StackTraceElement getCall(StackTraceElement[] stackTrace, String throwingClass) {
		logger.error("getCall class=" + throwingClass);
		if(throwingClass == null) {
			return stackTrace[1];
		} else {
			for(int i = 0; i < stackTrace.length; i++) {
				if(stackTrace[i] != null) {
					if(stackTrace[i].getFileName() != null) {
						logger.error("getCall " + i + " => " + stackTrace[i].getFileName());
						if(stackTrace[i].getFileName().equals(throwingClass + ".java")
								|| stackTrace[i].getFileName().equals(throwingClass + ".groovy_preproc")) {
							logger.error("returned i=" + i);
							return stackTrace[i];
						}
					}
				}
			}
		}
		return null;
	}

	public static String getComingStackTraceElementLocation(Throwable e, Class arrivalClass) {
		// Looking for the call just before TestHarness
		StackTraceElement callAtClass = getCall(e.getStackTrace(), arrivalClass.getSimpleName());
		int i = 0;
		for(StackTraceElement ste : e.getStackTrace()) {
			if(ste == callAtClass) {
				break;
			}
			i++;
		}
		StackTraceElement callBeforeClass = e.getStackTrace()[i - 1];

		return callBeforeClass.getFileName() + ":" + callBeforeClass.getLineNumber();
	}

	public static String getStackTraceElementLocation(Exception e, String classNameMatchingRegEx) {
		// Looking for the call just before TestHarness
		int i = 0;
		StackTraceElement ste = e.getStackTrace()[0];
		for(StackTraceElement s : e.getStackTrace()) {
			if(TestToolStrings.matches(classNameMatchingRegEx, s.getFileName())) {
				ste = s;
				break;
			}
			i++;
		}

		return ste.getFileName() + ":" + ste.getLineNumber();
	}

}
