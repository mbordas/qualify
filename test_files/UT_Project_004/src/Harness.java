import qualify.TestHarness;

public class Harness extends TestHarness {
	
	public static void main(String[]args) {
		int exitCode = TestHarness.runTestHarness(args, new Harness());
		
		System.exit(exitCode);
	}

	protected void registerTestCases() {
		register(new Test1());
		register(new Test2());
		register(new Test3());
		register(new Test4());
	}

}

