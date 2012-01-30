package org.handwerkszeug.dns.record;

import java.util.List;

import org.handwerkszeug.dns.Name;
import org.handwerkszeug.dns.NameCompressor;
import org.handwerkszeug.dns.RRType;
import org.handwerkszeug.dns.ResourceRecord;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 * 3.3.7. MINFO RDATA format (EXPERIMENTAL)
 * 
 * @author taichi
 */
public class MINFORecord extends AbstractRecord<MINFORecord> {

	/**
	 * A <domain-name> which specifies a mailbox which is responsible for the
	 * mailing list or mailbox. If this domain name names the root, the owner of
	 * the MINFO RR is responsible for itself. Note that many existing mailing
	 * lists use a mailbox X-request for the RMAILBX field of mailing list X,
	 * e.g., Msgroup-request for Msgroup. This field provides a more general
	 * mechanism.
	 */
	protected Name rmailbx;

	/**
	 * A <domain-name> which specifies a mailbox which is to receive error
	 * messages related to the mailing list or mailbox specified by the owner of
	 * the MINFO RR (similar to the ERRORS-TO: field which has been proposed).
	 * If this domain name names the root, errors should be returned to the
	 * sender of the message.
	 */
	protected Name emailbx;

	public MINFORecord() {
		super(RRType.MINFO);
	}

	public MINFORecord(MINFORecord from) {
		super(from);
		this.rmailbx(from.rmailbx());
		this.emailbx(from.emailbx());
	}

	@Override
	protected void parseRDATA(ChannelBuffer buffer) {
		this.rmailbx = new Name(buffer);
		this.emailbx = new Name(buffer);
	}

	@Override
	protected void writeRDATA(ChannelBuffer buffer, NameCompressor compressor) {
		this.rmailbx().write(buffer, compressor);
		this.emailbx().write(buffer, compressor);
	}

	@Override
	public void setRDATA(List<String> list) {
		if (2 == list.size()) {
			this.rmailbx(new Name(list.get(0)));
			this.emailbx(new Name(list.get(1)));
		} else {
			// TODO error message.
			throw new IllegalArgumentException();
		}
	}

	@Override
	protected ResourceRecord newInstance() {
		return new MINFORecord(this);
	}

	public Name rmailbx() {
		return this.rmailbx;
	}

	public void rmailbx(Name name) {
		this.rmailbx = name;
	}

	public Name emailbx() {
		return this.emailbx;
	}

	public void emailbx(Name name) {
		this.emailbx = name;
	}

	@Override
	public int compareTo(MINFORecord o) {
		if (this == o) {
			return 0;
		}
		int result = super.compareTo(o);
		if (result == 0) {
			result = this.rmailbx().compareTo(o.rmailbx());
			if (result == 0) {
				result = this.emailbx().compareTo(o.emailbx());
			}
		}
		return result;
	}

	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();
		stb.append(super.toString());
		stb.append(' ');
		stb.append(this.rmailbx());
		stb.append(' ');
		stb.append(this.emailbx());
		return stb.toString();
	}
}
