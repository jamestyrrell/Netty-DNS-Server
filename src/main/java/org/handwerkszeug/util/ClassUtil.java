package org.handwerkszeug.util;

public class ClassUtil {

	public static String toPackagePath(Class<?> clazz) {
		String result = clazz.getName();
		int index = result.lastIndexOf('.');
		if (index < 1) {
			return "";
		}
		result = result.substring(0, index);
		result = result.replace('.', '/');
		return result;
	}
}
