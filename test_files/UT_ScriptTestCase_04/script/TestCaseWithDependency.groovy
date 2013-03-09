import java.io.File;
import java.io.IOException;

import qualify.TestCase;

import Dependency;

public class TestCaseWithDependency extends TestCase {
	
	public void run() {
		Dependency dep = new Dependency();
		check(dep.isAlive());
		Dependency.sayHello();
	}

}