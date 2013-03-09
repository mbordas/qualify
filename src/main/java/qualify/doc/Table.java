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

public class Table implements DomElementAble {
	
	public static final String
	ELEMENT_NAME = "table",
	ELEMENT_ENTRY_NAME = "entry",
	ELEMENT_CELL_NAME = "cell";

	protected List<String> headers = null;
	protected List<List<DomElementAble>> entries = null;

	public Table(String[] headers) {
		this.headers = new ArrayList<String>();
		entries = new ArrayList<List<DomElementAble>>();
		for(String header : headers) {
			this.headers.add(header);
		}
	}
	
	public Table() {
		entries = new ArrayList<List<DomElementAble>>();
	}

	public int addEntry(DomElementAble[] elements) {
		List<DomElementAble> entry = new ArrayList<DomElementAble>();
		for(DomElementAble element : elements) {
			entry.add(element);
		}
		entries.add(entry);
		return entries.size() - 1;
	}
	
	public int addEntry(String[] values) {
		List<DomElementAble> entry = new ArrayList<DomElementAble>();
		for(String value : values) {
			entry.add(new DocString(value));
		}
		entries.add(entry);
		return entries.size() - 1;
	}

	public Element toDomElement() {
		Element mainElement = new Element(ELEMENT_NAME);
		for(List<DomElementAble> entry : entries) {
			Element entryElement = new Element(ELEMENT_ENTRY_NAME);
			for(DomElementAble cell : entry) {
				Element cellElement = new Element(ELEMENT_CELL_NAME);
				cellElement.addContent(cell.toDomElement());
				
				entryElement.addContent(cellElement);
			}
			
			mainElement.addContent(entryElement);
		}
		return mainElement;
	}
	
	public String toStringForConsole() {
		return "table";
	}

	public Map<File, String> getAttachedFiles() {
		return null;
	}

}
