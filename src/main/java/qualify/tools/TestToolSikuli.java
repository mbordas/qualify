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

package qualify.tools;

import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.KeyStroke;

import org.apache.log4j.Logger;
import org.sikuli.basics.WinUtil;
import org.sikuli.script.FindFailed;
import org.sikuli.script.IRobot;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

import qualify.ErrorsAndWarnings;
import qualify.TestCase;
import qualify.doc.Attachment;
import qualify.doc.Field;
import qualify.tools.geometry.Area;
import qualify.tools.geometry.Point;
import qualify.tools.pattern.AreaTransformer;
import qualify.tools.pattern.StringSelector;

/**
 * The TestToolSikuli provides methods to manipulate the mouse and the keyboard, and to analyze the screen. That tool uses the great MIT's
 * library called Sikuli, based on OpenCV (for the Computer Vision part) and Tesseract (for the Character Recognition). TestToolSikuli uses
 * PNG images as pattern to find in screen the area where to click, get text, type characters, etc. These pattern images can identified by
 * two ways: - the relative path to the PNG file. In that case, the origin folder is the one given at construction by
 * 'sikuliImagesDirectory' - the path of the image in an object repository (see the TestObject class for more information).
 */
public class TestToolSikuli {

	static Logger logger = Logger.getLogger(TestToolSikuli.class);

	private TestCase testCase = null;

	private TestToolImages images = null;

	private File imgDir = null;
	public final static String DEFAULT_IMAGES_DIRECTORY = "tmp_images";
	public final static String AUTO_CAPTURED_IMAGES_PREFIX = "autoCapture_";
	private static int AUTO_CAPTURED_IMAGES_COUNT = 0;
	public static final float DEFAULT_SIMILARITY = (float) 0.7;
	private static double DEFAULT_WAIT_TIMEOUT = 10.0;
	private Screen screen = null;
	private IRobot robot = null;

	private int mouse_x = 0, mouse_y = 0;

	public TestToolSikuli(TestCase tc, File sikuliImagesDirectory) {
		testCase = tc;
		images = new TestToolImages(testCase);
		setImagesDirectory(sikuliImagesDirectory);
		screen = new Screen();
		robot = Screen.getRobot(Screen.getPrimaryId());
	}

	public TestToolSikuli(TestCase tc, String sikuliImagesDirectoryName) {
		testCase = tc;
		images = new TestToolImages(testCase);
		setImagesDirectory(new File(sikuliImagesDirectoryName));
		screen = new Screen();
		robot = Screen.getRobot(Screen.getPrimaryId());
	}

	public TestToolSikuli(TestCase tc) {
		testCase = tc;
		images = new TestToolImages(testCase);
		setImagesDirectory(new File(DEFAULT_IMAGES_DIRECTORY));
		screen = new Screen();
		robot = Screen.getRobot(Screen.getPrimaryId());
	}

	private void setImagesDirectory(File dir) {
		imgDir = dir;
		if(!imgDir.exists()) {
			try {
				TestToolFile.createDir(dir);
			} catch(IOException e) {
				e.printStackTrace();
				testCase.addError("Images directory '" + imgDir.getAbsolutePath() + "' cannot be created.");
			}
		}
	}

	public TestCase getTestCase() {
		return this.testCase;
	}

	/**
	 * Returns the main sikuli.Screen object used by this instance.
	 * 
	 * @return The main sikuli.Screen object used by this instance.
	 */
	public Area getScreenArea() {
		return new Area(this, screen);
	}

	public Screen getScreen() {
		return screen;
	}

	/**
	 * Returns the java.awt.Robot object used by this instance.
	 * 
	 * @return the java.awt.Robot object used by this instance.
	 */
	public IRobot getRobot() {
		return robot;
	}

	/**
	 * Focuses on one application, or launch it if needed.
	 * 
	 * @param applicationPath
	 *            The path to the executable or the name of the application (e.g. "Mozilla Firefox")
	 */
	public void switchMSWindowsApp(String applicationPath) {
		WinUtil win = new WinUtil();
		win.switchApp(applicationPath);
	}

	/**
	 * Clicks on the center of the best matching region of the screen.
	 * 
	 * @param imageIdentifier
	 *            The identifier of the image used as pattern
	 */
	public void click(String imageIdentifier) {
		click(imageIdentifier, DEFAULT_SIMILARITY);
	}

