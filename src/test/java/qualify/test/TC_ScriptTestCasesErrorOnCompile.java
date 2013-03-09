package qualify.test;

import qualify.TestCase;
import qualify.tools.TestToolFile;
import qualify.tools.TestToolNumbers;
import qualify.tools.TestToolStrings;
import qualify.tools.TestToolXML;

public class TC_ScriptTestCasesErrorOnCompile extends TestCase {
	
	TestToolFile files = new TestToolFile(this);
	TestToolXML xml = new TestToolXML(this);
	TestToolStrings strings = new TestToolStrings(this);
	TestToolNumbers numbers = new TestToolNumbers(this);

	@Override
	public void run() {
		setRequirementTarget("Dynamic compilation");
		ProjectManager project = new ProjectManager(this);
		int exitCode = project.compileAndRunQualifyProject("UT_ScriptTestCase_03");
		numbers.checkEquality(1, exitCode);
	}

}
