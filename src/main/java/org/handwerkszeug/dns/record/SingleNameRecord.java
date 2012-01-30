package org.handwerkszeug.dns.record;

import java.util.List;

import org.handwerkszeug.dns.Name;
import org.handwerkszeug.dns.NameCompressor;
import org.handwerkszeug.dns.RRType;
import org.handwerkszeug.dns.ResourceRecord;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 * <ul>
 * <li>3.3.1. CNAME RDATA format</li>
 * <li>3.3.11. NS RDATA format</li>
 * <li>3.3.12. PTR RDATA format</li>
 * </ul>
 * 
 * 
 * @author taichi
 */
public class SingleNameRecord extends AbstractRecord<SingleNameRecord> {

	protected Name oneName;

	public SingleNameRecord(RRType type) {
		super(type);
	}

	public SingleNameRecord(RRType type, Name oneName) {
		super(type);
		this.oneName = oneName;
	}

	public SingleNameRecord(SingleNameRecord from) {
		super(from);
		this.oneName = from.oneName();
	}

	@Override
	protected void parseRDATA(ChannelBuffer buffer) {
		this.oneName = new Name(buffer);
	}

	@Override
	protected void writeRDATA(ChannelBuffer buffer, NameCompressor compressor) {
		this.oneName.write(buffer, compressor);
	}

	@Override
	public void setRDATA(List<String> list) {
		if (0 < list.size()) {
			this.oneName = new Name(list.get(0));
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	protected ResourceRecord newInstance() {
		return new SingleNameRecord(this);
	}

	public Name oneName() {
		return this.oneName;
	}

	@Override
	public int compareTo(SingleNameRecord o) {
		if ((this != o) && (super.compareTo(o) == 0)) {
			return this.oneName().compareTo(o.oneName());
		}
		return 0;
	}

	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();
		stb.append(super.toString());
		stb.append(' ');
		stb.append(this.oneName());
		return stb.toString();
	}
}
