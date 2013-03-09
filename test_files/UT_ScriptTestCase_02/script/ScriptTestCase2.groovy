import java.io.File;
import java.io.IOException;

import qualify.TestCase;
import qualify.tools.*;

public class ScriptTestCase2 extends TestCase {

	private TestToolStrings strings = new TestToolStrings(this);
	
	public void run() {
		strings.checkEquality("Test scripts work well", "Test scripts works well");
	}

}