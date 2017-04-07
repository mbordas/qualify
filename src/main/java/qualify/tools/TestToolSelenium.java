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
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import qualify.TestCase;
import qualify.TestException;

/**
 * TestToolSelenium extends the FirefoxDriver object from Selenium 2 API. New convenient methods are added, such as isPresent, waitPresent
 * and waitVanish. To better use that tool, you should use the object repositories (see the class called TestObject). The supported
 * TestObject have the following structure: <element id="my element"> <identifier id="xpath" value="//body/h1[1]"/> </element> The following
 * identifiers are supported: - id - name - xpath - linkText - partialLinkText - cssSelector - tagName - className
 * 
 * @author Mathieu Bordas
 */

public abstract class TestToolSelenium {

	private static final Logger logger = Logger.getLogger(TestToolSeleniumFirefox.class);

	protected WebDriver driver = null;
	protected TestCase testCase = null;

	private double defaultTimeout = 10.0;

	public TestToolSelenium(TestCase tc) {
		testCase = tc;
	}

	public void get(String url) {
		driver.get(url);
	}

	public void quit() {
		if(driver != null) {
			driver.quit();
		}
	}

	/**
	 * Redefines the Selenium findElement(By) in order to catch exception
	 */
	public WebElement findElement(By by) {
		try {
			return driver.findElement(by);
		} catch(NoSuchElementException e) {
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
	 * 
	 * @param elementId
	 * @return
	 */
	public boolean checkFindElementById(String elementId) {
		boolean result = false;
		result = (findElementById(elementId) != null);
		if(result == true) {
			testCase.addTestResult(true, "Expected dom element found from id '" + elementId + "'");
		} else {
			testCase.addTestResult(false, "Expected dom element not found from id '" + elementId + "'");
		}
		return result;
	}

	public WebElement find(String elementIdentifier) throws ElementNotFoundException {
		WebElement element = find(getElementIdentifier(elementIdentifier));
		if(element != null) {
			return element;
		} else {
			throw new ElementNotFoundException("Element not found with identifier: " + elementIdentifier);
		}
	}

	public WebElement find(By by) {
		WebElement result = findElement(by);
		return result;
	}

	public void click(String elementIdentifier) {
		waitPresent(elementIdentifier, defaultTimeout);
		WebElement w = findElement(getElementIdentifier(elementIdentifier));
		if(w != null) {
			w.click();
		} else {
			testCase.addTestResult(false, "Cannot click on null element (identifier='" + elementIdentifier + "').");
		}
	}

	public TestObject click(TestObject testObject) {
		if(testObject == null) {
			throw new TestException("Cannot click on null TestObject");
		}
		click(testObject.getPath());
		return testObject;
	}

	public void click(By by) {
		waitPresent(by, defaultTimeout);
		WebElement w = findElement(by);
		if(w != null) {
			w.click();
		} else {
			testCase.addTestResult(false, "Cannot click on null element (by='" + by.toString() + "').");
		}
	}

	public void forceClick(String elementIdentifier) {
		waitPresent(elementIdentifier, defaultTimeout);
		WebElement w = findElement(getElementIdentifier(elementIdentifier));
		if(w != null) {
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", w);
		} else {
			testCase.addTestResult(false, "Cannot click on null element (identifier='" + elementIdentifier + "').");
		}
	}

	public boolean checkAlert(String textRegex) {
		boolean result = false;
		String alertText = driver.switchTo().alert().getText();
		if(TestToolStrings.matches(textRegex, alertText)) {
			result = true;
		} else {
			result = false;
		}
		testCase.addTestResult(result, "regex=" + textRegex + " | tested=" + alertText);
		return result;
	}

	public void acceptAlert() {
		driver.switchTo().alert().accept();
	}

	public void dismissAlert() {
		driver.switchTo().alert().dismiss();
	}

	public void type(String elementIdentifier, String textToType) {
		WebElement w = findElement(getElementIdentifier(elementIdentifier));
		if(w != null) {
			w.sendKeys(textToType);
		} else {
			testCase.addTestResult(false, "Cannot type on null element (identifier='" + elementIdentifier + "').");
		}
	}

	/**
	 * Clear text of element.
	 * 
	 * @param elementIdentifier
	 */
	public void clear(String elementIdentifier) {
		WebElement w = findElement(getElementIdentifier(elementIdentifier));
		if(w != null) {
			w.clear();
		} else {
			testCase.addTestResult(false, "Cannot type on null element (identifier='" + elementIdentifier + "').");
		}
	}

	public boolean isPresent(String elementIdentifier) {
		try {
			return (find(elementIdentifier) != null);
		} catch(ElementNotFoundException e) {
			return false;
		}
	}

	public boolean isPresent(By by) {
		return (find(by) != null);
	}

	public boolean isDisplayed(String elementIdentifier) throws ElementNotFoundException {
		WebElement element = find(elementIdentifier);
		return element.isDisplayed();
	}

	public boolean isEnabled(String elementIdentifier) throws ElementNotFoundException {
		WebElement element = find(elementIdentifier);
		return element.isEnabled();
	}

	public boolean waitPresent(final String elementIdentifier, final double timeoutInSeconds) {
		try {
			(new WebDriverWait(driver, Double.valueOf(timeoutInSeconds).intValue())).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {
					return isPresent(elementIdentifier);
				}
			});
		} catch(TimeoutException e) {
		}
		return isPresent(elementIdentifier);
	}

