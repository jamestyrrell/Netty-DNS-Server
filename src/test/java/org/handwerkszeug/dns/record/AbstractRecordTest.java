package org.handwerkszeug.dns.record;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.net.InetAddress;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.handwerkszeug.dns.Name;
import org.handwerkszeug.dns.NameCompressor;
import org.handwerkszeug.dns.RRType;
import org.handwerkszeug.dns.ResourceRecord;
import org.jboss.netty.buffer.ChannelBuffer;
import org.junit.Before;
import org.junit.Test;

public class AbstractRecordTest {

	class R extends AbstractRecord<ARecord> {
		public R() {
			super(RRType.A);
		}

		@Override
		protected void parseRDATA(ChannelBuffer buffer) {

		}

		@Override
		protected void writeRDATA(ChannelBuffer buffer,
				NameCompressor compressor) {

		}

		@Override
		protected ResourceRecord newInstance() {
			return null;
		}

		@Override
		public void setRDATA(List<String> list) {
		}
	}

	AbstractRecord<ARecord> target;

	@Before
	public void setUp() {
		this.target = new R();
	}

	@Test
	public void testToArray() {
		String begin = "\"\\\\\\\"ab\\127\"";
		byte[] actual = this.target.toArrayFromQuoted(begin);
		byte[] exp = new byte[] { '\\', '"', 'a', 'b', 127 };
		assertArrayEquals(exp, actual);

		String act = this.target.toQuoteString(actual).toString();
		assertEquals(begin, act);

	}

	protected ARecord a(String name, String addr) throws Exception {
		ARecord result = new ARecord();
		result.name(new Name(name));
		result.address(InetAddress.getByName(addr));
		return result;
	}

	@Test
	public void testRecordEq() throws Exception {
		ARecord first = a("example.co.jp.", "192.168.0.1");
		ARecord second = a("example.co.jp.", "192.168.100.1");
		Set<ARecord> set = new ConcurrentSkipListSet<ARecord>();
		set.add(first);
		set.add(second);

		assertEquals(2, set.size());
	}
}
