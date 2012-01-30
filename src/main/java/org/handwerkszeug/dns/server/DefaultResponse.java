package org.handwerkszeug.dns.server;

import org.handwerkszeug.dns.RCode;
import org.handwerkszeug.dns.Response;

public abstract class DefaultResponse implements Response {
	final RCode rcode;

	protected DefaultResponse(RCode rcode) {
		this.rcode = rcode;
	}

	@Override
	public RCode rcode() {
		return this.rcode;
	}
}