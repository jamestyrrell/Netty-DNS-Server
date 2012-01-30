package org.handwerkszeug.dns;

import java.security.SecureRandom;
import java.text.MessageFormat;

import org.handwerkszeug.dns.nls.Messages;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 * RFC1035 4.1.1. Header section format
 * 
 * <pre>
 *                                   1  1  1  1  1  1
 *     0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                      ID                       |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |QR|   Opcode  |AA|TC|RD|RA|   Z    |   RCODE   |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                    QDCOUNT                    |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                    ANCOUNT                    |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                    NSCOUNT                    |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                    ARCOUNT                    |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * </pre>
 */
public class Header {

	static final int MIN_USHORT = 0;
	static final int MAX_USHORT = 0xFFFF;

	static final int FLAGS_QR = 15;
	static final int FLAGS_Opcode = 11;
	static final int FLAGS_AA = 10;
	static final int FLAGS_TC = 9;
	static final int FLAGS_RD = 8;
	static final int FLAGS_RA = 7;
	static final int FLAGS_Z = 4;
	static final int FLAGS_RCODE = 0;

	static final int[] FLAGS_ALL = { FLAGS_QR, FLAGS_AA, FLAGS_TC, FLAGS_RD,
			FLAGS_RA };
	static final String[] FLAGS_TXT = { "qr", "aa", "tc", "rd", "ra" };

	static final SecureRandom RANDOM;

	static {
		RANDOM = new SecureRandom();
		byte[] seed = RANDOM.generateSeed(20); // TODO more ? from config?
		RANDOM.setSeed(seed);
	}

	protected int id;
	// include there flags |QR| Opcode |AA|TC|RD|RA| Z | RCODE|
	protected int flags;

	protected int qdcount;
	protected int ancount;
	protected int nscount;
	protected int arcount;

	public Header() {
		this(RANDOM.nextInt(MAX_USHORT));
	}

	protected Header(int id) {
		id(id);
	}

	public Header(ChannelBuffer in) {
		this(in.readUnsignedShort());
		flags(in.readUnsignedShort());
		qdcount(in.readUnsignedShort());
		ancount(in.readUnsignedShort());
		nscount(in.readUnsignedShort());
		arcount(in.readUnsignedShort());

	}

	public Header(Header from) {
		this();
		this.flags(from.flags());
		this.qdcount(from.qdcount());
		this.ancount(from.ancount());
		this.nscount(from.nscount());
		this.arcount(from.arcount());
	}

	public void write(ChannelBuffer out) {
		out.writeShort(id());
		out.writeShort(flags());
		out.writeShort(qdcount());
		out.writeShort(ancount());
		out.writeShort(nscount());
		out.writeShort(arcount());
	}

	/**
	 * A 16 bit identifier assigned by the program that generates any kind of
	 * query. This identifier is copied the corresponding reply and can be used
	 * by the requester to match up replies to outstanding queries.
	 * 
	 */
	public int id() {
		return this.id;
	}

	public void id(int i) {
		this.id = verify16bitValue("ID", i);
	}

	private int verify16bitValue(String column, int i) {
		if ((i < MIN_USHORT) || (MAX_USHORT < i)) {
			throw new IllegalArgumentException(String.format(
					Messages.Not16bitValue, column, i));
		}
		return i;
	}

	public int flags() {
		return this.flags;
	}

	protected void flags(int flags) {
		this.flags = verify16bitValue("Flags", flags);
	}

	protected int flag(int shift, int mask) {
		return (this.flags >> shift) & mask;
	}

	/**
	 * A one bit field that specifies whether this message is a query (0), or a
	 * response (1).
	 */
	public boolean qr() {
		return flag(FLAGS_QR, 0x1) != 0;
	}

	public void qr(boolean is) {
		flip(FLAGS_QR, is);
	}

	/**
	 * @see OpCode
	 */
	public OpCode opcode() {
		int code = flag(FLAGS_Opcode, 0xF);
		return OpCode.valueOf(code); // TODO cache?
	}

	public void opcode(OpCode op) {
		// clear current opcode
		this.flags &= 0x87FF; // 1000 0111 1111 1111
		// set opcode
		this.flags |= op.value() << FLAGS_Opcode;
	}

	/**
	 * Authoritative Answer - this bit is valid in responses, and specifies that
	 * the responding name server is an authority for the domain name in
	 * question section.
	 */
	public boolean aa() {
		return flag(FLAGS_AA, 0x1) != 0;
	}

