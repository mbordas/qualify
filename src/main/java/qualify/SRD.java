/*Copyright (c) 2010, Mathieu Bordas
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

package qualify;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


/**
 * The static SRD class references all the requirements read during Qualify configuration loading
 * and inline requirements made from test cases.
 * @author Mathieu Bordas
 *
 */
public class SRD {

	static Logger logger = Logger.getLogger(SRD.class);

	private LinkedList<Requirement> requirements = new LinkedList<Requirement>();
	
	public SRD() {
		
	}

	public static SRD loadSRDFromXMLFile(File xmlFile) {
		SRD result = new SRD();
		if(xmlFile.exists()) {
			if(xmlFile.isFile()) {
				SAXBuilder parser = new SAXBuilder();
				Document srdDocument = null;
				try {
					logger.debug("Parsing SRD file '" + xmlFile.getAbsolutePath() + "'");
					srdDocument = parser.build(xmlFile);

					for(Object requirementTagObject : srdDocument.getRootElement().getChildren("requirement")) {
						Element requirementTag = (Element)requirementTagObject;
						result.addRequirement(Requirement.createRequirementFromDomElement(requirementTag));
					}
				} catch (JDOMException e) {
					e.printStackTrace();
					ErrorsAndWarnings.addError("SRD reading Exception : " + e.getMessage());
				} catch (IOException e) {
					e.printStackTrace();
					ErrorsAndWarnings.addError("SRD reading Exception : " + e.getMessage());
				}
			} else {
				ErrorsAndWarnings.addError("SRD '" + xmlFile + "' is not a file.");
			}
		} else {
			ErrorsAndWarnings.addError("SRD '" + xmlFile + "' does not exist.");
		}
		return result;
	}

	public static void addSRD(Element configurationTagForSRD) {
		if("xml".equals(configurationTagForSRD.getAttributeValue("type"))) {
			loadSRDFromXMLFile(new File(configurationTagForSRD.getAttributeValue("file")));
		} else {
			logger.error("Unknown_SRD_Type : " + configurationTagForSRD.getAttributeValue("type"));
		}
	}

	public static void addSRD(File xmlFile) {
		loadSRDFromXMLFile(xmlFile);
	}

	/**
	 * Returns the Requirement object from its ID.
	 * If ID is unknown, null is returned.
	 * @param requirementId The ID of the expected requirement.
	 * @return The unique Requirement object with the expected ID.
	 */
	public Requirement getRequirement(String requirementId) {
		Requirement result = null;
		for(Requirement requirement : requirements) {
			if(requirement.getId().equals(requirementId)) {
				result = requirement;
				break;
			} else {
				if(requirement.getSubRequirement(requirementId) != null) {
					result = requirement.getSubRequirement(requirementId);
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Returns all the Requirement objects but the EMPTY_REQUIREMENT if no check
	 * has been done.
	 * @return
	 */
	public LinkedList<Requirement> getRequirements() {
		LinkedList<Requirement> result = new LinkedList<Requirement>();
		for(Requirement req : requirements) {
			if(! req.getId().equals(Requirement.EMPTY_REQUIREMENT_ID)) {
				result.add(req);
			} else {
				// Here 'req' is the EMPTY_REQUIREMENT
				// It will be returned only if checks have been done.
				Requirement emptyReq = getRequirement(Requirement.EMPTY_REQUIREMENT_ID);
				if(emptyReq.getNOKs() + emptyReq.getOKs() > 0) {
					result.add(req);
				}
			}
		}
		return result;
	}
	
	public void createRequirement(String requirementId) {
		if(getRequirement(requirementId) == null) {
			addRequirement(new Requirement(requirementId));
		}
	}

	private void addRequirement(Requirement requirement) {
		requirements.add(requirement);
		requirement.setLocalIndex(requirements.size());
		logger.debug("Requirement '" + requirement.getId() + "' added to SRD");
	}

}
