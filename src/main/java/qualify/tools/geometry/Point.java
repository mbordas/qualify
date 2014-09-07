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

import java.io.File;
import java.util.Map;

import org.jdom.Element;

import qualify.tools.TestToolSikuli;


/**
 * A Point object is one point of the screen. It's localized by two integers (x, y).
 * The origin's point is the top left corner of the screen.
 * @author Mathieu Bordas
 *
 */
public class Point extends Shape {

	public static String POINT_DEFAULT_COLOR = "green";

	private TestToolSikuli sikuli = null;
	public int x, y;

	/**
	 * Point constructor: the point is placed to (x, y) coordinates. The origin's
	 * point is the top left corner of the screen.
	 * @param skl
	 * @param x
	 * @param y
	 */
	public Point(TestToolSikuli skl, int x, int y) {
		this.x = x;
		this.y = y;
		this.sikuli = skl;
		setColor(POINT_DEFAULT_COLOR);
	}

	public Point(Point p) {
		this.x = p.x;
		this.y = p.y;
		this.sikuli = p.sikuli;
		setColor(POINT_DEFAULT_COLOR);
	}

	/**
	 * Returns the translated point, following the vector (x, y).
	 * @param x Translation amount to the right.
	 * @param y Translation amount to the bottom.
	 * @return
	 */
	public Point translate(int x, int y) {
		this.x += x;
		this.y += y;
		return this;
	}

	/**
	 * Returns the distance separating the two points.
	 * @param point The point to witch the distance is computed.
	 * @return
	 */
	public double distanceTo(Point point) {
		int dX = point.x - x;
		int dY = point.y - y;
		return Math.sqrt(dX * dX + dY * dY);
	}

	public void click() {
		sikuli.click(this.x, this.y);
	}

	public void doubleClick() {
		sikuli.doubleClick(this.x, this.y);
	}

	public void type(String whatToType) {
		sikuli.click(x, y);
		sikuli.type(whatToType);
	}

	public Map<File, String> getAttachedFiles() {
		return null;
	}

	public Element toDomElement() {
		Element pointElement = new Element("point");
		pointElement.setAttribute("x", "" + x);
		pointElement.setAttribute("y", "" + y);
		if(label.length() > 0) {
			pointElement.setAttribute("label", label);
		} else {
			pointElement.setAttribute("label", "" + x + "," + y);
		}
		pointElement.setAttribute("color", color);
		return pointElement;
	}

	public String toStringForConsole() {
		return null;
	}

}
