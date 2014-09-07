/*Copyright (c) 2010-2011, Mathieu Bordas
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

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Keyboard {

	private TestToolSikuli sikuli = null;
	private HashMap<Character, int[]> keyMap = null;

	public Keyboard(TestToolSikuli skl) {
		sikuli = skl;
	}

	public Keyboard(File keyboardFile) throws IOException {
		// Loading key codes:
		InputStream ips = new FileInputStream(keyboardFile); 
		InputStreamReader ipsr = new InputStreamReader(ips);
		BufferedReader br = new BufferedReader(ipsr);
		String ligne;
		while ((ligne = br.readLine())!=null){
			String[] words = ligne.split(";");
			String characterName = words[0];
			String[] keyCodes = new String[words.length - 1];
			for(int i = 1; i < words.length; i++) {
				keyCodes[i] = words[i];
			}
			registerKey(characterName, keyCodes);
		}
		br.close();
	}

	public int[] getKeyCode(char c) {
		if(keyMap.containsKey(c)) {
			return keyMap.get(c);
		} else {
			return null;
		}
	}
	
	public void registerKey(String characterName, String[] keyCodes) {
		int[] codes = new int[keyCodes.length];
		int i = 0;
		for(String code : keyCodes) {
			codes[i] = getKeyCodeFromString(code);
			i++;
		}
		
		// Mapping for Characters
		if("UPPERCASE_A".equals(characterName)) {keyMap.put('A', codes);}
		if("UPPERCASE_B".equals(characterName)) {keyMap.put('B', codes);}
		if("UPPERCASE_C".equals(characterName)) {keyMap.put('C', codes);}
		if("UPPERCASE_D".equals(characterName)) {keyMap.put('D', codes);}
		if("UPPERCASE_E".equals(characterName)) {keyMap.put('E', codes);}
		if("UPPERCASE_F".equals(characterName)) {keyMap.put('F', codes);}
		if("UPPERCASE_G".equals(characterName)) {keyMap.put('G', codes);}
		if("UPPERCASE_H".equals(characterName)) {keyMap.put('H', codes);}
		if("UPPERCASE_I".equals(characterName)) {keyMap.put('I', codes);}
		if("UPPERCASE_J".equals(characterName)) {keyMap.put('J', codes);}
		if("UPPERCASE_K".equals(characterName)) {keyMap.put('K', codes);}
		if("UPPERCASE_L".equals(characterName)) {keyMap.put('L', codes);}
		if("UPPERCASE_M".equals(characterName)) {keyMap.put('M', codes);}
		if("UPPERCASE_N".equals(characterName)) {keyMap.put('N', codes);}
		if("UPPERCASE_O".equals(characterName)) {keyMap.put('O', codes);}
		if("UPPERCASE_P".equals(characterName)) {keyMap.put('P', codes);}
		if("UPPERCASE_Q".equals(characterName)) {keyMap.put('Q', codes);}
		if("UPPERCASE_R".equals(characterName)) {keyMap.put('R', codes);}
		if("UPPERCASE_S".equals(characterName)) {keyMap.put('S', codes);}
		if("UPPERCASE_T".equals(characterName)) {keyMap.put('T', codes);}
		if("UPPERCASE_U".equals(characterName)) {keyMap.put('U', codes);}
		if("UPPERCASE_V".equals(characterName)) {keyMap.put('V', codes);}
		if("UPPERCASE_W".equals(characterName)) {keyMap.put('W', codes);}
		if("UPPERCASE_X".equals(characterName)) {keyMap.put('X', codes);}
		if("UPPERCASE_Y".equals(characterName)) {keyMap.put('Y', codes);}
		if("UPPERCASE_Z".equals(characterName)) {keyMap.put('Z', codes);}
	}

	private int getKeyCodeFromString(String codeString) {
		int code = 0;
		// Mapping for KeyEvents
		if("VK_0".equals(codeString)) {code = KeyEvent.VK_0;}
		if("VK_1".equals(codeString)) {code = KeyEvent.VK_1;}
		if("VK_A".equals(codeString)) {code = KeyEvent.VK_A;}
		if("VK_B".equals(codeString)) {code = KeyEvent.VK_B;}
		return code;
	}
	
	public int[] getKeyCodes(char c) {
		return keyMap.get(c);
	}

	public void typeCharFRKeyboard(char c) {
		int[] keyCodes = getKeyCodeFRKeyboard(c);
		// pressing keys in normal order
		for(int i = 0; i < keyCodes.length; i++) {
			sikuli.getRobot().keyPress(keyCodes[i]);
		}
		// releasing keys in reverse order
		for(int i = 0; i < keyCodes.length; i++) {
			sikuli.getRobot().keyRelease(keyCodes[i]);
		}
	}

	public int[] getKeyCodeUSKeyboard(char c) {
		switch (c) {
		case 'a': return new int[]{KeyEvent.VK_A};
		case 'b': return new int[]{KeyEvent.VK_B};
		case 'c': return new int[]{KeyEvent.VK_C};
		case 'd': return new int[]{KeyEvent.VK_D};
		case 'e': return new int[]{KeyEvent.VK_E};
		case 'f': return new int[]{KeyEvent.VK_F};
		case 'g': return new int[]{KeyEvent.VK_G};
		case 'h': return new int[]{KeyEvent.VK_H};
		case 'i': return new int[]{KeyEvent.VK_I};
		case 'j': return new int[]{KeyEvent.VK_J};
		case 'k': return new int[]{KeyEvent.VK_K};
		case 'l': return new int[]{KeyEvent.VK_L};
		case 'm': return new int[]{KeyEvent.VK_M};
		case 'n': return new int[]{KeyEvent.VK_N};
		case 'o': return new int[]{KeyEvent.VK_O};
		case 'p': return new int[]{KeyEvent.VK_P};
		case 'q': return new int[]{KeyEvent.VK_Q};
		case 'r': return new int[]{KeyEvent.VK_R};
		case 's': return new int[]{KeyEvent.VK_S};
		case 't': return new int[]{KeyEvent.VK_T};
		case 'u': return new int[]{KeyEvent.VK_U};
		case 'v': return new int[]{KeyEvent.VK_V};
		case 'w': return new int[]{KeyEvent.VK_W};
		case 'x': return new int[]{KeyEvent.VK_X};
		case 'y': return new int[]{KeyEvent.VK_Y};
		case 'z': return new int[]{KeyEvent.VK_Z};
		case 'A': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_A};
		case 'B': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_B};
		case 'C': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_C};
		case 'D': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_D};
		case 'E': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_E};
		case 'F': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_F};
		case 'G': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_G};
		case 'H': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_H};
		case 'I': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_I};
		case 'J': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_J};
		case 'K': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_K};
		case 'L': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_L};
		case 'M': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_M};
		case 'N': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_N};
		case 'O': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_O};
		case 'P': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_P};
		case 'Q': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_Q};
		case 'R': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_R};
		case 'S': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_S};
		case 'T': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_T};
		case 'U': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_U};
		case 'V': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_V};
		case 'W': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_W};
		case 'X': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_X};
		case 'Y': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_Y};
		case 'Z': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_Z};
		case '`': return new int[]{KeyEvent.VK_BACK_QUOTE};
		case '0': return new int[]{KeyEvent.VK_0};
		case '1': return new int[]{KeyEvent.VK_1};
		case '2': return new int[]{KeyEvent.VK_2};
		case '3': return new int[]{KeyEvent.VK_3};
		case '4': return new int[]{KeyEvent.VK_4};
		case '5': return new int[]{KeyEvent.VK_5};
		case '6': return new int[]{KeyEvent.VK_6};
		case '7': return new int[]{KeyEvent.VK_7};
		case '8': return new int[]{KeyEvent.VK_8};
		case '9': return new int[]{KeyEvent.VK_9};
		case '-': return new int[]{KeyEvent.VK_MINUS};
		case '=': return new int[]{KeyEvent.VK_EQUALS};
		case '~': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_QUOTE};
		case '!': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_1};
		case '@': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_2};
		case '#': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_3};
		case '$': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_4};
		case '%': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_5};
		case '^': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_6};
		case '&': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_7};
		case '*': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_8};
		case '(': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_9};
		case ')': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_0};
		case '_': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_MINUS};
		case '+': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_EQUALS};
		case '\b': return new int[]{KeyEvent.VK_BACK_SPACE};
		case '\t': return new int[]{KeyEvent.VK_TAB};
		case '\r': return new int[]{KeyEvent.VK_ENTER};
		case '\n': return new int[]{KeyEvent.VK_ENTER};
		case '[': return new int[]{KeyEvent.VK_OPEN_BRACKET};
		case ']': return new int[]{KeyEvent.VK_CLOSE_BRACKET};
		case '\\': return new int[]{KeyEvent.VK_BACK_SLASH};
		case '{': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_OPEN_BRACKET};
		case '}': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_CLOSE_BRACKET};
		case '|': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_SLASH};
		case ';': return new int[]{KeyEvent.VK_SEMICOLON};
		case ':': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_SEMICOLON};
		case '\'': return new int[]{KeyEvent.VK_QUOTE};
		case '"': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_QUOTE};
		case ',': return new int[]{KeyEvent.VK_COMMA};
		case '<': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_COMMA};
		case '.': return new int[]{KeyEvent.VK_PERIOD};
		case '>': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_PERIOD};
		case '/': return new int[]{KeyEvent.VK_SLASH};
		case '?': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_SLASH};
		case ' ': return new int[]{KeyEvent.VK_SPACE};
		case '\u001b': return new int[]{KeyEvent.VK_ESCAPE};
		case '\ue000': return new int[]{KeyEvent.VK_UP};
		case '\ue001': return new int[]{KeyEvent.VK_RIGHT};
		case '\ue002': return new int[]{KeyEvent.VK_DOWN};
		case '\ue003': return new int[]{KeyEvent.VK_LEFT};
		case '\ue004': return new int[]{KeyEvent.VK_PAGE_UP};
		case '\ue005': return new int[]{KeyEvent.VK_PAGE_DOWN};
		case '\ue006': return new int[]{KeyEvent.VK_DELETE};
		case '\ue007': return new int[]{KeyEvent.VK_END};
		case '\ue008': return new int[]{KeyEvent.VK_HOME};
		case '\ue009': return new int[]{KeyEvent.VK_INSERT};
		case '\ue011': return new int[]{KeyEvent.VK_F1};
		case '\ue012': return new int[]{KeyEvent.VK_F2};
		case '\ue013': return new int[]{KeyEvent.VK_F3};
		case '\ue014': return new int[]{KeyEvent.VK_F4};
		case '\ue015': return new int[]{KeyEvent.VK_F5};
		case '\ue016': return new int[]{KeyEvent.VK_F6};
		case '\ue017': return new int[]{KeyEvent.VK_F7};
		case '\ue018': return new int[]{KeyEvent.VK_F8};
		case '\ue019': return new int[]{KeyEvent.VK_F9};
		case '\ue01A': return new int[]{KeyEvent.VK_F10};
		case '\ue01B': return new int[]{KeyEvent.VK_F11};
		case '\ue01C': return new int[]{KeyEvent.VK_F12};
		case '\ue01D': return new int[]{KeyEvent.VK_F13};
		case '\ue01E': return new int[]{KeyEvent.VK_F14};
		case '\ue01F': return new int[]{KeyEvent.VK_F15};
		case '\ue020': return new int[]{KeyEvent.VK_SHIFT};
		case '\ue021': return new int[]{KeyEvent.VK_CONTROL};
		case '\ue022': return new int[]{KeyEvent.VK_ALT};
		case '\ue023': return new int[]{KeyEvent.VK_META};
		case '\ue024': return new int[]{KeyEvent.VK_PRINTSCREEN};
		case '\ue025': return new int[]{KeyEvent.VK_SCROLL_LOCK};
		case '\ue026': return new int[]{KeyEvent.VK_PAUSE};
		case '\ue027': return new int[]{KeyEvent.VK_CAPS_LOCK};
		case '\ue030': return new int[]{KeyEvent.VK_NUMPAD0};
		case '\ue031': return new int[]{KeyEvent.VK_NUMPAD1};
		case '\ue032': return new int[]{KeyEvent.VK_NUMPAD2};
		case '\ue033': return new int[]{KeyEvent.VK_NUMPAD3};
		case '\ue034': return new int[]{KeyEvent.VK_NUMPAD4};
		case '\ue035': return new int[]{KeyEvent.VK_NUMPAD5};
		case '\ue036': return new int[]{KeyEvent.VK_NUMPAD6};
		case '\ue037': return new int[]{KeyEvent.VK_NUMPAD7};
		case '\ue038': return new int[]{KeyEvent.VK_NUMPAD8};
		case '\ue039': return new int[]{KeyEvent.VK_NUMPAD9};
		case '\ue03A': return new int[]{KeyEvent.VK_SEPARATOR};
		case '\ue03B': return new int[]{KeyEvent.VK_NUM_LOCK};
		case '\ue03C': return new int[]{KeyEvent.VK_ADD};
		case '\ue03D': return new int[]{KeyEvent.VK_MINUS};
		case '\ue03E': return new int[]{KeyEvent.VK_MULTIPLY};
		case '\ue03F': return new int[]{KeyEvent.VK_DIVIDE};
		}
		return null;
	}

	public static int[] getKeyCodeFRKeyboard(char c) {
		switch (c) {
		case 'a': return new int[]{KeyEvent.VK_A};
		case 'b': return new int[]{KeyEvent.VK_B};
		case 'c': return new int[]{KeyEvent.VK_C};
		case 'd': return new int[]{KeyEvent.VK_D};
		case 'e': return new int[]{KeyEvent.VK_E};
		case 'f': return new int[]{KeyEvent.VK_F};
		case 'g': return new int[]{KeyEvent.VK_G};
		case 'h': return new int[]{KeyEvent.VK_H};
		case 'i': return new int[]{KeyEvent.VK_I};
		case 'j': return new int[]{KeyEvent.VK_J};
		case 'k': return new int[]{KeyEvent.VK_K};
		case 'l': return new int[]{KeyEvent.VK_L};
		case 'm': return new int[]{KeyEvent.VK_M};
		case 'n': return new int[]{KeyEvent.VK_N};
		case 'o': return new int[]{KeyEvent.VK_O};
		case 'p': return new int[]{KeyEvent.VK_P};
		case 'q': return new int[]{KeyEvent.VK_Q};
		case 'r': return new int[]{KeyEvent.VK_R};
		case 's': return new int[]{KeyEvent.VK_S};
		case 't': return new int[]{KeyEvent.VK_T};
		case 'u': return new int[]{KeyEvent.VK_U};
		case 'v': return new int[]{KeyEvent.VK_V};
		case 'w': return new int[]{KeyEvent.VK_W};
		case 'x': return new int[]{KeyEvent.VK_X};
		case 'y': return new int[]{KeyEvent.VK_Y};
		case 'z': return new int[]{KeyEvent.VK_Z};
		case 'A': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_A};
		case 'B': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_B};
		case 'C': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_C};
		case 'D': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_D};
		case 'E': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_E};
		case 'F': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_F};
		case 'G': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_G};
		case 'H': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_H};
		case 'I': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_I};
		case 'J': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_J};
		case 'K': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_K};
		case 'L': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_L};
		case 'M': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_M};
		case 'N': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_N};
		case 'O': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_O};
		case 'P': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_P};
		case 'Q': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_Q};
		case 'R': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_R};
		case 'S': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_S};
		case 'T': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_T};
		case 'U': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_U};
		case 'V': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_V};
		case 'W': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_W};
		case 'X': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_X};
		case 'Y': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_Y};
		case 'Z': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_Z};
		case '`': return new int[]{KeyEvent.VK_BACK_QUOTE};
		case '0': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_0};
		case '1': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_1};
		case '2': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_2};
		case '3': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_3};
		case '4': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_4};
		case '5': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_5};
		case '6': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_6};
		case '7': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_7};
		case '8': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_8};
		case '9': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_9};
		case '-': return new int[]{KeyEvent.VK_MINUS};
		case '=': return new int[]{KeyEvent.VK_EQUALS};
		case '~': return new int[]{KeyEvent.VK_CONTROL, KeyEvent.VK_ALT, KeyEvent.VK_2};
		case '!': return new int[]{KeyEvent.VK_EXCLAMATION_MARK};
		case '@': return new int[]{KeyEvent.VK_CONTROL, KeyEvent.VK_ALT, KeyEvent.VK_0};
		case '#': return new int[]{KeyEvent.VK_CONTROL, KeyEvent.VK_ALT, KeyEvent.VK_3};
		case '$': return new int[]{KeyEvent.VK_DOLLAR};
		//case '%': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_5}; Unknown keyCode: 0x0
		case '^': return new int[]{KeyEvent.VK_DEAD_CIRCUMFLEX};
		case '&': return new int[]{KeyEvent.VK_1};
		case '*': return new int[]{KeyEvent.VK_ASTERISK}; // ALSO: VK_MULTIPLY
		case '(': return new int[]{KeyEvent.VK_5};
		case ')': return new int[]{KeyEvent.VK_RIGHT_PARENTHESIS};
		case '_': return new int[]{KeyEvent.VK_8};
		case '+': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_EQUALS};
		case '\b': return new int[]{KeyEvent.VK_BACK_SPACE};
		case '\t': return new int[]{KeyEvent.VK_TAB};
		case '\r': return new int[]{KeyEvent.VK_ENTER};
		case '\n': return new int[]{KeyEvent.VK_ENTER};
		case '[': return new int[]{KeyEvent.VK_OPEN_BRACKET};
		case ']': return new int[]{KeyEvent.VK_CLOSE_BRACKET};
		case '\\': return new int[]{KeyEvent.VK_BACK_SLASH};
		case '{': return new int[]{KeyEvent.VK_CONTROL, KeyEvent.VK_ALT, KeyEvent.VK_4};
		case '}': return new int[]{KeyEvent.VK_CONTROL, KeyEvent.VK_ALT, KeyEvent.VK_EQUALS};
		case '|': return new int[]{KeyEvent.VK_CONTROL, KeyEvent.VK_ALT, KeyEvent.VK_6};
		case ';': return new int[]{KeyEvent.VK_SEMICOLON};
		case ':': return new int[]{KeyEvent.VK_COLON};
		case '\'': return new int[]{KeyEvent.VK_QUOTE};
		case '"': return new int[]{KeyEvent.VK_3};
		case ',': return new int[]{KeyEvent.VK_COMMA};
		case '<': return new int[]{KeyEvent.VK_LESS};
		case '.': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_SEMICOLON};
		case '>': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_LESS};
		case '/': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_COLON};
		case '?': return new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_COMMA};
		case ' ': return new int[]{KeyEvent.VK_SPACE};
		case '\u001b': return new int[]{KeyEvent.VK_ESCAPE};
		case '\ue000': return new int[]{KeyEvent.VK_UP};
		case '\ue001': return new int[]{KeyEvent.VK_RIGHT};
		case '\ue002': return new int[]{KeyEvent.VK_DOWN};
		case '\ue003': return new int[]{KeyEvent.VK_LEFT};
		case '\ue004': return new int[]{KeyEvent.VK_PAGE_UP};
		case '\ue005': return new int[]{KeyEvent.VK_PAGE_DOWN};
		case '\ue006': return new int[]{KeyEvent.VK_DELETE};
		case '\ue007': return new int[]{KeyEvent.VK_END};
		case '\ue008': return new int[]{KeyEvent.VK_HOME};
		case '\ue009': return new int[]{KeyEvent.VK_INSERT};
		case '\ue011': return new int[]{KeyEvent.VK_F1};
		case '\ue012': return new int[]{KeyEvent.VK_F2};
		case '\ue013': return new int[]{KeyEvent.VK_F3};
		case '\ue014': return new int[]{KeyEvent.VK_F4};
		case '\ue015': return new int[]{KeyEvent.VK_F5};
		case '\ue016': return new int[]{KeyEvent.VK_F6};
		case '\ue017': return new int[]{KeyEvent.VK_F7};
		case '\ue018': return new int[]{KeyEvent.VK_F8};
		case '\ue019': return new int[]{KeyEvent.VK_F9};
		case '\ue01A': return new int[]{KeyEvent.VK_F10};
		case '\ue01B': return new int[]{KeyEvent.VK_F11};
		case '\ue01C': return new int[]{KeyEvent.VK_F12};
		case '\ue01D': return new int[]{KeyEvent.VK_F13};
		case '\ue01E': return new int[]{KeyEvent.VK_F14};
		case '\ue01F': return new int[]{KeyEvent.VK_F15};
		case '\ue020': return new int[]{KeyEvent.VK_SHIFT};
		case '\ue021': return new int[]{KeyEvent.VK_CONTROL};
		case '\ue022': return new int[]{KeyEvent.VK_ALT};
		case '\ue023': return new int[]{KeyEvent.VK_META};
		case '\ue024': return new int[]{KeyEvent.VK_PRINTSCREEN};
		case '\ue025': return new int[]{KeyEvent.VK_SCROLL_LOCK};
		case '\ue026': return new int[]{KeyEvent.VK_PAUSE};
		case '\ue027': return new int[]{KeyEvent.VK_CAPS_LOCK};
		case '\ue030': return new int[]{KeyEvent.VK_NUMPAD0};
		case '\ue031': return new int[]{KeyEvent.VK_NUMPAD1};
		case '\ue032': return new int[]{KeyEvent.VK_NUMPAD2};
		case '\ue033': return new int[]{KeyEvent.VK_NUMPAD3};
		case '\ue034': return new int[]{KeyEvent.VK_NUMPAD4};
		case '\ue035': return new int[]{KeyEvent.VK_NUMPAD5};
		case '\ue036': return new int[]{KeyEvent.VK_NUMPAD6};
		case '\ue037': return new int[]{KeyEvent.VK_NUMPAD7};
		case '\ue038': return new int[]{KeyEvent.VK_NUMPAD8};
		case '\ue039': return new int[]{KeyEvent.VK_NUMPAD9};
		case '\ue03A': return new int[]{KeyEvent.VK_SEPARATOR};
		case '\ue03B': return new int[]{KeyEvent.VK_NUM_LOCK};
		case '\ue03C': return new int[]{KeyEvent.VK_ADD};
		case '\ue03D': return new int[]{KeyEvent.VK_MINUS};
		case '\ue03E': return new int[]{KeyEvent.VK_MULTIPLY};
		case '\ue03F': return new int[]{KeyEvent.VK_DIVIDE};
		}
		return null;
	}

}