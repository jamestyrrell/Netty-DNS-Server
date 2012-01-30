package org.handwerkszeug.dns.server;

import java.util.Set;

import org.handwerkszeug.dns.DNSMessage;
import org.handwerkszeug.dns.RCode;
import org.handwerkszeug.dns.ResolveContext;
import org.handwerkszeug.dns.ResourceRecord;

public class NoErrorResponse extends DefaultResponse {
	final Set<ResourceRecord> records;
	final boolean aa;

	public NoErrorResponse(Set<ResourceRecord> records) {
		this(records, true);
	}

	public NoErrorResponse(Set<ResourceRecord> records, boolean aa) {
		super(RCode.NoError);
		this.records = records;
		this.aa = aa;
	}

	@Override
	public void postProcess(ResolveContext context) {
		DNSMessage res = context.response();
		res.header().rcode(this.rcode());
		res.header().aa(this.aa);
		res.answer().addAll(this.records);
		// TODO additional section ?
	}
}