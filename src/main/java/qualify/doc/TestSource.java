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

package qualify.doc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

public class TestSource {
	
	static Logger logger = Logger.getLogger(TestSource.class);
	
	private File originalFile = null;
	/**
	 * If Preprocessor is used to process the source, processed lines are stored here.
	 */
	private LinkedList<SourceLine> preprocessedLines = null;
	
	public TestSource(File file) {
		originalFile = file;
		loadSourceLines();
	}

	public static TestSource createTestSource(File originalFile) {
		if(originalFile.exists()) {
			if(originalFile.isFile()) {
				return new TestSource(originalFile);
			} else {
				logger.info("test source is not a file");
				return null;
			}
		} else {
			logger.info("test source does not exist: " + originalFile.getAbsolutePath());
			return null;
		}
	}
	
	public File getOriginalFile() {
		return this.originalFile;
	}
	
	public List<SourceLine> getLines() {
		return preprocessedLines;
	}

	public void loadSourceLines() {
		if(preprocessedLines == null) {
			preprocessedLines = new LinkedList<SourceLine>();
			if((originalFile != null) && (originalFile.exists())) {
				InputStream ips;
				try {
					ips = new FileInputStream(originalFile);
					InputStreamReader ipsr = new InputStreamReader(ips);
					BufferedReader br = new BufferedReader(ipsr);
					String line;
					int lineNumber = 1;
					while ((line = br.readLine()) != null){
						preprocessedLines.add(new SourceLine(preprocessedLines, lineNumber, line));
						lineNumber++;
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		}
	}
	
	public int getMappedLineNumber(int lineNumber) {
		int result = lineNumber;
		if((preprocessedLines != null) && (lineNumber > 0) && (lineNumber < preprocessedLines.size())) {
			result = preprocessedLines.get(lineNumber - 1).getMappedLineNumber();
		}
		return result;
	}
	
	public void exportProcessedFile(File outputFile) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));
			for(SourceLine line : getLines()) {
				out.write(line.getText() + "\n");
			}
			out.close();
		} catch (IOException e) {
		}
	}

}
