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

package qualify;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

import org.apache.log4j.*;

import qualify.server.HTTPServer;
import qualify.tools.CommandLineTool;

public class Qualify {

	static Logger logger = Logger.getLogger(Qualify.class);

	// Options
	public static final String OPTION_OPTIONS_FILE = "option_file",
	OPTION_TEST_CASE_LIST_NAME= "list",
	OPTION_SUT_NAME = "sut_name",
	OPTION_SUT_VERSION = "sut_version",
	OPTION_SUT_DATE = "sut_date",
	OPTION_SOURCE_DIRS = "source_dirs",
	OPTION_SCRIPT_DIRS = "script_dirs",
	OPTION_SCRIPT_DEPENDENCIES_DIRS = "script_dependencies_dirs",
	OPTION_SCRIPT_PREPROCESSORS_DIRS = "script_preprocessors_dirs",
	OPTION_RUN_ON_ERROR = "run_on_error",
	OPTION_OBJECT_REPOSITORIES_DIRS = "object_repositories_dirs",
	OPTION_SRD_FILE = "srd_file",
	OPTION_RELEASE_NOTE_FILE_NAME = "release_note",
	OPTION_CONTINUOUS_RELEASE_NOTE = "continuous_release_note",
	OPTION_REFERENCE_RELEASE_NOTE = "reference_release_note",
	OPTION_TEST_CASE_TABLE_FILE_NAME = "test_case_table",
	OPTION_RESULT_DIR = "result_dir",
	OPTION_SINGLE_TEST_TO_RUN = "test",
	OPTION_KEYWORD_TO_TEST = "keyword",
	OPTION_REQUIREMENT_TO_TEST = "requirement",
	OPTION_RUN_LAST_FAILED = "run_last_failed",
	OPTION_LOG_LEVEL = "log_level",
	OPTION_LOG_OUTPUT_FILE_NAME = "log_file",
	OPTION_HTTP_SERVER = "http_server",
	OPTION_HTTP_SERVER_WEBAPPS_DIR = "http_server_webapps_dir",
	OPTION_HTTP_SERVER_ROOT_DIR = "http_server_root_dir";

	private static HashMap<String, String> options = null;

	public static void main(String[] args) {
		Qualify.initLogs();

		if(args.length == 0) {
			args = new String[]{"-" + OPTION_OPTIONS_FILE, "options.xml"};
		}

		CommandLineTool cmd = new CommandLineTool(args);
		Qualify.setOptions(cmd.getOptions());

		if(cmd.isOptionInCommandLine(Qualify.OPTION_LOG_OUTPUT_FILE_NAME)) {
			String logFileName = cmd.getOptionValue(Qualify.OPTION_LOG_OUTPUT_FILE_NAME);
			logger.info("Setting log file: " + logFileName);
			try {
				Qualify.setLogFile(logFileName);
			} catch (IOException e) {
				e.printStackTrace();
				ErrorsAndWarnings.addError("Cannot set log file '" + new File(logFileName).getAbsolutePath() + "'");
			}
		}

		if(cmd.isOptionInCommandLine(Qualify.OPTION_LOG_LEVEL)) {
			String logLevel = cmd.getOptionValue(Qualify.OPTION_LOG_LEVEL);
			logger.info("Setting log level to: " + logLevel);
			Qualify.setLogLevel(logLevel);
		}

		HTTPServer server = null;
		if(cmd.isOptionInCommandLine(Qualify.OPTION_HTTP_SERVER)) {
			int serverPort = Integer.valueOf(cmd.getOptionValue(Qualify.OPTION_HTTP_SERVER));
			logger.info("Starting HTTP Server using port: " + serverPort);
			try {
				new HTTPServer(serverPort);
			} catch (Exception e) {
				e.printStackTrace();
				ErrorsAndWarnings.addError("Launching HTTP Server failed.");
			}
		}

		if(server != null) {
			try {
				server.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public static HashMap<String, String> getOptions() {
		return options;
	}

	public static void setOptions(HashMap<String, String> opts) {
		options = opts;
	}

	public static boolean isOption(String optionName) {
		if(options != null) {
			return options.containsKey(optionName);
		} else {
			return false;
		}
	}

	public static String getOptionValue(String optionName) {
		return options.get(optionName);
	}

	public static String[] getOptionValues(String optionName) {
		return options.get(optionName).split(CommandLineTool.OPTION_VALUE_SEPARATOR);
	}

	public static String getOptionParameter(String optionName) {
		if(isOption(optionName)) {
			return getOptionValues(optionName)[0];
		} else {
			return "";
		}
	}

	// Logger initialization
	public static final String LOGGER_PATTERN = "%d %-3p - %m%n";
	public static String LOGGER_OUTPUT_FILE_NAME = "traces.txt";
	private static boolean initialized = false;
	public static synchronized void initLogs() {
		if(initialized == false) {
			boolean consoleAppenderExists = false;
			@SuppressWarnings("unchecked")
			Enumeration<Appender> appenders = (Enumeration<Appender>)Logger.getRootLogger().getAllAppenders();
			while(appenders.hasMoreElements()) {
				Appender appender = appenders.nextElement();
				if(appender.getClass() == ConsoleAppender.class) {
					consoleAppenderExists = true;
				}
			}
			Logger.getRootLogger().setLevel(Level.INFO);
			if(! consoleAppenderExists) {
				Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout(LOGGER_PATTERN)));
			}
			initialized = true;
		}
	}

	public static void setLogFile(String logFileName) throws IOException {
		Logger.getRootLogger().addAppender(new FileAppender(new PatternLayout(LOGGER_PATTERN), logFileName));
	}

	public static void setLogLevel(String logLevel) {
		if(logLevel.toUpperCase().equals("ALL")) {
			Logger.getRootLogger().setLevel(Level.ALL);
		} else if(logLevel.toUpperCase().equals("DEBUG")) {
			Logger.getRootLogger().setLevel(Level.DEBUG);
		} else if(logLevel.toUpperCase().equals("ERROR")) {
			Logger.getRootLogger().setLevel(Level.ERROR);
		} else if(logLevel.toUpperCase().equals("FATAL")) {
			Logger.getRootLogger().setLevel(Level.FATAL);
		} else if(logLevel.toUpperCase().equals("INFO")) {
			Logger.getRootLogger().setLevel(Level.INFO);
		} else if(logLevel.toUpperCase().equals("OFF")) {
			Logger.getRootLogger().setLevel(Level.OFF);
		} else if(logLevel.toUpperCase().equals("TRACE")) {
			Logger.getRootLogger().setLevel(Level.TRACE);
		} else if(logLevel.toUpperCase().equals("WARN")) {
			Logger.getRootLogger().setLevel(Level.WARN);
		} else {
			ErrorsAndWarnings.addError("Unknown log level: '" + logLevel + "'");
		}
	}

	public static CommandLineTool loadOptions(String[] args) {
		if(args.length == 0) {
			args = new String[]{"-option_file", "options.xml"};
		}

		CommandLineTool cmd = new CommandLineTool(args);

		Qualify.setOptions(cmd.getOptions());
		return cmd;
	}
}
