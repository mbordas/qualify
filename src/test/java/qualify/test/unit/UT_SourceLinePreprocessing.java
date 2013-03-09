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

package qualify.test.unit;

import java.util.LinkedList;

import qualify.TestCase;
import qualify.doc.SourceLine;
import qualify.tools.TestToolNumbers;
import qualify.tools.TestToolStrings;

public class UT_SourceLinePreprocessing extends TestCase {
	
	TestToolStrings strings = new TestToolStrings(this);
	TestToolNumbers numbers = new TestToolNumbers(this);

	@Override
	public void run() {
		
		setRequirementTarget("SourceLine preprocessing");
		
		LinkedList<SourceLine> lines = new LinkedList<SourceLine>();
		for(int i = 1; i < 10; i++) {
			lines.add(new SourceLine(lines, i, "line number " + i));
		}
		
		SourceLine line1 = lines.get(0);
		// getText()
		strings.checkEquality("line number 1", line1.getText());
		// replaceAll(regex, replacement)
		line1.replaceAll("number", "nb");
		strings.checkEquality("line nb 1", line1.getText());
		// append(text)
		line1.append(" end");
		strings.checkEquality("line nb 1 end", line1.getText());
		// contains
		check(line1.contains("line nb"));
		check(! line1.contains("line number"));
		// next()
		strings.checkEquality("line number 2", line1.next().getText());
		check(lines.getLast().next() == null);
		// prec()
		check(line1.prec() == null);
		strings.checkEquality("line number 8", lines.getLast().prec().getText());
		// insertBefore()
		SourceLine newLine = line1.insertBefore();
		check(newLine.next() == line1);
		numbers.checkEquality(1, newLine.getMappedLineNumber());
		numbers.checkEquality(1, line1.getMappedLineNumber());
		numbers.checkEquality(2, line1.next().getMappedLineNumber());
		numbers.checkEquality(10, lines.size());
		
		// remove()
		line1.remove();
		numbers.checkEquality(9, lines.size());
	}

}
