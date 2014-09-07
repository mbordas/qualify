package qualify.test;

import java.io.File;

import org.openqa.selenium.OutputType;

import qualify.*;
import qualify.doc.Attachment.Type;
import qualify.tools.*;

public class TC_Selenium extends TestCase {

	@Override
	public void run() {
		// Q.requirement: TestToolSelenium
		TestToolSeleniumFirefox browser = new TestToolSeleniumFirefox(this);
		
		File f = new File(Qualify.getOptionValue('reference_release_note'));
		browser.get("file://" + f.getAbsolutePath());
		
		browser.quit();
	}

}
