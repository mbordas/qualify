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

package qualify.tools;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qualify.Qualify;
import qualify.TestCase;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class TestToolSeleniumFirefox extends TestToolSelenium {

	private static final Logger LOG = LoggerFactory.getLogger(TestToolSeleniumFirefox.class);

	public static final String OPTION_FIREFOX_BINARY = "firefox_binary";
	public static final String OPTION_FIREFOX_PROFILE = "firefox_profile";
	public static final String OPTION_FIREFOX_HEADLESS = "firefox_headless";
	public static final String OPTION_FIREFOX_LANGUAGE = "firefox_language"; // ex: en-GB
	public static final String OPTION_FIREFOX_GECKODRIVER_BINARY = "firefox_geckodriver_binary";
	public static final String OPTION_FIREFOX_PRIVATE_BROWSER = "firefox_private_browser";

	public TestToolSeleniumFirefox(TestCase tc) {
		super(tc);

		final String geckodriverBinary = Qualify.getOptionValue(OPTION_FIREFOX_GECKODRIVER_BINARY);
		if(geckodriverBinary != null) {
			System.setProperty("webdriver.gecko.driver", geckodriverBinary);
		}

		final FirefoxOptions options = new FirefoxOptions();

		final String profilePath = Qualify.getOptionValue(OPTION_FIREFOX_PROFILE);
		if(profilePath != null) {
			FirefoxProfile profile = new FirefoxProfile(new File(profilePath));
			options.setProfile(profile);
		}
		if(Qualify.isOption(OPTION_FIREFOX_LANGUAGE)) {
			final String language = Qualify.getOptionValue(OPTION_FIREFOX_LANGUAGE);
			System.out.println("Setting Firefox language: " + language);
			options.addPreference("intl.locale.requested", language);
		}

		final String binaryPath = Qualify.getOptionValue(OPTION_FIREFOX_BINARY);
		if(binaryPath != null) {
			final File firefoxBinary = new File(binaryPath);
			assert firefoxBinary.exists();
			options.setBinary(new FirefoxBinary(firefoxBinary));
		}

		if(Qualify.isOptionSet(OPTION_FIREFOX_HEADLESS)) {
			System.out.println("Using Firefox headless");
			options.addArguments("--headless");
		}

		if(Qualify.isOptionSet(OPTION_FIREFOX_PRIVATE_BROWSER)) {
			System.out.println("Using Firefox private browser");
			options.addArguments("--private");
		}

		options.setCapability("marionette", true);

		if(downloadDir != null) {
			final String downloadDirPath = downloadDir.getAbsolutePath();
			options.addPreference("browser.download.dir", downloadDirPath);
			options.addPreference("browser.download.lastDir", downloadDirPath);
			options.addPreference("browser.download.folderList", 2);
			System.out.println("Browser download directory set to : " + downloadDirPath);
		}

		driver = new FirefoxDriver(options);

		driver.manage().window().setSize(new Dimension(1920, 1080));
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}
}
