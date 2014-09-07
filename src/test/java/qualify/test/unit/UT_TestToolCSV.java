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

import java.io.File;
import java.io.IOException;

import qualify.TestCase;
import qualify.tools.TestToolCSV;

public class UT_TestToolCSV extends TestCase {

	@Override
	public void run() {
		TestToolCSV csv = new TestToolCSV(this, ";");
		
		setRequirementTarget("TestToolCSV");
		
		File f = new File("test_files/UT_TestToolCSV/sample.csv");
		need(f.exists());
		try {
			check(csv.getCSVLineNumber(f, 1, "A") == 1);
			check(csv.getCSVLineNumber(f, 1, "B") == 2);
			check(csv.getCSVLineNumber(f, 1, "C") == 3);
			check(csv.getCSVLineNumber(f, 1, "D") == 4);
			check(csv.getCSVLineNumber(f, 1, "E") == 0);
			
			check(csv.getCSVLineNumber(f, 2, "Albert") == 1);
			check(csv.getCSVLineNumber(f, 2, "Brandy") == 2);
			check(csv.getCSVLineNumber(f, 2, "Carl") == 3);
			check(csv.getCSVLineNumber(f, 2, "Diego") == 4);
			check(csv.getCSVLineNumber(f, 2, "Emma") == 0);
			
			check(csv.getCSVLineNumber(f, 3, "a") == 1);
			check(csv.getCSVLineNumber(f, 3, "b") == 2);
			check(csv.getCSVLineNumber(f, 3, "c") == 3);
			check(csv.getCSVLineNumber(f, 3, "d") == 4);
			check(csv.getCSVLineNumber(f, 3, "e") == 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
