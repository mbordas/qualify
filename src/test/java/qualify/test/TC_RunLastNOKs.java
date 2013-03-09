package qualify.test;

import java.io.File;
import java.io.IOException;

import qualify.TestCase;
import qualify.tools.TestToolFile;
import qualify.tools.TestToolNumbers;

public class TC_RunLastNOKs extends TestCase {
	
	TestToolNumbers numbers = new TestToolNumbers(this);
	TestToolFile files = new TestToolFile(this);

	@Override
	public void run() {
		setRequirementTarget("run last NOK");

		ProjectManager project = new ProjectManager(this);
		
		File releaseNoteFile = new File("test_files/TC_ScriptTestCase_06_LastNOK/result/release_note.xml");

		step("First run");
		//  ScriptTestCase => OK
		//  ScriptTestCase2 => NOK
		//  ScriptTestCase3 => NOK
		
		try {
			TestToolFile.copyFile("test_files/TC_ScriptTestCase_06_LastNOK/options_no_option.xml",
					"test_files/TC_ScriptTestCase_06_LastNOK/options.xml");
			TestToolFile.deleteDir("test_files/TC_ScriptTestCase_06_LastNOK/result");
			TestToolFile.copyFile(
					"test_files/TC_ScriptTestCase_06_LastNOK/script/ScriptTestCase1.groovy_OK",
					"test_files/TC_ScriptTestCase_06_LastNOK/script/ScriptTestCase1.groovy");
			TestToolFile.copyFile(
					"test_files/TC_ScriptTestCase_06_LastNOK/script/ScriptTestCase2.groovy_NOK",
					"test_files/TC_ScriptTestCase_06_LastNOK/script/ScriptTestCase2.groovy");
			TestToolFile.copyFile(
					"test_files/TC_ScriptTestCase_06_LastNOK/script/ScriptTestCase3.groovy_NOK",
					"test_files/TC_ScriptTestCase_06_LastNOK/script/ScriptTestCase3.groovy");
		} catch (IOException e) {
			e.printStackTrace();
			need(false);
		}

		int exitCode = project.runBat("TC_ScriptTestCase_06_LastNOK");
		numbers.checkEquality(0, exitCode);
		
		files.checkFind(project.getLogFile(), "TEST CASE STARTS: ScriptTestCase1");
		files.checkFind(project.getLogFile(), "TEST CASE STARTS: ScriptTestCase2");
		files.checkFind(project.getLogFile(), "TEST CASE STARTS: ScriptTestCase3");
		
		files.checkExists(releaseNoteFile);
		attachFile(releaseNoteFile);
		
		TestToolFile.deleteFile(project.getLogFile());
		
		
		step("Second run");
		// preparing tests:
		//  ScriptTestCase => OK
		//  ScriptTestCase2 => OK
		//  ScriptTestCase3 => NOK
		try {
			TestToolFile.copyFile("test_files/TC_ScriptTestCase_06_LastNOK/options_last_failed.xml",
			"test_files/TC_ScriptTestCase_06_LastNOK/options.xml");
			TestToolFile.copyFile(
					"test_files/TC_ScriptTestCase_06_LastNOK/script/ScriptTestCase2.groovy_OK",
					"test_files/TC_ScriptTestCase_06_LastNOK/script/ScriptTestCase2.groovy");
			TestToolFile.copyFile(
					"test_files/TC_ScriptTestCase_06_LastNOK/script/ScriptTestCase3.groovy_NOK",
					"test_files/TC_ScriptTestCase_06_LastNOK/script/ScriptTestCase3.groovy");
		} catch (IOException e) {
			e.printStackTrace();
			need(false);
		}
		
		exitCode = project.runBat("TC_ScriptTestCase_06_LastNOK");
		numbers.checkEquality(0, exitCode);
		
		attachFile(releaseNoteFile);
		
		files.checkNotFound(project.getLogFile(), "TEST CASE STARTS: ScriptTestCase1");
		files.checkFind(project.getLogFile(), "TEST CASE STARTS: ScriptTestCase2");
		files.checkFind(project.getLogFile(), "TEST CASE STARTS: ScriptTestCase3");
		
		TestToolFile.deleteFile(project.getLogFile());
		
		
		step("Third run");
		// preparing tests:
		//  ScriptTestCase => OK
		//  ScriptTestCase2 => OK
		//  ScriptTestCase3 => OK
		try {
			TestToolFile.copyFile(
					"test_files/TC_ScriptTestCase_06_LastNOK/script/ScriptTestCase3.groovy_OK",
					"test_files/TC_ScriptTestCase_06_LastNOK/script/ScriptTestCase3.groovy");
		} catch (IOException e) {
			e.printStackTrace();
			need(false);
		}
		
		exitCode = project.runBat("TC_ScriptTestCase_06_LastNOK");
		numbers.checkEquality(0, exitCode);
		
		attachFile(releaseNoteFile);
		
		files.checkNotFound(project.getLogFile(), "TEST CASE STARTS: ScriptTestCase1");
		files.checkNotFound(project.getLogFile(), "TEST CASE STARTS: ScriptTestCase2");
		files.checkFind(project.getLogFile(), "TEST CASE STARTS: ScriptTestCase3");
		
		TestToolFile.deleteFile(project.getLogFile());
		
		
		step("Fourth run");
		exitCode = project.runBat("TC_ScriptTestCase_06_LastNOK");
		numbers.checkEquality(0, exitCode);
		
		attachFile(releaseNoteFile);
		
		files.checkNotFound(project.getLogFile(), "TEST CASE STARTS: ScriptTestCase1");
		files.checkNotFound(project.getLogFile(), "TEST CASE STARTS: ScriptTestCase2");
		files.checkNotFound(project.getLogFile(), "TEST CASE STARTS: ScriptTestCase3");
	}

}
