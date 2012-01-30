package org.handwerkszeug.dns.record;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.handwerkszeug.dns.NameCompressor;
import org.handwerkszeug.dns.RRType;
import org.handwerkszeug.dns.ResourceRecord;
import org.handwerkszeug.util.CompareUtil;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 * 3.3.14. TXT RDATA format
 * 
 * @author taichi
 * 
 */
public class TXTRecord extends AbstractRecord<TXTRecord> {

	protected List<byte[]> strings;

	public TXTRecord() {
		super(RRType.TXT);
	}

	public TXTRecord(TXTRecord from) {
		super(from);
		if (from.strings != null) {
			List<byte[]> newone = new ArrayList<byte[]>(from.strings.size());
			for (byte[] b : from.strings) {
				newone.add(Arrays.copyOf(b, b.length));
			}
		}
	}

	@Override
	protected void parseRDATA(ChannelBuffer buffer) {
		this.strings = new ArrayList<byte[]>();
		ChannelBuffer part = buffer.readSlice(rdlength());
		while (part.readable()) {
			this.strings.add(readString(part));
		}
	}

	@Override
	protected void writeRDATA(ChannelBuffer buffer, NameCompressor compressor) {
		for (byte[] ary : this.strings) {
			writeString(buffer, ary);
		}
	}

	@Override
	public void setRDATA(List<String> list) {
		for (String s : list) {
			this.strings.add(s.getBytes());
		}
	}

	@Override
	protected ResourceRecord newInstance() {
		return new TXTRecord(this);
	}

	public String txt() {
		return this.toString();
	}

	@Override
	public int compareTo(TXTRecord o) {
		if (this == o) {
			return 0;
		}
		int result = super.compareTo(o);
		if (result != 0) {
			return result;
		}
		int mySize = this.strings.size();
		int yrSize = o.strings.size();
		int min = Math.min(mySize, yrSize);
		for (int i = 0; i < min; i++) {
			result = CompareUtil.compare(this.strings.get(i), o.strings.get(i));
			if (result != 0) {
				return result;
			}
		}
		return mySize - yrSize;
	}

	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();
		for (Iterator<byte[]> i = this.strings.iterator(); i.hasNext();) {
			stb.append(toQuoteString(i.next()));
			if (i.hasNext()) {
				stb.append(' ');
			}
		}
		return stb.toString();
	}
}
