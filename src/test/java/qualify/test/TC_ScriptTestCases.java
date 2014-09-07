package qualify.test;

import java.io.File;
import java.io.IOException;

import qualify.TestCase;
import qualify.tools.TestToolFile;
import qualify.tools.TestToolNumbers;
import qualify.tools.TestToolStrings;
import qualify.tools.TestToolXML;

public class TC_ScriptTestCases extends TestCase {
	
	TestToolFile files = new TestToolFile(this);
	TestToolXML xml = new TestToolXML(this);
	TestToolStrings strings = new TestToolStrings(this);
	TestToolNumbers numbers = new TestToolNumbers(this);

	@Override
	public void run() {
		setRequirementTarget("Mix compiled / scripted");
		ProjectManager project = new ProjectManager(this);
		int exitCode = project.compileAndRunQualifyProject("UT_ScriptTestCase_01");
		numbers.checkEquality(0, exitCode);

		File result = new File("test_files/UT_ScriptTestCase_01/result/result.xml");

		files.checkFind(result, "test_case name=\"Test\"");
		files.checkFind(result, "test_case name=\"ScriptTestCase\"");
		
		setRequirementTarget("Export : Test source");
		files.checkExists("test_files/UT_ScriptTestCase_01/result/test_source/Test.xml");
		files.checkExists("test_files/UT_ScriptTestCase_01/result/test_source/ScriptTestCase.xml");
		files.checkExists("test_files/UT_ScriptTestCase_01/result/test_source/ScriptTestCase2.xml");
		files.checkDoesntExist("test_files/UT_ScriptTestCase_01/result/test_source/ScriptTestCase3.xml");
		
		setRequirementTarget("Full scripted");
		exitCode = project.runBat("UT_ScriptTestCase_02");
		numbers.checkEquality(0, exitCode);
		files.checkExists("test_files/UT_ScriptTestCase_02/result/test_source/SanityScript.xml");
		files.checkExists("test_files/UT_ScriptTestCase_02/result/test_source/ScriptTestCase.xml");
		files.checkExists("test_files/UT_ScriptTestCase_02/result/test_source/ScriptTestCase2.xml");
		
		setRequirementTarget("Keywords: requirement");
		xml.parseFile("test_files/UT_ScriptTestCase_02/result/test_source/SanityScript.xml", "SanityScript.xml");
		strings.checkEquality("Sanity", xml.getAttributeValueAsString("SanityScript.xml", "source/step[1]/source_line[@line_number='12']/@requirement"));
		
		setRequirementTarget("Scripted dependencies");
		try {
			TestToolFile.deleteDir("test_files/UT_ScriptTestCase_04/dependencies/bin");
		} catch (IOException e) {
			e.printStackTrace();
			need(false);
		}
		exitCode = project.runBat("UT_ScriptTestCase_04", "run.bat");
		numbers.checkEquality(0, exitCode);
	}

}
