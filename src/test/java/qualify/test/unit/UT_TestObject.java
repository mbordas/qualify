/*
 * Copyright (c) 2012-2014 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package qualify.test.unit;

import java.io.File;

import org.openqa.selenium.By;

import qualify.TestCase;
import qualify.tools.TestObject;
import qualify.tools.TestToolFile;
import qualify.tools.TestToolSelenium;
import qualify.tools.TestToolSeleniumFirefox;
import qualify.tools.TestToolStrings;

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
