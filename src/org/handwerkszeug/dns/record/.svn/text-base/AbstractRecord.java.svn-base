package org.handwerkszeug.dns.record;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;

import org.handwerkszeug.dns.DNSClass;
import org.handwerkszeug.dns.Name;
import org.handwerkszeug.dns.NameCompressor;
import org.handwerkszeug.dns.RRType;
import org.handwerkszeug.dns.ResourceRecord;
import org.handwerkszeug.dns.nls.Messages;
import org.jboss.netty.buffer.ChannelBuffer;

import werkzeugkasten.common.util.StringUtil;

public abstract class AbstractRecord<T extends ResourceRecord> implements
		ResourceRecord, Comparable<T> {

	public static byte[] EMPTY_BYTE_ARRAY = new byte[0];

	public static int MAX_STRING_LENGTH = 255;

	protected RRType type;

	protected Name name;

	protected DNSClass dnsClass = DNSClass.IN;

	protected long ttl;

	protected int rdlength;

	public AbstractRecord(RRType type) {
		this.type = type;
	}

	public AbstractRecord(AbstractRecord<T> from) {
		this.type = from.type();
		this.name(from.name());
		this.dnsClass(from.dnsClass());
		this.ttl(from.ttl());
		this.rdlength(from.rdlength());
	}

	@Override
	public RRType type() {
		return this.type;
	}

	@Override
	public Name name() {
		return this.name;
	}

	@Override
	public void name(Name name) {
		this.name = name;
	}

	@Override
	public DNSClass dnsClass() {
		return this.dnsClass;
	}

	@Override
	public void dnsClass(DNSClass dnsClass) {
		this.dnsClass = dnsClass;
	}

	@Override
	public long ttl() {
		return this.ttl;
	}

	@Override
	public void ttl(long ttl) {
		this.ttl = ttl;
	}

	@Override
	public int rdlength() {
		return this.rdlength;
	}

	@Override
	public void rdlength(int value) {
		this.rdlength = value;
	}

	public static ResourceRecord parseSection(ChannelBuffer buffer) {
		Name n = new Name(buffer);
		RRType t = RRType.valueOf(buffer.readUnsignedShort());
		DNSClass dc = DNSClass.valueOf(buffer.readUnsignedShort());
		ResourceRecord result = t.newRecord();
		result.name(n);
		result.dnsClass(dc);
		return result;
	}

	@Override
	public void parse(ChannelBuffer buffer) {
		this.ttl(buffer.readUnsignedInt());
		this.rdlength(buffer.readUnsignedShort());

		parseRDATA(buffer);
	}

	public static void writeSection(ChannelBuffer buffer,
			NameCompressor compressor, ResourceRecord rr) {
		rr.name().write(buffer, compressor);
		buffer.writeShort(rr.type().value());
		buffer.writeShort(rr.dnsClass().value());
	}

	/**
	 * 4.1.3. Resource record format<br/>
	 * <b>RDATA</b> a variable length string of octets that describes the
	 * resource. The format of this information varies according to the TYPE and
	 * CLASS of the resource record.
	 * 
	 * @param buffer
	 */
	protected abstract void parseRDATA(ChannelBuffer buffer);

	@Override
	public void write(ChannelBuffer buffer, NameCompressor compressor) {
		buffer.writeInt((int) this.ttl());
		int rdlengthIndex = buffer.writerIndex();
		buffer.writeShort(0); // at first, write zero.

		writeRDATA(buffer, compressor);

		int rdlength = (buffer.writerIndex() - rdlengthIndex - 2) & 0xFFFF;
		buffer.setShort(rdlengthIndex, rdlength);
	}

	protected abstract void writeRDATA(ChannelBuffer buffer,
			NameCompressor compressor);

	/**
	 * 3.3. Standard RRs
	 * <p>
	 * &lt;character-string&gt; is a single length octet followed by that number
	 * of characters. &lt;character-string&gt; is treated as binary information,
	 * and can be up to 256 characters in length (including the length octet).
	 * </p>
	 * 
	 * @param buffer
	 * @return
	 */
	protected byte[] readString(ChannelBuffer buffer) {
		short length = buffer.readUnsignedByte();
		if (MAX_STRING_LENGTH < length) {
			throw new IllegalStateException(String.format(
					Messages.StringMustBe255orLess, length));
		}
		byte[] newone = new byte[length];
		buffer.readBytes(newone);
		return newone;
	}

	protected void writeString(ChannelBuffer buffer, byte[] ary) {
		int length = ary.length;
		buffer.writeByte(length);
		buffer.writeBytes(ary);
	}

	/**
	 * 5.1. Format
	 * 
	 * @param ary
	 * @return
	 */
	protected StringBuilder toString(byte[] ary) {
		DecimalFormat fmt = new DecimalFormat("###");
		StringBuilder result = new StringBuilder();
		for (byte b : ary) {
			int i = b & 0xFF;
			if ((i < 0x20) || (0x7E < i)) { // control code
				result.append('\\');
				result.append(fmt.format(i));
			} else if ((i == '"') || (i == '\\')) {
				result.append('\\');
				result.append((char) i);
			} else {
				result.append((char) i);
			}
		}
		return result;
	}

	protected StringBuilder toQuoteString(byte[] ary) {
		StringBuilder result = new StringBuilder();
		result.append('"');
		result.append(toString(ary));
		result.append('"');
		return result;
	}

	protected byte[] toArray(String string) {
		if (StringUtil.isEmpty(string)) {
			return EMPTY_BYTE_ARRAY;
		}
		byte[] bytes = string.getBytes();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		for (int i = 0, length = bytes.length; i < length; i++) {
			byte b = bytes[i];
			if (b == '\\') {
				byte next = bytes[++i];
				if (Character.isDigit(next)) {
					int value = ((next - '0') * 100)
							+ ((bytes[++i] - '0') * 10) + bytes[++i] - '0';
					out.write(value);
				} else {
					out.write(next);
				}
			} else {
				out.write(b);
			}
		}

		return out.toByteArray();
	}

	protected byte[] toArrayFromQuoted(String string) {
		if (StringUtil.isEmpty(string)) {
			return EMPTY_BYTE_ARRAY;
		}
		if (string.length() < 3) {
			return EMPTY_BYTE_ARRAY;
		}
		return toArray(string.substring(1, string.length() - 1));
	}

	@Override
	public ResourceRecord toQnameRecord(Name qname) {
		ResourceRecord newone = newInstance();
		newone.name(qname);
		return newone;
	}

	protected abstract ResourceRecord newInstance();

	@Override
	public int compareTo(T o) {
		if (o == null) {
			return 1;
		}
		int result = this.type().compareTo(o.type());
		if (result != 0) {
			return result;
		}
		result = this.name().compareTo(o.name());
		if (result != 0) {
			return result;
		}
		return this.dnsClass().compareTo(o.dnsClass());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.dnsClass == null) ? 0 : this.dnsClass.hashCode());
		result = prime * result
				+ ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result + this.rdlength;
		result = prime * result + (int) (this.ttl ^ (this.ttl >>> 32));
		result = prime * result
				+ ((this.type == null) ? 0 : this.type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof AbstractRecord) {
			@SuppressWarnings("unchecked")
			AbstractRecord<T> other = (AbstractRecord<T>) obj;
			return equals(other);
		}
		return false;
	}

	public boolean equals(AbstractRecord<T> other) {
		if (this.dnsClass != other.dnsClass) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.rdlength != other.rdlength) {
			return false;
		}
		if (this.ttl != other.ttl) {
			return false;
		}
		if (this.type != other.type) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();
		stb.append(this.name().toString());
		StringUtil.padRight(stb, ' ', 23);
		stb.append(' ');
		stb.append(this.ttl());
		StringUtil.padRight(stb, ' ', 31);
		stb.append(' ');
		stb.append(this.dnsClass().name());
		stb.append(' ');
		stb.append(this.type().name());
		StringUtil.padRight(stb, ' ', 39);
		return stb.toString();
	}
}
