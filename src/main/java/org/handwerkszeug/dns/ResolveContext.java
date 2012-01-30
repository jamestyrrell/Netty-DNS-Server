package org.handwerkszeug.dns;

public interface ResolveContext {

	DNSMessage request();

	DNSMessage response();

	Response resolve(Name qname, RRType qtype);
}
