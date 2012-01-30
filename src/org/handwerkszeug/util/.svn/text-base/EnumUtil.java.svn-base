package org.handwerkszeug.util;

public class EnumUtil {

	public static <E extends Enum<E> & VariableEnum> E find(E[] values,
			int value) {
		E result = find(values, value, null);
		if (result == null) {
			throw new IllegalArgumentException("value=" + value);
		}
		return result;
	}

	public static <E extends Enum<E> & VariableEnum> E find(E[] values,
			int value, E defaultValue) {
		for (E e : values) {
			if (e.value() == value) {
				return e;
			}
		}
		return defaultValue;
	}

	public static <E extends Enum<E>> E find(E[] values, String value,
			E defaultValue) {
		if (value == null || value.isEmpty()) {
			return defaultValue;
		}
		String key = value.toUpperCase();
		for (E e : values) {
			if (e.name().equals(key)) {
				return e;
			}
		}
		return defaultValue;
	}

}
