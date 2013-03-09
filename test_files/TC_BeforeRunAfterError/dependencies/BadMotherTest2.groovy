import java.io.File;
import java.io.IOException;

import qualify.TestCase;

public class BadMotherTest2 extends TestCase {

	protected String myString = null;
	
	boolean b;

	public void beforeRun() {
		System.out.println("running bad beforeRun 2");
	}
	
	public void run() {}
	
	public void afterRun() {
		System.out.println("running bad afterRun 2");
		int t = "toto"
	}

}