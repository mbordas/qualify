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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Element;

import qualify.ErrorsAndWarnings;
import qualify.tools.TestToolFile;

public class Attachment implements DomElementAble {
	
	private static Logger logger = Logger.getLogger(Attachment.class);

	private static final String
	ELEMENT_NAME = "attachment",
	SOURCE_FILE_ATTRIBUTE_NAME = "source_file",
	TYPE_ATTRIBUTE_NAME = "type",
	ATTACHMENT_FILE_ATTRIBUTE_NAME = "attachment_file";
	
	public static enum Type {
	    TEXT, BINARY, IMAGE, INLINE_IMAGE, XML, OTHER 
	}
	
	private int lineNumber = -1;
	private File tmpFile = null;
	private String sourceCompletePath = null;
	private File sourceFile = null, temporaryCopiedFile = null;
	private Type type = null;
	
	public Attachment(File file, String fileName, int lineNumber, Type type) {
		try {
			tmpFile = TestToolFile.createNewTemporaryFile(fileName);
			sourceCompletePath = file.getAbsolutePath();
			TestToolFile.copyFile(file, tmpFile);
		} catch (IOException e) {
			ErrorsAndWarnings.addException(e);
		}
		this.lineNumber = lineNumber;
		this.type = type;
	}
	
	public int getLineNumber() {
		return lineNumber;
	}
	
	public File getTemporaryAttachmentFile() {
		return temporaryCopiedFile;
	}
	
	public Element toDomElement() {
		Element attachmentElement = new Element(ELEMENT_NAME);
		attachmentElement.setAttribute(SOURCE_FILE_ATTRIBUTE_NAME, this.sourceCompletePath);
		attachmentElement.setAttribute(TYPE_ATTRIBUTE_NAME, type.toString());
		attachmentElement.setAttribute(ATTACHMENT_FILE_ATTRIBUTE_NAME, tmpFile.getName());
		attachFileToElement(attachmentElement, tmpFile, tmpFile.getName());
		return attachmentElement;
	}
	
	public String toStringForConsole() {
		return "Attachment source file: " + this.sourceFile.getAbsolutePath();
	}

	public Map<File, String> getAttachedFiles() {
		if(temporaryCopiedFile != null) {
			Map<File, String> result = new HashMap<File, String>();
			result.put(temporaryCopiedFile, temporaryCopiedFile.getName());
			return result;
		} else {
			return null;
		}
	}
	
	public static void attachFileToElement(Element element, File file, String relativePath) {
		int index = 1;
		while(element.getAttribute(getAttributeNameForFile(index)) != null) {
			index++;
		}
		element.setAttribute(getAttributeNameForFile(index), file.getAbsolutePath());
		element.setAttribute(getAttributeNameForPath(index), relativePath);
	}
	
	public static int copyAttachedFiles(Element element, File rootDir) {
		int result = 0;
		int index = 1;
		while(element.getAttribute(getAttributeNameForFile(index)) != null) {
			File srcFile = new File(element.getAttributeValue(getAttributeNameForFile(index)));
			File destFile = new File(rootDir.getPath() + "/" + element.getAttributeValue(getAttributeNameForPath(index)));
			try {
				TestToolFile.copyFile(srcFile, destFile);
				result++;
			} catch (IOException e) {
				ErrorsAndWarnings.addException(e);
			}
			index++;
		}
		for(Object subElementObject : element.getChildren()) {
			Element subElement = (Element)subElementObject;
			copyAttachedFiles(subElement, rootDir);
		}
		return result;
	}
	
	private static String getAttributeNameForFile(int index) {
		return "ATTACHED_FILE_SOURCE_" + index;
	}
	private static String getAttributeNameForPath(int index) {
		return "ATTACHED_FILE_DESTINATION_" + index;
	}

}
