package org.handwerkszeug.dns.client;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class WKProtocolsTest {

	protected WKProtocols target;

	@Before
	public void setUp() {
		this.target = new WKProtocols();
	}

	@Test
	public void testLoad() {
		this.target.load();

		assertEquals("GGP", this.target.find((short) 3));
		assertEquals(WKProtocols.UNKNOWN_PROTOCOL,
				this.target.find((short) 1000));

	}
}
