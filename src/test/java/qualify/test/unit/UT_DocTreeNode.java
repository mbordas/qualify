package qualify.test.unit;

import qualify.TestCase;
import qualify.doc.DocTreeNode;
import qualify.tools.TestToolStrings;

public class UT_DocTreeNode extends TestCase {

	@Override
	public void run() {
		setRequirementTarget("Export");
		TestToolStrings strings = new TestToolStrings(this);
		
		DocTreeNode n1 = new DocTreeNode();
		n1.setLocalIndex(1);
		DocTreeNode n2 = new DocTreeNode();
		n2.setLocalIndex(2);
		
		DocTreeNode n3 = new DocTreeNode();
		n2.addChild(n3);
		
		DocTreeNode n4 = new DocTreeNode();
		n3.addChild(n4);
		
		strings.checkEquality("2", n3.getPathIndex());
		strings.checkEquality("2-1", n4.getPathIndex());
	}

}
