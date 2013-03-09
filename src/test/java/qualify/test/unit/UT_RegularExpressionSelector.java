package qualify.test.unit;

import java.util.List;

import qualify.TestCase;
import qualify.tools.TestToolNumbers;
import qualify.tools.TestToolStrings;
import qualify.tools.pattern.RegularExpressionSelector;

public class UT_RegularExpressionSelector extends TestCase {

	@Override
	public void run() {
		TestToolNumbers numbers = new TestToolNumbers(this);

		List<String> list = TestToolStrings.toList(new String[]{"a", "ab", "abc", "abcd", "abcde"});

		setRequirementTarget("RegularExpressionSelector");
		numbers.checkEquality(3, (new RegularExpressionSelector("ab.d")).getIndex(list)); // selected string is "abcd" at index 3
		numbers.checkEquality(2, (new RegularExpressionSelector("[ab]{2}.{1}")).getIndex(list)); // selected string is "abc" at index 2
		
		numbers.checkSameValues(new int[]{3, 4}, (new RegularExpressionSelector(".*cd.*")).getIndexes(list));
		
		comment("test");
	}

}