	public boolean waitPresent(final By by, final double timeoutInSeconds) {
		try {
			(new WebDriverWait(driver, Double.valueOf(timeoutInSeconds).intValue())).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {
					return isPresent(by);
				}
			});
		} catch(TimeoutException e) {
		}
		return isPresent(by);
	}

	public boolean waitVanish(final String elementIdentifier, final double timeoutInSeconds) {
		try {
			(new WebDriverWait(driver, Double.valueOf(timeoutInSeconds).intValue())).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {
					return (!isPresent(elementIdentifier));
				}
			});
		} catch(TimeoutException e) {
		}
		return (!isPresent(elementIdentifier));
	}

	public String getPageSource() {
		return driver.getPageSource();
	}

	public File capturePage() {
		File page = null;
		String htmlContent = getPageSource();
		try {
			page = TestToolFile.createNewTemporaryFile("html");
		} catch(IOException e1) {
			testCase.addTestResult(false, "IOException raised: " + e1.getLocalizedMessage());
			testCase.addException(e1);
		}
		try {
			FileUtils.writeStringToFile(page, htmlContent);
		} catch(IOException e) {
			e.printStackTrace();
		}
		testCase.attachFile(page);
		return page;
	}

	/**
	 * Computes the By identifier (see the official Selenium documentation) from the TestObject found in the object repositories.
	 * 
	 * @param webElementIdentifier
	 *            The identifier from object repositories
	 * @return The By object supported by the Selenium 2 WebDriver
	 */
	public By getElementIdentifier(String webElementIdentifier) {
		logger.debug("searching web element from identifier: '" + webElementIdentifier + "'");
		By result = null;
		if(webElementIdentifier.contains("(")) {
			TestObject obj = TestObject.getFromRepo(webElementIdentifier);
			if(obj != null) {
				result = getElementIdentifier(obj);
			} else {
				testCase.addTestResult(false, "Web element from identifier '" + webElementIdentifier
						+ "' does not exist in object repositories.");
			}
		} else {
			result = new By.ById(webElementIdentifier);
		}
		return result;
	}

	public By getElementIdentifier(TestObject obj) {
		By result = null;
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
		} else {
			logger.error("No identifier found for TestObject '" + obj.getPath() + "'.");
		}
		return result;
	}

	public void close() {
		driver.close();
	}

	public String getElementTextById(String elementId) throws TestException {
		WebElement element = findElementById(elementId);
		if(element != null) {
			return element.getText();
		} else {
			throw new TestException("Cannot get text from element with id '" + elementId + "': element not found.");
		}
	}

	public boolean checkText(String elementId, String expectedValue) {
		return checkText(elementId, expectedValue, true);
	}

	public boolean checkText(String elementId, String expectedValue, boolean caseSensitive) {
		boolean result = false;
		WebElement element = findElementById(elementId);
		if(element != null) {
			String text = getText(elementId);
			result = TestToolStrings.checkEquality(testCase, expectedValue, text, caseSensitive);
		} else {
			result = false;
			testCase.addTestResult(false, "Web element not found with identifier '" + elementId);
		}
		return result;
	}

	public boolean checkText(String elementId, Double expectedValue, double epsilon) {
		boolean result = false;
		Double testedValue = null;
		try {
			testedValue = Double.valueOf(getText(elementId));
		} catch(NumberFormatException e) {
			testCase.addError(e.getMessage());
		}
		TestToolNumbers numbers = new TestToolNumbers(testCase);
		numbers.checkEquality(expectedValue, testedValue, epsilon);
		return result;
	}

	public boolean checkValue(String elementId, String expectedValue) {
		return checkValue(elementId, expectedValue, true);
	}

	public boolean checkValue(String elementId, String expectedValue, boolean caseSensitive) {
		boolean result = false;
		WebElement element = findElementById(elementId);
		if(element != null) {
			result = TestToolStrings.checkEquality(testCase, expectedValue, element.getAttribute("value"), caseSensitive);
		} else {
			result = false;
			testCase.addTestResult(false, "Web element not found with identifier '" + elementId);
		}
		return result;
	}

	public boolean checkSelection(String elementId, String expectedValue) {
		return checkSelection(elementId, expectedValue, true);
	}

	public boolean checkSelection(String elementId, String expectedValue, boolean caseSensitive) {
		boolean result = false;
		WebElement element = findElementById(elementId);
		if(element != null) {
			Select option = new Select(element);
			WebElement selectedOption = option.getFirstSelectedOption();
			result = TestToolStrings.checkEquality(testCase, expectedValue, selectedOption.getText(), caseSensitive);
		} else {
			result = false;
			testCase.addTestResult(false, "Web element not found with identifier '" + elementId);
		}
		return result;
	}

	/**
	 * Returns the text or the 'value' attribute of element.
	 * 
	 * @param elementId
	 * @return
	 */
	public String getText(String elementId) {
		WebElement element = findElementById(elementId);
		if(element != null) {
			if(element.getTagName().equals("input")) {
				return element.getAttribute("value");
			} else {
				return element.getText();
			}
		} else {
			throw new TestException("Web element not found with identifier '" + elementId + "'");
		}
	}

	/**
	 * Returns the 'value' attribute of element.
	 * 
	 * @param elementId
	 * @return
	 */
	public String getValue(String elementId) {
		return getAttribute(elementId, "value");
	}

	public Collection<String> getSelectOptions(String id) throws ElementNotFoundException {
		Collection<String> result = null;

		WebElement element = findElementById(id);
		if(element != null) {
			result = new ArrayList<String>();
			Select select = new Select(element);
			for(WebElement option : select.getOptions()) {
				result.add(option.getAttribute("value"));
			}
		}

		return result;
	}

	/**
	 * Returns the value of element's attribute <code>key</code>.
	 * 
	 * @param elementId
	 * @param key
	 * @return
	 */
	public String getAttribute(String elementId, String key) {
		WebElement element = findElementById(elementId);
		if(element != null) {
			try {
				return element.getAttribute(key);
			} catch(StaleElementReferenceException e) {
				// Such exception could be thrown when element is moved/modified between calls to 'find()' and 'getAttribute()'.
				// To avoid this, we just re-find it.
				element = findElementById(elementId);
				return element.getAttribute(key);
			}
		} else {
			throw new TestException("Web element not found with identifier '" + elementId + "'");
		}
	}

	public String getHTML(String elementId) {
		WebElement element = findElementById(elementId);
		if(element != null) {
			return element.getAttribute("innerHTML");
		} else {
			return null;
		}
	}

	/**
	 * Clears input text then type <code>value</code>.
	 * 
	 * @param elementId
	 * @param value
	 */
	public void setInput(String elementId, String value) {
		clear(elementId);
		type(elementId, value);
	}

	public void select(String identifier, String optionText) {
		Select select = new Select(driver.findElement(getElementIdentifier(identifier)));
		select.selectByVisibleText(optionText);
	}

	/**
	 * Generate a screenshot of the current view of the WebDriver and save it as a file
	 * 
	 * @param outputFile
	 * @throws Exception
	 */
	public void getScreenshotAsFile(File outputFile) throws Exception {
		Class<? extends WebDriver> driverClass = driver.getClass();
		// Check if the driver is implementing the interface TakesScreenshot
		if(TakesScreenshot.class.isAssignableFrom(driverClass)) {
			TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
			File screenFile = screenshotDriver.getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(screenFile, outputFile);
		} else {
			throw new Exception("Can not take screenshots with driver '" + driverClass.getName() + "'");
		}
	}

	/**
	 * Generate a screenshot of the current view of the WebDriver and get it as a Base64 encoded string
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getScreenshotAsBase64() throws Exception {
		Class<? extends WebDriver> driverClass = driver.getClass();
		// Check if the driver is implementing the interface TakesScreenshot
		if(TakesScreenshot.class.isAssignableFrom(driverClass)) {
			TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
			String screenFile = screenshotDriver.getScreenshotAs(OutputType.BASE64);
			return screenFile;
		} else {
			throw new Exception("Can not take screenshots with driver '" + driverClass.getName() + "'");
		}
	}

	public static class ElementNotFoundException extends Exception {

		public ElementNotFoundException(String message) {
			super(message);
		}
	}
}
