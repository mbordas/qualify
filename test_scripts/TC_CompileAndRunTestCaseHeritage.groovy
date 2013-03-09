package qualify.test;

import java.io.File;

import qualify.TestCase;
import qualify.tools.TestToolFile;
import qualify.tools.TestToolNumbers;
import qualify.tools.TestToolStrings;
import qualify.tools.TestToolXML;

public class TC_CompileAndRunTestCaseHeritage extends TestCase {

	TestToolFile files = new TestToolFile(this);
	TestToolXML xml = new TestToolXML(this);
	TestToolStrings strings = new TestToolStrings(this);
	TestToolNumbers numbers = new TestToolNumbers(this);

	@Override
	public void run() {		
		setRequirementTarget("Scripted dependencies");
		ProjectManager project = new ProjectManager(this);
		int exitCode = project.runBat("UT_Compilation_01");
		numbers.checkEquality(0, exitCode);
	}

}
