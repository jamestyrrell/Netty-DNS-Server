package org.handwerkszeug.dns.zone;

import static org.handwerkszeug.util.Validation.notNull;

import java.util.List;

import org.handwerkszeug.dns.DNSClass;
import org.handwerkszeug.dns.Name;
import org.handwerkszeug.dns.ResourceRecord;
import org.handwerkszeug.dns.Zone;

/**
 * @author taichi
 */
public class Query {

	protected Name origin;

	protected Name current;

	protected DNSClass dnsClass;

	protected Zone target;

	protected ZoneDatabase database;

	public Query(Name origin, Name current, DNSClass dnsClass, Zone target,
			ZoneDatabase database) {
		super();
		notNull(origin, "origin");
		notNull(current, "current");
		notNull(dnsClass, "dnsClass");
		notNull(target, "target");
		notNull(database, "database");

		this.origin = origin;
		this.current = current;
		this.dnsClass = dnsClass;
		this.target = target;
		this.database = database;
	}

	// public SearchResult execute(/* ResolveContext? */) {
	// List<ResourceRecord> rrs = this.target.resolve(this.current,
	// this.dnsClass);
	// SearchResult result = new SearchResult(rrs);
	// if (rrs.isEmpty()) {
	// result.status = Status.NXDOMAIN;
	// return result;
	// } else if (contains(rrs)) {
	// result.status = Status.SUCCESS;
	// return result;
	// } else {
	// Query q = this.database.prepare(this.origin, this.dnsClass);
	// SearchResult sr = q.execute();
	// result.rrs.addAll(sr.rrs);
	// result.status = sr.status;
	// return sr;
	// }
	// }

	protected boolean contains(List<ResourceRecord> rrs) {
		for (ResourceRecord rr : rrs) {
			if (this.origin.equals(rr.name())) {
				return true;
			}
		}
		return false;
	}
}
