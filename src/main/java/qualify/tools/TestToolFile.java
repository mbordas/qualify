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

package qualify.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

import org.apache.commons.io.FileUtils;

import qualify.ErrorsAndWarnings;
import qualify.TestCase;
import qualify.doc.DocTreeNode;
import qualify.testRunner.StreamListener;

/**
 * Provides methods to easily manage files and directory from the File System.
 * 
 * @author Mathieu Bordas
 */
public class TestToolFile {

	private TestCase testCase = null;

	public final static String TEMPORARY_FILES_DIRECTORY = "tmp_files";
	public final static String TEMPORARY_FILE_PREFIX = "tmpFile_";
	private static int TEMPORARY_FILE_COUNT = 0;

	public TestToolFile(TestCase tc) {
		testCase = tc;
	}

	public static void exportResourceFile(String resourcePath, File destFile) throws IOException {
		InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath);
		destFile.getParentFile().mkdirs();
		org.apache.commons.io.FileUtils.copyInputStreamToFile(is, destFile);
		is.close();
	}

	/**
	 * Checks that the file does not exist.
	 * 
	 * @param expectedFileName
	 *            The name of the file. That name is relative to TestHarness' working dir.
	 */
	public boolean checkDoesntExist(String expectedFileName) {
		File f = new File(expectedFileName);
		return checkDoesntExist(f);
	}

	public void checkDoesntExistElseAttach(String expectedFileName) {
		File f = new File(expectedFileName);
		if(!checkDoesntExist(f)) {
			testCase.attachFile(f);
		}
	}

	/**
	 * Checks that the file exists.
	 * 
	 * @param expectedFileName
	 *            The name of the file. That name is relative to TestHarness' working dir.
	 */
	public void checkExists(String expectedFileName) {
		File f = new File(expectedFileName);
		checkExists(f);
	}

	/**
	 * Checks that the file exists and attaches it to the test case report.
	 * 
	 * @param expectedFileName
	 *            The name of the file. That name is relative to TestHarness' working dir.
	 */
	public void checkExistsAndAttach(String expectedFileName) {
		File f = new File(expectedFileName);
		if(checkExists(f)) {
			testCase.attachFile(f);
		}
	}

	/**
	 * Checks that the file exists.
	 * 
	 * @param expectedFile
	 *            The file to check.
	 */
	public boolean checkExists(File expectedFile) {
		if(expectedFile.exists()) {
			testCase.addTestResult(true, "file '" + expectedFile.getAbsolutePath() + "' exists");
			return true;
		} else {
			testCase.addTestResult(false, "file '" + expectedFile.getAbsolutePath() + "' does not exist");
			return false;
		}
	}

	public static boolean exists(File expectedFile) {
		return expectedFile.exists();
	}

	public static boolean exists(String expectedFileName) {
		return exists(new File(expectedFileName));
	}

	public static String getExtension(File f) {
		String fileName = f.getName();
		String result = null;
		if(fileName.lastIndexOf('.') != -1) {
			result = fileName.substring(fileName.lastIndexOf('.'));
			if(result.startsWith(".")) {
				result = result.substring(1);
			}
		}
		return result;
	}

	/**
	 * Checks that the file does not exist.
	 * 
	 * @param expectedFile
	 *            The file to check.
	 */
	public boolean checkDoesntExist(File expectedFile) {
		if(expectedFile.exists()) {
			testCase.addTestResult(false, "file '" + expectedFile.getAbsolutePath() + "' exists");
			return false;
		} else {
			testCase.addTestResult(true, "file '" + expectedFile.getAbsolutePath() + "' does not exist");
			return true;
		}
	}

	public static String md5(File f) throws NoSuchAlgorithmException, IOException {
		InputStream fis = new FileInputStream(f);
		String result = TestToolStrings.md5(fis);
		fis.close();
		return result;
	}

	/**
	 * Checks the CRC32 of the file.
	 * 
	 * @param expectedFile
	 *            The file to check.
	 * @param expectedChecksum
	 *            The CRC32 checksum
	 * @throws IOException
	 */
	public void checkCRC32Checksum(File expectedFile, long expectedChecksum) throws IOException {
		if(expectedFile.exists()) {
			if(expectedFile.isFile()) {
				long testedChecksum = FileUtils.checksum(expectedFile, new CRC32()).getValue();
				testCase.addTestResult(testedChecksum == expectedChecksum, "expected file's CRC32 = '" + expectedChecksum
						+ "' | tested CRC32 = '" + testedChecksum + "'");
			} else {
				testCase.addTestResult(false, "expected file '" + expectedFile.getName() + "' id not a file");
			}
		} else {
			testCase.addTestResult(false, "expected file '" + expectedFile.getName() + "' does not exist");
		}
	}

	/**
	 * Recursively deletes the directory.
	 * 
	 * @param dirPath
	 *            The name of the directory. That name is relative to TestHarness' working dir.
	 * @throws IOException
	 */
	public static void deleteDir(String dirPath) throws IOException {
		deleteDir(new File(dirPath));
	}

	public static void deleteDir(File dir) throws IOException {
		FileUtils.deleteDirectory(dir);
	}

	/**
	 * Recursively deletes all files and sub-directories of the directory.
	 * 
	 * @param dirPath
	 *            The name of the directory. That name is relative to TestHarness' working dir.
	 * @throws IOException
	 */
	public static void emptyDir(String dirPath) throws IOException {
		emptyDir(new File(dirPath));
	}

	/**
	 * Recursively deletes all files and sub-directories of the directory.
	 * 
	 * @param dirFile
	 *            The directory's java.io.File.
	 * @throws IOException
	 */
	public static void emptyDir(File dirFile) throws IOException {
		try {
			FileUtils.deleteDirectory(dirFile);
			FileUtils.forceMkdir(dirFile);
		} catch(IOException e) {
			throw new IOException("Error when deleting dir: " + dirFile.getAbsolutePath(), e);
		}
	}

	public static List<File> listFiles(File dirToList, String nameFilter, boolean recursively) {
		Pattern p = Pattern.compile(nameFilter);
		String[] s = dirToList.list();
		if(s != null) {
			List<File> result = new ArrayList<File>();
			for(int i = 0; i < s.length; i++) {
				File f = new File(dirToList.getAbsolutePath() + "/" + s[i]);
				if(f.isFile()) {
					Matcher m = p.matcher(s[i]);
					if(m.matches()) {
						result.add(f);
					}
				} else {
					if(recursively) {
						result.addAll(listFiles(f, nameFilter, true));
					}
				}
			}
			return result;
		} else {
			return new LinkedList<File>();
		}

	}

	public static List<File> listDirs(File dirToList, String nameFilter) {
		Pattern p = Pattern.compile(nameFilter);
		String[] s = dirToList.list();
		List<File> result = new ArrayList<File>();
		for(int i = 0; i < s.length; i++) {
			File f = new File(dirToList.getAbsolutePath() + "/" + s[i]);
			if(f.isDirectory()) {
				Matcher m = p.matcher(s[i]);
				if(m.matches()) {
					result.add(f);
				}
			}
		}
		return result;
	}

	/**
	 * @param inputFile
	 *            The file to parse
	 * @param expectedString
	 *            The string to find
	 * @return true if expectedString is found in inputFile
	 * @throws IOException
	 */
	public static boolean find(File inputFile, String expectedString) throws IOException {
		return getLines(inputFile, expectedString).size() > 0;
	}

	/**
	 * Checks that the expected String is found at least one time in the input File
	 * 
	 * @param inputFile
	 *            The File to parse
	 * @param expectedString
	 *            The String to find
	 * @return
	 */
	public boolean checkFind(File inputFile, String expectedString) {
		boolean result = false;
		try {
			result = find(inputFile, expectedString);
			if(result == true) {
				testCase.addTestResult(true, "expected string '" + expectedString + "' found in file '" + inputFile.getAbsolutePath() + "'");
			} else {
				testCase.addTestResult(false, "expected string '" + expectedString + "' not found in file '" + inputFile.getAbsolutePath()
						+ "'");
			}
		} catch(IOException e) {
			result = false;
			testCase.addTestResult(false, "file '" + inputFile.getAbsolutePath() + "' does not exist");
		}
		return result;
	}

	/**
	 * Checks that the expected String is not found in the input File
	 * 
	 * @param inputFile
	 *            The File to parse
	 * @param expectedString
	 *            The String to look for
	 * @return true if unexpectedString is not found in inputFile
	 */
	public boolean checkNotFound(File inputFile, String expectedString) {
		boolean result = false;
		try {
			result = find(inputFile, expectedString);
			if(result == false) {
				testCase.addTestResult(true, "unexpected string '" + expectedString + "' not found in file '" + inputFile.getAbsolutePath()
						+ "'");
			} else {
				testCase.addTestResult(false, "unexpected string '" + expectedString + "' found in file '" + inputFile.getAbsolutePath()
						+ "'");
			}
		} catch(IOException e) {
			result = false;
			testCase.addTestResult(false, "file '" + inputFile.getAbsolutePath() + "' does not exist");
			e.printStackTrace();
		}
		return result;
	}

	public static int getLineNumber(File inputFile, String searchedValue) throws IOException {
		int result = 0;
		InputStream ips = new FileInputStream(inputFile);
		InputStreamReader ipsr = new InputStreamReader(ips);
		BufferedReader br = new BufferedReader(ipsr);
		String ligne;
		int lineNumber = 0;
		while((ligne = br.readLine()) != null) {
			lineNumber++;
			if(ligne.indexOf(searchedValue) != -1) {
				result = lineNumber;
			}
		}
		br.close();
		ipsr.close();
		ips.close();
		return result;
	}

	public static String getLine(File inputFile, String searchedValue) throws IOException {
		return getLines(inputFile, searchedValue).get(0);
	}

	public static List<String> getLines(File inputFile, String searchedValue) throws IOException {
		LinkedList<String> result = new LinkedList<String>();
		String fileContent = getContentAsString(inputFile, Charset.defaultCharset().displayName());

		for(String line : fileContent.split("\\r?\\n")) {
			if(line.indexOf(searchedValue) != -1) {
				result.add(line);
			}
		}
		return result;
	}

	public static String getContentAsString(InputStream is, String encoding) throws IOException {
		StringBuffer buf = new StringBuffer();
		InputStreamReader freader = null;
		try {
			freader = new InputStreamReader(is, Charset.forName(encoding));
			char array[] = new char[2000];
			int read = 0;
			while((read = freader.read(array)) > -1) {
				if(read > 0) {
					buf.append(array, 0, read);
				} else {
					Thread.sleep(50);
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			if(freader != null) {
				freader.close();
			}
		}
		return buf.toString();
	}

	public static String getContentAsString(File inputFile) throws IOException {
		return getContentAsString(inputFile, Charset.defaultCharset().displayName());
	}

	public static String getContentAsString(File inputFile, String encoding) throws IOException {
		FileInputStream fis = new FileInputStream(inputFile);
		String result = getContentAsString(fis, encoding);
		fis.close();
		return result;
	}

	public static void copyFile(File srcFile, File destFile) throws IOException {
		if(destFile.exists() && destFile.isDirectory()) {
			copyFileToDirectory(srcFile, destFile);
		} else {
			FileUtils.copyFile(srcFile, destFile);
		}
	}

	public static void copyFileToDirectory(File srcFile, File destDir) throws IOException {
		File dstFile = new File(destDir, srcFile.getName());
		if(dstFile.exists()) {
			FileUtils.deleteQuietly(dstFile);
		}
		FileUtils.copyFileToDirectory(srcFile, destDir);
	}

	public static void moveFileToDirectory(File srcFile, File destDir) throws IOException {
		FileUtils.moveFileToDirectory(srcFile, destDir, true);
	}

	public static void copyFile(String srcFilePath, String destFilePath) throws IOException {
		FileUtils.copyFile(new File(srcFilePath), new File(destFilePath));
	}

	public static void copyFile(URL url, File destFile) {
		InputStream input = null;
		FileOutputStream fos = null;

		try {
			URLConnection connection = url.openConnection();
			int fileLength = connection.getContentLength();

			if(fileLength == -1) {
				ErrorsAndWarnings.addError("Invalid URL: " + url);
				return;
			}

			input = connection.getInputStream();

			fos = new FileOutputStream(destFile);
			byte[] buffer = new byte[1024];
			int read;

			while((read = input.read(buffer)) > 0) {
				fos.write(buffer, 0, read);
			}
			fos.flush();
		} catch(IOException e) {
			ErrorsAndWarnings.addError("Error while trying to download from url: " + url);
			e.printStackTrace();
		} finally {
			try {
				fos.close();
				input.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void createFile(File file) throws IOException {
		if(!file.exists()) {
			if(file.getParentFile() != null) {
				createDir(file.getParentFile());
			}
			FileUtils.touch(file);
		}
	}

	public static void createDir(File dir) throws IOException {
		FileUtils.forceMkdir(dir);
	}

	public static void createDir(String dirRelativePath) throws IOException {
		createDir(new File(dirRelativePath));
	}

	public static void deleteFile(File file) {
		deleteFile(file.getAbsolutePath());
	}

	public static void deleteFile(String filePath) {
		FileUtils.deleteQuietly(new File(filePath));
	}

	public static boolean isFileNewer(File file, File reference) {
		return FileUtils.isFileNewer(file, reference);
	}

	public static StreamListener tail(File f) throws FileNotFoundException {
		StreamListener result = new StreamListener(new FileInputStream(f));
		return result;
	}

	public static void append(File textFile, String text) {
		FileWriter fw = null;
		BufferedWriter bw = null;

		try {
			fw = new FileWriter(textFile, true);
			bw = new BufferedWriter(fw);
			bw.write(text);
			bw.flush();
		} catch(IOException e) {
			ErrorsAndWarnings.addException(e);
		} finally {
			if(bw != null) {
				try {
					bw.close();
				} catch(IOException e) {
					ErrorsAndWarnings.addException(e);
				}
			}
			if(fw != null) {
				try {
					fw.close();
				} catch(IOException e) {
					ErrorsAndWarnings.addException(e);
				}
			}
		}
	}

	public DocTreeNode getTreeDescription(File f, String filter) {
		DocTreeNode result = null;
		if(f.isDirectory()) {
			result = new DocTreeNode();
			result.setParameter("type", "dir");
			result.setParameter("name", f.getName());
			for(File subFile : listFiles(f, filter, false)) {
				DocTreeNode subNode = new DocTreeNode();
				subNode.setParameter("type", "file");
				subNode.setParameter("name", subFile.getName());
				result.addChild(subNode);
			}
			for(File subDir : listDirs(f, ".*")) {
				DocTreeNode subNode = getTreeDescription(subDir, filter);
				if(subNode.getChildrenCount("type", "file") > 0) {
					result.addChild(subNode);
				}
			}
		} else {
			result = null;
		}
		return result;
	}

	public static File createNewTemporaryFile(String fileName) throws IOException {
		String tmpfileName = TEMPORARY_FILES_DIRECTORY + "/" + TEMPORARY_FILE_PREFIX + (TEMPORARY_FILE_COUNT + 1) + "_" + fileName;
		TEMPORARY_FILE_COUNT++;
		File result = new File(tmpfileName);
		if(result.exists()) {
			return createNewTemporaryFile(fileName);
		} else {
			createFile(result);
			return result;
		}
	}

	public static void deleteTemporaryDir() {
		try {
			emptyDir(TEMPORARY_FILES_DIRECTORY);
			deleteDir(TEMPORARY_FILES_DIRECTORY);
		} catch(IOException e) {
			ErrorsAndWarnings.addException(e);
		}
	}

	public void copyDir(File srcDir, File destDir, boolean removeDstDirContentBeforeCopy) {
		try {
			if(removeDstDirContentBeforeCopy && destDir.exists()) {
				emptyDir(destDir);
			}
			destDir.mkdirs();
			FileUtils.copyDirectory(srcDir, destDir);
		} catch(IOException e) {
			this.testCase.addTestResult(false, e.getMessage());
		}

	}

	public File rename(File srcFile, String newSimpleName) throws IOException {
		File destFile = new File(srcFile.getParentFile(), newSimpleName);
		FileUtils.copyFile(srcFile, destFile);
		FileUtils.deleteQuietly(srcFile);
		return destFile;
	}
}
