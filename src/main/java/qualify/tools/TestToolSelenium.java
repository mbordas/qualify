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

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
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
import qualify.doc.DocList;
import qualify.doc.DocString;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

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

	private double defaultTimeout_s = 10.0;

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

	public boolean checkNotExists(String elementId) {
		driver.manage().timeouts().implicitlyWait(200, TimeUnit.MILLISECONDS);
		final boolean result = findElementById(elementId) == null;
		if(result) {
			testCase.addTestResult(true, "Not expected dom element not found from id '" + elementId + "'");
		} else {
			testCase.addTestResult(false, "Not expected dom element found from id '" + elementId + "'");
		}
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		return result;
	}

	/**
	 * Checks that the element identified by the specified ID is present in the actual web page
	 *
	 * @param elementId
	 * @return
	 */
	public boolean checkFindElementById(String elementId) {
		boolean result;
		result = (findElementById(elementId) != null);
		if(result) {
			testCase.addTestResult(true, "Expected dom element found from id '" + elementId + "'");
		} else {
			testCase.addTestResult(false, "Expected dom element not found from id '" + elementId + "'");
		}
		return result;
	}

	public WebElement find(String elementIdentifier) throws ElementNotFoundException {
		By identifier = getElementIdentifier(elementIdentifier);
		if(identifier == null) {
			throw new ElementNotFoundException("Unparsed identifier: " + elementIdentifier);
		}
		WebElement element = find(identifier);
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
		By identifier = getElementIdentifier(elementIdentifier);
		if(identifier == null) {
			throw new TestException("Unparsed element identifier: " + elementIdentifier);
		}
		if(waitPresent(elementIdentifier, defaultTimeout_s)) {
			WebElement w = findElement(identifier);
			if(w != null) {
				w.click();
			} else {
				testCase.addTestResult(false, "Cannot click on null element (identifier='" + elementIdentifier + "').");
			}
		} else {
			testCase.addTestResult(false, "Cannot click on element not found (identifier='" + elementIdentifier + "').");
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
		waitPresent(by, defaultTimeout_s);
		WebElement w = findElement(by);
		if(w != null) {
			w.click();
		} else {
			testCase.addTestResult(false, "Cannot click on null element (by='" + by.toString() + "').");
		}
	}

	public void forceClick(String elementIdentifier) {
		waitPresent(elementIdentifier, defaultTimeout_s);
		WebElement w = findElement(getElementIdentifier(elementIdentifier));
		if(w != null) {
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", w);
		} else {
			testCase.addTestResult(false, "Cannot click on null element (identifier='" + elementIdentifier + "').");
		}
	}

	public boolean isAlertPresent() {
		try {
			driver.switchTo().alert();
			return true;
		} catch(Exception e) {
			return false;
		}
	}

	private Alert waitAlert(int timeout_s) {
		logger.info("waitAlert(timeout_s) isAlertPresent=" + isAlertPresent());
		long start_ms = System.currentTimeMillis();
		while(!isAlertPresent() && System.currentTimeMillis() < start_ms + timeout_s * 1000) {
			try {
				Thread.sleep(500);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(isAlertPresent()) {
			return driver.switchTo().alert();
		} else {
			throw new TestException("No alert present after %d seconds", (System.currentTimeMillis() - start_ms) / 1000);
		}
	}

	private Alert waitAlert() {
		logger.info("waitAlert() isAlertPresent=" + isAlertPresent());
		return waitAlert((int) defaultTimeout_s);
	}

	private void waitNoAlert() {
		logger.info("waitAlert(timeout_s) isAlertPresent=" + isAlertPresent());
		long start_ms = System.currentTimeMillis();
		while(isAlertPresent() && System.currentTimeMillis() < start_ms + defaultTimeout_s * 1000) {
			try {
				Thread.sleep(500);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean checkAlert(String textRegex) {
		boolean result = false;
		String alertText = waitAlert().getText();
		if(TestToolStrings.matches(textRegex, alertText)) {
			result = true;
		} else {
			result = false;
		}
		testCase.addTestResult(result, "regex=" + textRegex + " | tested=" + alertText);
		return result;
	}

	/**
	 * Accepts alert and waits until it is no more displayed.
	 *
	 * @throws InterruptedException
	 */
	public void acceptAlert() throws InterruptedException {
		Alert alert = waitAlert();
		final String text = alert.getText();
		logger.info("Accepting alert '" + text + "'");
		alert.accept();

		Thread.sleep(1000);

		// When alert is accepted current Java thread goes on, but the alert could persist in the browser.
		// Some milliseconds later, a call to checkAlert could check the same alert one more time (because it not
		// closed in the browser).

		// To prevent such an confusing state, here we wait until this alert is hidden.
		// 2 cases: either there is no more alert displayed, either the alert displayed is another one.

		boolean sameAlertIsStillDisplayed = true;
		long start_ms = System.currentTimeMillis();
		while(sameAlertIsStillDisplayed && System.currentTimeMillis() < start_ms + defaultTimeout_s * 1000) {
			try {
				Alert _alert = waitAlert(1);
				sameAlertIsStillDisplayed = text.equals(_alert.getText());

				Thread.sleep(500);
			} catch(Exception e) {
				sameAlertIsStillDisplayed = false;
			}
		}
	}

	public void dismissAlert() {
		waitAlert().dismiss();
	}

	public void type(String elementIdentifier, String textToType) {
		By by = getElementIdentifier(elementIdentifier);
		type(by, textToType);
	}

	public void type(By elementIdentifier, String textToType) {
		WebElement w = findElement(elementIdentifier);
		if(w != null) {
			if(textToType.endsWith("\n")) {
				w.sendKeys(textToType.replaceAll("\n", ""));
				w.sendKeys(Keys.ENTER);
			} else {
				w.sendKeys(textToType);
			}
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
		By by = getElementIdentifier(elementIdentifier);
		clear(by);
	}

	public void clear(By elementIdentifier) {
		WebElement w = findElement(elementIdentifier);
		if(w != null) {
			w.sendKeys(Keys.chord(Keys.CONTROL, "a"));
			w.sendKeys(Keys.BACK_SPACE);
			w.clear();
		} else {
			testCase.addTestResult(false, "Cannot clear null element (identifier='" + elementIdentifier + "').");
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

	private boolean explicitWait(ExpectedCondition<Boolean> condition, final double timeout_s) {
		boolean result;
		try {
			(new WebDriverWait(driver, Double.valueOf(timeout_s).intValue())).until(condition);
			result = true;
		} catch(TimeoutException e) {
			result = false;
		}
		return result;
	}

	public boolean waitPresent(final String elementIdentifier, final double timeout_s) {
		return explicitWait(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver webDriver) {
				return isPresent(elementIdentifier);
			}
		}, timeout_s);
	}

	public boolean waitPresent(final By by, final double timeout_s) {
		return explicitWait(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver webDriver) {
				return isPresent(by);
			}
		}, timeout_s);
	}

	public boolean waitVanish(final String elementIdentifier, final double timeout_s) {
		return explicitWait(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver webDriver) {
				return !isPresent(elementIdentifier);
			}
		}, timeout_s);
	}

	public boolean waitText(final String elementIdentifier, final String text, final double timeout_s) {
		return explicitWait(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver webDriver) {
				return isPresent(elementIdentifier) && ((text != null && text.equals(getText(elementIdentifier)))
						|| getText(elementIdentifier) == null);
			}
		}, timeout_s);
	}

	public boolean waitElementProperty(final String elementIdentifier, final String propertyName, final String expectedValue,
			final double timeout_s) {
		return explicitWait(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver webDriver) {
				WebElement element = findElementById(elementIdentifier);
				if(element != null) {
					String value = element.getAttribute(propertyName);
					return expectedValue.equals(value);
				}
				return false;
			}
		}, timeout_s);
	}

	public boolean waitEnabled(final String elementIdentifier, final double timeout_s) {

		return explicitWait(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver webDriver) {
				WebElement element = findElementById(elementIdentifier);
				if(element != null) {
					return element.isEnabled();
				}
				return false;
			}
		}, timeout_s);
	}

	public boolean waitDisabled(final String elementIdentifier, final double timeout_s) {

		return explicitWait(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver webDriver) {
				WebElement element = findElementById(elementIdentifier);
				if(element != null) {
					return !element.isEnabled();
				}
				return false;
			}
		}, timeout_s);
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
	 * @param webElementIdentifier The identifier from object repositories
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
				testCase.addTestResult(false,
						"Web element from identifier '" + webElementIdentifier + "' does not exist in object repositories.");
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
		By element = getElementIdentifier(elementId);
		if(element != null) {
			return checkText(element, expectedValue, caseSensitive);
		} else {
			testCase.addTestResult(false, "Web element not found with identifier '" + elementId);
			return false;
		}
	}

	public boolean checkText(By elementId, String expectedValue, boolean caseSensitive) {
		boolean result = false;
		if(elementId != null) {
			String text = getText(elementId);
			result = TestToolStrings.checkEquality(testCase, expectedValue, text, caseSensitive);
		} else {
			result = false;
			testCase.addTestResult(false, "Web element not found with null identifier");
		}
		return result;
	}

	public boolean checkText(String elementId, Double expectedValue, double epsilon) {
		By by = getElementIdentifier(elementId);
		if(by != null) {
			return checkText(by, expectedValue, epsilon);
		} else {
			testCase.addTestResult(false, "Web element not found with identifier '" + elementId);
			return false;
		}
	}

	public boolean checkText(By elementId, Double expectedValue, double epsilon) {
		boolean result = false;
		Double testedValue = null;
		try {
			testedValue = Double.valueOf(getText(elementId));
		} catch(NumberFormatException e) {
			testCase.addError(e.getMessage());
		}
		TestToolNumbers numbers = new TestToolNumbers(testCase);
		return numbers.checkEquality(expectedValue, testedValue, epsilon);
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
		By by = getElementIdentifier(elementId);
		if(by != null) {
			return getText(by);
		} else {
			throw new TestException("Web element not found with identifier '" + elementId + "'");
		}
	}

	public String getText(By elementId) {
		WebElement element = findElement(elementId);
		if(element != null) {
			String tagName = element.getTagName();
			if(tagName != null && tagName.equals("input")) {
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

	public void commentOptions(String id) throws ElementNotFoundException {
		DocList list = new DocList();
		for(String option : getSelectOptions(id)) {
			list.addItem(new DocString(option));
		}
		testCase.addComment(list, getClass());
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
		click(identifier);
		Select select = new Select(driver.findElement(getElementIdentifier(identifier)));
		select.selectByVisibleText(optionText);
	}

	public void selectByValue(String identifier, String optionValue) {
		click(identifier);
		Select select = new Select(driver.findElement(getElementIdentifier(identifier)));
		select.selectByValue(optionValue);
	}

	public void selectCheckbox(String identifier, boolean checked) {
		WebElement element = findElement(getElementIdentifier(identifier));
		if(element != null) {
			if(element.isSelected() != checked) {
				element.click();
			}
		}
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
