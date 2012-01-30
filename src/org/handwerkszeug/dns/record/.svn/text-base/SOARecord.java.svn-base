package org.handwerkszeug.dns.record;

import java.util.List;

import org.handwerkszeug.dns.Name;
import org.handwerkszeug.dns.NameCompressor;
import org.handwerkszeug.dns.RRType;
import org.handwerkszeug.dns.ResourceRecord;
import org.handwerkszeug.util.CompareUtil;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 * 3.3.13. SOA RDATA format
 * 
 * <pre>
 * +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * /                     MNAME                     /
 * /                                               /
 * +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * /                     RNAME                     /
 * +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * |                    SERIAL                     |
 * |                                               |
 * +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * |                    REFRESH                    |
 * |                                               |
 * +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * |                     RETRY                     |
 * |                                               |
 * +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * |                    EXPIRE                     |
 * |                                               |
 * +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * |                    MINIMUM                    |
 * |                                               |
 * +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * </pre>
 */
public class SOARecord extends AbstractRecord<SOARecord> {

	/**
	 * The <domain-name> of the name server that was the original or primary
	 * source of data for this zone.
	 */
	protected Name mname;

	/**
	 * A <domain-name> which specifies the mailbox of the person responsible for
	 * this zone.
	 */
	protected Name rname;

	/**
	 * The unsigned 32 bit version number of the original copy of the zone. Zone
	 * transfers preserve this value. This value wraps and should be compared
	 * using sequence space arithmetic.
	 */
	protected long serial;

	/**
	 * A 32 bit time interval before the zone should be refreshed.
	 */
	protected long refresh;

	/**
	 * A 32 bit time interval that should elapse before a failed refresh should
	 * be retried.
	 */
	protected long retry;

	/**
	 * A 32 bit time value that specifies the upper limit on the time interval
	 * that can elapse before the zone is no longer authoritative.
	 */
	protected long expire;

	/**
	 * The unsigned 32 bit minimum TTL field that should be exported with any RR
	 * from this zone.
	 */
	protected long minimum;

	public SOARecord() {
		super(RRType.SOA);
	}

	public SOARecord(SOARecord from) {
		super(from);
		this.mname(from.mname());
		this.rname(from.rname());
		this.serial(from.serial());
		this.refresh(from.refresh());
		this.retry(from.retry());
		this.expire(from.expire());
		this.minimum(from.minimum());
	}

	@Override
	protected void parseRDATA(ChannelBuffer buffer) {
		this.mname(new Name(buffer));
		this.rname(new Name(buffer));
		this.serial(buffer.readUnsignedInt());
		this.refresh(buffer.readUnsignedInt());
		this.retry(buffer.readUnsignedInt());
		this.expire(buffer.readUnsignedInt());
		this.minimum(buffer.readUnsignedInt());
	}

	@Override
	protected void writeRDATA(ChannelBuffer buffer, NameCompressor compressor) {
		this.mname().write(buffer, compressor);
		this.rname().write(buffer, compressor);
		buffer.writeInt((int) this.serial());
		buffer.writeInt((int) this.refresh());
		buffer.writeInt((int) this.retry());
		buffer.writeInt((int) this.expire());
		buffer.writeInt((int) this.minimum());
	}

	@Override
	public void setRDATA(List<String> list) {
		if (6 < list.size()) {
			// XXX
		} else {
			// TODO error message.
			throw new IllegalArgumentException();
		}
	}

	@Override
	protected ResourceRecord newInstance() {
		return new SOARecord(this);
	}

	/**
	 * The <domain-name> of the name server that was the original or primary
	 * source of data for this zone.
	 */
	public Name mname() {
		return this.mname;
	}

	public void mname(Name name) {
		this.mname = name;
	}

	public Name rname() {
		return this.rname;
	}

	public void rname(Name name) {
		this.rname = name;
	}

	public long serial() {
		return this.serial;
	}

	public void serial(long uint) {
		this.serial = uint & 0xFFFFFFFFL;
	}

	public long refresh() {
		return this.refresh;
	}

	public void refresh(long uint) {
		this.refresh = uint & 0xFFFFFFFFL;
	}

	public long retry() {
		return this.retry;
	}

	public void retry(long uint) {
		this.retry = uint & 0xFFFFFFFFL;
	}

	public long expire() {
		return this.expire;
	}

	public void expire(long uint) {
		this.expire = uint & 0xFFFFFFFFL;
	}

	public long minimum() {
		return this.minimum;
	}

	public void minimum(long uint) {
		this.minimum = uint & 0xFFFFFFFFL;
	}

	@Override
	public int compareTo(SOARecord o) {
		if (this == o) {
			return 0;
		}
		int result = super.compareTo(o);
		if (result != 0) {
			return result;
		}
		result = this.mname().compareTo(o.mname());
		if (result != 0) {
			return result;
		}
		result = this.rname().compareTo(o.rname());
		if (result != 0) {
			return result;
		}
		result = CompareUtil.compare(this.serial(), o.serial());
		if (result != 0) {
			return result;
		}
		result = CompareUtil.compare(this.refresh(), o.refresh());
		if (result != 0) {
			return result;
		}
		result = CompareUtil.compare(this.retry(), o.retry());
		if (result != 0) {
			return result;
		}
		result = CompareUtil.compare(this.expire(), o.expire());
		if (result != 0) {
			return result;
		}
		return CompareUtil.compare(this.minimum(), o.minimum());
	}

	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();
		stb.append(super.toString());
		stb.append(' ');
		stb.append(this.mname());
		stb.append(' ');
		stb.append(this.rname());
		stb.append(' ');
		stb.append(this.serial());
		stb.append(' ');
		stb.append(this.refresh());
		stb.append(' ');
		stb.append(this.retry());
		stb.append(' ');
		stb.append(this.expire());
		stb.append(' ');
		stb.append(this.minimum());
		return stb.toString();
	}
}
