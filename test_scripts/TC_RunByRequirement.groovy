package qualify.test;

import java.io.File;

import qualify.TestCase;
import qualify.tools.TestToolFile;
import qualify.tools.TestToolNumbers;
import qualify.tools.TestToolStrings;
import qualify.tools.TestToolXML;

public class TC_RunByRequirement extends TestCase {

	TestToolFile files = new TestToolFile(this);
	TestToolXML xml = new TestToolXML(this);
	TestToolStrings strings = new TestToolStrings(this);
	TestToolNumbers numbers = new TestToolNumbers(this);

	@Override
	public void run() {
		TestToolFile.deleteDir("test_files/TC_RunByRequirement/rst");
		setRequirementTarget("run by requirement");
		ProjectManager project = new ProjectManager(this);
		try {
			TestToolFile.copyFile("test_files/TC_RunByRequirement/options_1.xml", "test_files/TC_RunByRequirement/options.xml");
			int exitCode = project.runBat("TC_RunByRequirement");
			numbers.checkEquality(0, exitCode);
			
			files.checkExists("test_files/TC_RunByRequirement/rst/reference_release_note.xml");
			files.checkExists("test_files/TC_RunByRequirement/rst/Test1/Test1.xml");
			files.checkExists("test_files/TC_RunByRequirement/rst/Test2/Test2.xml");
			
			files.deleteFile("test_files/TC_RunByRequirement/rst/Test1/Test1.xml");
			files.deleteFile("test_files/TC_RunByRequirement/rst/Test2/Test2.xml");
			
			TestToolFile.copyFile("test_files/TC_RunByRequirement/options_2.xml", "test_files/TC_RunByRequirement/options.xml");
			exitCode = project.runBat("TC_RunByRequirement");
			numbers.checkEquality(1, exitCode); // 1 regression should be found: Requirement 2
			
			files.checkExists("test_files/TC_RunByRequirement/rst/Test1/Test1.xml");
			files.checkExists("test_files/TC_RunByRequirement/rst/Test2/Test2.xml");
			// Test2 should not run. Its source is exported, but without no result:
			files.checkNotFound(new File("test_files/TC_RunByRequirement/rst/Test2/Test2.xml"), "result_ok");
			files.checkNotFound(new File("test_files/TC_RunByRequirement/rst/Test2/Test2.xml"), "result_nok");
			
		} catch (IOException e) {
			check(false);
			e.printStackTrace();
		}
	}

}
