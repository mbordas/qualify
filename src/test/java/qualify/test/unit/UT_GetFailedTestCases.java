package qualify.test.unit;

import java.io.File;
import java.util.List;

import qualify.TestCase;
import qualify.doc.ReleaseNote;
import qualify.tools.TestToolNumbers;
import qualify.tools.TestToolStrings;

public class UT_GetFailedTestCases extends TestCase {

	@Override
	public void run() {
		setRequirementTarget("run last NOK");
		TestToolStrings strings = new TestToolStrings(this);
		TestToolNumbers numbers = new TestToolNumbers(this);
		
		File releaseNote = new File("test_files/TC_ScriptTestCase_06_LastNOK/release_note_with_NOKs.xml");
		List<String> testCasesNames = ReleaseNote.getFailedTestCases(releaseNote);
		numbers.checkEquality(1, testCasesNames.size());
				
		strings.checkContains("TC_Process", testCasesNames, true);
	}

}
