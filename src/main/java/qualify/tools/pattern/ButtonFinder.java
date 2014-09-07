package qualify.tools.pattern;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import qualify.ErrorsAndWarnings;
import qualify.TestCase;
import qualify.tools.TestObject;
import qualify.tools.TestToolSikuli;
import qualify.tools.geometry.Area;

public class ButtonFinder implements AreaFinder {
	
	static Logger logger = Logger.getLogger(ButtonFinder.class);
	
	private TestObject testObject = null;
	private float similarity = TestToolSikuli.DEFAULT_SIMILARITY;
	private int MAX_WIDTH = 500;
	
	public ButtonFinder(String objectIdentifier) {
		TestObject obj = TestObject.getFromRepo(objectIdentifier);
		if(obj != null) {
			testObject = obj;
			if(obj.get("float(similarity)") != null) {
				setSimilarity(Double.valueOf(obj.get("float(similarity)").attribute("value")).floatValue());
			}
		} else {
			ErrorsAndWarnings.addError("Object '" + objectIdentifier + "' not found in object repository");
		}
	}
	
	public void setSimilarity(float s) {
		similarity = s;
	}
	
	public float getSimilarity() {
		return similarity;
	}

	public Area[] findAreas(Area restrictedArea) {
		logger.debug("looking for areas");
		boolean enableComment = TestCase.deactivateTestToolsLog();

		List<Area> result = new ArrayList<Area>();
		for(Area left : restrictedArea.findAreas(testObject.get("image(left)").getPath(), similarity)) {
			logger.debug("left image found at x" + left.topLeftCorner().x + " y=" + left.topLeftCorner().y);
			Area[] rightAreas = left.stretch(1.2).eastStripe(MAX_WIDTH).findAreas(testObject.get("image(right)").getPath(), similarity);
			Area right = null;
			if(rightAreas.length == 1) {
				right = rightAreas[0];
			} else if(rightAreas.length > 1) {
				right = rightAreas[0];
				for(int index = 1; index < rightAreas.length; index++) {
					if(rightAreas[index].topLeftCorner().x < right.topLeftCorner().x) {
						right = rightAreas[index];
					}
				}
			}
			if(right != null) {
				logger.debug("right image found at x" + right.topLeftCorner().x + " y=" + right.topLeftCorner().y);
				int width = right.topLeftCorner().x - left.topRightCorner().x;
				result.add(left.eastStripe(width));
			}
		}
		
		TestCase.activateTestToolsLog(enableComment);
		return result.toArray(new Area[]{});
	}

}
