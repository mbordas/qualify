/*Copyright (c) 2010-2012, Mathieu Bordas
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1- Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
2- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
3- Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package qualify.tools.geometry;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Region;

import qualify.TestCase;
import qualify.tools.TestToolSikuli;

/**
 * An Area is a rectangular region of the screen. The Area class provides methods
 * to easily manipulate areas.
 *
 */
public class Area extends Shape {
	
	public static String AREA_DEFAULT_COLOR = "red";

	private Region region = null;
	private TestToolSikuli sikuli = null;
	private TestCase testCase = null;

	public Area(TestToolSikuli skl, Region rgn) {
		region = rgn;
		sikuli = skl;
		testCase = skl.getTestCase();
		setColor(AREA_DEFAULT_COLOR);
	}

	public Area(final Area original) {
		region = new Region(original.region);
		sikuli = original.sikuli;
		testCase = sikuli.getTestCase();
		setColor(AREA_DEFAULT_COLOR);
	}

	public Area subArea(Point p1, Point p2) {
		int width = Math.abs(p2.x - p1.x);
		int height = Math.abs(p2.y - p1.y);
		Region rg = new Region(new Rectangle(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), width, height));
		return new Area(sikuli, rg);
	}

	public static Area getBoundingBox(Area[] areas) {
		if(areas.length == 0) {
			return null;
		} else {
			Point p0 = areas[0].topLeftCorner();
			Point p1 = areas[0].bottomRightCorner();
			int x1 = p0.x, y1 = p0.y, x2 = p1.x, y2 = p1.y;
			for(Area a : areas) {
				x1 = Math.min(x1, a.topLeftCorner().x);
				y1 = Math.min(y1, a.topLeftCorner().y);
				x2 = Math.max(x2, a.bottomRightCorner().x);
				y2 = Math.max(y2, a.bottomRightCorner().y);
			}
			return new Area(areas[0].sikuli, new Region(new Rectangle(x1, y1, x2 - x1, y2 - y1)));
		}
	}
	
	public static Area getBoundingBox(List<Area> areas) {
		return getBoundingBox(areas.toArray(new Area[]{}));
	}

	/**
	 * Finds contained from image pattern.
	 * @param imageIdentifier The identifier of the image used as pattern.
	 * @return The Area that best matches the pattern image.
	 */
	public Area findArea(String imageIdentifier) {
		return sikuli.findArea(imageIdentifier, this);
	}
	
	public Area[] findAreas(String imageIdentifier) {
		return findAreas(imageIdentifier, TestToolSikuli.DEFAULT_SIMILARITY);
	}
	
	public Area[] findAreas(String imageIdentifier, float similarity) {
		return sikuli.findAreas(imageIdentifier, this, similarity);
	}

	/**
	 * Returns if the pattern image is contained.
	 * @param imageIdentifier The identifier of the image used as pattern.
	 * @return True if an Area inside matches the pattern image
	 */
	public boolean find(String imageIdentifier) {
		return sikuli.find(imageIdentifier, this);
	}

	/**
	 * Returns the Area at the East side. It has the same height, and a specific width.
	 * @param stripeWidth The width of the Area to return.
	 * @return An Area in the East side, using the specific width.
	 */
	public Area eastStripe(int stripeWidth) {
		return new Area(sikuli, region.right(stripeWidth));
	}

	/**
	 * Returns the Area at the West side. It has the same height, and a specific width.
	 * @param stripeWidth The width of the Area to return.
	 * @return An Area in the West side, using the specific width.
	 */
	public Area westStripe(int stripeWidth) {
		return new Area(sikuli, region.left(stripeWidth));
	}

	public Area northStripe(int stripeHeight) {
		return new Area(sikuli, new Region(region.x, region.y - stripeHeight, region.w, stripeHeight));
	}

	public Area southStripe(int stripeHeight) {
		return new Area(sikuli, new Region(region.x, region.y + region.h, region.w, stripeHeight));
	}

	/**
	 * Creates an Area that is stretched using a specific scaling factor
	 * @param factor The scale factor to use. 1.0 for no stretching, 2.0 for double stretching, 0.5 for half, etc.
	 * @return
	 */
	public Area stretch(double factor) {
		int newWidth = Double.valueOf(factor * region.w).intValue();
		int newHeight = Double.valueOf(factor * region.h).intValue();
		int newX = Double.valueOf(region.x + (region.w - newWidth) / 2.0).intValue();
		int newY = Double.valueOf(region.y + (region.h - newHeight) / 2.0).intValue();

		return new Area(sikuli, new Region(new Rectangle(newX, newY, newWidth, newHeight)));
	}

	/**
	 * Creates an Area from the right part, using a specific percentage.
	 * @param factor The part of the actual Area to be used. 1.0 will return the same Area, 0.5 will return the right half of the Area
	 * @return An Area from the right part, using a specific percentage.
	 */
	public Area right(double factor) {
		return new Area(sikuli, new Region(region.x + region.w - (int)(factor * region.w), region.y, (int)(factor * region.w), region.h)); 
	}

	public Area left(double factor) {
		return new Area(sikuli, new Region(region.x, region.y, (int)(factor * region.w), region.h));
	}

	public Area top(double factor) {
		return new Area(sikuli, new Region(region.x, region.y, region.w, (int)(factor * region.h)));
	}

	public Area bottom(double factor) {
		int newWidth = region.w;
		int newHeight = Double.valueOf(factor * region.h).intValue();
		int newX = region.x;
		int newY = region.y + region.h - newHeight;
		return new Area(sikuli, new Region(new Rectangle(newX, newY, newWidth, newHeight)));
	}

	public Area bottom(Point p) {
		int newHeight = region.y + region.h - p.y;
		int newY = p.y;
		return new Area(sikuli, new Region(new Rectangle(region.x, newY, region.w, newHeight)));
	}

	/**
	 * Returns the text appearing in the Area.
	 * @return The text appearing in the Area.
	 */
	public String getText() {
		return sikuli.getText(this);
	}

	public int getWidth() {
		return region.w;
	}

	public int getHeight() {
		return region.h;
	}

	/**
	 * Returns if it contains the specific text
	 * @param textToFind The text to look for
	 * @return True if the specific text is contained. False elsewhere.
	 */
	public boolean contains(String textToFind) {
		String insideText = sikuli.getText(this);
		return insideText.contains(textToFind);
	}

	/**
	 * Returns the Point in the center of the Area.
	 * @return The Point in the center of the Area.
	 */
	public Point center() {
		return new Point(this.sikuli, region.x + region.w / 2, region.y + region.h / 2);
	}

	public Point topLeftCorner() {
		return new Point(this.sikuli, region.x, region.y);
	}

	public Point bottomLeftCorner() {
		return new Point(this.sikuli, region.x, region.y + region.h);
	}

	public Point topRightCorner() {
		return new Point(this.sikuli, region.x + region.w, region.y);
	}

	public Point bottomRightCorner() {
		return new Point(this.sikuli, region.x + region.w, region.y + region.h);
	}

	public Region getRegion() {
		return region;
	}

	public void moveTo(Point p) {
		moveTo(p.x, p.y);
	}

	public Area translate(int x, int y) {
		region.x += x;
		region.y += y;
		return this;
	}

	public void moveTo(int x, int y) {
		region.x = x;
		region.y = y;
	}

	public void setWidth(int w) {
		region.w = w;
	}

	public void setHeight(int h) {
		region.h = h;
	}

	public BufferedImage getImage() {
		return sikuli.getScreen().capture(region).getImage();
	}

	/**
	 * Clicks on the center of the Area.
	 */
	public void click() {
		center().click();
	}
	
	public void doubleClick() {
		center().doubleClick();
	}
	
	public void type(String whatToType) {
		try {
			getRegion().type(whatToType);
		} catch (FindFailed e) {
			e.printStackTrace();
			testCase.addTestResult(false,
					"unexpected error when trying to paste in Area",
					Area.class);
		}
	}

	public void paste(String textToPaste) {
		try {
			getRegion().paste(textToPaste);
		} catch (FindFailed e) {
			e.printStackTrace();
			testCase.addTestResult(false,
					"unexpected error when trying to paste in Area",
					Area.class);
		}
	}

	public Map<File, String> getAttachedFiles() {
		return null;
	}

	public Element toDomElement() {
		Element areaElement = new Element("area");
		areaElement.setAttribute("x", "" + topLeftCorner().x);
		areaElement.setAttribute("y", "" + topLeftCorner().y);
		areaElement.setAttribute("width", "" + getWidth());
		areaElement.setAttribute("height", "" + getHeight());
		areaElement.setAttribute("label", label);
		areaElement.setAttribute("color", color);
		return areaElement;
	}

	public String toStringForConsole() {
		return null;
	}

}
