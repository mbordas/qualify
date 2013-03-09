/*Copyright (c) 2011, Mathieu Bordas
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

public class Span implements DomElementAble {

	public static final String ELEMENT_NAME = "span";

	private String cssClass = null;
	private String text = null;
	private List<DomElementAble> elements = null;

	public Span(String text, String cssClass) {
		this.text = text;
		this.cssClass = cssClass;
	}

	public void add(DomElementAble element) {
		if(elements == null) {
			elements = new ArrayList<DomElementAble>();
		}
		elements.add(element);
	}

	public Element toDomElement() {
		Element mainElement = new Element(ELEMENT_NAME);
		if(cssClass != null) {
			mainElement.setAttribute("css_class", cssClass);
		}
		if(text != null) {
			mainElement.setText(text);
		}
		if(elements != null) {
			for(DomElementAble element : elements) {
				mainElement.addContent(element.toDomElement());
			}
		}
		return mainElement;
	}

	public String toStringForConsole() {
		String result = "";
		if(text != null) {
			result = text + "\n";
		}
		if(elements != null) {
			for(DomElementAble element : elements) {
				result += " " + element.toStringForConsole();
			}
		}
		return result;
	}

	public Map<File, String> getAttachedFiles() {
		return null;
	}

}
