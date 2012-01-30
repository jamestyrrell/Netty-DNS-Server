package org.handwerkszeug.dns.server;

import static org.handwerkszeug.util.Validation.notNull;

import org.handwerkszeug.dns.DNSMessage;
import org.handwerkszeug.dns.Name;
import org.handwerkszeug.dns.RRType;
import org.handwerkszeug.dns.ResolveContext;
import org.handwerkszeug.dns.Response;

public class DefaultResolveContext implements ResolveContext {

	final DNSMessage request;
	final DNSMessage response;

	public DefaultResolveContext(DNSMessage request) {
		this(request, new DNSMessage());
	}

	public DefaultResolveContext(DNSMessage request, DNSMessage response) {
		notNull(request, "request");
		notNull(response, "response");
		this.request = request;
		this.response = response;
	}

	@Override
	public DNSMessage request() {
		return this.request;
	}

	@Override
	public DNSMessage response() {
		return this.response;
	}

	@Override
	public Response resolve(Name qname, RRType qtype) {
		// TODO Auto-generated method stub
		return null;
	}

}
