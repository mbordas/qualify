/*Copyright (c) 2010-2012, Mathieu Bordas
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

package qualify.tools.pattern;

import java.util.List;

import qualify.tools.TestToolStrings;

public class LevenshteinSelector implements StringSelector {

	private String text;
	private int maximumDistance = 0;
	private boolean distanceRestriction = false;

	public LevenshteinSelector(String text) {
		this.text = text;
		this.maximumDistance = 0;
		this.distanceRestriction = false;
	}

	public int getIndex(List<String> list) {
		int result = StringSelector.NO_RESULT_INDEX;
		int minD = -1;

		int index = 0;
		for(String currentString : list) {
			int ld = TestToolStrings.getLevenshteinDistance(currentString, text);
			if((minD == -1) || (minD > ld)) {
				minD = ld;
				result = index;
			}
			index++;
		}
		
		if(distanceRestriction) {
			if(minD > maximumDistance) {
				result = NO_RESULT_INDEX;
			}
		}

		return result;
	}

	public int[] getIndexes(List<String> list) {
		// TODO Auto-generated method stub
		return null;
	}

}
