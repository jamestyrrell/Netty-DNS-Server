package org.handwerkszeug.dns.server;

import org.handwerkszeug.dns.RCode;
import org.handwerkszeug.dns.RRType;
import org.handwerkszeug.dns.ResolveContext;
import org.handwerkszeug.dns.ResourceRecord;
import org.handwerkszeug.dns.Response;
import org.handwerkszeug.dns.record.SingleNameRecord;

public class CNAMEResponse extends DefaultResponse {
	final SingleNameRecord cname;
	final RRType qtype;

	public CNAMEResponse(ResourceRecord cname, RRType qtype) {
		super(RCode.NoError);
		this.cname = SingleNameRecord.class.cast(cname);
		this.qtype = qtype;
	}

	@Override
	public void postProcess(ResolveContext context) {
		context.response().answer().add(this.cname);
		Response r = context.resolve(this.cname.oneName(), this.qtype);
		r.postProcess(context);
	}
}