package werkzeugkasten.common.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	public static final String[] EMPTY_ARRAY = {};

	public static boolean isEmpty(Object s) {
		return (s == null) || (s.toString().length() < 1);
	}

	public static String toString(Object s) {
		return toString(s, "");
	}

	public static String toString(Object s, String r) {
		return isEmpty(s) ? r : s.toString();
	}

	public static String replace(String txt, String from, String to) {
		String result = "";
		if ((isEmpty(txt) == false) && (isEmpty(from) == false)) {
			Pattern p = Pattern.compile(from);
			StringBuilder stb = new StringBuilder(txt);
			Matcher m = p.matcher(stb);
			int index = 0;
			while ((index < stb.length()) && m.find(index)) {
				index = m.start() + to.length();
				stb.replace(m.start(), m.end(), to);
				m = p.matcher(stb);
			}
			result = stb.toString();
		}
		return result;
	}

	public static String replace(String template, Map<String, String> context) {
		String result = "";
		if (StringUtil.isEmpty(template) == false) {
			Pattern p = Pattern.compile("\\$\\{[^\\$\\{\\}]*\\}");
			StringBuffer stb = new StringBuffer(template);
			Matcher m = p.matcher(stb);
			int index = 0;
			while ((index < stb.length()) && m.find(index)) {
				String s = m.group();
				String v = toString(context.get(s.substring(2, s.length() - 1)));
				index = m.start() + v.length();
				stb.replace(m.start(), m.end(), v);
				m = p.matcher(stb);
			}
			result = stb.toString();
		}
		return result;
	}

	public static String toCamelCase(String s) {
		if (isEmpty(s) == false) {
			StringBuilder stb = new StringBuilder(s);
			stb.replace(0, 1,
					String.valueOf(Character.toUpperCase(s.charAt(0))));
			return stb.toString();
		}
		return s;
	}

	public static boolean isJavaIdentifier(String s) {
		if (StringUtil.isEmpty(s)) {
			return false;
		}
		char[] chars = s.toCharArray();
		if (Character.isJavaIdentifierStart(chars[0]) == false) {
			return false;
		}
		for (int i = 1; i < chars.length; i++) {
			if (Character.isJavaIdentifierPart(chars[i]) == false) {
				return false;
			}
		}
		return true;
	}

	public static void padRight(StringBuilder stb, char c, int upTo) {
		for (int i = stb.length(); i < upTo; i++) {
			stb.append(c);
		}
	}
}
