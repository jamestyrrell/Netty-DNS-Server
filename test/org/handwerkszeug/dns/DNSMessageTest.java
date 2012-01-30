package org.handwerkszeug.dns;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.handwerkszeug.dns.record.ARecord;
import org.handwerkszeug.dns.record.MXRecord;
import org.handwerkszeug.dns.record.SingleNameRecord;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Before;
import org.junit.Test;

public class DNSMessageTest {

	// ;; ->>HEADER<<- opcode: QUERY, status: NOERROR, id: 64158
	// ;; flags: qr rd ra ; qd: 1 an: 4 au: 4 ad: 4
	// ;; QUESTIONS:
	// ;; google.com., type = MX, class = IN
	//
	// ;; ANSWERS:
	// google.com. 805 IN MX 400 google.com.s9b2.psmtp.com.
	// google.com. 805 IN MX 100 google.com.s9a1.psmtp.com.
	// google.com. 805 IN MX 200 google.com.s9a2.psmtp.com.
	// google.com. 805 IN MX 300 google.com.s9b1.psmtp.com.
	//
	// ;; AUTHORITY RECORDS:
	// google.com. 336761 IN NS ns4.google.com.
	// google.com. 336761 IN NS ns3.google.com.
	// google.com. 336761 IN NS ns2.google.com.
	// google.com. 336761 IN NS ns1.google.com.
	//
	// ;; ADDITIONAL RECORDS:
	// ns1.google.com. 303485 IN A 216.239.32.10
	// ns2.google.com. 303485 IN A 216.239.34.10
	// ns3.google.com. 303485 IN A 216.239.36.10
	// ns4.google.com. 303485 IN A 216.239.38.10
	//
	// ;; Message size: 298 bytes

	static byte[] data = { -6, -98, -127, -128, 0, 1, 0, 4, 0, 4, 0, 4, 6, 103,
			111, 111, 103, 108, 101, 3, 99, 111, 109, 0, 0, 15, 0, 1, -64, 12,
			0, 15, 0, 1, 0, 0, 3, 37, 0, 26, 1, -112, 6, 103, 111, 111, 103,
			108, 101, 3, 99, 111, 109, 4, 115, 57, 98, 50, 5, 112, 115, 109,
			116, 112, -64, 19, -64, 12, 0, 15, 0, 1, 0, 0, 3, 37, 0, 20, 0,
			100, 6, 103, 111, 111, 103, 108, 101, 3, 99, 111, 109, 4, 115, 57,
			97, 49, -64, 58, -64, 12, 0, 15, 0, 1, 0, 0, 3, 37, 0, 20, 0, -56,
			6, 103, 111, 111, 103, 108, 101, 3, 99, 111, 109, 4, 115, 57, 97,
			50, -64, 58, -64, 12, 0, 15, 0, 1, 0, 0, 3, 37, 0, 20, 1, 44, 6,
			103, 111, 111, 103, 108, 101, 3, 99, 111, 109, 4, 115, 57, 98, 49,
			-64, 58, -64, 12, 0, 2, 0, 1, 0, 5, 35, 121, 0, 6, 3, 110, 115, 52,
			-64, 12, -64, 12, 0, 2, 0, 1, 0, 5, 35, 121, 0, 6, 3, 110, 115, 51,
			-64, 12, -64, 12, 0, 2, 0, 1, 0, 5, 35, 121, 0, 6, 3, 110, 115, 50,
			-64, 12, -64, 12, 0, 2, 0, 1, 0, 5, 35, 121, 0, 6, 3, 110, 115, 49,
			-64, 12, -64, -28, 0, 1, 0, 1, 0, 4, -95, 125, 0, 4, -40, -17, 32,
			10, -64, -46, 0, 1, 0, 1, 0, 4, -95, 125, 0, 4, -40, -17, 34, 10,
			-64, -64, 0, 1, 0, 1, 0, 4, -95, 125, 0, 4, -40, -17, 36, 10, -64,
			-82, 0, 1, 0, 1, 0, 4, -95, 125, 0, 4, -40, -17, 38, 10 };

	ChannelBuffer buffer;

	@Before
	public void setUp() throws Exception {
		buffer = ChannelBuffers.wrappedBuffer(data);
	}

	@Test
	public void testParseChannelBuffer() {
		DNSMessage msg = new DNSMessage(buffer);

		assertEquals(298, msg.messageSize());

		List<ResourceRecord> q = msg.question();
		ResourceRecord google = q.get(0);
		assertEquals(RRType.MX, google.type());
		assertEquals(DNSClass.IN, google.dnsClass());
		assertEquals("google.com.", google.name().toString());

		assertEquals(4, msg.answer().size());

		// google.com. 805 IN MX 200 google.com.s9a2.psmtp.com.
		ResourceRecord ans3 = msg.answer().get(2);
		assertEquals(805, ans3.ttl());
		assertEquals(RRType.MX, ans3.type());
		MXRecord ans3mx = (MXRecord) ans3;
		assertEquals(200, ans3mx.preference());
		assertEquals("google.com.s9a2.psmtp.com.", ans3mx.exchange().toString());

		// google.com. 336761 IN NS ns3.google.com.
		ResourceRecord auth = msg.authority().get(1);
		assertEquals(RRType.NS, auth.type());
		SingleNameRecord ns = (SingleNameRecord) auth;
		assertEquals("ns3.google.com.", ns.oneName().toString());

		// ns4.google.com. 303485 IN A 216.239.38.10
		ResourceRecord add = msg.additional().get(3);
		assertEquals(RRType.A, add.type());
		assertEquals(303485L, add.ttl());
		ARecord a = (ARecord) add;
		assertEquals("216.239.38.10", a.address().getHostAddress());
	}

	@Test
	public void writeTest() {
		DNSMessage msg = new DNSMessage(buffer);
		ChannelBuffer newone = ChannelBuffers.dynamicBuffer();
		msg.write(newone);
		byte[] actual = new byte[newone.writerIndex()];
		newone.getBytes(0, actual);
		assertArrayEquals(data, actual);
	}

}
