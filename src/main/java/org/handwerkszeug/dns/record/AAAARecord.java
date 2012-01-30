package org.handwerkszeug.dns.record;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import org.handwerkszeug.dns.NameCompressor;
import org.handwerkszeug.dns.RRType;
import org.handwerkszeug.dns.ResourceRecord;
import org.handwerkszeug.util.AddressUtil;
import org.handwerkszeug.util.CompareUtil;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 * <a href="http://datatracker.ietf.org/doc/rfc3596/">RFC3596</a>
 * 
 * @author taichi
 * 
 */
public class AAAARecord extends AbstractRecord<AAAARecord> {

	/**
	 * A 128 bit IPv6 address is encoded in the data portion of an AAAA resource
	 * record in network byte order (high-order byte first).
	 */
	protected byte[] address;

	public AAAARecord() {
		super(RRType.AAAA);
	}

	public AAAARecord(AAAARecord from) {
		super(from);
		byte[] ary = from.address;
		if (ary != null) {
			this.address = Arrays.copyOf(ary, ary.length);
		}
	}

	@Override
	protected void parseRDATA(ChannelBuffer buffer) {
		byte[] newone = new byte[16];
		buffer.readBytes(newone);
		this.address = newone;
	}

	@Override
	protected void writeRDATA(ChannelBuffer buffer, NameCompressor compressor) {
		buffer.writeBytes(this.address);
	}

	@Override
	protected ResourceRecord newInstance() {
		return new AAAARecord(this);
	}

	@Override
	public void setRDATA(List<String> list) {
		if (0 < list.size()) {
			String s = list.get(0);
			if (AddressUtil.v6Address.matcher(s).matches()) {
				InetAddress addr = AddressUtil.getByName(s);
				this.address = addr.getAddress();
			}
		} else {
			// TODO error message.
			throw new IllegalArgumentException();
		}
	}

	public InetAddress address() {
		try {
			return InetAddress.getByAddress(this.address);
		} catch (UnknownHostException e) {
			return null;
		}
	}

	public void address(Inet6Address v6address) {
		this.address = v6address.getAddress();
	}

	@Override
	public int compareTo(AAAARecord o) {
		if ((this != o) && (super.compareTo(o) == 0)) {
			return CompareUtil.compare(this.address, o.address);
		}
		return 0;
	}

	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();
		stb.append(super.toString());
		stb.append(' ');
		InetAddress ia = address();
		if (ia == null) {
			stb.append("null");
		} else {
			stb.append(ia.getHostAddress());
		}
		return stb.toString();
	}
}
