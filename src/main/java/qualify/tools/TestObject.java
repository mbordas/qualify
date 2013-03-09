/*Copyright (c) 2010-2011, Mathieu Bordas
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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import qualify.ErrorsAndWarnings;

public class TestObject {

	static Logger logger = Logger.getLogger(TestObject.class);

	public static final String ATTRIBUTE_ID = "id";

	private TestObject parent = null;
	private String id = null, type = null;
	private HashMap<String, String> attributes = null;
	private HashMap<String, TestObject> children = null;

	private File repositoryFile = null, 
	attachedFile = null;

	private static HashMap<String, TestObject> repositories = new HashMap<String, TestObject>();

	public static TestObject loadObjectRepository(File objectRepository) {
		TestObject result = new TestObject(objectRepository);
		String repoName = result.getId();
		if(repoName != null) {
			if(! repositories.containsKey(repoName)) {
				repositories.put(getLocalPath(result), result);
			} else {
				ErrorsAndWarnings.addError("Cannot load object repository " +
						objectRepository.getAbsolutePath() +
				" because a repository is already loaded with same root id");
			}
		} else {
			result = null;
			ErrorsAndWarnings.addError("Cannot load object repository " +
					objectRepository.getAbsolutePath() +
			" because root element doesn't have id");
		}
		return result;
	}

	private TestObject(Element objectElement, File repositoryFile) {
		loadElement(objectElement, repositoryFile);
	}

	public TestObject(File objectRepository) {
		if(objectRepository.exists()) {
			if(objectRepository.isFile()) {
				SAXBuilder parser = new SAXBuilder();
				Document objectRepositoryDocument = null;
				logger.info("Parsing Object repository file '" + objectRepository.getAbsolutePath() + "'");
				try {
					objectRepositoryDocument = parser.build(objectRepository);
					loadElement(objectRepositoryDocument.getRootElement(), objectRepository);
				} catch (JDOMException e) {
					e.printStackTrace();
					ErrorsAndWarnings.addError("Object repository reading Exception : " + e.getMessage());
				} catch (IOException e) {
					e.printStackTrace();
					ErrorsAndWarnings.addError("Object repository reading Exception : " + e.getMessage());
				}
			}
		}
	}

	private void loadElement(Element objectElement, File repositoryFile) {
		this.type = objectElement.getName();
		this.id = objectElement.getAttributeValue(ATTRIBUTE_ID);
		this.repositoryFile = repositoryFile;

		attributes = new HashMap<String, String>();
		children = new HashMap<String, TestObject>();

		// Loading attributes
		for(Object attributeObject : objectElement.getAttributes()) {
			Attribute attribute = (Attribute) attributeObject;
			String value = attribute.getValue();
			if(! attributes.containsKey(attribute.getName())) {
				attributes.put(attribute.getName(), value);
			} else {
				ErrorsAndWarnings.addWarning("Attribute '" + attribute.getName() + "' for object with id '" + id + "' ignored because already exists.");
			}

			if("file".equals(attribute.getName())) {
				String filePath = null;
				if(this.repositoryFile.getParent() != null) {
					filePath = this.repositoryFile.getParent() + "/" + value;
				} else {
					filePath = "./" + value;
				}
				logger.debug("loading object file '" + filePath + "'");
				this.attachedFile = new File(filePath);
			}
		}

		// Loading children
		for(Object childObject : objectElement.getChildren()) {
			Element childElement = (Element) childObject;
			String childId = childElement.getAttributeValue(ATTRIBUTE_ID);
			String childType = childElement.getName();
			if(! children.containsKey(childType + "(" + childId + ")")) {
				TestObject child = new TestObject(childElement, repositoryFile);
				child.parent = this;
				child.repositoryFile = this.repositoryFile;
				children.put(getLocalPath(child), child);
				logger.debug("loading object '" + getLocalPath(child) + "'");
			} else {
				ErrorsAndWarnings.addWarning("Object with id '" + childId + "' ignored because already exists.");
			}
		}
	}

	public static TestObject getById(String id) {
		return repositories.get(id);
	}

	public String getType() {
		return type;
	}

	public String getId() {
		return id;
	}

	public String attribute(String attributeName) {
		return attributes.get(attributeName);
	}

	public TestObject getParent() {
		return parent;
	}

	public File getFile() {
		return this.attachedFile;
	}

	public TestObject get(String path) {
		String localPath = getBeginningPath(path);
		if(path.contains(">")) {
			String subPath = path.substring(path.indexOf(">") + 1);
			TestObject child = children.get(localPath);
			return child.get(subPath);
		} else {
			return children.get(localPath);
		}
	}
	
	public String getPath() {
		return getPath(this);
	}
	
	public static String getPath(TestObject object) {
		String result = getLocalPath(object);
		TestObject currentObject = object.getParent();
		while(currentObject != null) {
			result = getLocalPath(currentObject) + ">" + result;
			currentObject = currentObject.getParent();
		}
		return result;
	}

	private static String getBeginningPath(String path) {
		String result = path;
		if(path.contains(">")) {
			result = path.split(">")[0];
		}
		return result;
	}

	/**
	 * Returns the object from loaded repositories.
	 * @param path The path of the searched object, like: repository(name)>objectType(Name)
	 * @return the TestObject if found, null elsewhere
	 */
	public static TestObject getFromRepo(String path) {
		TestObject repo = repositories.get(getBeginningPath(path));
		if(path.contains(">")) {
			if(repo != null) {
				return repo.get(path.substring(path.indexOf(">") + 1));
			} else {
				return null;
			}
		} else {
			return repo;
		}
	}

	private static String getLocalPath(TestObject object) {
		return "" + object.getType() + "(" + object.getId() + ")";
	}

}
