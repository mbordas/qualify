package qualify.test;

import java.io.File;

import qualify.TestCase;
import qualify.tools.TestToolFile;
import qualify.tools.TestToolNumbers;
import qualify.tools.TestToolStrings;
import qualify.tools.TestToolXML;

public class TC_Preprocessors extends TestCase {

	TestToolFile files = new TestToolFile(this);
	TestToolXML xml = new TestToolXML(this);
	TestToolStrings strings = new TestToolStrings(this);
	TestToolNumbers numbers = new TestToolNumbers(this);

	@Override
	public void run() {
		TestToolFile.deleteDir("test_files/TC_Preprocessors/result");
		setRequirementTarget("Preprocessors");
		ProjectManager project = new ProjectManager(this);
		int exitCode = project.runBat("TC_Preprocessors");
		numbers.checkEquality(0, exitCode);

		// First script is not changed
		files.checkExists("test_files/TC_Preprocessors/result/Test1/Test1.xml");
		
		// Second script is changed: requirement is set with: "// Q.req:Requirement 2"
		files.checkExists("test_files/TC_Preprocessors/result/Test2/Test2.xml");
		xml.parseFile("test_files/TC_Preprocessors/result/Test2/Test2.xml", "Test2.xml");
		strings.checkEquality("Requirement 2", xml.getAttributeValueAsString("Test2.xml", "source/step[1]/source_line[@line_number='10']/@requirement"));
		strings.checkEquality("result_ok", xml.getAttributeValueAsString("Test2.xml", "source/step[1]/source_line[@line_number='10']/@type"));
		strings.checkEquality("result_nok", xml.getAttributeValueAsString("Test2.xml", "source/step[1]/source_line[@line_number='11']/@type"));
		
		// Third script is completely translated:
		// - Java imports, class definition structure
		// - requirement is set with: "Q.req:Requirement 2"
		files.checkExists("test_files/TC_Preprocessors/result/Test3/Test3.xml");
		attachFile(new File("test_files/TC_Preprocessors/result/Test3/Test3.xml"));
		xml.parseFile("test_files/TC_Preprocessors/result/Test3/Test3.xml", "Test3.xml");
		strings.checkEquality("Requirement 3", xml.getAttributeValueAsString("Test3.xml", "source/step[2]/source_line[@line_number='4']/@requirement"));
		strings.checkEquality("result_ok", xml.getAttributeValueAsString("Test3.xml", "source/step[2]/source_line[@line_number='4']/@type"));
		strings.checkEquality("result_nok", xml.getAttributeValueAsString("Test3.xml", "source/step[3]/source_line[@line_number='8']/@type"));
		
		// Preprocessor does not exist
		exitCode = project.runBat("TC_Preprocessors", "run2.bat");
		numbers.checkEquality(1, exitCode);
		File logFile = new File("test_files/TC_Preprocessors/log.txt");
		files.checkNotFound(logFile, "Exception");
		attachFile(logFile);

	}

}
