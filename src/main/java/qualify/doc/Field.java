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
import java.util.Map;

import org.jdom.Element;

public class Field implements DomElementAble {

	public static final String
	ELEMENT_NAME = "field",
	ELEMENT_LABEL_NAME = "label",
	ELEMENT_VALUE_NAME = "value";

	private String label = null;
	private DomElementAble value = null;

	public Field(String label, String value) {
		this.label = label;
		this.value = new DocString(value);
	}
	
	public Field(String label, DomElementAble value) {
		this.label = label;
		this.value = value;
	}

	public Element toDomElement() {
		Element mainElement = new Element(ELEMENT_NAME);
		mainElement.setAttribute(ELEMENT_LABEL_NAME, label);
		mainElement.addContent(value.toDomElement());
		return mainElement;
	}

	public String toStringForConsole() {
		return label + ": " + value.toStringForConsole();
	}

	public Map<File, String> getAttachedFiles() {
		return null;
	}

}
