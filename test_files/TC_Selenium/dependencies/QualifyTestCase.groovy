import qualify.doc.*;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import java.io.File;
import java.io.IOException;
import qualify.*;
import qualify.tools.*;
import qgtt.*;
import org.sikuli.script.*;

public abstract class QualifyTestCase extends TestCase {

	public TestToolSikuli sikuli;
	public TestToolSelenium selenium;

	public void beforeRun() {
		sikuli = new TestToolSikuli(this);
		selenium = new TestToolSeleniumFirefox();
	}
	public void afterRun() {
		selenium.quit();
	}
}