import java.io.File;
import java.io.IOException;

import qualify.TestCase;

public class Test4 extends BadMotherTest2 {

	public void run() {
		// BadMotherTest2 throws java.lang Exception at runAfter()
		check(b)
		System.out.println("I'm in the script 3");
	}

}