package org.handwerkszeug.dns;

public interface Zone {
	Name name();

	DNSClass dnsClass();

	ZoneType type();

	Response find(Name qname, RRType qtype);
}
