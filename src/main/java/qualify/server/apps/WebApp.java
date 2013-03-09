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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.groovy.control.CompilationFailedException;
import org.eclipse.jetty.server.Request;

import qualify.Requirement;
import qualify.tools.TestToolGroovy;

public class WebApp {
	
	private static Logger logger = Logger.getLogger(WebApp.class);
	
	public static final String APP_RESOURCES_FOLDER = "app";
	private File appDir = null;

	public String getAppName() {
		return this.getClass().getSimpleName();
	}
	
	public void handleRequest(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
		processResponse(response, getResourceAsInputStream("app.template"), null);
	}
	
	public void processResponse(HttpServletResponse response, InputStream template, HashMap<String, Object> variables) {
		response.setContentType("text;charset=utf-8");
		String result;
		try {
			result = TestToolGroovy.evaluateTemplate(template, variables);
			response.getWriter().write(result);
		} catch (CompilationFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getResourcesRootPath() {
		return APP_RESOURCES_FOLDER + "/" + getAppName();
	}
	
	public InputStream getResourceAsInputStream(String path) {
		String resourceFilePath = getResourcesRootPath() + "/" + path;
		logger.info("getting resource: " + resourceFilePath);
		return Requirement.class.getClassLoader().getResourceAsStream(resourceFilePath);
	}
	
	public File getAppFile(String path) {
		return new File(appDir.getAbsolutePath() + "/" + path);
	}
	
	public InputStream getAppFileAsInputStream(String path) throws FileNotFoundException {
		return new FileInputStream(getAppFile(path));
	}
	
	public File getAppDir() {
		return appDir;
	}

	public void setAppDir(File appDir) {
		this.appDir = appDir;
	}

}
