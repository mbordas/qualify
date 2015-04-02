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

package qualify.doc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import qualify.Requirement;

public class DocumentUtils {

	static Logger logger = Logger.getLogger(Requirement.class);

	/**
	 * Makes a copy of one internal resource file to a relative folder from ouputFile's parent folder.
	 * 
	 * @param outputFile
	 *            The file from which the relative path will determine where to to copy the resource.
	 * @param resourceFilePath
	 *            The internal path of the resource.
	 * @param relativeOutputPath
	 *            The relative path of the copied file from the output file's parent folder.
	 * @throws IOException
	 *             Thrown if the resource cannot be copied on file system.
	 */
	public static void copyFileFromResources(File outputDir, String resourceFilePath, String relativeOutputPath) throws IOException {
		logger.debug("copyFileFromResources('" + outputDir.getAbsolutePath() + "', '" + resourceFilePath + "', '" + relativeOutputPath
				+ "'");
		InputStream input = Requirement.class.getClassLoader().getResourceAsStream(resourceFilePath);
		File file = null;
		if(outputDir != null) {
			FileUtils.forceMkdir(outputDir);
			file = new File(outputDir.getPath() + "/" + relativeOutputPath);
		}
		FileUtils.forceMkdir(file.getParentFile());
		FileOutputStream fos = new FileOutputStream(file);
		IOUtils.copy(input, fos);
		fos.close();
	}

	public static void generateStyleFolder(File parentDir) throws IOException {
		FileUtils.forceMkdir(parentDir);
		DocumentUtils.copyFileFromResources(parentDir, "style/release_note.xsl", "style/release_note.xsl");
		DocumentUtils.copyFileFromResources(parentDir, "style/source.xsl", "style/source.xsl");
		DocumentUtils.copyFileFromResources(parentDir, "style/test_cases_table.xsl", "style/test_cases_table.xsl");
		DocumentUtils.copyFileFromResources(parentDir, "style/qualify.css", "style/qualify.css");
		DocumentUtils.copyFileFromResources(parentDir, "style/jquery.treeTable.css", "style/jquery.treeTable.css");
		DocumentUtils.copyFileFromResources(parentDir, "style/jquery.tools.min.js", "style/jquery.tools.min.js");
		DocumentUtils.copyFileFromResources(parentDir, "style/jquery.js", "style/jquery.js");
		DocumentUtils.copyFileFromResources(parentDir, "style/jquery.ui.js", "style/jquery.ui.js");
		DocumentUtils.copyFileFromResources(parentDir, "style/jquery.treeTable.js", "style/jquery.treeTable.js");
		DocumentUtils.copyFileFromResources(parentDir, "style/toggle-collapse-dark.png", "style/toggle-collapse-dark.png");
		DocumentUtils.copyFileFromResources(parentDir, "style/toggle-expand-dark.png", "style/toggle-expand-dark.png");
	}

}
