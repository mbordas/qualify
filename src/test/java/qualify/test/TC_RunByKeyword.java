package qualify.test;

import java.io.File;
import java.io.IOException;

import qualify.TestCase;
import qualify.tools.TestToolFile;
import qualify.tools.TestToolNumbers;
import qualify.tools.TestToolStrings;
import qualify.tools.TestToolXML;

public class TC_RunByKeyword extends TestCase {
	
	TestToolFile files = new TestToolFile(this);
	TestToolXML xml = new TestToolXML(this);
	TestToolStrings strings = new TestToolStrings(this);
	TestToolNumbers numbers = new TestToolNumbers(this);

	@Override
	public void run() {
		setRequirementTarget("run by keyword");
		
		try {
			TestToolFile.deleteDir("test_files/TC_RunByKeyword/rst");
			
			ProjectManager project = new ProjectManager(this);
			int exitCode = project.runBat("TC_RunByKeyword");
			numbers.checkEquality(0, exitCode);
			
			File log = project.getLogFile();
			files.checkFind(log, "TEST CASE STARTS: Test1");
			files.checkNotFound(log, "TEST CASE STARTS: Test2");
			files.checkFind(log, "TEST CASE STARTS: Test3");
			
		} catch (IOException e) {
			check(false);
			e.printStackTrace();
		}
	}

}
