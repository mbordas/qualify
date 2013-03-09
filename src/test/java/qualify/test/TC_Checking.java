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

package qualify.test;

import java.io.File;
import java.io.IOException;

import qualify.TestCase;
import qualify.tools.TestToolFile;
import qualify.tools.TestToolNumbers;
import qualify.tools.TestToolStrings;
import qualify.tools.TestToolXML;

public class TC_Checking extends TestCase {

	ProjectManager project = new ProjectManager(this);

	TestToolFile files = new TestToolFile(this);
	TestToolXML xml = new TestToolXML(this);
	TestToolStrings strings = new TestToolStrings(this);
	TestToolNumbers numbers = new TestToolNumbers(this);

	@Override
	public void run() {

		try {
			TestToolFile.emptyDir("test_files/UT_Project_001/bin");
		} catch (IOException e) {
			e.printStackTrace();
		}
		project.runBat("UT_Project_001", "compile.bat");
		project.runBat("UT_Project_001");

		setRequirementTarget("Export : Test source");

		xml.parseFile(new File("test_files/UT_Project_001/result/test_source/Test.xml"), "Test.xml");
		xml.parseFile(new File("test_files/UT_Project_001/result/test_source/Test2.xml"), "Test2.xml");
		xml.parseFile(new File("test_files/UT_Project_001/result/test_source/Test3.xml"), "Test3.xml");
		
		setRequirementTarget("NOK check");
		strings.checkEquality("result_nok", xml.getNode("Test2.xml", "source/step/source_line[@line_number='9']/@type").getTextContent());
		strings.checkEquality("result_nok", xml.getNode("Test3.xml", "source/step/source_line[@line_number='9']/@type").getTextContent());
		
		setRequirementTarget("OK check");
		strings.checkEquality("result_ok", xml.getNode("Test3.xml", "source/step/source_line[@line_number='10']/@type").getTextContent());

	}

}
