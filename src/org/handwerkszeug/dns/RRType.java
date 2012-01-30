package org.handwerkszeug.dns;

import org.handwerkszeug.dns.nls.Messages;
import org.handwerkszeug.dns.record.AAAARecord;
import org.handwerkszeug.dns.record.ARecord;
import org.handwerkszeug.dns.record.HINFORecord;
import org.handwerkszeug.dns.record.MINFORecord;
import org.handwerkszeug.dns.record.MXRecord;
import org.handwerkszeug.dns.record.NULLRecord;
import org.handwerkszeug.dns.record.SOARecord;
import org.handwerkszeug.dns.record.SingleNameRecord;
import org.handwerkszeug.dns.record.TXTRecord;
import org.handwerkszeug.dns.record.WKSRecord;
import org.handwerkszeug.util.EnumUtil;
import org.handwerkszeug.util.VariableEnum;

/**
 * 3.2.2. TYPE values
 * 
 * @author taichi
 * @see <a href="http://www.iana.org/assignments/dns-parameters">Domain Name
 *      System (DNS) Parameters</a>
 */
public enum RRType implements VariableEnum {
	/**
	 * a host address
	 */
	A(1) {
		@Override
		public ResourceRecord newRecord() {
			return new ARecord();
		}
	},
	/**
	 * an authoritative name server
	 */
	NS(2) {
		@Override
		public ResourceRecord newRecord() {
			return new SingleNameRecord(this);
		}
	},
	/**
	 * a mail destination (Obsolete - use MX)
	 */
	MD(3) {
		@Override
		public ResourceRecord newRecord() {
			return new SingleNameRecord(this);
		}
	},
	/**
	 * a mail forwarder (Obsolete - use MX)
	 */
	MF(4) {
		@Override
		public ResourceRecord newRecord() {
			return new SingleNameRecord(this);
		}
	},
	/**
	 * the canonical name for an alias
	 */
	CNAME(5) {
		@Override
		public ResourceRecord newRecord() {
			return new SingleNameRecord(this);
		}
	},
	/**
	 * marks the start of a zone of authority
	 */
	SOA(6) {
		@Override
		public ResourceRecord newRecord() {
			return new SOARecord();
		}
	},
	/**
	 * a mailbox domain name (EXPERIMENTAL)
	 */
	MB(7) {
		@Override
		public ResourceRecord newRecord() {
			return new SingleNameRecord(this);
		}
	},
	/**
	 * a mail group member (EXPERIMENTAL)
	 */
	MG(8) {
		@Override
		public ResourceRecord newRecord() {
			return new SingleNameRecord(this);
		}
	},
	/**
	 * a mail rename domain name (EXPERIMENTAL)
	 */
	MR(9) {
		@Override
		public ResourceRecord newRecord() {
			return new SingleNameRecord(this);
		}
	},
	/**
	 * a null RR (EXPERIMENTAL)
	 */
	NULL(10) {
		@Override
		public ResourceRecord newRecord() {
			return new NULLRecord();
		}
	},
	/**
	 * a well known service description
	 */
	WKS(11) {
		@Override
		public ResourceRecord newRecord() {
			return new WKSRecord();
		}
	},
	/**
	 * a domain name pointer
	 */
	PTR(12) {
		@Override
		public ResourceRecord newRecord() {
			return new SingleNameRecord(this);
		}
	},
	/**
	 * host information
	 */
	HINFO(13) {
		@Override
		public ResourceRecord newRecord() {
			return new HINFORecord();
		}
	},
	/**
	 * mailbox or mail list information
	 */
	MINFO(14) {
		@Override
		public ResourceRecord newRecord() {
			return new MINFORecord();
		}
	},
	/**
	 * mail exchange
	 */
	MX(15) {
		@Override
		public ResourceRecord newRecord() {
			return new MXRecord();
		}
	},
	/**
	 * text strings
	 */
	TXT(16) {
		@Override
		public ResourceRecord newRecord() {
			return new TXTRecord();
		}
	},
	/**
	 * IP6 Address
	 */
	AAAA(28) {
		@Override
		public ResourceRecord newRecord() {
			return new AAAARecord();
		}
	},

	/**
	 * Naming Authority Pointer
	 * 
	 * @see http://tools.ietf.org/html/rfc3403#section-4
	 */
	NAPTR(35) {
		@Override
		public ResourceRecord newRecord() {
			// TODO not implemented...
			throw new UnsupportedOperationException();
		}
	},

	/**
	 * Non-Terminal DNS Name Redirection
	 * 
	 * @see http://www.ietf.org/rfc/rfc2672.txt
	 */
	DNAME(39) {
		@Override
		public ResourceRecord newRecord() {
			return new SingleNameRecord(this);
		}
	},

	// RFC1035 3.2.3. QTYPE values
	/**
	 * A request for a transfer of an entire zone
	 */
	AXFR(252) {
		@Override
		public ResourceRecord newRecord() {
			throw new UnsupportedOperationException(String.format(
					Messages.NoResourceRecord, AXFR.name()));
		}
	},
	/**
	 * A request for mailbox-related records (MB, MG or MR)
	 */
	MAILB(253) {
		@Override
		public ResourceRecord newRecord() {
			throw new UnsupportedOperationException(String.format(
					Messages.NoResourceRecord, MAILB.name()));
		}
	},
	/**
	 * A request for mail agent RRs (Obsolete - see MX)
	 */
	MAILA(254) {
		@Override
		public ResourceRecord newRecord() {
			throw new UnsupportedOperationException(String.format(
					Messages.NoResourceRecord, MAILA.name()));
		}
	},
	/**
	 * A request for all records
	 */
	ANY(255) {
		@Override
		public ResourceRecord newRecord() {
			throw new UnsupportedOperationException(String.format(
					Messages.NoResourceRecord, ANY.name()));
		}
	},
	UNKNOWN(-1) {
		@Override
		public ResourceRecord newRecord() {
			return new NULLRecord();
		}
	};

	private int code;

	private RRType(int i) {
		this.code = i;
	}

	public abstract ResourceRecord newRecord();

	@Override
	public int value() {
		return this.code;
	}

	public static RRType valueOf(int code) {
		return EnumUtil.find(RRType.values(), code, UNKNOWN);
	}

	public static RRType find(String value) {
		return EnumUtil.find(RRType.values(), value, null);
	}
}
