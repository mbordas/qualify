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

package qualify.doc;

import java.util.LinkedList;

public class SourceLine {

	private int mappedLineNumber = 0;
	private String text;

	private LinkedList<SourceLine> lines = null;

	public SourceLine(LinkedList<SourceLine> lines, int lineNumber, String text) {
		this.mappedLineNumber = lineNumber;
		this.text = text;
		this.lines = lines;
	}

	public void setText(String newText) {
		text = newText;
	}

	public String getText() {
		return text;
	}

	public int getMappedLineNumber() {
		return mappedLineNumber;
	}
	
	public int getMappedLineNumber(int lineIndex) {
		return lines.get(lineIndex).getMappedLineNumber();
	}

	public void replaceAll(String regex, String replacement) {
		text = text.replaceAll(regex, replacement);
	}
	
	public void replaceFirst(String regex, String replacement) {
		text = text.replaceFirst(regex, replacement);
	}
	
	public boolean startsWith(String string) {
		return text.startsWith(string);
	}

	public void append(String appendix) {
		text = text + appendix;
	}
	
	public void insert(String beginning) {
		text = beginning + text;
	}

	public boolean contains(String subString) {
		return text.contains(subString);
	}

	public void remove() {
		lines.remove(this);
	}
	
	public void comment() {
		insert("//");
	}

	public SourceLine prec() {
		SourceLine precLine = null;
		for(SourceLine line : lines) {
			if(line == this) {
				return precLine;
			}
			precLine = line;
		}
		return null;
	}

	public SourceLine next() {
		boolean found = false;
		for(SourceLine line : lines) {
			if(found) {
				return line;
			}
			if(line == this) {
				found = true;
			}
		}
		return null;
	}

	public SourceLine insertBefore() {
		SourceLine result = new SourceLine(lines, mappedLineNumber, "");
		lines.add(getIndex(), result);
		return result;
	}
	
	public SourceLine insertAfter() {
		SourceLine result = new SourceLine(lines, mappedLineNumber, "");
		lines.add(getIndex() + 1, result);
		return result;
	}

	private int getIndex() {
		for(int i = 0; i < lines.size(); i++) {
			SourceLine currentLine = lines.get(i);
			if(currentLine == this) {
				return i;
			}
		}
		return -1;
	}

}
