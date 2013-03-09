import qualify.TestHarness;

public class Harness extends TestHarness {
	
	public static void main(String[]args) {
		TestHarness.runTestHarness(args, new Harness());
	}

	protected void registerTestCases() {
		register(new Test());
		register(new Test2());
		register(new Test3());
		register(new Test4());
	}

}

