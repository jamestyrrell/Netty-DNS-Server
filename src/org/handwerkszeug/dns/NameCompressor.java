package org.handwerkszeug.dns;


/**
 * 3.3. Standard RRs
 * <p>
 * The following RR definitions are expected to occur, at least potentially, in
 * all classes. In particular, NS, SOA, CNAME, and PTR will be used in all
 * classes, and have the same format in all classes. Because their RDATA format
 * is known, all domain names in the RDATA section of these RRs may be
 * compressed.
 * </p>
 * 
 * 4.1.4. Message compression
 * 
 * @author taichi
 * 
 */
public interface NameCompressor {

	void put(Name name, int offset);

	int get(Name name);

}
