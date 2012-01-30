package org.handwerkszeug.dns;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Before;
import org.junit.Test;

public class HeaderTest {

	protected Header target;

	@Before
	public void setUp() {
		this.target = new Header();
	}

	@Test
	public void testEmit() {
		this.target.flags(0x20F3);
		this.target.opcode(OpCode.IQUERY);
		this.target.rcode(RCode.FormErr);
		this.target.qdcount(1000);
		this.target.ancount(2000);
		this.target.nscount(3000);
		this.target.arcount(4000);

		ChannelBuffer buf = ChannelBuffers.buffer(100);
		this.target.write(buf);

		Header newone = new Header(buf);
		assertEquals(this.target, newone);

	}

	@Test
	public void testId() {
		try {
			new Header(-1);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
		try {
			new Header(65535 + 1);
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testQr() {
		this.target.flags(32768); // 1000 0000 0000 0000
		assertTrue(this.target.qr());
		this.target.flags(16384); // 0100 0000 0000 0000
		assertFalse(this.target.qr());

	}

	@Test
	public void testOpcode() {
		OpCode oc = OpCode.IQUERY;
		this.target.opcode(oc);
		assertEquals(oc, this.target.opcode());
	}

	@Test
	public void testAa() {
		this.target.flags(1024); // 0000 0100 0000 0000
		assertTrue(this.target.aa());
		this.target.aa(true);
		assertTrue(this.target.aa());
		this.target.aa(false);
		assertFalse(this.target.aa());
	}

	@Test
	public void testTc() {
		this.target.flags(512); // 0000 0010 0000 0000
		assertTrue(this.target.tc());
		this.target.tc(false);
		assertFalse(this.target.tc());
		this.target.tc(true);
		assertTrue(this.target.tc());
	}

	@Test
	public void testRd() {
		this.target.flags(256); // 0000 0001 0000 0000
		assertTrue(this.target.rd());
		this.target.rd(false);
		assertFalse(this.target.rd());
		this.target.rd(true);
		assertTrue(this.target.rd());
	}

	@Test
	public void testRa() {
		this.target.flags(128); // 0000 0000 1000 0000
		assertTrue(this.target.ra());
		this.target.ra(true);
		assertTrue(this.target.ra());
		this.target.ra(false);
		assertFalse(this.target.ra());
	}

	@Test
	public void testZ() {
		this.target.flags(0); // 0000 0000 0000 0000
		assertEquals(0, this.target.z());
	}

	@Test
	public void testRcode() {
		this.target.flags(4); // 0000 0000 0000 0100
		RCode rc = RCode.NotImp;
		assertEquals(rc, this.target.rcode());

		rc = RCode.FormErr;
		this.target.rcode(rc);
		assertEquals(rc, this.target.rcode());
	}

	@Test
	public void testToString() {
		String exp = ";; ->>HEADER<<- opcode: IQUERY, rcode: FormErr, id: 38246\n"
				+ ";; flags: qr aa tc rd ra ; QUERY: 1000, ANSWER: 2000, AUTHORITY: 3000, ADDITIONAL: 4000";
		this.target.id(38246);
		this.target.flags(0xFFFF);
		this.target.opcode(OpCode.IQUERY);
		this.target.rcode(RCode.FormErr);
		this.target.qdcount(1000);
		this.target.ancount(2000);
		this.target.nscount(3000);
		this.target.arcount(4000);
		assertEquals(exp, this.target.toString());
	}

	@Test
	public void testWrite() {
		this.target.id(38246);
		this.target.flags(0xFFFF);
		this.target.opcode(OpCode.IQUERY);
		this.target.rcode(RCode.FormErr);
		this.target.qdcount(1000);
		this.target.ancount(2000);
		this.target.nscount(3000);
		this.target.arcount(4000);
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		this.target.write(buffer);
		ChannelBuffer newone = buffer.slice(0, buffer.writerIndex());
		Header h = new Header(newone);
		assertEquals(this.target, h);

	}
}
