package org.handwerkszeug.dns.conf;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ResolveConfTest {

	@Test
	public void testParse() {
		ResolvConf target = new ResolvConf();
		List<String> list = new ArrayList<String>();

		target.parse("domain example.com", list);
		target.parse("search search.example.com", list);
		target.parse("NAMEserver\t127.0.0.1 ", list);

		assertEquals(1, list.size());
		assertEquals("127.0.0.1", list.get(0));
	}

}
