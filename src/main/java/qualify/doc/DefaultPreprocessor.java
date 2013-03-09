/*Copyright (c) 2011, Mathieu Bordas
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

package qualify.doc;

import org.apache.log4j.Logger;

import qualify.tools.TestToolStrings;

public class DefaultPreprocessor extends Preprocessor {

	static Logger logger = Logger.getLogger(DefaultPreprocessor.class);

	@Override
	public String getPattern() {
		return ".*";
	}

	@Override
	public void process(TestSource source) {
		String regexRequirement = ".*// *Q\\.requirement *:(.*)";

		for(SourceLine line : source.getLines()) {
			if(TestToolStrings.matches(regexRequirement, line.getText())) {
				String requirement = TestToolStrings.getGroups(regexRequirement, line.getText())[1].trim();
				line.insert("setRequirementTarget(\"" + requirement + "\");");
			}
		}
	}

	public String[] getKeywords(TestSource source) {
		String regexKeywords = ".*// *Q\\.keywords *: *([ _\\-a-zA-Z]*)";
		String[] keywords = new String[]{};
		logger.debug("Looking for keywords for test " + source.getOriginalFile().getAbsolutePath());
		for(SourceLine line : source.getLines()) {
			if(TestToolStrings.matches(regexKeywords, line.getText())) {
				String[] words = TestToolStrings.getGroups(regexKeywords, line.getText());
				for(int i = 1; i < words.length; i++) {
					logger.debug("keyword found: '" + words[i] + "'");
				}
				keywords = TestToolStrings.concat(keywords, words, 1, words.length - 1);
			}
		}
		return keywords;
	}
}