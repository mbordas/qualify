/*
 * Copyright (c) 2012-2014 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package qualify.test.unit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import qualify.ErrorsAndWarnings;
import qualify.TestHarness;

public class RetryTest {

	@Test
	public void retryTest() {
		ErrorsAndWarnings.reset();

		TestHarness harness = new TestHarness() {
			@Override
			protected void registerTestCases() {
				register(FakeTestCase.class);
			}
		};

		FakeTestCase.reset(2);

		// option set: <option name="max_attempt_per_test_case">5</option>
		TestHarness.runTestHarness(new String[] { "-option_file", "target/test-classes/RetryTest/options.xml" }, harness);

		assertEquals(3, FakeTestCase.getExecutions());
		assertEquals(0, ErrorsAndWarnings.getErrorsCount());
	}
}
