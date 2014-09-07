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

package qualify.tools;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import qualify.ErrorsAndWarnings;
import qualify.TestCase;

public class TestToolImages {
	
	private TestCase testCase = null;
	
	public TestToolImages(TestCase tc) {
		this.testCase = tc;
	}
	
	public static File saveImage(BufferedImage img, File outputFile) {
		File result = null;
		try {
			TestToolFile.createDir(outputFile.getParentFile());
			ImageIO.write(img, "png", outputFile);
			result = outputFile;
		} catch (IOException e) {
			ErrorsAndWarnings.addException(e);
		}
		return result;
	}
	
	public static BufferedImage verticalConcat(BufferedImage topImage, BufferedImage bottomImage) {
		int globalWidth = Math.max(topImage.getWidth(), bottomImage.getWidth());
		int globalHeight = topImage.getHeight() + bottomImage.getHeight();
		
		BufferedImage finalImg = new BufferedImage(globalWidth, globalHeight, topImage.getType());  
		
		finalImg.createGraphics().drawImage(topImage, 0, 0, null);
		finalImg.createGraphics().drawImage(bottomImage, 0, topImage.getHeight(), null);
		
        return finalImg;
	}
	
	public static Dimension getImageDimension(File imageFile) {
	    Dimension result = null;
	    String suffix = TestToolFile.getExtension(imageFile);
	    Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
	    if (iter.hasNext()) {
	        ImageReader reader = iter.next();
	        try {
	            ImageInputStream stream = new FileImageInputStream(imageFile);
	            reader.setInput(stream);
	            int width = reader.getWidth(reader.getMinIndex());
	            int height = reader.getHeight(reader.getMinIndex());
	            result = new Dimension(width, height);
	        } catch (IOException e) {
	            ErrorsAndWarnings.addException(e);
	        } finally {
	            reader.dispose();
	        }
	    } else {
	        ErrorsAndWarnings.addError("No reader found for image with extension: " + suffix);
	    }
	    return result;
	}


}
