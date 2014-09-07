/*Copyright (c) 2011-2012, Mathieu Bordas
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

package qualify.server.apps;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;

import qualify.Requirement;

public class ResourceProvider extends WebApp {

	private static Logger logger = Logger.getLogger(ResourceProvider.class);

	public String getAppName() {
		return "resource";
	}

	public void handleRequest(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text;charset=utf-8");
		String filePath = "." + target.substring(("/" + getAppName()).length(), target.length());
		logger.debug("Providing resource: " + filePath);
		if(filePath.endsWith("png")) {
			response.setContentType("image/png");
		} else {
			response.setContentType("text;charset=utf-8");
		}

		try {
			InputStream in = getResourceAsInputStream(filePath);
			if(in == null) {
				logger.error("resource not available: " + filePath);
			} else {
				IOUtils.copyLarge(getResourceAsInputStream(filePath), response.getOutputStream());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public InputStream getResourceAsInputStream(String path) {
		return Requirement.class.getClassLoader().getResourceAsStream(path);
	}

}
