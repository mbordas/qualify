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

package qualify.testRunner;

import java.io.*;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * The StreamListener is a thread used to redirect output and error streams from external executing processes
 * @author Mathieu Bordas
 *
 */
public class StreamListener extends Thread {
	
	public static long PRINTING_PERIOD_IN_MILLIS = 1000;
	
	private static Logger logger = Logger.getLogger(StreamListener.class);

	private Vector<PrintWriter> printWriters;
	private Vector<String> prompts;

	private BufferedReader bufferedReader = null;

	private boolean killed = false;

	private boolean logToConsole = false;

	public StreamListener(InputStream inputStream) {
		printWriters = new Vector<PrintWriter>();
		prompts = new Vector<String>();
		InputStreamReader isr = new InputStreamReader(inputStream);
		bufferedReader = new BufferedReader(isr);
	}

	public void setLogToConsole(boolean logToConsole) {
		this.logToConsole = logToConsole;
	}

	public void registerOutputStream(String prompt, OutputStream outputStream) {
		prompts.add(prompt);
		printWriters.add(new PrintWriter(outputStream));
	}

	public void run() {
		StringBuffer sbuff = new StringBuffer();
		try {
			while(!killed) {
				if(bufferedReader.ready()) {
					int line = bufferedReader.read();
					sbuff.append(Character.toChars(line));
				} else {
					if((sbuff.length() > 0) && logToConsole) {
						System.out.print(sbuff.toString());
					}
					for(PrintWriter pw : printWriters) {
						pw.print(sbuff.toString());
					}
					sbuff = new StringBuffer();
					Thread.sleep(PRINTING_PERIOD_IN_MILLIS);
				}
			}
			while(bufferedReader.ready()) {
				int line = bufferedReader.read();
				sbuff.append(Character.toChars(line));
			}
			if((sbuff.length() > 0) && logToConsole) {
				System.out.print(sbuff.toString());
			}
			for(PrintWriter pw : printWriters) {
				pw.print(sbuff.toString());
			}
			for(PrintWriter pw : printWriters) {
				pw.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug("StreamListener: end of run()");
	}

	public synchronized void close() {
		killed = true;
	}
}

/*
 private static Logger logger = Logger.getLogger(StreamListener.class);

	private Vector<PrintWriter> printWriters;
	private Vector<String> prompts;

	private BufferedReader bufferedReader = null;
	
	private boolean killed = false;

	public StreamListener(InputStream inputStream) {
		printWriters = new Vector<PrintWriter>();
		prompts = new Vector<String>();
		InputStreamReader isr = new InputStreamReader(inputStream);
		bufferedReader = new BufferedReader(isr);
	}

	public void registerOutputStream(String prompt, OutputStream outputStream) {
		prompts.add(prompt);
		printWriters.add(new PrintWriter(outputStream));
	}

	public void run() {
		try {
			while(! killed) {
				if (bufferedReader.ready()) {
					int line = bufferedReader.read();
					for(int i = 0; i < printWriters.size(); i++) {
						PrintWriter pw = printWriters.get(i);
						pw.print(Character.toChars(line));
					}
				} else {
					Thread.sleep(1000);
				}
			}
			for(PrintWriter pw : printWriters) {
				pw.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug("StreamListener: end of run()");
	}

	public synchronized void close() {
		killed = true;
	}
	*/
