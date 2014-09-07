import java.io.File;
import java.io.IOException;

import qualify.TestCase;
import qualify.tools.*;

public abstract class MotherTest extends TestCase {

	TestToolStrings strings = new TestToolStrings(this);
	
	protected boolean check(boolean condition) {
		comment("call to MotherClass.check");
		return super.check(!condition);
	}

}
