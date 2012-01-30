package org.handwerkszeug.util;

import werkzeugkasten.common.util.ArrayUtil;

public class CompareUtil {

	public static int compare(long left, long right) {
		if (left < right) {
			return -1;
		} else if (left > right) {
			return 1;
		}
		return 0;
	}

	public static int compare(byte[] lefts, byte[] rights) {
		return ArrayUtil.compare(lefts, rights);
	}
}
