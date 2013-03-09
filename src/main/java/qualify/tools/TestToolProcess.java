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

import org.apache.log4j.Logger;

import qualify.ErrorsAndWarnings;
import qualify.TestCase;
import qualify.testRunner.TProcess;

/**
 * Provides methods to easily launch executables, command line processes, and analyze
 * exit codes and outputs.
 *
 */
public class TestToolProcess {

	private Logger logger = Logger.getLogger(TestToolProcess.class);

	private TestCase testCase = null;

	public TestToolProcess(TestCase tc) {
		testCase = tc;
	}

	/**
	 * Executes a command line from a CMD process. Works for Windows only.
	 * @param commandLine The command line arguments.
	 * @param workingDir The directory from where the command is launched.
	 * @param logFile The file where standard and error output of CMD will be redirected.
	 * @param timeoutInSeconds The maximum time allowed for executing the command. Beyond
	 * that time, a timer will destroy the running process.
	 * @return Wait until the command is terminated or the timeout has been reached.
	 */
	public int executeMSWindowsCommandLine(String[] commandLine, File workingDir, File logFile, double timeoutInSeconds) {
		// Preparing the command line
		String[] msWCommandLine = new String[commandLine.length + 2];
		msWCommandLine[0] = "cmd.exe";
		msWCommandLine[1] = "/C";
		int index = 2;
		for(String commandTerm : commandLine) {
			msWCommandLine[index] = commandTerm;
			index++;
		}

		int exitCode = -1;

		exitCode = execute(msWCommandLine, workingDir, logFile, timeoutInSeconds);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return exitCode;
	}

	/**
	 * Launches a command line from a CMD process. Works for Windows only.
	 * @param commandLine The command line arguments.
	 * @param workingDir The directory from where the command is launched.
	 * @param logFile The file where standard and error output of CMD will be redirected.
	 * @param timeoutInSeconds The maximum time allowed for executing the command. Beyond
	 * that time, a timer will destroy the running process.
	 * @return The created TProcess.
	 */
	public TProcess executeMSWindowsCommandLineInBackground(String[] commandLine, File workingDir, File logFile, double timeoutInSeconds) {
		// Preparing the command line
		String[] msWCommandLine = new String[commandLine.length + 2];
		msWCommandLine[0] = "cmd.exe";
		msWCommandLine[1] = "/C";
		int index = 2;
		for(String commandTerm : commandLine) {
			msWCommandLine[index] = commandTerm;
			index++;
		}

		TProcess proc = executeInBackground(msWCommandLine, workingDir, logFile, timeoutInSeconds);

		return proc;
	}

	/**
	 * Executes a command line from a BASH process. Works for Linux only (tested with Ubuntu).
	 *@param commandLine The command line arguments.
	 * @param workingDir The directory from where the command is launched.
	 * @param logFile The file where standard and error output of CMD will be redirected.
	 * @param timeoutInSeconds The maximum time allowed for executing the command. Beyond
	 * that time, a timer will destroy the running process.
	 * @return Wait until the command is terminated or the timeout has been reached.
	 */
	public int executeLinuxCommandLine(String[] commandLine, File workingDir, File logFile, double timeoutInSeconds) {
		// Preparing the command line
		String[] linuxCommandLine = new String[commandLine.length + 1];
		linuxCommandLine[0] = "/bin/bash";
		int index = 1;
		for(String commandTerm : commandLine) {
			linuxCommandLine[index] = commandTerm;
			index++;
		}

		int exitCode = -1;

		exitCode = execute(linuxCommandLine, workingDir, logFile, timeoutInSeconds);

		return exitCode;
	}

	/**
	 * Performs one call to executeMSWindowsCommandLine then checks the exit code. Automatically attaches the log file
	 * to the test result.
	 * @param commandLine The command line arguments(usually separated with space " ").
	 * @param workingDir The directory from where the command is launched.
	 * @param logFile The file where standard and error output will be redirected.
	 * @param timeoutInSeconds
	 * @param expectedExitCode The expected value for exit code
	 */
	public void checkExecuteMSWindowsCommandLine(String[] commandLine, File workingDir, File logFile, double timeoutInSeconds, int expectedExitCode) {
		int exitCode = -1;

		exitCode = executeMSWindowsCommandLine(commandLine, workingDir, logFile, timeoutInSeconds);

		if(logFile != null) {
			testCase.attachFile(logFile);
		} else {
			ErrorsAndWarnings.addError("Cannot attach file with null reference");
		}
		new TestToolNumbers(testCase).checkEquality(expectedExitCode, exitCode);
	}

	/**
	 * Performs one call to executeLinuxCommandLine then checks the exit code. Automatically attaches the log file
	 * to the test result.
	 * @param commandLine The command line arguments (usually separated with space " ").
	 * @param workingDir The directory from where the command is launched.
	 * @param logFile The file where standard and error output will be redirected.
	 * @param timeoutInSeconds
	 * @param expectedExitCode The expected value for exit code
	 */
	public void checkExecuteLinuxCommandLine(String[] commandLine, File workingDir, File logFile, double timeoutInSeconds, int expectedExitCode) {
		int exitCode = -1;

		exitCode = executeLinuxCommandLine(commandLine, workingDir, logFile, timeoutInSeconds);

		if(logFile != null) {
			testCase.attachFile(logFile);
		} else {
			ErrorsAndWarnings.addError("Cannot attach file with null reference");
		}
		new TestToolNumbers(testCase).checkEquality(expectedExitCode, exitCode);
	}

	/**
	 * Executes a file in a new process.
	 * @param commandLine The command line to execute. Must begins with the path to executable file.
	 * @param workingDir The working directory for the new process to run.
	 * @param logFile The file where standard output and standard error streams will be redirected.
	 * @param timeoutInSeconds
	 * @return Only when process is to the end.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public int execute(String[] commandLine, File workingDir, File logFile, double timeoutInSeconds) {		
		TProcess proc = executeInBackground(commandLine, workingDir, logFile, timeoutInSeconds);

		return proc.waitFor();
	}

	/**
	 * Executes a file in a new process.
	 * @param commandLine The command line to execute. Must begins with the path to executable file.
	 * @param workingDir The working directory for the new process to run.
	 * @param logFile The file where standard output and standard error streams will be redirected.
	 * @param timeoutInSeconds
	 * @return The Process object of the running process.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public TProcess executeInBackground(String[] commandLine, File workingDir, File logFile, double timeoutInSeconds) {
		logger.info("Running: " + commandLine + " workingDir=" + workingDir + " logFile=" + logFile);

		TProcess result = null;

		Runtime rt = Runtime.getRuntime();

		// Executing the command
		Process proc = null;
		try {
			proc = rt.exec(commandLine, null, workingDir);

			result = new TProcess(proc, timeoutInSeconds);
			if(logFile != null) {
				result.registerOutput(logFile, "");
			}

		} catch (IOException e) {
			e.printStackTrace();
			testCase.addTestResult(false, "An error occured during process execution", TestToolProcess.class);
		}

		return result;
	}

	/**
	 * Returns if the current running OS is Linux
	 * @return 'true' if your test is running under a Linux OS.
	 */
	public boolean isOSLinux() {
		boolean result = false;
		String linuxEnvKey = "HOME";
		String linuxValue = "/";
		if(System.getenv(linuxEnvKey) != null) {
			if(System.getenv(linuxEnvKey).startsWith(linuxValue)) {
				result = true;
			}
		}
		return result;
	}

}
