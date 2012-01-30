package org.handwerkszeug.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.handwerkszeug.util.AddressUtil.FromHostname;
import org.handwerkszeug.util.AddressUtil.FromV4Address;
import org.handwerkszeug.util.AddressUtil.FromV6Address;
import org.junit.Before;
import org.junit.Test;

public class AddressUtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testFromV4() {
		FromV4Address convert = new FromV4Address();
		String[] v4address = { "192.168.0.1", "127.0.0.1", "255.255.0.1" };
		for (String s : v4address) {
			int port = 53;
			InetSocketAddress sa = convert.to(s, port);
			assertNotNull(sa);
			assertEquals(s, sa.getAddress().getHostAddress());
			assertEquals(port, sa.getPort());
		}
		String[] v4addrWithPort = { "192.168.0.1:1", "127.0.0.1:80",
				"255.255.0.1:8080" };
		int[] ports = { 1, 80, 8080 };
		for (int i = 0, length = ports.length; i < length; i++) {
			String s = v4addrWithPort[i];
			InetSocketAddress sa = convert.to(s, 22);
			assertNotNull(sa);
			assertEquals(s.substring(0, s.lastIndexOf(':')), sa.getAddress()
					.getHostAddress());
			assertEquals(ports[i], sa.getPort());
		}

		String[] v4addrWithIlleagalPort = { "192.168.0.1:65536",
				"127.0.0.1:80801" };
		for (String s : v4addrWithIlleagalPort) {
			assertNull(convert.to(s, 21));
		}
	}

	@Test
	public void testFromHostName() {
		FromHostname convert = new FromHostname();
		int port = 22;
		InetSocketAddress sa = convert.to("localhost", port);
		assertNotNull(sa);
		assertEquals("127.0.0.1", sa.getAddress().getHostAddress());
		assertEquals(port, sa.getPort());

		sa = convert.to("localhost:8080", 31);
		assertNotNull(sa);
		assertEquals("127.0.0.1", sa.getAddress().getHostAddress());
		assertEquals(8080, sa.getPort());

		assertNull(convert.to("localhost:65536", 21));
	}

	@Test
	public void testFromV6() {
		FromV6Address convert = new FromV6Address();
		String[] v6address = { "2001:db8:0:0:8:800:200c:417a",
				"ff02:0:0:0:0:1:ffff:ffff", "2001:db8:aaaa:bbbb:cccc:dddd::1",
				"2001:db8::1", "2001::1", "::1", "::", "2001:db8::",
				"2001::db8:aaaa:bbbb:cccc:dddd:eeee", "::1:2:3:4" };
		for (String s : v6address) {
			int port = 53;
			InetSocketAddress sa = convert.to(s, port);
			assertNotNull(s, sa);
			assertEquals(port, sa.getPort());
		}
		String[] invalidCompressedAddr = {
				"2001:db8:aaaa:bbbb:cccc:dddd::1:03:ff:2b", "2001:db8::aa::bb",
				"2001:db8::256.0.0.1" };
		for (String s : invalidCompressedAddr) {
			InetSocketAddress sa = convert.to(s, 1);
			assertNull(s, sa);
		}
		String[] v6addrWithPort = { "[::]:81", "[2001:db8::1]:82",
				"2001:db8::1:1#22", "2001:db8:1:2::1.80", "2001:db8::1:2:3#83" };
		String[] addrExp = { "0:0:0:0:0:0:0:0", "2001:db8:0:0:0:0:0:1",
				"2001:db8:0:0:0:0:1:1", "2001:db8:1:2:0:0:0:1",
				"2001:db8:0:0:0:1:2:3" };
		int[] ports = { 81, 82, 22, 80, 83 };
		for (int i = 0, length = addrExp.length; i < length; i++) {
			String s = v6addrWithPort[i];
			String ae = addrExp[i];
			int p = ports[i];
			InetSocketAddress sa = AddressUtil.convertTo(s, 30);
			assertNotNull(s, sa);
			assertEquals(ae, sa.getAddress().getHostAddress());
			assertEquals(p, sa.getPort());
		}
	}

	@Test
	public void testGetByAddress() {
		String expected = "192.168.10.1";
		// 11000000 10101000 00001010 00000001
		long address = 3232238081L;
		InetAddress actual = AddressUtil.getByAddress(address);
		assertNotNull(actual);
		assertEquals(expected, actual.getHostAddress());

		// 11 11000000 10101000 00001010 00000001
		address = 16117139969L;
		actual = AddressUtil.getByAddress(address);

		assertEquals(expected, actual.getHostAddress());
	}

	@Test
	public void testToLong() throws Exception {
		// 11000000 10101000 00001010 00000001
		testToLong(3232238081L);
		// 00110011 00110011 00111100 11001100
		testToLong(858995916L);

	}

	protected void testToLong(long expected) {
		long actual = AddressUtil.toLong((Inet4Address) AddressUtil
				.getByAddress(expected));
		assertEquals(expected, actual);
	}

}
