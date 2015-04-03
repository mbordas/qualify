/*
 * Copyright (c) 2012-2014 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package qualify.test.unit;

import java.util.concurrent.atomic.AtomicInteger;

import qualify.TestCase;

public class FakeTestCase extends TestCase {

	private static AtomicInteger s_executions = new AtomicInteger(0);
	private static AtomicInteger s_failures = new AtomicInteger(0);

	@Override
	public void run() throws Throwable {
		comment(s_executions.incrementAndGet());
		check(s_failures.getAndDecrement() <= 0);
		// need(s_failures.getAndDecrement() <= 0);
	}

	public static void reset() {
		s_executions.set(0);
		s_failures.set(0);
	}

	public static void reset(int failures) {
		s_executions.set(0);
		s_failures.set(failures);
	}

	public static int getExecutions() {
		return s_executions.get();
	}
}
