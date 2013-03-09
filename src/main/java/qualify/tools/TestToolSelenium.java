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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import qualify.ErrorsAndWarnings;
import qualify.TestCase;

/**
 * TestToolSelenium extends the FirefoxDriver object from Selenium 2 API.
 * New convenient methods are added, such as isPresent, waitPresent and waitVanish.
 * To better use that tool, you should use the object repositories (see the class called TestObject).
 * The supported TestObject have the following structure:
 * <element id="my element">
 *    <identifier id="xpath" value="//body/h1[1]"/>
 * </element>
 * 
 * The following identifiers are supported:
 * - id
 * - name
 * - xpath
 * - linkText
 * - partialLinkText
 * - cssSelector
 * - tagName
 * - className 
 * 
 * @author Mathieu Bordas
 *
 */

public class TestToolSelenium {
	
	private Logger logger = Logger.getLogger(TestToolSeleniumFirefox.class);

	public WebDriver driver = null;
	private TestCase testCase = null;
	
	public TestToolSelenium(TestCase tc) {
		testCase = tc;
	}
	
	public void get(String url) {
		driver.get(url);
	}
	
	public void quit() {
		driver.quit();
	}

	/**
	 * Redefines the Selenium findElement(By) in order to catch exception
	 */
	public WebElement findElement(By by) {
		try {
			return driver.findElement(by);
		} catch (NoSuchElementException e) {
			return null;
		}
	}
	
	/**
	 * Redefines the Selenium findElementById(String) in order to catch exception
	 */
	public WebElement findElementById(String id) {
		try {
			return driver.findElement(By.id(id));
		} catch(NoSuchElementException e) {
			return null;
		}
	}

	/**
	 * Checks that the element identified by the specified ID is present in the actual web page
	 * @param elementId
	 * @return
	 */
	public boolean checkFindElementById(String elementId) {
		boolean result = false;
		result = (findElementById(elementId) != null);
		if(result == true) {
			testCase.addTestResult(true, "Expected dom element found from id '" + elementId + "'", TestToolSeleniumFirefox.class);
		} else {
			testCase.addTestResult(false, "Expected dom element not found from id '" + elementId + "'", TestToolSeleniumFirefox.class);
		}
		return result;
	}

	public WebElement find(String elementIdentifier) {
		WebElement result = findElement(getElementIdentifier(elementIdentifier));
		return result;
	}

	public void click(String elementIdentifier) {
		WebElement w = findElement(getElementIdentifier(elementIdentifier));
		if(w != null) {
			w.click();
		} else {
			testCase.addTestResult(false, "Cannot click on null element (identifier='" + elementIdentifier + "').", TestToolSeleniumFirefox.class);
		}
	}

	public void type(String elementIdentifier, String textToType) {
		WebElement w = findElement(getElementIdentifier(elementIdentifier));
		if(w != null) {
			w.sendKeys(textToType);
		} else {
			testCase.addTestResult(false, "Cannot type on null element (identifier='" + elementIdentifier + "').", TestToolSeleniumFirefox.class);
		}
	}

	public boolean isPresent(String elementIdentifier) {
		return (find(elementIdentifier) != null);
	}

	public boolean waitPresent(final String elementIdentifier, final double timeoutInSeconds) {
		try {
			(new WebDriverWait(driver, Double.valueOf(timeoutInSeconds).intValue())).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {
					return isPresent(elementIdentifier);
				}
			});
		} catch (TimeoutException e) { }
		return isPresent(elementIdentifier);
	}

	public boolean waitVanish(final String elementIdentifier, final double timeoutInSeconds) {
		try {
			(new WebDriverWait(driver, Double.valueOf(timeoutInSeconds).intValue())).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {
					return (! isPresent(elementIdentifier));
				}
			});
		} catch (TimeoutException e) { }
		return (! isPresent(elementIdentifier));
	}
	
	public String getPageSource() {
		return driver.getPageSource();
	}
	
	public File capturePage() {
		File page = null;
		String htmlContent = getPageSource();
		try {
			page = TestToolFile.createNewTemporaryFile("html");
		} catch (IOException e1) {
			testCase.addTestResult(false, "IOException raised: " + e1.getLocalizedMessage(), TestToolSelenium.class);
			ErrorsAndWarnings.addException(e1);
		}
		try {
			FileUtils.writeStringToFile(page, htmlContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
		testCase.attachFile(page);
		return page;
	}

	/**
	 * Computes the By identifier (see the official Selenium documentation) from the TestObject found in the object repositories.
	 * @param webElementIdentifier The identifier from object repositories
	 * @return The By object supported by the Selenium 2 WebDriver
	 */
	public By getElementIdentifier(String webElementIdentifier) {
		logger.debug("searching web element from identifier: '" + webElementIdentifier + "'");
		By result = null;
		if(webElementIdentifier.contains("(")) {
			TestObject obj = TestObject.getFromRepo(webElementIdentifier);
			if(obj != null) {
				if(obj.get("identifier(id)") != null) {
					result = By.id(obj.get("identifier(id)").attribute("value"));
				} else if(obj.get("identifier(name)") != null) {
					result = By.name(obj.get("identifier(name)").attribute("value"));
				} else if(obj.get("identifier(xpath)") != null) {
					result = By.xpath(obj.get("identifier(xpath)").attribute("value"));
				} else if(obj.get("identifier(linkText)") != null) {
					result = By.linkText(obj.get("identifier(linkText)").attribute("value"));
				} else if(obj.get("identifier(partialLinkText)") != null) {
					result = By.partialLinkText(obj.get("identifier(partialLinkText)").attribute("value"));
				} else if(obj.get("identifier(cssSelector)") != null) {
					result = By.cssSelector(obj.get("identifier(cssSelector)").attribute("value"));
				} else if(obj.get("identifier(tagName)") != null) {
					result = By.tagName(obj.get("identifier(tagName)").attribute("value"));
				} else if(obj.get("identifier(className)") != null) {
					result = By.className(obj.get("identifier(className)").attribute("value"));
				}
			} else {
				testCase.addTestResult(false, "Web element from identifier '" + webElementIdentifier + "' does not exist in object repositories.", TestToolSikuli.class);
			}
		} else {
			result = new By.ByName(webElementIdentifier);
		}
		return result;
	}

}
