import java.io.File;
import java.io.IOException;

import qualify.TestCase;

public class Test extends MotherTest {
	
	public void run() {
		setRequirementTarget("REQ-1");
		check(false);
		
		strings.checkEquality("toto", "tata");
	}

}
