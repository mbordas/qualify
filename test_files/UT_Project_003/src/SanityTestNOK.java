import java.io.File;
import java.io.IOException;

import qualify.TestCase;

public class SanityTestNOK extends TestCase {
	
	public void run() {
		setRequirementTarget("REQ-1");
		check(false);
	}

}
