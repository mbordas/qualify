package qualify.test;

import java.io.IOException;

import qualify.TestCase;
import qualify.tools.TestToolFile;
import qualify.tools.TestToolNumbers;
import qualify.tools.TestToolStrings;
import qualify.tools.TestToolXML;

public class TC_BeforeRunAfterError extends TestCase {

	@Override
	public void run() {
		TestToolNumbers numbers = new TestToolNumbers(this);
		TestToolStrings strings = new TestToolStrings(this);
		TestToolXML xml = new TestToolXML(this);
		TestToolFile files = new TestToolFile(this);
		ProjectManager project = new ProjectManager(this);
		
		setRequirementTarget("Run");
		try {
			TestToolFile.deleteDir("test_files/TC_BeforeRunAfterError/result");
		} catch (IOException e) {
			e.printStackTrace();
			need(false);
		}
		int exitCode = project.runBat("TC_BeforeRunAfterError");
		numbers.checkEquality(1, exitCode);
		
		setRequirementTarget("Export : Test source");
		
		files.checkExists("test_files/TC_BeforeRunAfterError/result/Test1/Test1.xml");
		files.checkExists("test_files/TC_BeforeRunAfterError/result/Test2/Test2.xml");
		files.checkExists("test_files/TC_BeforeRunAfterError/result/Test3/Test3.xml");
		files.checkExists("test_files/TC_BeforeRunAfterError/result/Test4/Test4.xml");
		files.checkExists("test_files/TC_BeforeRunAfterError/result/Test5/Test5.xml");
		files.checkExists("test_files/TC_BeforeRunAfterError/result/Test6/Test6.xml");
		files.checkExists("test_files/TC_BeforeRunAfterError/result/Test7/Test7.xml");
		
		setRequirementTarget("Exception handling");
		
		// Test1 fails with exception during beforeRun()
		xml.parseFile("test_files/TC_BeforeRunAfterError/result/Test1/Test1.xml", "Test1");
		need(xml.getNode("Test1", "source/exception") != null);
		strings.checkContains("Exception raised during beforeRun()", xml.getAttributeValueAsString("Test1", "source/exception[1]/@label"), true);
		
		// Test2 fails without any exception
		xml.parseFile("test_files/TC_BeforeRunAfterError/result/Test2/Test2.xml", "Test2");
		check(xml.getNode("Test2", "source/exception") == null);
		
		// Test3 fails with exception during constructor
		xml.parseFile("test_files/TC_BeforeRunAfterError/result/Test3/Test3.xml", "Test3");
		need(xml.getNode("Test3", "source/exception") != null);
		strings.checkContains("Exception raised during compilation", xml.getAttributeValueAsString("Test3", "source/exception[1]/@label"), true);
		
		// Test4 fails with exception during afterRun()
		xml.parseFile("test_files/TC_BeforeRunAfterError/result/Test4/Test4.xml", "Test4");
		need(xml.getNode("Test4", "source/exception") != null);
		strings.checkContains("Exception raised during afterRun()", xml.getAttributeValueAsString("Test4", "source/exception[1]/@label"), true);
		
		// Test5 fails with exception during constructor
		xml.parseFile("test_files/TC_BeforeRunAfterError/result/Test5/Test5.xml", "Test5");
		need(xml.getNode("Test5", "source/exception") != null);
		strings.checkContains("Exception raised during compilation", xml.getAttributeValueAsString("Test5", "source/exception[1]/@label"), true);
		
		// Test6 fails with exception during run() raised by Test6
		xml.parseFile("test_files/TC_BeforeRunAfterError/result/Test6/Test6.xml", "Test6");
		need(xml.getNode("Test6", "source/exception") != null);
		strings.checkContains("Exception raised during run()", xml.getAttributeValueAsString("Test6", "source/exception[1]/@label"), true);
		need(xml.getNode("Test6", "source//source_line[@line_number='10']/test_result[1]/string[1]") != null);
		strings.checkContains("GroovyCastException: Cannot cast object", xml.getAttributeValueAsString("Test6", "source//source_line[@line_number='10']/test_result[1]/string[1]"), true);
		
		// Test7 fails with exception during run() raised by TestCase
		xml.parseFile("test_files/TC_BeforeRunAfterError/result/Test7/Test7.xml", "Test7");
		need(xml.getNode("Test7", "source/exception") != null);
		strings.checkContains("Exception raised during run()", xml.getAttributeValueAsString("Test7", "source/exception[1]/@label"), true);
		need(xml.getNode("Test7", "source//source_line[@line_number='10']/test_result[1]/string[1]") != null);
		strings.checkContains("java.lang.RuntimeException: Needed condition is 'false'", xml.getAttributeValueAsString("Test7", "source//source_line[@line_number='10']/test_result[1]/string[1]"), true);
	}

}
