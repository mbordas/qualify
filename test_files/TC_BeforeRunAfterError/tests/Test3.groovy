import java.io.File;
import java.io.IOException;

import qualify.TestCase;

public class Test3 extends MotherTest {

	public Test3() {
		// myUnexistingString is not declared. Exception is thrown here:
		myUnexistingString = 'hello world';
	}

	public void run() {
		check(b)
		System.out.println("I'm in the script 3");
	}

}