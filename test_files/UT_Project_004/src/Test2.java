import java.io.File;
import java.io.IOException;

import qualify.TestCase;

public class Test2 extends TestCase {
	
	public void run() {
		setRequirementTarget("REQ-2");
		check(true);
		
		setRequirementTarget("REQ-3");
		check(false);
	}

}
