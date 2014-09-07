import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import qualify.TestCase;
import qualify.tools.*;
import qualify.tools.geometry.*;
import qualify.doc.*;

public class ScriptTestCase extends TestCase {
	
	public void run() {
		TestToolSikuli sikuli = new TestToolSikuli(this);
		
		sikuli.click(sikuli.findArea('app(firefox)>panel(main_view)>image(bottom_right)'))
		
		Area a1 = sikuli.findArea('app(firefox)>panel(main_view)>image(top_left)')		
		Area a2 = sikuli.getScreenArea().bottom(0.5).findArea('app(firefox)>panel(main_view)>image(bottom_right)')
		
		Graphic g = new Graphic(sikuli.getScreenArea());
		g.addArea(a1, 'a1');
		g.addArea(a2, 'a2');
		comment(g);
		
		check(a1 != null)
		check(a2 != null)
		/*
		double controlProportionFactor = 0.1
		
		// Getting the visible zone of the scrollable pane
		Area a = sikuli.getScreenArea().subArea(a1.bottomLeftCorner(), a2.bottomLeftCorner())
		sikuli.getRobot().mouseWheel(-50)
		pause(0.3)
		BufferedImage pageImage = a.getImage()
		sikuli.attachInlineImage(pageImage)
		
		Area cz = a.bottom(0.1)
		
		int scrollAmount = 1;
		
		boolean stillScrolling = true;
		while(stillScrolling) {
			// getting the control image
			BufferedImage bi = cz.getImage()
			sikuli.getRobot().mouseWheel(scrollAmount)
			pause(0.1)
			Area b = a.bottom(sikuli.findArea(bi).bottomLeftCorner())
			if(b.getHeight() == 0) {
				stillScrolling = false;
			} else {
				pageImage = TestToolImages.verticalConcat(pageImage, b.getImage())
				// computing the new scroll amount
				scrollAmount = Math.max(Double.valueOf(scrollAmount * 0.9 * a.getHeight() / b.getHeight()).intValue(), 1)
				comment("" + scrollAmount)
			}
		}
		
		sikuli.attachInlineImage(pageImage)
*/
	}

}