import java.io.File;
import java.io.IOException;

import qualify.TestCase;

public class Test extends TestCase {
	
	public void run() {
		setRequirementTarget("REQ-1");
		check(true);
		
		setRequirementTarget("REQ-2");
		check(false);
		
		//setRequirementTarget("REQ-3");
		//check(true);
	}

}
