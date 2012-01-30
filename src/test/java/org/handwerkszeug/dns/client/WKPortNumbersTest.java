package org.handwerkszeug.dns.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.handwerkszeug.dns.record.WKSRecord;
import org.junit.Before;
import org.junit.Test;

public class WKPortNumbersTest {
	WKPortNumbers target;

	@Before
	public void setUp() throws Exception {
		this.target = new WKPortNumbers();
		this.target.load();
	}

	@Test
	public void testLoad() {
		assertEquals(WKPortNumbers.UNKNOWN_PORT, this.target.find(3000));
		assertEquals("smpnameres", this.target.find(901));
	}

	@Test
	public void testSetServices() throws Exception {
		WKSRecord rr = new WKSRecord();
		String[] data = { "ftp", "sql*net", "85", "echo", "TELNET" };
		this.target.setServices(rr, data);
		byte[] bitmap = rr.bitmap();
		assertNotNull(bitmap);
		StringBuilder stb = new StringBuilder();
		this.target.appendServices(rr, stb);
		String exp = "echo ftp telnet sql*net mit-ml-dev ";
		assertEquals(exp, stb.toString());
	}
}