	/**
	 * See click(String). Here the similarity factor (see Sikuli's doc for more details) is given through parameters: the first matching
	 * region is used.
	 * 
	 * @param imageIdentifier
	 *            The identifier of the image used as pattern
	 * @param similarity
	 *            The similarity factor.
	 */
	public void click(String imageIdentifier, float similarity) {
		List<File> imageFiles = getImageFiles(imageIdentifier);

		for(File imageFile : imageFiles) {
			attachInlineImage(imageFile);
		}

		if(imageFiles == null) {
			testCase.addTestResult(false, "expected image " + getImageDescription(imageIdentifier) + " doesnt exist");
		} else {
			boolean clickPerformed = false;
			for(File imageFile : imageFiles) {
				if(tryClick(imageFile, similarity)) {
					clickPerformed = true;
					break;
				}
			}
			if(!clickPerformed) {
				testCase.addTestResult(false, "expected image " + getImageDescription(imageIdentifier) + " (similarity=" + similarity
						+ ") not found in screen");
			}
		}
	}

	/**
	 * Try to click on the image given through parameter.
	 * 
	 * @param patternImage
	 * @param similarity
	 * @return True if the pattern is found and click. False elsewhere
	 */
	public boolean tryClick(File patternImage, float similarity) {
		boolean clickPerformed = false;

		try {
			screen.click(new Pattern(patternImage.getAbsolutePath()).similar(similarity), 0);
			clickPerformed = true;
		} catch(FindFailed e) {
			clickPerformed = false;
		}

		return clickPerformed;
	}

	/**
	 * Moves mouse to the point defined by (x,y) from the top left corner of the best matching region and clicks (main button).
	 * 
	 * @param x
	 *            The x coordinate, beginning from the top left corner of the best matching region of the screen.
	 * @param y
	 *            The y coordinate, beginning from the top left corner of the best matching region of the screen.
	 */
	public void clickRelativeTopLeftCorner(String imageIdentifier, int x, int y) {
		List<File> imageFiles = getImageFiles(imageIdentifier);

		for(File imageFile : imageFiles) {
			attachInlineImage(imageFile);
		}

		boolean found = false;
		for(File imageFile : imageFiles) {
			String imagePath = imageFile.getAbsolutePath();
			try {
				Match match = screen.find(imagePath);
				click(match.x + x, match.y + y);
				found = true;
				break;
			} catch(FindFailed e) {
			}
		}

		if(!found) {
			testCase.addTestResult(false, "expected image " + getImageDescription(imageIdentifier) + " not found in screen");
		}
	}

	/**
	 * Moves mouse to the point defined by (x,y) and clicks (main button).
	 * 
	 * @param x
	 *            The x coordinate, beginning from the top left corner of the screen.
	 * @param y
	 *            The y coordinate, beginning from the top left corner of the screen.
	 */
	public void click(int x, int y) {
		mouseMove(x, y);
		robot.mouseDown(InputEvent.BUTTON1_MASK);
		robot.mouseUp(InputEvent.BUTTON1_MASK);
	}

