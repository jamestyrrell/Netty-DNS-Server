package org.handwerkszeug.dns;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;

public class RRTypeTest {

	@Before
	public void setUp() throws Exception {
	}

	// @Test
	public void testName() throws Exception {
		// printing for grammar file.
		Set<RRType> set = new HashSet<RRType>();
		set.add(RRType.NULL);
		set.add(RRType.ANY);
		set.add(RRType.UNKNOWN);
		for (RRType rr : RRType.values()) {
			if (set.contains(rr)) {
				System.out.print("// ");
			}
			System.out.printf("\t|\t%s%n", rr.name());
		}
		for (RRType rr : RRType.values()) {
			String name = rr.name();
			StringBuilder stb = new StringBuilder();
			char[] ary = name.toCharArray();
			for (int i = 0, l = ary.length; i < l; i++) {
				stb.append("(");
				char c = ary[i];
				stb.append("'");
				stb.append(Character.toLowerCase(c));
				stb.append("'");
				stb.append("|");
				stb.append("'");
				stb.append(Character.toUpperCase(c));
				stb.append("'");
				stb.append(")");
			}
			if (set.contains(rr)) {
				System.out.print("// ");
			}
			System.out.printf("%s\t:\t%s;%n", name, stb.toString());
		}
	}
}
