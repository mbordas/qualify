import java.io.File;
import java.io.IOException;

import qualify.TestCase;

public class BadMotherTest extends TestCase {

	protected String myString = null;
	
	boolean b;

	public void beforeRun() {
		System.out.println("running bad beforeRun");
		int t = "toto"
	}
	
	public void run() {}
	
	public void afterRun() {
		System.out.println("running bad afterRun");
	}

}