	public void doubleClick(int x, int y) {
		mouseMove(x, y);
		robot.mouseDown(InputEvent.BUTTON1_MASK);
		robot.mouseUp(InputEvent.BUTTON1_MASK);
		try {
			Thread.sleep(50);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		robot.mouseDown(InputEvent.BUTTON1_MASK);
		robot.mouseUp(InputEvent.BUTTON1_MASK);
	}

	/**
	 * Moves the mouse to the point and clicks (main button).
	 * 
	 * @param p
	 *            The point to click
	 */
	public void click(Point p) {
		click(p.x, p.y);
	}

	public void doubleClick(Point p) {
		moveMouse(p);
		robot.mouseDown(InputEvent.BUTTON1_MASK);
		robot.mouseUp(InputEvent.BUTTON1_MASK);
		try {
			Thread.sleep(50);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		robot.mouseDown(InputEvent.BUTTON1_MASK);
		robot.mouseUp(InputEvent.BUTTON1_MASK);
	}

	/**
	 * Moves the mouse and clicks on the center of the area.
	 * 
	 * @param a
	 *            The Area to click
	 */
	public void click(Area a) {
		click(a.center());
	}

	/**
	 * Moves the mouse to the point.
	 * 
	 * @param target
	 *            The point where to move
	 */
	public void moveMouse(Point target) {
		robot.mouseMove(target.x, target.y);
	}

	/**
	 * Moves the mouse to the center of the best matching area of the screen.
	 * 
	 * @param imageIdentifier
	 *            The identifier of the image used as pattern
	 */
	public void moveMouse(String imageIdentifier) {
		moveMouse(findArea(imageIdentifier).center());
	}

	public void scrollUp(int wheelAmount) {
		getRobot().mouseWheel(-wheelAmount);
	}

	public void scrollDown(int wheelAmount) {
		getRobot().mouseWheel(wheelAmount);
	}

	/**
	 * Translates the mouse from its actual position to position + (x, y)
	 * 
	 * @param x
	 * @param y
	 */
	public void translateMouse(int x, int y) {
		PointerInfo info = MouseInfo.getPointerInfo();
		mouse_x = info.getLocation().x;
		mouse_y = info.getLocation().y;
		mouseMove(mouse_x + x, mouse_y + y);
	}

	/**
	 * Presses the main button of the mouse. Use releaseMouse() to release that button.
	 */
	public void pressMouse() {
		robot.mouseDown(InputEvent.BUTTON1_MASK);
	}

	/**
	 * Releases the main button of the mouse. Use pressMouse() to press that button.
	 */
	public void releaseMouse() {
		robot.mouseUp(InputEvent.BUTTON1_MASK);
	}

	/**
	 * Clicks with the main button of the mouse.
	 */
	public void click() {
		robot.mouseDown(InputEvent.BUTTON1_MASK);
		robot.mouseUp(InputEvent.BUTTON1_MASK);
	}

	/**
	 * Clicks with the right button of the mouse
	 */
	public void rightClick() {
		robot.mouseDown(InputEvent.BUTTON2_MASK);
		robot.mouseUp(InputEvent.BUTTON2_MASK);
	}

	/**
	 * Clicks the 3rd button of the mouse (wheel button for example)
	 */
	public void thirdClick() {
		robot.mouseDown(InputEvent.BUTTON3_MASK);
		robot.mouseUp(InputEvent.BUTTON3_MASK);
	}

	/**
	 * Click on the center of the pattern image found in the intersecting region. The intersecting region is the intersection between the
	 * horizontal stripe made from imageIntersectH and the vertical stripe made from imageIntersectV.
	 * 
	 * @param imageToClickName
	 *            The pattern image to click on.
	 * @param imageIntersectH
	 *            The pattern image used to determine horizontal stripe.
	 * @param imageIntersectV
	 *            The pattern image used to determine vertical stripe.
	 */
	public void clickIntersection(String imageToClickName, String imageIntersectH, String imageIntersectV) {
		attachInlineImage(imageToClickName);
		attachInlineImage(imageIntersectH);
		attachInlineImage(imageIntersectV);
		// Finding the intersecting images
		Match matchH = null, matchV = null;
		boolean matchesHaveBeenFound = true;
		try {
			matchH = screen.find(getImageCompleteName(imageIntersectH));
		} catch(FindFailed e) {
			e.printStackTrace();
			testCase.addTestResult(false, "expected image for horizontal intersection '" + imageIntersectH + "' not found in screen");
			matchesHaveBeenFound = false;
		}
		try {
			matchV = screen.find(getImageCompleteName(imageIntersectV));
		} catch(FindFailed e) {
			e.printStackTrace();
			testCase.addTestResult(false, "expected image for vertical intersection '" + imageIntersectV + "' not found in screen");
			matchesHaveBeenFound = false;
		}
		if(matchesHaveBeenFound == false) {
			testCase.addTestResult(false, "Intersection region can not be determined");
			return;
		}
		// Computing the intersection region
		Region intersectionRegion = new Region(new Rectangle(matchV.x, matchH.y, matchV.w, matchH.h));
		try {
			intersectionRegion.click(getImageCompleteName(imageToClickName), 0);
		} catch(FindFailed e) {
			testCase.addTestResult(false, "expected image '" + imageIntersectV + "' not found in intersection Region");
			File readRegion = getNewImageFile();
			images.saveImage(screen.capture(intersectionRegion).getImage(), readRegion);
			testCase.attachFile(readRegion, Attachment.Type.INLINE_IMAGE);
		}
	}

	/**
	 * Double clicks on the center of the best matching region of the screen.
	 * 
	 * @param imageIdentifier
	 *            The identifier of the image used as pattern.
	 */
	public void doubleClick(String imageIdentifier) {
		List<File> imageFiles = getImageFiles(imageIdentifier);

		for(File imageFile : imageFiles) {
			attachInlineImage(imageFile);
		}

		for(File imageFile : imageFiles) {
			String imagePath = imageFile.getAbsolutePath();
			try {
				screen.doubleClick(imagePath, 0);
			} catch(FindFailed e) {
				e.printStackTrace();
				testCase.addTestResult(false, "expected image " + getImageDescription(imageIdentifier) + " not found in screen");
			}
		}
	}

	/**
	 * Waits for the pattern image to appear in screen. The default timeout is used. See setAutoWaitTimeoutInSeconds to set that timeout.
	 * 
	 * @param imageIdentifier
	 *            The identifier of the image used as pattern.
	 */
	public void wait(String imageIdentifier) {
		wait(getImageFiles(imageIdentifier).get(0));
	}

	/**
	 * Waits for the pattern image to appear in screen. The pattern image is identified by the file of the TestObject
	 * 
	 * @param object
	 *            The object from object repository.
	 */
	public void wait(TestObject object) {
		wait(object.getFile(), DEFAULT_WAIT_TIMEOUT);
	}

	/**
	 * Waits for the pattern image to appear in screen.
	 * 
	 * @param image
	 *            The image's File
	 */
	public void wait(File image) {
		wait(image, DEFAULT_WAIT_TIMEOUT);
	}

	/**
	 * Acts like wait(TestObject object) but with specific timeout in seconds.
	 * 
	 * @param imageIdentifier
	 *            The identifier of the image used as pattern.
	 * @param timeoutInSeconds
	 *            The timeout in seconds (precision is millisecond).
	 */
	public void wait(String imageIdentifier, double timeoutInSeconds) {
		File imageFile = getImageFiles(imageIdentifier).get(0);
		wait(imageFile, timeoutInSeconds);
	}

	/**
	 * Acts like wait(File image) but with specific timeout in seconds.
	 * 
	 * @param image
	 *            The image's File
	 * @param timeoutInSeconds
	 *            The timeout in seconds (precision is millisecond).
	 */
	public void wait(File image, double timeoutInSeconds) {
		attachInlineImage(image);
		if(image == null) {
			testCase.addTestResult(false, "cannot wait for null image");
		} else {
			try {
				screen.wait(image.getAbsolutePath(), timeoutInSeconds);
			} catch(FindFailed e) {
				e.printStackTrace();
				testCase.addTestResult(false, "expected image '" + image.getAbsolutePath() + "' not found in screen");
			}
		}
	}

	/**
	 * Sets the default timeout used for methods: wait
	 * 
	 * @param seconds
	 *            The timeout in seconds (precision is millisecond).
	 */
	public void setDefaultTimeoutInSeconds(double seconds) {
		DEFAULT_WAIT_TIMEOUT = seconds;
	}

	/**
	 * Clicks on the center of the best matching region of the screen, then type the text given through parameters.
	 * 
	 * @param imageIdentifier
	 *            The identifier of the image used as pattern. 'sikuliImagesDirectory' configured by the constructor
	 * @param whatToType
	 *            The text to be typed. Use '\n' for key 'ENTER' and '\t' for key 'TAB'
	 */
	public void type(String imageIdentifier, String whatToType) {
		List<File> imageFiles = getImageFiles(imageIdentifier);

		for(File imageFile : imageFiles) {
			attachInlineImage(imageFile);
		}

		for(File imageFile : imageFiles) {
			String imagePath = getImagePath(imageIdentifier);
			if(imageFile == null) {
				testCase.addTestResult(false, "expected image " + getImageDescription(imageIdentifier) + " doesnt exist");
			} else {
				attachInlineImage(imageIdentifier);
				try {
					screen.type(imagePath, whatToType, 0);
					break;
				} catch(FindFailed e) {
					e.printStackTrace();
					testCase.addTestResult(false, "expected image '" + imageIdentifier + "' not found in screen");
				}
			}
		}
	}

	public void type(Area a, String whatToType) {
		a.getRegion().type(whatToType);
	}

	/**
	 * Presses a specific key with modifier.
	 * 
	 * @param key
	 *            The key to be pressed.
	 * @param modifier
	 *            The modifier.
	 */
	public void press(String key, int modifier) {
		try {
			screen.type(null, "" + key, modifier);
		} catch(FindFailed e) {
			e.printStackTrace();
		}
	}

	/**
	 * Types all chars of a String using the default keyboard.
	 * 
	 * @param whatToType
	 *            The String made of all characters to be typed.
	 */
	public void type(String whatToType) {
		try {
			screen.type(null, whatToType, 0);
		} catch(FindFailed e) {
			e.printStackTrace();
		}
	}

	/**
	 * Types the text using the French keyboard (to succeed, the computer has to be configured as French).
	 * 
	 * @param text
	 */
	public void typeFRKeyboard(String text) {
		for(char c : text.toCharArray()) {
			typeCharFRKeyboard(c);
		}
	}

	/**
	 * Types a char using a French keyboard.
	 * 
	 * @param c
	 *            The char to type.
	 */
	public void typeCharFRKeyboard(char c) {
		System.out.println("typeCharFRKeyboard: " + KeyStroke.getKeyStroke(c).getKeyCode());

		int[] keyCodes = Keyboard.getKeyCodeFRKeyboard(c);
		// pressing keys in normal order
		for(int i = 0; i < keyCodes.length; i++) {
			robot.keyDown(keyCodes[i]);
		}
		// releasing keys in reverse order
		for(int i = 0; i < keyCodes.length; i++) {
			robot.keyUp(keyCodes[i]);
		}
	}

	/**
	 * Clicks on the center of the best matching region of the screen, then paste the text given through parameters.
	 * 
	 * @param imageIdentifier
	 *            The identifier of the image used as pattern.
	 * @param whatToType
	 *            The text to be pasted.
	 */
	public void paste(String imageIdentifier, String whatToType) {
		Area area = findArea(imageIdentifier);

		if(area == null) {
			testCase.addTestResult(false, "expected image " + getImageDescription(imageIdentifier) + " doesnt exist");
		} else {
			area.paste(whatToType);
		}
	}

	/**
	 * Pastes the text given through parameters.
	 * 
	 * @param whatToType
	 *            The text to be pasted.
	 */
	public void paste(String whatToType) {
		screen.paste(whatToType);
	}

	/**
	 * Checks that the pattern image is found in screen.
	 * 
	 * @param imageIdentifier
	 *            The identifier of the image used as pattern.
	 */
	public void checkFind(String imageIdentifier) {
		if(find(imageIdentifier)) {
			testCase.addTestResult(true, "expected image " + getImageDescription(imageIdentifier) + " is found in screen");
		} else {
			testCase.addTestResult(false, "expected image " + getImageDescription(imageIdentifier) + " doesnt exist");
		}
	}

	/**
	 * Returns if the pattern image is found on the screen.
	 * 
	 * @param imageIdentifier
	 *            The identifier of the image used as pattern.
	 * @return True if the pattern image is found on the screen. False elsewhere.
	 */
	public boolean find(String imageIdentifier) {
		return findArea(imageIdentifier) != null;
	}

	/**
	 * Acts like find(String imageIdentifier), but with a specific similarity (1.0 for exact matching, 0.1 for bad matching)
	 * 
	 * @param imageIdentifier
	 *            The identifier of the image used as pattern.
	 * @param similarity
	 *            The similarity factor to be used: from 0.0 (bad match) to 1.0 (exact match)
	 * @return True if the pattern image is found on the screen. False elsewhere.
	 */
	public boolean find(String imageIdentifier, float similarity) {
		return findArea(imageIdentifier, similarity) != null;
	}

	/**
	 * Acts like find(String imageIdentifier), but the search is limited to a specific Area
	 * 
	 * @param imageIdentifier
	 *            The identifier of the image used as pattern.
	 * @param restrictedArea
	 *            The Area in which the pattern image is searched.
	 * @return True if the pattern image is found on the screen. False elsewhere.
	 */
	public boolean find(String imageIdentifier, Area restrictedArea) {
		return findArea(imageIdentifier, restrictedArea) != null;
	}

	/**
	 * Acts like find(String imageIdentifier), but with a specific similarity (1.0 for exact matching, 0.1 for bad matching) and the search
	 * is limited to a specific Area
	 * 
	 * @param imageIdentifier
	 *            The identifier of the image used as pattern.
	 * @param restrictedArea
	 *            The Area in which the pattern image is searched.
	 * @param similarity
	 *            The similarity factor to be used: from 0.0 (bad match) to 1.0 (exact match)
	 * @return True if the pattern image is found on the screen. False elsewhere.
	 */
	public boolean find(String imageIdentifier, Area restrictedArea, float similarity) {
		return findArea(imageIdentifier, restrictedArea, similarity) != null;
	}

	/**
	 * Returns the Area that is the best matching to the pattern image.
	 * 
	 * @param imageIdentifier
	 *            The identifier of the image used as pattern.
	 * @return The Area that is the best matching to the pattern image. Null if no match is found.
	 */
	public Area findArea(String imageIdentifier) {
		return findArea(imageIdentifier, getScreenArea(), DEFAULT_SIMILARITY);
	}

	/**
	 * Acts like findArea(String imageIdentifier), but the search is limited to a specific Area.
	 * 
	 * @param imageIdentifier
	 *            The identifier of the image used as pattern.
	 * @param restrictedArea
	 *            The Area in which the pattern image is searched.
	 * @return The Area that is the best matching to the pattern image. Null if no match is found.
	 */
	public Area findArea(String imageIdentifier, Area restrictedArea) {
		return findArea(imageIdentifier, restrictedArea, DEFAULT_SIMILARITY);
	}

	/**
	 * Acts like findArea(String imageIdentifier), but with a specific similarity (1.0 for exact matching, 0.1 for bad matching)
	 * 
	 * @param imageIdentifier
	 *            The identifier of the image used as pattern.
	 * @param similarity
	 *            The similarity factor to be used: from 0.0 (bad match) to 1.0 (exact match)
	 * @return The Area that is the best matching to the pattern image. Null if no match is found.
	 */
	public Area findArea(String imageIdentifier, float similarity) {
		return findArea(imageIdentifier, getScreenArea(), similarity);
	}

	/**
	 * Acts like findArea(String imageIdentifier), but with a specific similarity (1.0 for exact matching, 0.1 for bad matching) and the
	 * search is limited into a specific Area.
	 * 
	 * @param imageIdentifier
	 *            The identifier of the image used as pattern.
	 * @param restrictedArea
	 *            The Area in which the pattern image is searched.
	 * @param similarity
	 *            The similarity factor to be used: from 0.0 (bad match) to 1.0 (exact match)
	 * @return The Area that is the best matching to the pattern image. Null if no match is found.
	 */
	public Area findArea(String imageIdentifier, Area restrictedArea, float similarity) {
		List<File> imageFiles = getImageFiles(imageIdentifier);

		for(File imageFile : imageFiles) {
			attachInlineImage(imageFile);
		}

		Match match = null;

		if(imageFiles == null) {
			throw new RuntimeException("File " + getImageDescription(imageIdentifier) + " doesn't exist");
		} else {
			for(File imageFile : imageFiles) {
				String imagePath = imageFile.getAbsolutePath();
				try {
					match = restrictedArea.getRegion().find(new Pattern(imagePath).similar(similarity));
					break;
				} catch(FindFailed e) {
				}
			}
		}

		if(match == null) {
			this.testCase.addComment("Area not found", TestToolSikuli.class);
			return null;
		} else {
			Area result = new Area(this, match);
			this.testCase.addComment("Area found at: (x=" + result.topLeftCorner().x + ",y=" + result.topLeftCorner().y + ")",
					TestToolSikuli.class);
			return result;
		}
	}

	public Area findArea(BufferedImage image) {
		String imagePath = attachInlineImage(image).getAbsolutePath();

		Match match = null;
		try {
			match = screen.find(new Pattern(imagePath).similar(DEFAULT_SIMILARITY));
		} catch(FindFailed e) {
			e.printStackTrace();
		}
		if(match == null) {
			this.testCase.addComment("Area not found", TestToolSikuli.class);
			return null;
		} else {
			Area result = new Area(this, match);
			this.testCase.addComment("Area found at: (x=" + result.topLeftCorner().x + ",y=" + result.topLeftCorner().y + ")",
					TestToolSikuli.class);
			return result;
		}
	}

	public Area[] findAreas(String imageIdentifier) {
		return findAreas(imageIdentifier, getScreenArea(), DEFAULT_SIMILARITY);
	}

	public Area[] findAreas(String imageIdentifier, float similarity) {
		return findAreas(imageIdentifier, getScreenArea(), similarity);
	}

	public Area[] findAreas(String imageIdentifier, Area restrictedArea, float similarity) {
		logger.debug("findAreas imageIdentifier='" + imageIdentifier + "' similarity=" + similarity);
		List<File> imageFiles = getImageFiles(imageIdentifier);

		if(TestCase.enableTestToolsLogs) {
			for(File imageFile : imageFiles) {
				attachInlineImage(imageFile);
			}
		}

		List<Match> matches = new ArrayList<Match>();

		if(imageFiles == null) {
			throw new RuntimeException("File '" + getImageDescription(imageIdentifier) + "' doesn't exist");
		} else {
			for(File imageFile : imageFiles) {
				String imagePath = imageFile.getAbsolutePath();
				try {
					Iterator<Match> ite = restrictedArea.getRegion().findAll(new Pattern(imagePath).similar(similarity));
					while(ite.hasNext()) {
						matches.add(ite.next());
					}
				} catch(FindFailed e) {
				}
			}
		}

		Area[] areas = new Area[matches.size()];
		int index = 0;
		for(Match m : matches) {
			areas[index] = new Area(this, m);
			index++;
		}
		return areas;
	}

	public Area[] getAreas(String imageIdentifier, AreaTransformer transformer) {
		Area[] allAreas = findAreas(imageIdentifier);
		List<Area> areas = new ArrayList<Area>();
		for(Area area : allAreas) {
			areas.add(transformer.transform(area));
		}
		return areas.toArray(new Area[areas.size()]);
	}

	public Area getBestAreaFromText(String imageIdentifier, AreaTransformer transformer, StringSelector selector) {
		return getBestAreaFromText(imageIdentifier, transformer, selector, getScreenArea(), DEFAULT_SIMILARITY);
	}

	public Area getBestAreaFromText(String imageIdentifier, AreaTransformer transformer, StringSelector selector, Area restrictedArea,
			float similarity) {
		Area result = null;

		List<String> texts = new LinkedList<String>();
		List<Area> areas = new LinkedList<Area>();

		boolean ttl = testCase.deactivateTestToolsLog();
		for(Area area : findAreas(imageIdentifier, restrictedArea, similarity)) {
			texts.add(transformer.transform(area).getText());
			areas.add(area);
		}
		testCase.activateTestToolsLog(ttl);

		int index = selector.getIndex(texts);
		if((index >= 0) && (index < texts.size())) {
			return areas.get(index);
		} else {
			return result;
		}
	}

	/**
	 * Returns the text appearing near the best match, from matchCenter(x,y) + offset(x,y) until matchCenter(x,y) + offset(x,y) + (width,
	 * height)
	 * 
	 * @param imageIdentifier
	 *            The identifier of the image used as pattern.
	 * @param offsetX
	 *            the 'x' offset from center of match for starting mouse selection
	 * @param offsetY
	 *            the 'y' offset from center of match for starting mouse selection
	 * @param width
	 *            the width of the region to read
	 * @param height
	 *            the height of the region to read
	 * @return The text recognized by Tesseract (the OCR used by Sikuli)
	 */
	public String getText(String imageIdentifier, int offsetX, int offsetY, int width, int height) {
		String imageCompleteName = getImageCompleteName(imageIdentifier);
		if(imageIdentifier.contains("(")) {
			imageCompleteName = TestObject.getFromRepo(imageIdentifier).getFile().getAbsolutePath();
		}

		attachInlineImage(imageCompleteName);
		try {
			Match match = screen.find(imageCompleteName);
			// computing the reading area
			int startX = match.x + (int) (match.w / 2.0) + offsetX;
			int startY = match.y + (int) (match.h / 2.0) + offsetY;
			System.out.println("Region: " + startX + ", " + startY + ", " + width + ", " + height);
			Region region = new Region(new Rectangle(startX, startY, width, height));
			File readRegion = getNewImageFile();
			images.saveImage(screen.capture(region).getImage(), readRegion);
			testCase.attachFile(readRegion, Attachment.Type.INLINE_IMAGE);
			String result = region.text();
			testCase.addComment(new Field("text", result), TestToolSikuli.class);
			return result;
		} catch(FindFailed e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns the text appearing in the Area.
	 * 
	 * @param area
	 *            The Area where to read the text.
	 * @return The text appearing in the Area.
	 */
	public String getText(Area area) {
		return area.getRegion().text();
	}

	/**
	 * Moves the mouse with left-click pressed from the center of the first matching region to (xMove, yMove) more pixels.
	 * 
	 * @param imageIdentifier
	 *            The identifier of the image used as pattern.
	 * @param xMove
	 *            The x coordinate of the drop point.
	 * @param yMove
	 *            The y coordinate of the drop point.
	 */
	public void dragDrop(String imageIdentifier, int xMove, int yMove) {
		Area area = findArea(imageIdentifier);

		if(area != null) {
			int startX = area.center().x;
			int startY = area.center().y;
			int endX = startX + xMove;
			int endY = startY + yMove;
			mouseMove(startX, startY);
			robot.mouseDown(InputEvent.BUTTON1_MASK);
			mouseMove(endX, endY);
			robot.mouseUp(InputEvent.BUTTON1_MASK);
		} else {
			testCase.addTestResult(false, "expected image '" + getImageDescription(imageIdentifier) + "' not found in screen");
		}
	}

	/**
	 * Saves a screenshot and attaches the file to the test result.
	 */
	public void captureScreen() {
		File capturedImage = getNewImageFile();
		TestToolImages.saveImage(screen.capture().getImage(), capturedImage);
		testCase.attachFile(capturedImage, Attachment.Type.IMAGE);
	}

	/**
	 * Saves a screenshot that fit the Region and attaches the file to the test result.
	 * 
	 * @param region
	 *            The Region to capture.
	 */
	public void captureRegion(Region region) {
		File capturedImage = getNewImageFile();
		TestToolImages.saveImage(screen.capture(region).getImage(), capturedImage);
		testCase.attachFile(capturedImage, Attachment.Type.IMAGE);
	}

	/**
	 * Saves a screenshot that fit the Area and attaches the file to the test result.
	 * 
	 * @param area
	 *            The Area to capture.
	 */
	public void captureArea(Area area) {
		captureArea(area, true);
	}

	public void captureArea(Area area, boolean inline) {
		File capturedImage = getNewImageFile();
		TestToolImages.saveImage(screen.capture(area.getRegion()).getImage(), capturedImage);
		if(inline) {
			testCase.attachFile(capturedImage, Attachment.Type.INLINE_IMAGE);
		} else {
			testCase.attachFile(capturedImage, Attachment.Type.IMAGE);
		}
	}

	/**
	 * Includes a copy of the image into the test case's result.
	 * 
	 * @param imageName
	 *            The name of the file to include
	 */
	private void attachInlineImage(String imageName) {
		testCase.attachFile(getImageFiles(imageName).get(0), Attachment.Type.INLINE_IMAGE);
	}

	/**
	 * Includes a copy of the image into the test case's result.
	 * 
	 * @param image
	 *            the file to include
	 */
	private void attachInlineImage(File image) {
		testCase.attachFile(image, Attachment.Type.INLINE_IMAGE);
	}

	public File attachInlineImage(BufferedImage buffImg) {
		File capturedImage = getNewImageFile();
		TestToolImages.saveImage(buffImg, capturedImage);
		testCase.attachFile(capturedImage, Attachment.Type.IMAGE);
		return capturedImage;
	}

	/**
	 * Returns the image's File from it's identifier
	 * 
	 * @param imageIdentifier
	 *            The identifier of the image used as pattern.
	 * @return The image's File from it's identifier
	 */
	public List<File> getImageFiles(String imageIdentifier) {
		List<File> result = new LinkedList<File>();
		if(imageIdentifier.contains("(")) {
			TestObject obj = TestObject.getFromRepo(imageIdentifier);
			if(obj != null) {
				result = getImageFiles(obj);
			} else {
				testCase.addTestResult(false, "Image from identifier '" + imageIdentifier + "' does not exist.");
			}
		} else {
			result.add(new File(imgDir.getPath() + "/" + imageIdentifier));
		}
		return result;
	}

	public static List<File> getImageFiles(TestObject obj) {
		List<File> result = new LinkedList<File>();
		result.add(obj.getFile());
		boolean noMoreAlternativeFile = false;
		int alternativeIndex = 1;
		while(noMoreAlternativeFile != true) {
			String alternativeImageId = "alt_" + alternativeIndex;
			TestObject alt = obj.get("image(" + alternativeImageId + ")");
			if(alt == null) {
				noMoreAlternativeFile = true;
			} else {
				if(alt.getFile() == null) {
					ErrorsAndWarnings.addError("Error in object repository: object '" + TestObject.getPath(obj)
							+ "' does not describe correct file");
				} else {
					result.add(alt.getFile());
				}
			}
			alternativeIndex++;
		}
		return result;
	}

	/**
	 * Returns the path of the image's file.
	 * 
	 * @param imageIdentifier
	 *            The identifier of the image used as pattern.
	 * @return The path of the image's file.
	 */
	public String getImagePath(String imageIdentifier) {
		File img = getImageFiles(imageIdentifier).get(0);
		if(img != null) {
			return img.getAbsolutePath();
		} else {
			return null;
		}
	}

	/**
	 * Returns a string describing shortly the image.
	 * 
	 * @param imageIdentifier
	 *            The identifier of the image used as pattern.
	 * @return A string describing shortly the image.
	 */
	public String getImageDescription(String imageIdentifier) {
		return "'" + imageIdentifier + "' (" + getImagePath(imageIdentifier) + ")";
	}

	/**
	 * Acts like getImageDescription(String imageIdentifier), but using a TestObject instead of imageIdentifier
	 * 
	 * @param object
	 *            The TestObject defining the image.
	 * @return A string describing shortly the image.
	 */
	public String getImageDescription(TestObject object) {
		return "'" + TestObject.getPath(object) + "' (" + object.attribute("file") + ")";
	}

	public static void takeScreenShot(File outputFile) {
		Screen staticScreen = new Screen();
		TestToolImages.saveImage(staticScreen.capture().getImage(), outputFile);
	}

	/**
	 * Moves the mouse to the coordinates.
	 * 
	 * @param x
	 *            The x coordinate where the mouse is moved.
	 * @param y
	 *            The y coordinate where the mouse is moved.
	 */
	private void mouseMove(int x, int y) {
		robot.mouseMove(x, y);
		this.mouse_x = x;
		this.mouse_y = y;
	}

	/**
	 * Creates a new empty File with auto-generated name.
	 * 
	 * @return a new empty File with auto-generated name.
	 */
	private File getNewImageFile() {
		String imageFileName = imgDir.getPath() + "/" + AUTO_CAPTURED_IMAGES_PREFIX + (AUTO_CAPTURED_IMAGES_COUNT + 1) + ".png";
		AUTO_CAPTURED_IMAGES_COUNT++;
		return new File(imageFileName);
	}

	/**
	 * Returns the absolute path of an image File from it's local name (relative to images' directory).
	 * 
	 * @param imageName
	 *            The relative path of the image from the origin folder used in constructor.
	 * @return
	 */
	private String getImageCompleteName(String imageName) {
		File image = getImageFiles(imageName).get(0);
		if(image != null) {
			return image.getAbsolutePath();
		} else {
			return null;
		}
	}
}
