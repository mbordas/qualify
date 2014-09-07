package qualify.test;

import java.io.File;
import java.io.IOException;

import qualify.TestCase;
import qualify.tools.TestToolFile;
import qualify.tools.TestToolNumbers;
import qualify.tools.TestToolStrings;
import qualify.tools.TestToolXML;

public class TC_SanityTesting extends TestCase {

	TestToolFile files = new TestToolFile(this);
	TestToolXML xml = new TestToolXML(this);
	TestToolStrings strings = new TestToolStrings(this);
	TestToolNumbers numbers = new TestToolNumbers(this);

	@Override
	public void run() {
		checkSanityTestIsRunningFirst();
		checkSanityTestMustBeSuccessful();
		checkSanityScripts();
	}

	private void checkSanityTestIsRunningFirst() {
		step("Sanity test is running first");
		setRequirementTarget("Compilation");
		try {
			TestToolFile.deleteFile("test_files/UT_Project_003/src/Harness.java");
			TestToolFile.copyFile("test_files/UT_Project_003/src/INPUT_HarnessOK.java", "test_files/UT_Project_003/src/Harness.java");
		} catch (IOException e) {
			e.printStackTrace();
			need(false);
		}

		ProjectManager project = new ProjectManager(this);
		int exitCode = project.compileAndRunQualifyProject("UT_Project_003");
		numbers.checkEquality(0, exitCode);

		setRequirementTarget("Sanity testing");
		File result = new File("test_files/UT_Project_003/result/result.xml");

		files.checkFind(result, "test_case name=\"SanityTestOK\""); // SanityTestOK  runs OK
		files.checkNotFound(result, "test_case name=\"Test2\""); // Test2 is not called
		files.checkFind(result, "test_case name=\"Test3\""); // Test3 is called by option 'test'
		
		setRequirementTarget("Export : Test source");
		files.checkExists("test_files/UT_Project_003/result/test_source/SanityTestOK.xml");
		files.checkExists("test_files/UT_Project_003/result/test_source/Test2.xml");
		files.checkExists("test_files/UT_Project_003/result/test_source/Test3.xml");
	}

	private void checkSanityTestMustBeSuccessful() {
		step("Sanity test must be successful");
		setRequirementTarget("Compilation");
		try {
			TestToolFile.deleteFile("test_files/UT_Project_003/src/Harness.java");
			TestToolFile.copyFile("test_files/UT_Project_003/src/INPUT_HarnessNOK.java", "test_files/UT_Project_003/src/Harness.java");
		} catch (IOException e) {
			need(false);
			e.printStackTrace();
		}

		ProjectManager project = new ProjectManager(this);
		int exitCode = project.compileAndRunQualifyProject("UT_Project_003");
		numbers.checkEquality(1, exitCode);

		setRequirementTarget("Sanity testing");
		File result = new File("test_files/UT_Project_003/result/result.xml");

		files.checkFind(result, "test_case name=\"SanityTestNOK\""); // SanityTestNOK runs NOK
		files.checkNotFound(result, "test_case name=\"Test2\""); // Test2 is not called
		files.checkNotFound(result, "test_case name=\"Test3\""); // Test3 is called by option 'test'
		
		setRequirementTarget("Export : Test source");
		files.checkExists("test_files/UT_Project_003/result/test_source/SanityTestNOK.xml");
		files.checkExists("test_files/UT_Project_003/result/test_source/Test2.xml");
		files.checkExists("test_files/UT_Project_003/result/test_source/Test3.xml");
	}
	
	private void checkSanityScripts() {
		step("Sanity test by keyword");
		setRequirementTarget("run by keyword");
		
		try {
			TestToolFile.deleteDir("test_files/TC_ScriptTestCase_05_Sanity/result");
			
			ProjectManager project = new ProjectManager(this);
			int exitCode = project.runBat("TC_ScriptTestCase_05_Sanity");
			numbers.checkEquality(1, exitCode); // sanity test fails
			
			File log = project.getLogFile();
			files.checkFind(log, "SANITY TEST STARTS: SanityTest");
			
		} catch (IOException e) {
			check(false);
			e.printStackTrace();
		}
	}

}
