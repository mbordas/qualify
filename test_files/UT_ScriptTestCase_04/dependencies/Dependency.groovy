import java.io.File;
import java.io.IOException;

import qualify.TestCase;

public class Dependency extends ZDependency {

	public boolean isAlive() {
		return true;
	}
	
	public static void sayHello() {
		System.out.println("Hello from scripted dependency");
	}

}