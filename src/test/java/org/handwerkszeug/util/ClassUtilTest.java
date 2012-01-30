package org.handwerkszeug.util;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

public class ClassUtilTest {

	@Test
	public void testToPackagePath() {
		String actual = ClassUtil.toPackagePath(Map.class);
		assertEquals("java/util", actual);
	}

}
