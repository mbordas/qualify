import java.io.File;
import java.io.IOException;

import qualify.doc.*;

public class GroovyPreprocessor extends Preprocessor {

	public String getPattern() {
		return ".*\\.groovy";
	}

	public void process(TestSource source) {
		for(SourceLine line : source.getLines()) {
			if(line.contains("// Q.req:")) {
				line.replaceAll("// Q\\.req:", "setRequirementTarget(\"");
				line.append("\");");
				
				System.out.println("Replacing source line: " + line.getText());
			}
		}
	}

}