	public void aa(boolean is) {
		flip(FLAGS_AA, is);
	}

	private void flip(int index, boolean is) {
		int i = 1 << index; // TODO move to caller ?
		if (is) {
			this.flags |= i;
		} else {
			this.flags &= i ^ 0xFFFF;
		}
	}

	/**
	 * TrunCation - specifies that this message was truncated due to length
	 * greater than that permitted on the transmission channel.
	 */
	public boolean tc() {
		return flag(FLAGS_TC, 0x1) != 0;
	}

	public void tc(boolean is) {
		flip(FLAGS_TC, is);
	}

	/**
	 * Recursion Desired - this bit may be set in a query and is copied into the
	 * response. If RD is set, it directs the name server to pursue the query
	 * recursively. Recursive query support is optional.
	 */
	public boolean rd() {
		return flag(FLAGS_RD, 0x1) != 0;
	}

	public void rd(boolean is) {
		flip(FLAGS_RD, is);
	}

	/**
	 * Recursion Available - this be is set or cleared in a response, and
	 * denotes whether recursive query support is available in the name server.
	 */
	public boolean ra() {
		return flag(FLAGS_RA, 0x1) != 0;
	}

	public void ra(boolean is) {
		flip(FLAGS_RA, is);
	}

	/**
	 * Reserved for future use. Must be zero in all queries and responses.
	 */
	public int z() {
		return flag(FLAGS_Z, 0x7);
	}

	/**
	 * @see RCode
	 */
	public RCode rcode() {
		int code = flag(FLAGS_RCODE, 0xF);
		return RCode.valueOf(code); // TODO cache ?
	}

	public void rcode(RCode rc) {
		// clear current response code
		this.flags &= 0xFFF0; // 1111 1111 1111 0000
		// set response code
		this.flags |= rc.value();
	}

	/**
	 * an unsigned 16 bit integer specifying the number of entries in the
	 * question section.
	 */
	public int qdcount() {
		return this.qdcount;
	}

	public void qdcount(int value) {
		this.qdcount = verify16bitValue("qdcount", value);
	}

	/**
	 * an unsigned 16 bit integer specifying the number of resource records in
	 * the answer section.
	 */
	public int ancount() {
		return this.ancount;
	}

	public void ancount(int value) {
		this.ancount = verify16bitValue("ancount", value);
	}

	/**
	 * an unsigned 16 bit integer specifying the number of name server resource
	 * records in the authority records section.
	 */
	public int nscount() {
		return this.nscount;
	}

	public void nscount(int value) {
		this.nscount = verify16bitValue("nscount", value);
		;
	}

	/**
	 * an unsigned 16 bit integer specifying the number of resource records in
	 * the additional records section.
	 */
	public int arcount() {
		return this.arcount;
	}

	public void arcount(int value) {
		this.arcount = verify16bitValue("arcount", value);
	}

	@Override
	public String toString() {
		MessageFormat form = new MessageFormat(";; ->>HEADER<<- "
				+ "opcode: {0}, rcode: {1}, id: {2,number,#}\n"
				+ ";; flags: {3}; QUERY: {4,number,#}, "
				+ "ANSWER: {5,number,#}, " + "AUTHORITY: {6,number,#}, "
				+ "ADDITIONAL: {7,number,#}");
		Object[] args = { opcode().name(), rcode().name(), id(),
				toFlagsString(), qdcount(), ancount(), nscount(), arcount() };
		return form.format(args);
	}

	protected String toFlagsString() {
		StringBuilder stb = new StringBuilder();
		for (int i = 0, length = FLAGS_ALL.length; i < length; i++) {
			if (flag(FLAGS_ALL[i], 0x1) != 0) {
				stb.append(FLAGS_TXT[i]);
				stb.append(" ");
			}
		}
		return stb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.id;
		result = prime * result + this.flags;
		result = prime * result + this.qdcount;
		result = prime * result + this.ancount;
		result = prime * result + this.nscount;
		result = prime * result + this.arcount;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Header) {
			Header other = (Header) obj;
			return equals(other);
		}
		return false;
	}

	public boolean equals(Header other) {
		if (other == null) {
			return false;
		}
		if (this.id != other.id) {
			return false;
		}
		if (this.flags != other.flags) {
			return false;
		}
		if (this.qdcount != other.qdcount) {
			return false;
		}
		if (this.ancount != other.ancount) {
			return false;
		}
		if (this.nscount != other.nscount) {
			return false;
		}
		if (this.arcount != other.arcount) {
			return false;
		}
		return true;
	}
}