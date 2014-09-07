import java.io.File;
import java.io.IOException;

import qualify.TestCase;

public class Test4 extends TestCase {
	
	public void run() {
		comment("begin");
		
		step("first step");
		
		comment("into first step");
		check(true);
		
		step("second step");
		comment("into second step");
		
		step("third step");
		comment("into third step");
		check(false);
		
		step("fourth step");
		comment("into fourth step");
		check(false);
		check(true);
	}

}
