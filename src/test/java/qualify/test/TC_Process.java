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
import qualify.testRunner.TProcess;
import qualify.tools.TestToolDates;
import qualify.tools.TestToolFile;
import qualify.tools.TestToolProcess;

public class TC_Process extends TestCase {

	TestToolProcess process = new TestToolProcess(this);
	TestToolDates dates = new TestToolDates(this);

	@Override
	public void run() {
		setRequirementTarget("TestToolProcess");
		
		try {
			TestToolFile.emptyDir("./test_files/FakeProject_01/out/");
		} catch (IOException e) {
			need(false);
			e.printStackTrace();
		}
		
		ProjectManager project = new ProjectManager(this);
		project.addToClasspath(new File("./target"));

		project.runBat("FakeProject_01", "compile.bat");

		project.addToClasspath(new File("./test_files/FakeProject_01/bin"));

		TProcess run = project.runJava("Main", null, new File("./test_files/FakeProject_01"), null);
		pause(2.0);
		run.stop();
		
		pause(2.0);

		check(TestToolFile.exists("./test_files/FakeProject_01/out/TICK_0.tic"));
		check(! TestToolFile.exists("./test_files/FakeProject_01/out/TICK_2.tic"));

	}

}
