package org.handwerkszeug.dns;

import org.handwerkszeug.util.EnumUtil;
import org.handwerkszeug.util.VariableEnum;

/**
 * Response code - this 4 bit field is set as part of responses. The values have
 * the following interpretation:
 * 
 * @author taichi
 * @see <a href="http://www.iana.org/assignments/dns-parameters">Domain Name
 *      System (DNS) Parameters</a>
 */
public enum RCode implements VariableEnum {

	/**
	 * No error condition
	 */
	NoError(0),

	/**
	 * Format error - The name server was unable to interpret the query.
	 */
	FormErr(1),

	/**
	 * Server failure - The name server was unable to process this query due to
	 * a problem with the name server.
	 */
	ServFail(2),

	/**
	 * Name Error - Meaningful only for responses from an authoritative name
	 * server, this code signifies that the domain name referenced in the query
	 * does not exist.
	 */
	NXDomain(3),

	/**
	 * Not Implemented - The name server does not support the requested kind of
	 * query.
	 */
	NotImp(4),

	/**
	 * Refused - The name server refuses to perform the specified operation for
	 * policy reasons. For example, a name server may not wish to provide the
	 * information to the particular requester, or a name server may not wish to
	 * perform a particular operation (e.g., zone transfer) for particular data.
	 */
	Refused(5),

	/**
	 * Name Exists when it should not
	 */
	YXDomain(6),

	/**
	 * RR Set Exists when it should not
	 */
	YXRRSet(7),

	/**
	 * RR Set that should exist does not
	 */
	NXRRSet(8),

	/**
	 * Server Not Authoritative for zone
	 */
	NotAuth(9),

	/**
	 * Name not contained in zone
	 */
	NotZone(10);

	private int code;

	@Override
	public int value() {
		return this.code;
	}

	private RCode(int i) {
		this.code = i;
	}

	public static RCode valueOf(int code) {
		return EnumUtil.find(RCode.values(), code);
	}
}
