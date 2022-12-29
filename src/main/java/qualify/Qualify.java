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

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import qualify.server.HTTPServer;
import qualify.tools.CommandLineTool;

import java.io.IOException;
import java.util.HashMap;

public class Qualify {

	static Logger logger = Logger.getLogger(Qualify.class);

	// Options
	public static final String OPTION_OPTIONS_FILE = "option_file";
	public static final String OPTION_TEST_CASE_LIST_NAME = "list";
	public static final String OPTION_SUT_NAME = "sut_name";
	public static final String OPTION_SUT_VERSION = "sut_version";
	public static final String OPTION_SUT_DATE = "sut_date";
	public static final String OPTION_SOURCE_DIRS = "source_dirs";
	public static final String OPTION_SCRIPT_DIRS = "script_dirs";
	public static final String OPTION_SCRIPT_DEPENDENCIES_DIRS = "script_dependencies_dirs";
	public static final String OPTION_SCRIPT_PREPROCESSORS_DIRS = "script_preprocessors_dirs";
	public static final String OPTION_RUN_ON_ERROR = "run_on_error";
	public static final String OPTION_OBJECT_REPOSITORIES_DIRS = "object_repositories_dirs";
	public static final String OPTION_SRD_FILE = "srd_file";
	public static final String OPTION_RELEASE_NOTE_FILE_NAME = "release_note";
	public static final String OPTION_CONTINUOUS_RELEASE_NOTE = "continuous_release_note";
	public static final String OPTION_REFERENCE_RELEASE_NOTE = "reference_release_note";
	public static final String OPTION_TEST_CASE_TABLE_FILE_NAME = "test_case_table";
	public static final String OPTION_RESULT_DIR = "result_dir";
	public static final String OPTION_TEST_TO_RUN = "test";
	public static final String OPTION_KEYWORD_TO_TEST = "keyword";
	public static final String OPTION_REQUIREMENT_TO_TEST = "requirement";
	public static final String OPTION_NO_SANITY = "no_sanity";
	public static final String OPTION_RUN_LAST_FAILED = "run_last_failed";
	public static final String OPTION_HTTP_SERVER = "http_server";
	public static final String OPTION_HTTP_SERVER_WEBAPPS_DIR = "http_server_webapps_dir";
	public static final String OPTION_HTTP_SERVER_ROOT_DIR = "http_server_root_dir";
	public static final String OPTION_TEST_CASE_MAX_ATTEMPT = "max_attempt_per_test_case";

	private static HashMap<String, String> options = null;

	public static void main(String[] args) {
		if(args.length == 0) {
			args = new String[] { OPTION_OPTIONS_FILE + "=options.xml" };
		}

		CommandLineTool cmd = new CommandLineTool(args);
		Qualify.setOptions(cmd.getOptions());

		if(cmd.isOptionInCommandLine(Qualify.OPTION_HTTP_SERVER)) {
			int serverPort = Integer.valueOf(cmd.getOptionValue(Qualify.OPTION_HTTP_SERVER));
			logger.info("Starting HTTP Server using port: " + serverPort);
			try {
				new HTTPServer(serverPort);
			} catch(Exception e) {
				e.printStackTrace();
				ErrorsAndWarnings.addError("Launching HTTP Server failed.");
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

	public static boolean isOptionSet(String optionName) {
		String value = System.getProperty(CommandLineTool.OPTION_SYSTEM_PROPERTIES_PREFIX + optionName);
		if(value == null) {
			value = options.get(optionName);
		}

		if(value == null || value.equals("")) {
			return false;
		} else {
			return Boolean.valueOf(value);
		}
	}

	public static String getOptionValue(String optionName) {
		String value = System.getProperty(CommandLineTool.OPTION_SYSTEM_PROPERTIES_PREFIX + optionName);
		if(value == null) {
			value = options.get(optionName);
		}
		return value;
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
	public static final String LOGGER_PATTERN = "%d{ISO8601} [%.20t] %-5p %c %x - %m%n";
	public static String LOGGER_OUTPUT_FILE_NAME = "traces.txt";
	private static boolean initialized = false;

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
			args = new String[] { "option_file=options.xml" };
		}

		CommandLineTool cmd = new CommandLineTool(args);

		Qualify.setOptions(cmd.getOptions());
		return cmd;
	}
}
