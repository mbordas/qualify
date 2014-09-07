import java.io.File;
import java.io.IOException;

import qualify.TestCase;

public class MotherTest extends TestCase {

	protected String myString = null;
	
	boolean b;

	public void beforeRun() {
		System.out.println("running beforeRun");
	}
	
	public void run() {}
	
	public void afterRun() {
		System.out.println("running afterRun");
		check(myString.equals('hello world'))
	}

}