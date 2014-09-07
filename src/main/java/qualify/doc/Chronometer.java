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

package qualify.doc;

import java.io.File;
import java.util.LinkedList;
import java.util.Map;

import org.jdom.Element;
import org.joda.time.DateTime;
import org.joda.time.Period;

import qualify.tools.TestToolDates;

public class Chronometer implements DomElementAble {
	
	public static String timeFormat = "yyyyMMdd hh:mm:ss.SSS";
	
	private DateTime
	startTime = null,
	lastMarkTime = null,
	stopTime = null;
	private LinkedList<DateTime> times = null;
	private LinkedList<String> marks = null;
	
	public Chronometer() {
		startTime = new DateTime();
		marks = new LinkedList<String>();
		times = new LinkedList<DateTime>();
	}
	
	public void start() {
		startTime = new DateTime();
	}
	
	public void mark(String label) {
		lastMarkTime = new DateTime();
		marks.add(label);
		times.add(new DateTime(lastMarkTime));
	}
	
	public void stop() {
		stopTime = new DateTime();
	}

	public Element toDomElement() {
		DocList result = new DocList();
		result.addItem(new DocString("Chronometer"));
		Table table = new Table(new String[]{"mark", "time", "duration from start", "duration from last"});
		String[] entry = new String[4];
		entry[0] = "start";
		entry[1] = TestToolDates.toString(startTime, timeFormat);
		entry[2] = "";
		entry[3] = "";
		table.addEntry(entry);
		DateTime lastTime = startTime;
		for(int i = 0; i < times.size(); i++) {
			DateTime time = times.get(i);
			String mark = marks.get(i);
			entry[0] = mark;
			entry[1] = TestToolDates.toString(time, timeFormat);
			entry[2] = TestToolDates.toString(new Period(startTime, time));
			entry[3] = TestToolDates.toString(new Period(lastTime, time));
			table.addEntry(entry);
			lastTime = time;
		}
		if(stopTime != null) {
			entry[0] = "stop";
			entry[1] = TestToolDates.toString(stopTime, timeFormat);
			entry[2] = TestToolDates.toString(new Period(startTime, stopTime));
			entry[3] = TestToolDates.toString(new Period(lastTime, stopTime));
			table.addEntry(entry);
		}
		result.addItem(table);
		return result.toDomElement();
	}

	public String toStringForConsole() {
		return toDomElement().toString();
	}

	public Map<File, String> getAttachedFiles() {
		return null;
	}

}
