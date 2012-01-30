package werkzeugkasten.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CollectionUtil {

	public static List<?> toList(Object maybeList) {
		if (maybeList instanceof List<?>) {
			return (List<?>) maybeList;
		} else if (maybeList instanceof Collection<?>) {
			return new ArrayList<Object>((Collection<?>) maybeList);
		} else if (maybeList != null) {
			Class<?> clazz = maybeList.getClass();
			if (clazz.isArray()) {
				Object[] ary = (Object[]) maybeList;
				return Arrays.asList(ary);
			} else {
				List<Object> list = new ArrayList<Object>(1);
				list.add(maybeList);
				return list;
			}
		}
		return Collections.emptyList();
	}
}
