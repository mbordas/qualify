package qualify.test.unit;

import qualify.TestCase;
import qualify.tools.TestToolNumbers;
import qualify.tools.TestToolSikuli;
import qualify.tools.geometry.Area;

public class UT_Area extends TestCase {

	@Override
	public void run() {
		setRequirementTarget("Area");
		
		TestToolNumbers numbers = new TestToolNumbers(this);
		
		TestToolSikuli sikuli = new TestToolSikuli(this);
		Area screen = sikuli.getScreenArea();
		
		int globalWidth = screen.getWidth();
		int globalHeight = screen.getHeight();
		comment(globalWidth);
		comment(globalHeight);
		
		Area a = screen.stretch(1.0);
		numbers.checkNear(globalWidth, a.getWidth(), 1);
		numbers.checkNear(globalHeight, a.getHeight(), 1);
		
		a = screen.stretch(0.5);
		numbers.checkNear(globalWidth / 2, a.getWidth(), 1);
		numbers.checkNear(globalHeight / 2, a.getHeight(), 1);
		
		a = screen.bottom(0.5);
		numbers.checkNear(globalWidth, a.getWidth(), 1);
		numbers.checkNear(globalHeight / 2, a.getHeight(), 1);
		numbers.checkNear(a.topLeftCorner().y, globalHeight / 2, 1);
		
		Area b = new Area(a);
		b.moveTo(10, 12);
		numbers.checkEquality(10, b.topLeftCorner().x);
		numbers.checkEquality(12, b.topLeftCorner().y);
		numbers.checkEquality(a.getWidth(), b.getWidth());
		numbers.checkEquality(a.getHeight(), b.getHeight());
		b.setHeight(123);
		b.setWidth(456);
		numbers.checkEquality(123, b.getHeight());
		numbers.checkEquality(456, b.getWidth());
	}

}
