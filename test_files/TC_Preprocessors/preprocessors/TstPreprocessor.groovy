import java.io.File;
import java.io.IOException;

import qualify.doc.*;

public class TstPreprocessor extends Preprocessor {

	public String getPattern() {
		return ".*\\.tst";
	}

	public void process(TestSource source) {
		for(SourceLine line : source.getLines()) {
			if(line.contains("Q.req:")) {
				line.replaceAll("Q\\.req:", "setRequirementTarget(\"");
				line.append("\");");
			} else if(line.getText().startsWith("--")) {
				line.replaceAll("--", "comment(\"");
				line.append("\");");
			}
		}
		
		SourceLine line1 = source.getLines().getFirst();
		line1.insertBefore().setText("import java.io.File;");
		line1.insertBefore().setText("import java.io.IOException;");
		line1.insertBefore().setText("import qualify.TestCase;");
		line1.insertBefore().setText("public class Test3 extends TestCase {");
		line1.insertBefore().setText("public void run() {");
		
		source.getLines().getLast().insertAfter().setText("}}");
	}

}