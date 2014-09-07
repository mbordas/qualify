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

package qualify.server;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.groovy.control.CompilationFailedException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import qualify.ErrorsAndWarnings;
import qualify.Qualify;
import qualify.TestScript;
import qualify.server.apps.*;
import qualify.tools.TestToolFile;
import qualify.tools.TestToolStrings;

public class HTTPHandler extends AbstractHandler {

	private static Logger logger = Logger.getLogger(HTTPHandler.class);

	private static HashMap<String, WebApp> apps = null;

	public HTTPHandler() {
		apps = new HashMap<String, WebApp>();
		registerApp(new FileProvider());
		registerApp(new ResourceProvider());
		registerApp(new SMK());
		if(Qualify.isOption(Qualify.OPTION_HTTP_SERVER_WEBAPPS_DIR)) {
			File appDir = new File(Qualify.getOptionValue(Qualify.OPTION_HTTP_SERVER_WEBAPPS_DIR));
			for(File appScript : TestToolFile.listFiles(appDir, ".*\\.groovy", true)) {
				try {
					TestScript.loadAppFromScript(appScript);
				} catch (CompilationFailedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		logger.debug("target: " + target);
		String uri = request.getRequestURI();

		String appName = uri.split("/")[1];
		logger.info("Handling HTTP request with app: " + appName);
		WebApp app = apps.get(appName);

		app.handleRequest(target, baseRequest, request, response);

		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);

		logRequest(request);
	}

	private void logRequest(HttpServletRequest request) {
		logger.debug("*** New request");
		logger.debug("HTTP request context path: " + request.getContextPath());
		logger.debug("HTTP request local address: " + request.getLocalAddr());
		logger.debug("HTTP request URI: " + request.getRequestURI());
		for(String name : TestToolStrings.toList(request.getHeaderNames())) {
			logger.debug("HTTP header '" + name + "' : " + request.getHeader(name));
		}
		for(String name : TestToolStrings.toList(request.getParameterNames())) {
			logger.debug("HTTP parameter '" + name + "' : " + request.getParameter(name));
		}
		logger.debug("HTTP request AuthType: " + request.getAuthType());
		for(String name : TestToolStrings.toList(request.getAttributeNames())) {
			logger.debug("HTTP request attribute '" + name + "' : " + request.getAttribute(name));
		}

	}

	public static void registerApp(WebApp app) {
		if(app != null) {
			String appName = app.getAppName();
			logger.info("registering app: " + appName);
			apps.put(appName, app);
		} else {
			ErrorsAndWarnings.addError("Cannot register null app");
		}
	}
}