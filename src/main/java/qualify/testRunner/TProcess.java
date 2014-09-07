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

package qualify.testRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;

import qualify.ErrorsAndWarnings;

/**
 * TProcess, for "Timed" Process is just one java.lang.Process with a timer.
 *
 */
public class TProcess implements TimerListener {

	private static Logger logger = Logger.getLogger(TProcess.class);

	private Process process = null;
	private ProcessTimer timer = null;

	private StreamListener
	errorStream = null,
	outputStream = null;

	/**
	 * Creates a TProcess, composed by the Process and one timer.
	 * Call waitFor() to wait until the process returns.
	 * @param processToManage The process to destroy after timeout
	 * @param timeoutInSeconds The timeout in seconds (precision is millisecond)
	 */
	public TProcess(Process processToManage, double timeoutInSeconds) {
		process = processToManage;
		timer = ProcessTimer.createTimer(this, timeoutInSeconds);
	}

	/**
	 * Waits until the process is terminated or the timeout is elapsed.
	 * @return The exit code of the process
	 */
	public int waitFor() {
		int exitCode = -1;
		if(process != null) {
			try {
				exitCode = process.waitFor();
				logger.debug("Closing errorStream...");
				errorStream.close();
				logger.debug("Closing outputStream...");
				outputStream.close();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ProcessTimer.removeTimer(timer);
			logger.debug("TProcess ended: exitCode = " + exitCode);
		} else {
			ErrorsAndWarnings.addError("Cannot call waitFor() on a null TProcess");
		}
		return exitCode;
	}

	/**
	 * Destroys the process
	 */
	public void stop() {
		ProcessTimer.removeTimer(timer);
		try {
			Thread.sleep(StreamListener.PRINTING_PERIOD_IN_MILLIS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(errorStream != null) {
			logger.debug("Closing errorStream...");
			errorStream.close();
		}
		if(outputStream != null) {
			logger.debug("Closing outputStream...");
			outputStream.close();
		}
		if(process != null) {
			logger.debug("destroying TProcess...");
			process.destroy();
			logger.debug("TProcess destroyed");
		}
	}

	/**
	 * Makes the TProcess print out and error streams into one specified file.
	 * Several files can be registered.
	 * @param logFile The file to print.
	 * @throws FileNotFoundException
	 */
	public void registerOutput(File logFile, String prompt) throws FileNotFoundException {

		// Links to standard and error outputs
		if(errorStream == null || outputStream == null) {
			errorStream = new StreamListener(process.getErrorStream());
			outputStream = new StreamListener(process.getInputStream());

			errorStream.start();
			outputStream.start();
		}

		// Links to output file
		if(logFile != null) {
			FileOutputStream fileOut = null;

			fileOut = new FileOutputStream(logFile);
			errorStream.registerOutputStream(prompt, fileOut);
			outputStream.registerOutputStream(prompt, fileOut);
		}
	}

	public void setLogToConsole(boolean logToConsole) {
		errorStream.setLogToConsole(logToConsole);
		outputStream.setLogToConsole(logToConsole);
	}

	public void onTimeout() {
		stop();
	}

}

/*
private static Logger logger = Logger.getLogger(TProcess.class);

private Process process = null;
private ProcessTimer timer = null;

private StreamListener
errorStream = null,
outputStream = null;

/**
 * Creates a TProcess, composed by the Process and one timer.
 * Call waitFor() to wait until the process returns.
 * @param processToManage The process to destroy after timeout
 * @param timeoutInSeconds The timeout in seconds (precision is millisecond)

public TProcess(Process processToManage, double timeoutInSeconds) {
	process = processToManage;
	timer = ProcessTimer.createTimer(processToManage, timeoutInSeconds);
}

/**
 * Waits until the process is terminated or the timeout is elapsed.
 * @return The exit code of the process

public int waitFor() {
	int exitCode = -1;
	if(process != null) {
		try {
			exitCode = process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ProcessTimer.removeTimer(timer);
		logger.debug("TProcess ended: exitCode = " + exitCode);
	} else {
		ErrorsAndWarnings.addError("Cannot call waitFor() on a null TProcess");
	}
	return exitCode;
}

/**
 * Destroys the process

public void destroy() {
	ProcessTimer.removeTimer(timer);
	logger.debug("Closing errorStream...");
	errorStream.close();
	logger.debug("Closing outputStream...");
	outputStream.close();
	logger.debug("destroying TProcess...");
	process.destroy();
	logger.debug("TProcess destroyed");
}

/**
 * Makes the TProcess print out and error streams into one specified file.
 * Several files can be registered.
 * @param logFile The file to print.
 * @throws FileNotFoundException

public void registerOutput(File logFile, String prompt) throws FileNotFoundException {

	// Links to standard and error outputs
	if(errorStream == null || outputStream == null) {
		errorStream = new StreamListener(process.getErrorStream());
		outputStream = new StreamListener(process.getInputStream());

		errorStream.registerOutputStream("proc.err > ", System.out);
		outputStream.registerOutputStream("proc.out > ", System.out);

		errorStream.start();
		outputStream.start();
	}

	// Links to output file
	if(logFile != null) {
		FileOutputStream fileOut = null;

		fileOut = new FileOutputStream(logFile);
		errorStream.registerOutputStream(prompt, fileOut);
		outputStream.registerOutputStream(prompt, fileOut);
	}
}

 */