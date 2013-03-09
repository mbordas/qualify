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
import java.util.HashMap;
import java.util.Map;

import org.jdom.Element;

public class DocTreeNode implements DomElementAble {

	private ArrayList<DocTreeNode> children = null;
	private HashMap<String, String> parameters = null;
	private DocTreeNode parent = null;
	private int localIndex = 0;
	private String htmlId = null;

	private static int treeUniqueIndex = 0;

	public DocTreeNode() {
		children = new ArrayList<DocTreeNode>();
		parameters = new HashMap<String, String>();
		treeUniqueIndex++;
		htmlId = "" + treeUniqueIndex;
	}

	public DocTreeNode(String key, String value) {
		children = new ArrayList<DocTreeNode>();
		parameters = new HashMap<String, String>();
		parameters.put(key, value);
		treeUniqueIndex++;
		htmlId = "" + treeUniqueIndex;
	}

	public void setParent(DocTreeNode parent) {
		this.parent = parent;
	}

	public DocTreeNode getParent() {
		return parent;
	}

	public void setLocalIndex(int localIndex) {
		this.localIndex = localIndex;
	}

	public int getLocalIndex() {
		return localIndex;
	}

	private void removeHtmlId() {
		htmlId = null;
	}

	public String getHtmlId() {
		return htmlId;
	}

	public void setParameter(String key, String value) {
		parameters.put(key, value);
	}

	public String getParameterValue(String key) {
		return parameters.get(key);
	}

	public void addChild(DocTreeNode child) {
		children.add(child);
		child.setParent(this);
		child.setLocalIndex(children.size());
		child.removeHtmlId();
	}

	public int getChildrenCount() {
		return children.size();
	}

	public int getChildrenCount(String key, String value) {
		int result = 0;
		for(DocTreeNode n : children) {
			if(value.equals(n.getParameterValue(key))) {
				result++;
			}
			result += n.getChildrenCount(key, value);
		}
		return result;
	}

	public Element toDomElement() {
		Element nodeElement = new Element("tree_node");
		if(getHtmlId() != null) {
			nodeElement.setAttribute("id", getHtmlId());
		}
		for(String key : parameters.keySet()) {
			nodeElement.setAttribute(key, parameters.get(key));
		}
		for(DocTreeNode child : children) {
			nodeElement.addContent(child.toDomElement());
		}
		nodeElement.setAttribute("path_index", getPathIndex());
		nodeElement.setAttribute("local_index", "" + getLocalIndex());
		return nodeElement;
	}

	public String toStringForConsole() {
		return toStringForConsole("");
	}

	public String toStringForConsole(String prefix) {
		String result = prefix;
		if(children.size() > 0) {
			result += "+";
		} else {
			result += ">";
		}
		result += " ";
		for(String key : parameters.keySet()) {
			result += key + " = '" + parameters.get(key) + "'; ";
		}
		for(DocTreeNode child : children) {
			result += "\n";
			result += child.toStringForConsole(prefix + "|");
		}
		return result;
	}

	public String getPathIndex() {
		String result = "";
		DocTreeNode currentNode = getParent();
		while(currentNode != null) {
			result = "" + currentNode.getLocalIndex() + result;
			if(currentNode.getParent() != null) {
				result = "-" + result;
			}
			currentNode = currentNode.getParent();
		}
		return result;
	}

	public Map<File, String> getAttachedFiles() {
		return null;
	}

}
