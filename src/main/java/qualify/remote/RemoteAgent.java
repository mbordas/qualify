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

package qualify.remote;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

public class RemoteAgent {
	
	private static Logger logger = Logger.getLogger(RemoteAgent.class);

	private MBeanServer mbs = null;

	public RemoteAgent() {

		// Get the platform MBeanServer
		mbs = ManagementFactory.getPlatformMBeanServer();

		// Registering MBeans
		registerObject(new RemoteProcess());
		registerObject(new RemoteFile());
		registerObject(new RemoteGroovy());
	}

	private <T> void registerObject(T object) {
		try {
			System.out.println("Registering object: " + getObjectName(object.getClass()));
			mbs.registerMBean(object, getObjectName(object.getClass()));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	// Utility method: so that the application continues to run
	private static void waitForEnterPressed() {
		try {
			System.out.println("Press to stop RemoteAgent...");
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String argv[]) {
		RemoteAgent agent = new RemoteAgent();
		logger.info("RemoteAgent is up");
		RemoteAgent.waitForEnterPressed();
	}

	public static <T> ObjectName getObjectName(Class<T> t) throws MalformedObjectNameException, NullPointerException {
		return new ObjectName("RemoteAgent:name=" + t.toString().replaceAll("class ", "").replaceAll("interface ", "").replaceAll("MBean", ""));
	}
	
	public static String getRemoteAgentName(String hostname, int port) {
		return "@[RemoteAgent " + hostname + ":" + port + "]";
	}

}
