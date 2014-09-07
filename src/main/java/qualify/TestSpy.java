package qualify;


public abstract class TestSpy {

	abstract public void beforeHarness(TestHarness harness);
	abstract public void afterHarness(TestHarness harness);
	
	abstract public void beforeTest(TestCase testCase);
	abstract public void afterTest(TestCase testCase);

}
