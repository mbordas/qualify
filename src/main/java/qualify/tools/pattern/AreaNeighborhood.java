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

import org.apache.log4j.Logger;

import qualify.ErrorsAndWarnings;
import qualify.tools.geometry.Area;

public class AreaNeighborhood implements AreaTransformer {
	
	static Logger logger = Logger.getLogger(AreaNeighborhood.class);
	
	private String direction = null;
	private int sizeInPixels = 0;
	
	public AreaNeighborhood(String direction, int sizeInPixels) {
		this.direction = direction;
		this.sizeInPixels = sizeInPixels;
	}

	public Area transform(final Area area) {
		logger.debug("Transforming to '" + direction + "' with size=" + sizeInPixels);
		
		int x = area.topLeftCorner().x;
		int y = area.topLeftCorner().y;
		int width = area.getWidth();
		int height = area.getHeight();
		
		logger.debug("old position is " + x + "," + y + "," + width + "," + height);
		
		// Computing position
		if("west".equals(direction)) {
			x = area.topLeftCorner().x - sizeInPixels;
			width = sizeInPixels;
		} else if("east".equals(direction)) {
			x = area.topRightCorner().x;
			width = sizeInPixels;
		} else if("north".equals(direction)) {
			y = area.topLeftCorner().y - sizeInPixels;
			height = sizeInPixels;
		} else if("south".equals(direction)) {
			y = area.bottomLeftCorner().y;
			height = sizeInPixels;
		} else {
			ErrorsAndWarnings.addError("Unknown direction: " + direction);
		}
		
		logger.debug("new position is " + x + "," + y + "," + width + "," + height);
		
		// Creating the resulting Area with new position and dimensions
		Area result = new Area(area);
		result.moveTo(x, y);
		result.setWidth(width);
		result.setHeight(height);
		
		return result;
	}

}
