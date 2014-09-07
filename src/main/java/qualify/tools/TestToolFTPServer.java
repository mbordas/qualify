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

package qualify.tools;

import java.util.ArrayList;
import java.util.List;

import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import qualify.TestCase;

public class TestToolFTPServer {
	
	static final String DEFAULT_HOST = "localhost";
	static final int DEFAULT_PORT = 1985;
	static final String DEFAULT_PASSIVE_PORTS = "2221";
	static final String DEFAULT_LOGIN = "tester";
	static final String DEFAULT_PASSWORD = "password";
	static final String DEFAULT_HOME_PATH = "TestToolFTPServer";
	
	String host = DEFAULT_HOST;
	int port = DEFAULT_PORT;
	String passivePorts = DEFAULT_PASSIVE_PORTS;
	String login = DEFAULT_LOGIN;
	String password = DEFAULT_PASSWORD;
	String homeDir = DEFAULT_HOME_PATH;
	
	FtpServer server = null;
	
	public TestToolFTPServer(TestCase testCase) {
	}
	
	public void setHomeDir(String homeDir) {
		this.homeDir = homeDir;
	}
	
	public void start() throws FtpException {
		FtpServerFactory serverFactory = new FtpServerFactory();
		ListenerFactory factory = new ListenerFactory();

		factory.setPort(port);

		DataConnectionConfigurationFactory dataConnectionFonfigFactory = new DataConnectionConfigurationFactory();
		dataConnectionFonfigFactory.setPassivePorts(passivePorts);

		factory.setDataConnectionConfiguration(dataConnectionFonfigFactory.createDataConnectionConfiguration());

		serverFactory.addListener("default", factory.createListener());

		server = serverFactory.createServer();

		PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
		userManagerFactory.setFile(null);
		userManagerFactory.setPasswordEncryptor(new ClearTextPasswordEncryptor());

		UserManager userManager = userManagerFactory.createUserManager();

		BaseUser user = new BaseUser();
		user.setName(login);
		user.setPassword(password);
		user.setHomeDirectory(homeDir);

		List<Authority> authorities = new ArrayList<Authority>();
		authorities.add(new WritePermission());

		user.setAuthorities(authorities);        
		userManager.save(user);

		serverFactory.setUserManager(userManager);

		server.start();
	}
	
	public void stop() {
		if(server != null) {
			server.stop();
		}
	}

	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
	
	public String getLogin() {
		return login;
	}
	
	public String getPassword() {
		return password;
	}

}
