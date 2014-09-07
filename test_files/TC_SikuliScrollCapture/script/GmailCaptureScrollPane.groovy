import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import qualify.TestCase;
import qualify.tools.*;

public class GmailCaptureScrollPane extends TestCase {
	
	public void run() {
		TestToolSikuli sikuli = new TestToolSikuli(this);
		TestToolSelenium browser = new TestToolSeleniumInternetExplorer(this);
		
		browser.get('http://www.gmail.com')
		pause(2.0)
		sikuli.captureScreen()
		
		browser.capturePage()
		
		
		pause(0.2)
		browser.type('app(gmail)>page(login)>field(password)', 'mdcgn832')
		browser.type('app(gmail)>page(login)>field(login)', 'mat.bordas\n')
		
		pause(2.0)
		sikuli.captureScreen()

		browser.quit();
	}

}