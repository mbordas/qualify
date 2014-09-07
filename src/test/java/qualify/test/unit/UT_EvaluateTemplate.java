package qualify.test.unit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.codehaus.groovy.control.CompilationFailedException;

import qualify.TestCase;
import qualify.tools.TestToolFile;
import qualify.tools.TestToolGroovy;
import qualify.tools.TestToolStrings;

public class UT_EvaluateTemplate extends TestCase {

	@Override
	public void run() {
		setRequirementTarget("TestToolGroovy");
		TestToolStrings strings = new TestToolStrings(this);
		TestToolGroovy groovy = new TestToolGroovy(this);

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("year1", 2010);
		map.put("year2", 2012);
		map.put("who", "Mathieu Bordas");

		// Templates defined in strings
		strings.checkEquality("Copyright (c) 2010-2012, Mathieu Bordas",
				groovy.evaluateTemplate("Copyright (c) <% print year1 %>-<% print year2%>, ${who}", map));
		
		strings.checkEquality("Copyright (c) 2010-2013, Mathieu Bordas",
				groovy.evaluateTemplate("Copyright (c) <% print year1 %>-<% print (year2 + 1)%>, ${who}", map));
		
		// Template does processing and output
		strings.checkEquality("Copyright (c) 2010-2014, Mathieu Bordas",
				groovy.evaluateTemplate("<% def year3 = year2 + 2%>Copyright (c) <% print year1 %>-<% print year3%>, ${who}", map));

		// Template written in file
		File templateScript;
		try {
			templateScript = TestToolFile.createNewTemporaryFile("txt");
			TestToolFile.append(templateScript, "<% def year3 = year2 + 2%>Copyright (c) <% print year1 %>-<% print year3%>, ${who}");
			strings.checkEquality("Copyright (c) 2010-2014, Mathieu Bordas", groovy.evaluateTemplate(templateScript, map));
		} catch (IOException e) {
			e.printStackTrace();
			need(false);
		} catch (CompilationFailedException e) {
			e.printStackTrace();
			need(false);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			need(false);
		}
	}

}
