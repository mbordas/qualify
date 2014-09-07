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

package qualify.doc;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

import qualify.ErrorsAndWarnings;
import qualify.tools.TestToolFile;
import qualify.tools.TestToolImages;
import qualify.tools.geometry.Area;
import qualify.tools.geometry.Shape;

public class Graphic implements DomElementAble {

	public static String
	TAG_NAME = "graphic",
	BACKGROUND_IMAGE_ATTRIBUTE = "background_image";

	private File backgroundImage = null;
	private List<Shape> shapes = null;
	private List<String> labels = null;
	private int x=0, y=0;

	public Graphic() {
		shapes = new LinkedList<Shape>();
		labels = new LinkedList<String>();
	}

	public Graphic(File backgroundImage) {
		shapes = new LinkedList<Shape>();
		labels = new LinkedList<String>();
		setBackgroundImage(backgroundImage);
	}

	public Graphic (Area area) {
		shapes = new LinkedList<Shape>();
		labels = new LinkedList<String>();
		setBackgroundArea(area);
	}
	
	public void setBackgroundArea(Area area) {
		x = area.topLeftCorner().x;
		y = area.topLeftCorner().y;
		File bgdImage;
		try {
			bgdImage = TestToolImages.saveImage(area.getImage(), TestToolFile.createNewTemporaryFile("png"));
			setBackgroundImage(bgdImage);
		} catch (IOException e) {
			ErrorsAndWarnings.addException(e);
		}
		
	}

	public void setBackgroundImage(File img) {
		if(img == null) {
			ErrorsAndWarnings.addError("Background image file is null");
		}
		this.backgroundImage = img;
	}
	
	public void addShape(Shape s) {
		if(s != null) {
			shapes.add(s);
		}
	}

	public void addShape(Shape s, String label) {
		if(s != null) {
			s.setLabel(label);
			shapes.add(s);
		}
	}

	public Element toDomElement() {
		// Building the 'graphic' tag
		Element result = new Element(TAG_NAME);
		int width = 50;
		int height = 50;
		if(backgroundImage != null) {
			Dimension d = TestToolImages.getImageDimension(backgroundImage);
			width = d.width;
			height = d.height;
			result.setAttribute(BACKGROUND_IMAGE_ATTRIBUTE, backgroundImage.getName());
			Attachment.attachFileToElement(result, backgroundImage, backgroundImage.getName());
		}
		result.setAttribute("x", "" + this.x);
		result.setAttribute("y", "" + this.y);
		result.setAttribute("width", "" + width);
		result.setAttribute("height", "" + height);
		result.setAttribute("show_areas", "true");

		// Adding the shapes
		for(int index = 0; index < shapes.size(); index++) {
			Shape shape = shapes.get(index);
			result.addContent(shape.toDomElement());
		}

		return result;
	}

	public String toStringForConsole() {
		return null;
	}

	public Map<File, String> getAttachedFiles() {
		if(backgroundImage != null) {
			Map<File, String> result = new HashMap<File, String>();
			result.put(backgroundImage, backgroundImage.getName());
			return result;
		} else {
			return null;
		}
	}

}
