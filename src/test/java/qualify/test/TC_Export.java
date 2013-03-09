package qualify.test;

import java.io.File;
import java.io.IOException;

import qualify.TestCase;
import qualify.tools.TestToolFile;
import qualify.tools.TestToolNumbers;
import qualify.tools.TestToolStrings;
import qualify.tools.TestToolXML;

public class TC_Export extends TestCase {

	TestToolFile files = new TestToolFile(this);
	TestToolXML xml = new TestToolXML(this);
	TestToolStrings strings = new TestToolStrings(this);
	TestToolNumbers numbers = new TestToolNumbers(this);

	@Override
	public void run() {
		ProjectManager project = new ProjectManager(this);
		
		try {
			TestToolFile.emptyDir("test_files/UT_Project_001/bin");
		} catch (IOException e) {
			e.printStackTrace();
		}
		project.runBat("UT_Project_001", "compile.bat");
		project.runBat("UT_Project_001");
		
		setRequirementTarget("Export : Release note");
		attachFile(new File("test_files/UT_Project_001/result/result.xml"));
		files.checkExists(new File("test_files/UT_Project_001/result/result.xml"));
		files.checkExists(new File("test_files/UT_Project_001/result/style/jquery.js"));
		files.checkExists(new File("test_files/UT_Project_001/result/style/jquery.tools.min.js"));
		files.checkExists(new File("test_files/UT_Project_001/result/style/jquery.treeTable.css"));
		files.checkExists(new File("test_files/UT_Project_001/result/style/jquery.treeTable.js"));
		files.checkExists(new File("test_files/UT_Project_001/result/style/jquery.ui.js"));
		files.checkExists(new File("test_files/UT_Project_001/result/style/release_note.xsl"));
		
		setRequirementTarget("Export : Test cases table");
		files.checkExists(new File("test_files/UT_Project_001/result/test_cases_table.xml"));
		files.checkExists(new File("test_files/UT_Project_001/result/style/jquery.js"));
		files.checkExists(new File("test_files/UT_Project_001/result/style/jquery.tools.min.js"));
		files.checkExists(new File("test_files/UT_Project_001/result/style/jquery.treeTable.css"));
		files.checkExists(new File("test_files/UT_Project_001/result/style/jquery.treeTable.js"));
		files.checkExists(new File("test_files/UT_Project_001/result/style/jquery.ui.js"));
		files.checkExists(new File("test_files/UT_Project_001/result/style/test_cases_table.xsl"));
		
		setRequirementTarget("Export : Test source");
		files.checkExists(new File("test_files/UT_Project_001/result/Test/Test.xml"));
		files.checkExists(new File("test_files/UT_Project_001/result/Test2/Test2.xml"));
		files.checkExists(new File("test_files/UT_Project_001/result/Test3/Test3.xml"));
		files.checkExists(new File("test_files/UT_Project_001/result/Test4/Test4.xml"));
		files.checkExists(new File("test_files/UT_Project_001/result/ScriptTestCase/ScriptTestCase.xml"));
		
		files.checkExists(new File("test_files/UT_Project_001/result/style/jquery.js"));
		files.checkExists(new File("test_files/UT_Project_001/result/style/jquery.tools.min.js"));
		files.checkExists(new File("test_files/UT_Project_001/result/style/jquery.treeTable.css"));
		files.checkExists(new File("test_files/UT_Project_001/result/style/jquery.treeTable.js"));
		files.checkExists(new File("test_files/UT_Project_001/result/style/jquery.ui.js"));
		files.checkExists(new File("test_files/UT_Project_001/result/style/test_cases_table.xsl"));
		
		xml.parseFile(new File("test_files/UT_Project_001/result/Test/Test.xml"), "Test.xml");
		strings.checkEquality("main", xml.getAttributeValueAsString("Test.xml", "source/step[1]/@title"));
		
		xml.parseFile(new File("test_files/UT_Project_001/result/Test4/Test4.xml"), "Test4.xml");
		strings.checkEquality("start", xml.getAttributeValueAsString("Test4.xml", "source/step[1]/@title"));
		strings.checkEquality("first step", xml.getAttributeValueAsString("Test4.xml", "source/step[2]/@title"));
		strings.checkEquality("second step", xml.getAttributeValueAsString("Test4.xml", "source/step[3]/@title"));
		strings.checkEquality("third step", xml.getAttributeValueAsString("Test4.xml", "source/step[4]/@title"));
		strings.checkEquality("fourth step", xml.getAttributeValueAsString("Test4.xml", "source/step[5]/@title"));
		
		xml.parseFile(new File("test_files/UT_Project_001/result/ScriptTestCase/ScriptTestCase.xml"), "ScriptTestCase.xml");
		strings.checkEquality("start", xml.getAttributeValueAsString("ScriptTestCase.xml", "source/step[1]/@title"));
		strings.checkEquality("first step", xml.getAttributeValueAsString("ScriptTestCase.xml", "source/step[2]/@title"));
		strings.checkEquality("second step", xml.getAttributeValueAsString("ScriptTestCase.xml", "source/step[3]/@title"));
	}
}
