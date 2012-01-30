package org.handwerkszeug.dns.record;

import java.util.List;

import org.handwerkszeug.dns.Name;
import org.handwerkszeug.dns.NameCompressor;
import org.handwerkszeug.dns.RRType;
import org.handwerkszeug.dns.ResourceRecord;
import org.handwerkszeug.util.CompareUtil;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 * 3.3.9. MX RDATA format
 * 
 * <pre>
 *                                   1  1  1  1  1  1
 *     0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5
 *    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *    |                  PREFERENCE                   |
 *    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *    /                   EXCHANGE                    /
 *    /                                               /
 *    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * </pre>
 * 
 * @author taichi
 */
public class MXRecord extends AbstractRecord<MXRecord> {

	/**
	 * A 16 bit integer which specifies the preference given to this RR among
	 * others at the same owner. Lower values are preferred.
	 */
	protected int preference;

	/**
	 * RFC 974 the name of a host.
	 */
	protected Name exchange;

	public MXRecord() {
		super(RRType.MX);
	}

	public MXRecord(MXRecord from) {
		super(from);
		this.preference = from.preference();
		this.exchange = from.exchange();
	}

	@Override
	protected void parseRDATA(ChannelBuffer buffer) {
		this.preference = buffer.readUnsignedShort();
		this.exchange = new Name(buffer);

	}

	@Override
	protected void writeRDATA(ChannelBuffer buffer, NameCompressor compressor) {
		buffer.writeShort(this.preference);
		this.exchange.write(buffer, compressor);
	}

	@Override
	public void setRDATA(List<String> list) {
		if (2 == list.size()) {
			int pref = Integer.parseInt(list.get(0));
			if (-1 < pref && pref < 65536) {
				this.preference = pref;
			} else {
				// TODO error message.
				throw new IllegalArgumentException();
			}
			this.exchange = new Name(list.get(1));
		} else {
			// TODO error message
			throw new IllegalArgumentException();
		}
	}

	@Override
	protected ResourceRecord newInstance() {
		return new MXRecord(this);
	}

	public int preference() {
		return this.preference;
	}

	public Name exchange() {
		return this.exchange;
	}

	@Override
	public int compareTo(MXRecord o) {
		if (this == o) {
			return 0;
		}
		int result = super.compareTo(o);
		if (result == 0) {
			result = CompareUtil.compare(this.preference(), o.preference());
			if (result == 0) {
				result = this.exchange().compareTo(o.exchange());
			}
		}
		return result;
	}

	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();
		stb.append(super.toString());
		stb.append(' ');
		stb.append(this.preference());
		stb.append(' ');
		stb.append(this.exchange());
		return stb.toString();
	}
}
