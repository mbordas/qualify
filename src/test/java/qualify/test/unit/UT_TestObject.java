/* Copyright (c) 2024, Mathieu Bordas
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

package qualify.test.unit;

import org.openqa.selenium.By;
import qualify.TestCase;
import qualify.tools.TestObject;
import qualify.tools.TestToolFile;
import qualify.tools.TestToolSelenium;
import qualify.tools.TestToolSeleniumFirefox;
import qualify.tools.TestToolStrings;

import java.io.File;

public class UT_TestObject extends TestCase {

	TestToolSelenium selenium = new TestToolSeleniumFirefox(this);

	@Override
	public void afterRun() {
		selenium.quit();
	}

	@Override
	public void run() throws Throwable {
		TestToolStrings strings = new TestToolStrings(this);
		File repositoryFile = new File("tmp/repository.qor");
		TestToolFile.exportResourceFile("input/repository.qor", repositoryFile);

		TestObject root = TestObject.loadObjectRepository(repositoryFile);
		strings.checkEquality("appOne", root.getId());

		TestObject webServer = root.get("webServer(testWebServer)");
		need(webServer != null);

		strings.checkEquality("testWebServer", webServer.getId());

		TestObject nav = root.get("page(home)>nav(page1)");
		need(nav != null);

		By by = selenium.getElementIdentifier(nav);

		need(by != null);
		comment(by.toString());
	}

}
