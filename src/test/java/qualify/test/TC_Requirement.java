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

public class TC_Requirement extends TestCase {
	
	ProjectManager project = new ProjectManager(this);

	TestToolFile files = new TestToolFile(this);
	TestToolXML xml = new TestToolXML(this);
	TestToolStrings strings = new TestToolStrings(this);
	TestToolNumbers numbers = new TestToolNumbers(this);

	@Override
	public void run() {
		try {
			TestToolFile.copyFile("test_files/UT_Project_004/options_select_by_requirement.xml", "test_files/UT_Project_004/options.xml");
		} catch (IOException e) {
			e.printStackTrace();
			need(false);
		}
		project.compileAndRunQualifyProject("UT_Project_004");
		
		File result = new File("test_files/UT_Project_004/result/result.xml");
		setRequirementTarget("Export : Release note");
		need(TestToolFile.exists(result));
		
		setRequirementTarget("Requirement without test");
		xml.parseFile(result, "result.xml");
		
		check(xml.getNode("result.xml", "release_note/requirement[@id='REQ-0']") != null);
		strings.checkEquality("NOK", xml.getNode("result.xml", "release_note/requirement[@id='REQ-0']/@result").getTextContent());
		
		check(xml.getNode("result.xml", "release_note/requirement[@id='REQ-1']") != null);
		strings.checkEquality("NOK", xml.getNode("result.xml", "release_note/requirement[@id='REQ-1']/@result").getTextContent());
		
		setRequirementTarget("Inline requirement overriding");
		check(xml.getNode("result.xml", "release_note/requirement[@id='REQ-2']") != null);
		check(xml.getNode("result.xml", "release_note/requirement[@id='REQ-3']") != null);
		check(xml.getNode("result.xml", "release_note/requirement[@id='REQ-4']") != null);
		check(xml.getNode("result.xml", "release_note/requirement[@id='REQ-5']") != null);
		
		setRequirementTarget("Requirement by keyword");
		check(xml.getNode("result.xml", "release_note/requirement[@id='1st requirement (001)']") != null);
	}

}
