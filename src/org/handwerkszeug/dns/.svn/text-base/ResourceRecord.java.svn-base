package org.handwerkszeug.dns;

import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * 4.1.3. Resource record format<br/>
 * The answer, authority, and additional sections all share the same format: a
 * variable number of resource records, where the number of records is specified
 * in the corresponding count field in the header. Each resource record has the
 * following format:
 * 
 * <pre>
 *                                     1  1  1  1  1  1
 *       0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5
 *     +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *     |                                               |
 *     /                                               /
 *     /                      NAME                     /
 *     |                                               |
 *     +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *     |                      TYPE                     |
 *     +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *     |                     CLASS                     |
 *     +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *     |                      TTL                      |
 *     |                                               |
 *     +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *     |                   RDLENGTH                    |
 *     +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--|
 *     /                     RDATA                     /
 *     /                                               /
 *     +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * </pre>
 * 
 * @author taichi
 * @see <a href="http://www.iana.org/assignments/dns-parameters">Domain Name
 *      System (DNS) Parameters</a>
 */
public interface ResourceRecord {

	/**
	 * two octets containing one of the RR type codes. This field specifies the
	 * meaning of the data in the RDATA field.
	 */
	RRType type();

	/**
	 * a domain name to which this resource record pertains.
	 */
	Name name();

	void name(Name name);

	/**
	 * two octets which specify the class of the data in the RDATA field.
	 */
	DNSClass dnsClass();

	void dnsClass(DNSClass dnsClass);

	/**
	 * a 32 bit unsigned integer that specifies the time interval (in seconds)
	 * that the resource record may be cached before it should be discarded.
	 * Zero values are interpreted to mean that the RR can only be used for the
	 * transaction in progress, and should not be cached.
	 */
	long ttl();

	void ttl(long ttl);

	/**
	 * an unsigned 16 bit integer that specifies the length in octets of the
	 * RDATA field.
	 */
	int rdlength();

	void rdlength(int rdlength);

	void setRDATA(List<String> list);

	void parse(ChannelBuffer buffer);

	void write(ChannelBuffer buffer, NameCompressor compressor);

	ResourceRecord toQnameRecord(Name qname);
}
