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

package qualify.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import qualify.Qualify;
import qualify.TestCase;
import qualify.TestException;
import qualify.doc.Field;
import qualify.testRunner.TProcess;
import qualify.tools.TestToolFile;
import qualify.tools.TestToolProcess;

public class ProjectManager {

	static Logger logger = Logger.getLogger(ProjectManager.class);

	TestCase tc = null;
	TestToolProcess process = null;

	ArrayList<File> classpath = null;
	
	public File compileLogFile = null,
	runLogFile = null;

	public ProjectManager(TestCase tc) {
		this.tc = tc;
		process = new TestToolProcess(this.tc);
		classpath = new ArrayList<File>();
	}
	
	public File getLogFile() {
		return runLogFile;
	}

	public void addToClasspath(File fileToAdd) {
		if(fileToAdd.isDirectory()) {
			classpath.add(fileToAdd);
			for(File f : fileToAdd.listFiles()) {
				addToClasspath(f);
			}
		} else {
			if(fileToAdd.getName().toUpperCase().endsWith(".JAR")) {
				classpath.add(fileToAdd);
			}
		}
	}

	public void addDirToClasspath(File dirToAdd) {
		classpath.add(dirToAdd);
	}

	public String getClassPath() {
		String result = "";
		for(File f : classpath) {
			result += f.getAbsolutePath() + ";";
		}
		return result;
	}

	public int compile(String mainSourceFilePath) {
		File mainSourceFile = new File(mainSourceFilePath);

		String sourcePath = mainSourceFile.getParentFile().getAbsolutePath();
		String binPath = mainSourceFile.getParentFile().getParentFile().getAbsolutePath() + "/bin";

		try {
			TestToolFile.emptyDir(new File(binPath));
			TestToolFile.createDir(new File(binPath));
		} catch (IOException e) {
			e.printStackTrace();
			throw new TestException(e.getLocalizedMessage());
		}

		ArrayList<String> args = new ArrayList<String>();
		args.add("javac");
		if(getClassPath().length() > 0) {
			args.add("-cp");
			args.add(getClassPath());
		}
		args.add(mainSourceFile.getAbsolutePath());
		args.add("-sourcepath");
		args.add(sourcePath);
		args.add("-d");
		args.add(binPath);

		String[] compileCommand = args.toArray(new String[args.size()]);

		String commandLine = "";
		for(String arg : compileCommand) {
			commandLine += " " + arg;
		}

		tc.addComment(new Field("command line", commandLine), ProjectManager.class);

		compileLogFile = new File("compile_log.txt");
		int exitCode = process.executeMSWindowsCommandLine(compileCommand, new File("."), compileLogFile, 60.0);
		tc.attachFile(compileLogFile);

		return exitCode;
	}

	public TProcess runJava(String mainClassFileName, String[] additionalArgs, File workDir, File logFile) {

		ArrayList<String> args = new ArrayList<String>();
		args.add("java");
		if(getClassPath().length() > 0) {
			args.add("-cp");
			args.add(getClassPath());
		}
		args.add(mainClassFileName);

		if(additionalArgs != null) {
			for(String additionalArg : additionalArgs) {
				args.add(additionalArg);
			}
		}

		String[] runCommand = args.toArray(new String[args.size()]);
		String commandLine = "";
		for(String arg : runCommand) {
			commandLine += " " + arg;
		}

		tc.addComment(new Field("command line", commandLine), ProjectManager.class);

		return process.executeMSWindowsCommandLineInBackground(runCommand, workDir, logFile, 60.0);
	}

	public int runBat(String projectName) {
		return runBat(projectName, "run.bat");
	}
	
	public int runBat(String projectName, String batToRun) {
		runLogFile = new File("test_files/" + projectName + "/" + batToRun.replaceAll("\\.", "_") + ".log");
		int result = process.executeMSWindowsCommandLine(new String[]{batToRun}, new File("test_files/" + projectName), runLogFile, 60.0); 
		tc.attachFile(runLogFile);
		return result;
	}

	public int compileAndRunQualifyProject(String testFolder) {
		try {
			TestToolFile.deleteDir("test_files/" + testFolder + "/bin");
			TestToolFile.createDir("test_files/" + testFolder + "/bin");
		} catch (IOException e) {
			e.printStackTrace();
		}
		runBat(testFolder, "compile");
		return runBat(testFolder, "run.bat");
	}


}
