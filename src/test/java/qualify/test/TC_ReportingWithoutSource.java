package qualify.test;

import java.io.File;

import qualify.TestCase;
import qualify.tools.TestToolFile;
import qualify.tools.TestToolNumbers;
import qualify.tools.TestToolStrings;
import qualify.tools.TestToolXML;

public class TC_ReportingWithoutSource extends TestCase {
	
	ProjectManager project = new ProjectManager(this);

	TestToolFile files = new TestToolFile(this);
	TestToolXML xml = new TestToolXML(this);
	TestToolStrings strings = new TestToolStrings(this);
	TestToolNumbers numbers = new TestToolNumbers(this);

	@Override
	public void run() {
		setRequirementTarget("Compile");
		int exitCode = project.runBat("UT_Project_005_no_source", "compile.bat");
		numbers.checkEquality(0, exitCode);

		setRequirementTarget("Run");
		exitCode = project.runBat("UT_Project_005_no_source", "run.bat");
		numbers.checkEquality(0, exitCode);
		
		setRequirementTarget("Export : Report without source");
		files.checkExists(new File("test_files/UT_Project_005_no_source/result/test_source/Test.xml"));
		files.checkExists(new File("test_files/UT_Project_005_no_source/result/test_source/Test2.xml"));
		files.checkExists(new File("test_files/UT_Project_005_no_source/result/test_source/Test3.xml"));
		
		xml.parseFile("test_files/UT_Project_005_no_source/result/test_source/Test.xml", "Test.xml");
		check(xml.getNode("Test.xml", "source/step/source_line[@line_number='23']/attachment") != null);
		check(xml.getNode("Test.xml", "source/step/source_line[@line_number='24']/comment") != null);
		strings.checkEquality("end of test", xml.getNode("Test.xml", "source/step/source_line[@line_number='24']/comment/string").getTextContent());
	}

}
