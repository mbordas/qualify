package qualify.test;

import java.io.File;

import qualify.TestCase;
import qualify.tools.TestToolFile;
import qualify.tools.TestToolNumbers;
import qualify.tools.TestToolStrings;
import qualify.tools.TestToolXML;

public class TC_TestCasesTable extends TestCase {
	
	ProjectManager project = new ProjectManager(this);

	TestToolFile files = new TestToolFile(this);
	TestToolXML xml = new TestToolXML(this);
	TestToolStrings strings = new TestToolStrings(this);
	TestToolNumbers numbers = new TestToolNumbers(this);

	@Override
	public void run() {
		project.compileAndRunQualifyProject("UT_Project_001");
		
		File testCasesTable = new File("test_files/UT_Project_001/result/test_cases_table.xml");
		attachFile(testCasesTable);
		xml.parseFile(testCasesTable, "test_cases_table.xml");
		
		setRequirementTarget("Export : Test cases table");
		need(xml.getNode("test_cases_table.xml", "test_cases/test_case[@name='Test2']/@test_source") != null);
		strings.checkEquality("Test2/Test2.xml", xml.getNode("test_cases_table.xml", "test_cases/test_case[@name='Test2']/@test_source").getTextContent());
		need(xml.getNode("test_cases_table.xml", "test_cases/test_case[@name='Test3']/@test_source") != null);
		strings.checkEquality("Test3/Test3.xml", xml.getNode("test_cases_table.xml", "test_cases/test_case[@name='Test3']/@test_source").getTextContent());
		need(xml.getNode("test_cases_table.xml", "test_cases/test_case[@name='Test']/@test_source") != null);
		strings.checkEquality("Test/Test.xml", xml.getNode("test_cases_table.xml", "test_cases/test_case[@name='Test']/@test_source").getTextContent());
	}

}
