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

package qualify.remote;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;

/**
 * The RemoteManager provides methods to invoke remote objects running on remote agents.
 *
 */
public class RemoteManager {

	private static Logger logger = Logger.getLogger(RemoteManager.class);

	public static <T> T getMbeanProxy(String hostname, int port, Class<T> interfaceClass) throws IOException {
		T result = null;

		String url = "service:jmx:rmi://" + hostname + ":" + port + "/jndi/rmi://" + hostname + ":" + port + "/jmxrmi";

		JMXServiceURL serviceUrl = new JMXServiceURL(url);
		JMXConnector jmxc = JMXConnectorFactory.connect(serviceUrl, null);

		MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

		logger.debug("Domains:");
		String domains[] = mbsc.getDomains();
		Arrays.sort(domains);
		for (String domain : domains) {
			logger.debug("Domain = " + domain);
		}

		logger.debug("MBeanServer default domain = " + mbsc.getDefaultDomain());

		logger.debug("MBean count = " + mbsc.getMBeanCount());
		logger.debug("Query MBeanServer MBeans:");
		Set<ObjectName> names = new TreeSet(mbsc.queryNames(null, null));
		for (ObjectName name : names) {
			logger.debug("ObjectName = " + name);
		}

		try {
			ObjectName mbeanName = RemoteAgent.getObjectName(interfaceClass);
			result = (T) JMX.newMBeanProxy(mbsc, mbeanName, interfaceClass, true);
		} catch (MalformedObjectNameException e1) {
			e1.printStackTrace();
		} catch (NullPointerException e1) {
			e1.printStackTrace();
		}

		return result;
	}

}
