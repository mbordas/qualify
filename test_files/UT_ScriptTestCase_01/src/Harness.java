import qualify.TestHarness;

public class Harness extends TestHarness {
	
	public static void main(String[]args) {
		TestHarness.runTestHarness(args, new Harness());
	}

	protected void registerTestCases() {
		register(new Test());
	}

}

