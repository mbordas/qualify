package qualify.test.unit;

import qualify.TestCase;
import qualify.doc.Chronometer;

public class UT_TestToolDates extends TestCase {

	@Override
	public void run() {		
		Chronometer chrono = new Chronometer();
		pause(2.5);
		chrono.mark("top 1");
		pause(1.5);
		chrono.mark("top 2");
		chrono.mark("top 3");
		pause(3.0);
		chrono.stop();
		
		comment(chrono);
	}

}
