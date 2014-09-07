import java.io.File;
import java.io.IOException;

import qualify.TestCase;
import qualify.tools.TestToolFile;
import qualify.tools.TestToolNumbers;
import qualify.tools.TestToolStrings;
import qualify.tools.TestToolXML;

public class Test extends TestCase {

	TestToolFile files = new TestToolFile(this);
	TestToolXML xml = new TestToolXML(this);
	TestToolStrings strings = new TestToolStrings(this);
	TestToolNumbers numbers = new TestToolNumbers(this);
	
	public void run() {
		setRequirementTarget("Example requirement");
		comment("start of test");
		String expected = "Report is generated even without test's source code";
		strings.checkEquality(expected, "Report is generated when test's source code is not available");
		
		attachFile(new File("options.xml"));
		comment("end of test");
	}

}
