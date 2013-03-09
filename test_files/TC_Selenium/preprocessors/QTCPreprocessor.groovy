import java.io.File;
import java.io.IOException;

import qualify.doc.*;

public class QTCPreprocessor extends Preprocessor {

    public String getPattern() {
        return ".*\\.qtc";
    }

    public void process(TestSource source) {
        for(SourceLine line : source.getLines()) {
        /*    
			if(line.contains('exists(')) {
				if(line.startsWith('exists(')) {
					line.replaceFirst('exists\\(', 'check(sikuli.find(');
					line.append(')');
				} else {
					line.replaceFirst('exists\\(', 'sikuli.find(');
				}
			} else if(line.contains('find(')) {
				line.replaceFirst('find\\(', 'sikuli.find(');
			}
			
			line.replaceFirst('click\\(', 'sikuli.click(');
			line.replaceFirst('wait\\(', 'sikuli.wait(');
			line.replaceFirst('doubleClick\\(', 'sikuli.doubleClick(');
			line.replaceFirst('rightClick\\(', 'sikuli.rightClick(');
			line.replaceFirst('hover\\(', 'sikuli.moveMouse(');
			line.replaceFirst('type\\(', 'sikuli.type(');
			line.replaceFirst('press\\(', 'sikuli.press(');
			line.replaceFirst('captureScreen\\(', 'sikuli.captureScreen(');
			line.replaceFirst('typeFRKeyboard\\(', 'sikuli.typeFRKeyboard(');
			line.replaceFirst('translateMouse\\(', 'sikuli.translateMouse(');
			line.replaceFirst('pressMouse\\(', 'sikuli.pressMouse(');
			line.replaceFirst('releaseMouse\\(', 'sikuli.releaseMouse(');
			*/
			
			line.insert('\t');
			line.replaceAll("\"",  "'")
		}
		
		SourceLine line1 = source.getLines().getFirst();
		line1.insertBefore().setText('import java.util.regex.Pattern;');
		
		line1.insertBefore().setText('import java.util.concurrent.TimeUnit;');
		line1.insertBefore().setText('import org.openqa.selenium.*;');
		line1.insertBefore().setText('import org.openqa.selenium.firefox.FirefoxDriver;');
		line1.insertBefore().setText('import org.openqa.selenium.support.ui.Select;');
		line1.insertBefore().setText('import java.io.File;');
		line1.insertBefore().setText('import java.io.IOException;');
		line1.insertBefore().setText('import qualify.*;');
		line1.insertBefore().setText('import qualify.tools.*;');
		line1.insertBefore().setText('import qgtt.*;');
		line1.insertBefore().setText('import org.sikuli.script.*;');
		line1.insertBefore().setText('import QualifyTestCase;');
		
		line1.insertBefore();
		line1.insertBefore().setText('public class ' + source.getOriginalFile().getName().split('\\.')[0] + ' extends QualifyTestCase {');
		line1.insertBefore();
		
		line1.insertBefore().setText('\tpublic void run() {');

		SourceLine lastLine = source.getLines().getLast();
		lastLine.insertAfter().setText('}');
		lastLine.insertAfter().setText('\t}');
	}
}