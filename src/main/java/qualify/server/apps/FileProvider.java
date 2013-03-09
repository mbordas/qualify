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
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.codehaus.groovy.control.CompilationFailedException;
import org.eclipse.jetty.server.Request;

import qualify.ErrorsAndWarnings;
import qualify.Qualify;
import qualify.tools.TestToolFile;
import qualify.tools.TestToolGroovy;

public class FileProvider extends WebApp {

	private static Logger logger = Logger.getLogger(FileProvider.class);

	private File rootDir = null;

	public String getAppName() {
		return "file";
	}

	public FileProvider() {
		if(Qualify.isOption(Qualify.OPTION_HTTP_SERVER_ROOT_DIR)) {
			rootDir = new File(Qualify.getOptionParameter(Qualify.OPTION_HTTP_SERVER_ROOT_DIR));
			if(! (rootDir.exists() && rootDir.isDirectory())) {
				ErrorsAndWarnings.addError("Http server's root dir '" +
						Qualify.getOptionParameter(Qualify.OPTION_HTTP_SERVER_ROOT_DIR) +
				"' does not exist or is not a directory");
			}
		} else {
			rootDir = new File(".");
		}
	}

	public void handleRequest(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text;charset=utf-8");
		String filePath = target.substring("/file".length(), target.length());

		if(filePath.endsWith("png")) {
			response.setContentType("image/png");
		} else {
			response.setContentType("text;charset=utf-8");
		}

		File file = getFile(filePath);
		if(file.exists() && file.isFile()) {
			try {
				IOUtils.copy(new FileInputStream(getFile(filePath)), response.getOutputStream());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			logger.info("Unable to provide file: " + file.getAbsolutePath());
			File dir = null;
			if(file.isFile()) {
				dir = file.getParentFile();
			} else {
				dir = file;
			}
			HashMap<String, Object> vars = new HashMap<String, Object>();
			vars.put("dirs", TestToolFile.listDirs(dir, ".*"));
			vars.put("files", TestToolFile.listFiles(dir, ".*", false));
			vars.put("target", target);
			
			try {
				String result = TestToolGroovy.evaluateTemplate(getResourceAsInputStream("directory.template"), vars);
				response.getWriter().write(result);
			} catch (CompilationFailedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private File getFile(String fileNameRelativeFromRootDir) {
		return new File(rootDir.getAbsolutePath() + "/" + fileNameRelativeFromRootDir);
	}
}
