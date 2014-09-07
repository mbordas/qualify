/*Copyright (c) 2010, Mathieu Bordas
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1- Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
2- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
3- Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package qualify.test;

import org.apache.log4j.Logger;

import qualify.TestHarness;

/**
 * This main qualifies the kernel's basic classes: Qualify, Requirement, TestCase, TestResult
 * @author Mathieu Bordas
 *
 */
public class KernelTest extends TestHarness {
	
	static Logger logger = Logger.getLogger(KernelTest.class);
	
	public static void main(String[] args) {
		TestHarness.runTestHarness(args, new KernelTest());
	}

	public void registerTestCases() {
		register(new qualify.test.unit.UT_SRD());
		register(new qualify.test.unit.UT_TestToolCSV());
		register(new qualify.test.unit.UT_SourceLinePreprocessing());
		register(new qualify.test.unit.UT_TestToolStrings());
		register(new qualify.test.unit.UT_TestToolSikuli());
		register(new qualify.test.unit.UT_TestToolDates());
		register(new qualify.test.unit.UT_Area());
		register(new qualify.test.unit.UT_AreaNeighborhood());
		register(new qualify.test.unit.UT_DocTreeNode());
		register(new qualify.test.unit.UT_LevenshteinSelector());
		register(new qualify.test.unit.UT_RegularExpressionSelector());
		register(new qualify.test.unit.UT_GetFailedTestCases());
		register(new qualify.test.unit.UT_EvaluateTemplate());
		
		addSanityTest(new ST_PackageAndDependencies());
		
		register(new TC_CompileAndRun());
		register(new TC_BeforeRunAfterError());
		register(new TC_Export());
		register(new TC_Checking());
		register(new TC_TestCasesTable());
		register(new TC_ReleaseNote());
		register(new TC_ReportingWithoutSource());
		register(new TC_RegressionDetection());
		register(new TC_SanityTesting());
		register(new TC_Requirement());
		register(new TC_RunByKeyword());
		register(new TC_RunByName());
		register(new TC_Process());
		register(new TC_DocTreeNode());
		register(new TC_ScriptTestCases());
		register(new TC_ScriptTestCasesErrorOnCompile());
		register(new TC_RunLastNOKs());
	}

}
