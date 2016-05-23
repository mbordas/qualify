/*
 * Copyright (c) 2012-2016 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package qualify.doc;

import java.io.File;
import java.util.Map;

import org.jdom.Element;

public class Base64Image implements DomElementAble {
	private static final String TAG_NAME = "base64Image";
	private static final String CONTENT_NAME = "content";
	
	private String content = null;
	
	public Base64Image(String content) {
		this.content = content;
	}

	public Element toDomElement() {
		Element element = new Element(TAG_NAME);

		element.setAttribute(CONTENT_NAME, content);

		return element;
	}

	public Map<File, String> getAttachedFiles() {
		return null;
	}

	public String toStringForConsole() {
		// TODO Auto-generated method stub
		return null;
	}

}
