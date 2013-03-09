package qualify.test.unit;

import java.util.List;

import qualify.TestCase;
import qualify.tools.TestToolNumbers;
import qualify.tools.TestToolStrings;
import qualify.tools.pattern.LevenshteinSelector;

public class UT_LevenshteinSelector extends TestCase {

	@Override
	public void run() {
		TestToolNumbers numbers = new TestToolNumbers(this);
		
		List<String> list = TestToolStrings.toList(new String[]{"a", "ab", "abc", "abcd", "abcde"});
		
		setRequirementTarget("LevenshteinSelector");
		numbers.checkEquality(3, (new LevenshteinSelector("abcd")).getIndex(list)); // selected string is "abcd" at index 3
		numbers.checkEquality(2, (new LevenshteinSelector("abcX")).getIndex(list)); // selected string is "abc" at index 2 (so good as "abcd" at index 3)
	}

}
