package qualify.test;

import java.io.File;

import qualify.TestCase;
import qualify.tools.TestToolFile;
import qualify.tools.TestToolNumbers;
import qualify.tools.TestToolStrings;
import qualify.tools.TestToolXML;

public class TC_BeforeRunAfter extends TestCase {

	TestToolFile files = new TestToolFile(this);
	TestToolXML xml = new TestToolXML(this);
	TestToolStrings strings = new TestToolStrings(this);
	TestToolNumbers numbers = new TestToolNumbers(this);

	@Override
	public void run() {		
		setRequirementTarget("beforeRun, afterRun");
		ProjectManager project = new ProjectManager(this);
		int exitCode = project.runBat("TC_BeforeRunAfter");
		numbers.checkEquality(0, exitCode);
		
		check(TestToolFile.find(project.runLogFile, "beforeRun is called for Test: Test1"));
		check(TestToolFile.find(project.runLogFile, "I'm in the script 1"));
		check(TestToolFile.find(project.runLogFile, "afterRun is called for Test: Test1"));
		
		check(TestToolFile.find(project.runLogFile, "beforeRun is called for Test: Test2"));
		check(! TestToolFile.find(project.runLogFile, "I'm in the script 2"));
		check(! TestToolFile.find(project.runLogFile, "afterRun is called for Test: Test2"));
	}

}
