package org.handwerkszeug.dns;

import static org.handwerkszeug.util.Validation.notNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.handwerkszeug.dns.nls.Messages;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class Name implements Comparable<Name> {

	public static final Name NULL_NAME;
	public static final Name WILDCARD;

	static final byte[] NULL_ARRAY = new byte[0];
	static final byte[] WILDCARD_ARRAY = new byte[] { '*' };

	static {
		NULL_NAME = create(NULL_ARRAY);
		WILDCARD = create(WILDCARD_ARRAY);
	}

	static Name create(byte[] array) {
		List<byte[]> l = new ArrayList<byte[]>(1);
		l.add(array);
		return new Name(Collections.unmodifiableList(l));
	}

	/**
	 * 4.1.4. Message compression
	 */
	public static final int MASK_POINTER = 0xC0; // 1100 0000

	/**
	 * 2.3.4. Size limits
	 */
	public static final int MAX_LABEL_SIZE = 63; // 0011 1111

	/**
	 * 2.3.4. Size limits
	 */
	public static final int MAX_NAME_SIZE = 255;

	protected final List<byte[]> name;

	public Name(ChannelBuffer buffer) {
		this.name = this.parse(buffer);
	}

	public Name(String name) {
		this.name = this.parse(name);
	}

	protected Name(List<byte[]> rawdata) {
		this.name = rawdata;
	}

	protected List<byte[]> parse(ChannelBuffer buffer) {
		List<byte[]> list = new ArrayList<byte[]>();
		boolean jumped = false;

		int namesize = 0;
		for (int length = buffer.readUnsignedByte(); -1 < length; length = buffer
				.readUnsignedByte()) {
			if (length == 0) {
				list.add(NULL_ARRAY);
				break;
			} else if ((length & MASK_POINTER) != 0) {
				int p = ((length ^ MASK_POINTER) << 8)
						+ buffer.readUnsignedByte();
				if (jumped == false) {
					buffer.markReaderIndex();
					jumped = true;
				}
				buffer.readerIndex(p);
			} else if (length <= MAX_LABEL_SIZE) {
				namesize += length;
				if (MAX_NAME_SIZE < namesize) {
					throw new IllegalArgumentException(String.format(
							Messages.NamesMustBe255orLess, namesize));
				}
				byte[] ary = new byte[length];
				buffer.readBytes(ary);
				list.add(ary);
			} else {
				throw new IllegalStateException(String.format(
						Messages.InvalidCompressionMask, length));
			}
		}

		if (jumped) {
			buffer.resetReaderIndex();
		}

		return Collections.unmodifiableList(list);
	}

	/**
	 * 5.1. Format
	 * 
	 * <pre>
	 * \X              where X is any character other than a digit (0-9), is
	 *                 used to quote that character so that its special meaning
	 *                 does not apply.  For example, "\." can be used to place
	 *                 a dot character in a label.
	 * 
	 * \DDD            where each D is a digit is the octet corresponding to
	 *                 the decimal number described by DDD.  The resulting
	 *                 octet is assumed to be text and is not checked for
	 *                 special meaning.
	 * </pre>
	 * 
	 * @param namedata
	 * @return
	 */
	protected List<byte[]> parse(String namedata) {
		if (".".equals(namedata)) {
			return NULL_NAME.name;
		}
		// TODO IDN support from RFC3490 RFC3491 RFC3492 RFC3454
		List<byte[]> result = new ArrayList<byte[]>();
		byte[] bytes = namedata.getBytes();
		int namesize = 0;
		ChannelBuffer buffer = ChannelBuffers.buffer(MAX_LABEL_SIZE);
		int current = 0;
		int length = bytes.length;

		boolean escape = false;
		int digits = 0;
		int value = 0;
		for (; current < length; current++) {
			byte b = bytes[current];
			if (escape) {
				if ((('0' <= b) && (b <= '9')) && (digits++ < 3)) {
					value *= 10;
					value += (b - '0');
					if (255 < value) {
						throw new IllegalArgumentException(String.format(
								Messages.EscapedDecimalIsInvalid, value));
					}
					if (2 < digits) {
						appendByte(namedata, buffer, (byte) value);
						escape = false;
					}
				} else if (0 < digits) {
					throw new IllegalArgumentException(
							String.format(
									Messages.MixtureOfEscapedDigitAndNonDigit,
									namedata));
				} else {
					appendByte(namedata, buffer, b);
					escape = false;
				}
			} else if (b == '\\') {
				escape = true;
				digits = 0;
				value = 0;
			} else if (b == '.') {
				namesize = namesize + addBytes(result, buffer) + 1;
			} else {
				appendByte(namedata, buffer, b);
			}
		}

		if (escape) {
			throw new IllegalArgumentException(String.format(
					Messages.InvalidEscapeSequence, namedata));
		}

		if (buffer.readable()) {
			// relative domain name
			namesize = namesize + addBytes(result, buffer);
		} else {
			// absolute domain name
			result.add(NULL_ARRAY);
		}
		namesize += 1;
		if (MAX_NAME_SIZE < namesize) {
			throw new IllegalArgumentException(String.format(
					Messages.NamesMustBe255orLess, namesize));
		}
		return result;
	}

	protected void appendByte(String namedata, ChannelBuffer buffer, byte b) {
		if (buffer.writable()) {
			buffer.writeByte(b);
		} else {
			throw new IllegalArgumentException(String.format(
					Messages.LabelsMustBe63orLess, namedata));
		}
	}

	protected int addBytes(List<byte[]> result, ChannelBuffer buffer) {
		int size = buffer.readableBytes();
		if (size < 1) {
			throw new IllegalArgumentException(Messages.NullLabelIsNotValid);
		}
		byte[] newone = new byte[size];
		buffer.readBytes(newone);
		buffer.clear();
		result.add(newone);
		return size;
	}

	public void write(ChannelBuffer buffer, NameCompressor compressor) {
		// TODO DNAME and other non compress RR
		// TODO need writing cache?
		if (writePointer(buffer, compressor, this) == false) {
			compressor.put(this, buffer.writerIndex());
			for (int i = 0, size = this.name.size(); i < size; i++) {
				byte[] current = this.name.get(i);
				int cl = current.length;
				buffer.writeByte(cl);
				if (0 < cl) {
					buffer.writeBytes(current);
					if (i + 1 < size) {
						Name n = new Name(this.name.subList(i + 1, size));
						if (writePointer(buffer, compressor, n)) {
							break;
						} else {
							compressor.put(n, buffer.writerIndex());
						}
					}
				}
			}
		}
	}

	protected boolean writePointer(ChannelBuffer buffer,
			NameCompressor compressor, Name n) {
		int position = compressor.get(n);
		if (-1 < position) {
			int pointer = (MASK_POINTER << 8) | position;
			buffer.writeShort(pointer);
			return true;
		}
		return false;
	}

	public Name toParent() {
		int size = this.name.size();
		if (1 < size) {
			List<byte[]> newone = this.name.subList(1, size);
			return new Name(newone);
		}
		return NULL_NAME;
	}

	public Name toWildcard() {
		int size = this.name.size();
		if (1 < size) {
			List<byte[]> newone = new ArrayList<byte[]>(size);
			newone.add(WILDCARD_ARRAY);
			newone.addAll(this.name.subList(1, size));
			return new Name(Collections.unmodifiableList(newone));
		}
		return WILDCARD;
	}

	public boolean contains(Name other) {
		notNull(other, "other");
		int mySize = this.name.size();
		int otherSize = other.name.size();
		if (mySize < otherSize) {
			return false;
		}
		int diff = mySize - otherSize;
		for (int i = 0; i < otherSize; i++) {
			byte[] me = this.name.get(i + diff);
			byte[] yu = other.name.get(i);
			if (Arrays.equals(me, yu) == false) {
				return false;
			}
		}
		return true;
	}

	public Name replace(Name from, Name to) {
		notNull(from, "from");
		notNull(to, "to");
		if (contains(from)) {
			int diff = this.name.size() - from.name.size();
			int newsize = diff + to.name.size();
			List<byte[]> newone = new ArrayList<byte[]>(newsize);
			newone.addAll(this.name.subList(0, diff));
			newone.addAll(to.name);
			int size = 0;
			for (byte[] ary : newone) {
				size += ary.length;
			}
			if (size < MAX_NAME_SIZE) {
				return new Name(Collections.unmodifiableList(newone));
			}
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for (byte[] b : this.name) {
			result = prime * result + Arrays.hashCode(b);
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Name) {
			return equals(Name.class.cast(obj));
		}
		return false;
	}

	public boolean equals(Name other) {
		if (other == null) {
			return false;
		}
		int mySize = this.name.size();
		int yrSize = other.name.size();
		if (mySize != yrSize) {
			return false;
		}
		for (int i = 0; i < mySize; i++) {
			byte[] me = this.name.get(i);
			byte[] yu = other.name.get(i);
			if (Arrays.equals(me, yu) == false) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int compareTo(Name o) {
		// TODO use more effective algorithm for red black tree.
		int mySize = this.name.size();
		int yrSize = o.name.size();
		if (mySize != yrSize) {
			return mySize - yrSize;
		}
		int minSize = Math.min(mySize, yrSize);
		for (int i = minSize - 1; (-1 < i); i--) {
			byte[] mine = this.name.get(i);
			byte[] other = o.name.get(i);
			if (Arrays.equals(mine, other) == false) {
				int size = Math.min(mine.length, other.length);
				for (int ii = size - 1; -1 < ii; i--) {
					byte mb = mine[ii];
					byte yb = other[ii];
					if (mb != yb) {
						if (mb < yb) {
							return -1;
						} else {
							return 1;
						}
					}
				}
			}
		}
		return 0;
	}

	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();
		DecimalFormat fmt = new DecimalFormat();
		fmt.setMinimumIntegerDigits(3);
		for (Iterator<byte[]> cursor = this.name.iterator(); cursor.hasNext();) {
			byte[] ary = cursor.next();
			for (int i = 0, l = ary.length; i < l; i++) {
				int b = ary[i] & 0xFF;
				if ((b < 0x21) || (0x7F < b)) {
					stb.append('\\');
					stb.append(fmt.format(b));
				} else {
					switch (b) {
					case '"':
					case '(':
					case ')':
					case '.':
					case ';':
					case '\\':
					case '@':
					case '$':
						stb.append('\\');
					default:
						stb.append((char) b);
						break;
					}
				}
			}
			if (cursor.hasNext()) {
				stb.append('.');
			}
		}
		return stb.toString();
	}
}
