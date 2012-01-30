package org.handwerkszeug.dns.record;

import java.util.Arrays;
import java.util.List;

import org.handwerkszeug.dns.NameCompressor;
import org.handwerkszeug.dns.RRType;
import org.handwerkszeug.dns.ResourceRecord;
import org.handwerkszeug.util.CompareUtil;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 * 3.3.2. HINFO RDATA format
 * 
 * <p>
 * Standard values for CPU and OS can be found in [RFC-1010].
 * </p>
 * 
 * @author taichi
 * 
 */
public class HINFORecord extends AbstractRecord<HINFORecord> {

	/**
	 * A <character-string> which specifies the CPU type.
	 */
	protected byte[] cpu;

	/**
	 * A <character-string> which specifies the operating system type.
	 */
	protected byte[] os;

	public HINFORecord() {
		super(RRType.HINFO);
	}

	public HINFORecord(HINFORecord from) {
		super(from);
		byte[] c = from.cpu;
		if (c != null) {
			this.cpu = Arrays.copyOf(c, c.length);
		}
		byte[] o = from.os;
		if (o != null) {
			this.os = Arrays.copyOf(o, o.length);
		}

	}

	@Override
	protected void parseRDATA(ChannelBuffer buffer) {
		this.cpu = readString(buffer);
		this.os = readString(buffer);
	}

	@Override
	protected void writeRDATA(ChannelBuffer buffer, NameCompressor compressor) {
		writeString(buffer, this.cpu);
		writeString(buffer, this.os);
	}

	@Override
	public void setRDATA(List<String> list) {
		if (list.size() == 2) {
			this.cpu(list.get(0));
			this.os(list.get(1));
		} else {
			// TODO error message.
			throw new IllegalArgumentException();
		}
	}

	@Override
	protected ResourceRecord newInstance() {
		return new HINFORecord(this);
	}

	public String cpu() {
		return new String(this.cpu);
	}

	public void cpu(String cpu) {
		this.cpu = cpu.getBytes(); // TODO encoding ?
	}

	public String os() {
		return new String(this.os);
	}

	public void os(String os) {
		this.os = os.getBytes(); // TODO encoding ?
	}

	@Override
	public int compareTo(HINFORecord o) {
		if (this == o) {
			return 0;
		}
		int result = super.compareTo(o);
		if (result == 0) {
			result = CompareUtil.compare(this.cpu, o.cpu);
			if (result == 0) {
				result = CompareUtil.compare(this.os, o.os);
			}
		}
		return result;
	}

	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();
		stb.append(super.toString());
		stb.append(' ');
		stb.append(this.cpu());
		stb.append(' ');
		stb.append(this.os());
		return stb.toString();
	}
}
