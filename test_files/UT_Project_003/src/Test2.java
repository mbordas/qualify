import java.io.File;
import java.io.IOException;

import qualify.TestCase;

public class Test2 extends TestCase {
	
	public void run() {
		setRequirementTarget("REQ-4");
		check(true);
		
		setRequirementTarget("REQ-5");
		check(false);
		
		//setRequirementTarget("REQ-6");
		//check(false);
	}

}
