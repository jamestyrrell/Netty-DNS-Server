package org.handwerkszeug.dns.server;

import org.handwerkszeug.dns.DNSMessage;
import org.handwerkszeug.dns.RCode;
import org.handwerkszeug.dns.ResolveContext;
import org.handwerkszeug.dns.record.SOARecord;

public class NotFoundResponse extends DefaultResponse {
	final SOARecord soaRecord;

	public NotFoundResponse(RCode rcode, SOARecord soaRecord) {
		super(rcode);
		this.soaRecord = soaRecord;
	}

	@Override
	public void postProcess(ResolveContext context) {
		DNSMessage res = context.response();
		res.header().rcode(this.rcode());
		res.authority().add(this.soaRecord);
	}
}