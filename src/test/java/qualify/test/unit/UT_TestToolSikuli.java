package qualify.test.unit;

import java.io.File;
import java.util.List;

import qualify.TestCase;
import qualify.tools.TestObject;
import qualify.tools.TestToolFile;
import qualify.tools.TestToolNumbers;
import qualify.tools.TestToolSikuli;

public class UT_TestToolSikuli extends TestCase {
	
	TestToolFile files = new TestToolFile(this);
	TestToolNumbers numbers = new TestToolNumbers(this);
	
	@Override
	public void run() {
		setRequirementTarget("TestToolSikuli");
		
		TestToolSikuli s1 = new TestToolSikuli(this);
		check(TestToolFile.exists(new File(TestToolSikuli.DEFAULT_IMAGES_DIRECTORY)));
		TestToolFile.deleteFile(new File(TestToolSikuli.DEFAULT_IMAGES_DIRECTORY));
		
		// Alternative images from object repositories
		TestObject.loadObjectRepository(new File("test_files/TC_SikuliScrollCapture/repo/firefox.qor"));
		TestObject obj = TestObject.getFromRepo("app(firefox)>panel(main_view)>image(bottom_right)");
		need(obj != null);
		files.checkExists(obj.getFile());
		List<File> alternativeImages = TestToolSikuli.getImageFiles(obj);
		need(alternativeImages != null);
		numbers.checkEquality(3, alternativeImages.size());
		for(File f : alternativeImages) {
			files.checkExists(f);
		}
	}

}
