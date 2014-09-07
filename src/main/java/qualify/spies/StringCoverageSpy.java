package qualify.spies;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import qualify.TestCase;
import qualify.TestHarness;
import qualify.TestSpy;

public class StringCoverageSpy extends TestSpy {

	private static Logger logger = Logger.getLogger(StringCoverageSpy.class);

	String title = null;
	Map<String, Integer> harnessCoverage = new TreeMap<String, Integer>();

	public StringCoverageSpy(String title) {
		this.title = title;
	}

	@Override
	public void beforeHarness(TestHarness harness) {
	}

	@Override
	public void afterHarness(TestHarness harness) {
		logger.info("* COVERAGE: " + title);
		for(Entry<String, Integer> entry : harnessCoverage.entrySet()) {
			String key = entry.getKey();
			int coverage = entry.getValue();
			if(coverage == 0) {
				logger.warn("! " + coverage + "\t" + key);
			} else {
				logger.info("  " + coverage + "\t" + key);
			}
		}
	}

	@Override
	public void beforeTest(TestCase testCase) {
	}

	@Override
	public void afterTest(TestCase testCase) {
		if(testCase instanceof Coverageable) {
			Map<String, Integer> coverage = ((Coverageable)testCase).getCoverage(title);

			if(coverage != null) {
				for(Entry<String, Integer> entry : coverage.entrySet()) {
					String key = entry.getKey();
					Integer value = entry.getValue();

					if(value != null) {
						Integer precValue = harnessCoverage.get(key);
						if(precValue == null) {
							harnessCoverage.put(key, value);
						} else {
							harnessCoverage.put(key, precValue + value);
						}
					}
				}
			}
		}
	}

	public interface Coverageable {
		public Map<String, Integer> getCoverage(String title);
	}

}
