package qualify.test.unit;

import qualify.TestCase;
import qualify.tools.TestToolNumbers;
import qualify.tools.TestToolSikuli;
import qualify.tools.geometry.Area;
import qualify.tools.pattern.AreaNeighborhood;
import qualify.tools.pattern.AreaTransformer;

public class UT_AreaNeighborhood extends TestCase {

	@Override
	public void run() {
		setRequirementTarget("Area");
		
		TestToolNumbers numbers = new TestToolNumbers(this);
		TestToolSikuli sikuli = new TestToolSikuli(this);		
		final Area a = sikuli.getScreenArea().stretch(0.2);
		
		a.moveTo(500, 600);
		numbers.checkEquality(500, a.topLeftCorner().x);
		numbers.checkEquality(600, a.topLeftCorner().y);
		a.setWidth(200);
		a.setHeight(150);
		numbers.checkEquality(200, a.getWidth());
		numbers.checkEquality(150, a.getHeight());
		
		// Transformation to the WEST
		AreaNeighborhood t = new AreaNeighborhood("west", 80);
		Area b = t.transform(a);
		numbers.checkEquality(420, b.topLeftCorner().x);
		numbers.checkEquality(600, b.topLeftCorner().y);
		numbers.checkEquality(80, b.getWidth());
		numbers.checkEquality(150, b.getHeight());
		
		// Transformation to the EAST
		t = new AreaNeighborhood("east", 80);
		b = t.transform(a);
		numbers.checkEquality(700, b.topLeftCorner().x);
		numbers.checkEquality(600, b.topLeftCorner().y);
		numbers.checkEquality(80, b.getWidth());
		numbers.checkEquality(150, b.getHeight());
		
		// Transformation to the NORTH
		t = new AreaNeighborhood("north", 80);
		b = t.transform(a);
		numbers.checkEquality(500, b.topLeftCorner().x);
		numbers.checkEquality(520, b.topLeftCorner().y);
		numbers.checkEquality(200, b.getWidth());
		numbers.checkEquality(80, b.getHeight());
		
		// Transformation to the SOUTH
		t = new AreaNeighborhood("south", 80);
		b = t.transform(a);
		numbers.checkEquality(500, b.topLeftCorner().x);
		numbers.checkEquality(750, b.topLeftCorner().y);
		numbers.checkEquality(200, b.getWidth());
		numbers.checkEquality(80, b.getHeight());
	}
	
	

}
