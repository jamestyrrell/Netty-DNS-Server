package org.handwerkszeug.dns.zone;

import static org.handwerkszeug.util.Validation.notNull;

import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.handwerkszeug.dns.Name;
import org.handwerkszeug.dns.RCode;
import org.handwerkszeug.dns.RRType;
import org.handwerkszeug.dns.ResourceRecord;
import org.handwerkszeug.dns.Response;
import org.handwerkszeug.dns.ZoneType;
import org.handwerkszeug.dns.record.SOARecord;
import org.handwerkszeug.dns.server.CNAMEResponse;
import org.handwerkszeug.dns.server.DNAMEResponse;
import org.handwerkszeug.dns.server.NoErrorResponse;
import org.handwerkszeug.dns.server.NotFoundResponse;
import org.handwerkszeug.dns.server.ReferralResponse;

public class MasterZone extends AbstractZone {

	final ConcurrentMap<Name, ConcurrentMap<RRType, NavigableSet<ResourceRecord>>> records = new ConcurrentSkipListMap<Name, ConcurrentMap<RRType, NavigableSet<ResourceRecord>>>();
	final Response nxDomain;
	final Response nxRRSet;

	public MasterZone(Name name, SOARecord soaRecord) {
		super(ZoneType.master, name);
		this.nxDomain = new NotFoundResponse(RCode.NXDomain, soaRecord);
		this.nxRRSet = new NotFoundResponse(RCode.NXRRSet, soaRecord);
	}

	@Override
	public Response find(Name qname, RRType qtype) {
		notNull(qname, "qname");
		notNull(qtype, "qtype");

		if (qname.contains(this.name()) == false) {
			return this.nxDomain;
		}

		ConcurrentMap<RRType, NavigableSet<ResourceRecord>> exactMatch = this.records
				.get(qname);
		if (exactMatch != null) {
			NavigableSet<ResourceRecord> rrs = exactMatch.get(qtype);
			if (rrs != null) {
				synchronized (rrs) {
					if (rrs.isEmpty() == false) {
						return new NoErrorResponse(rrs);
					}
				}
			}
			if (RRType.ANY.equals(qtype)) {
				Set<ResourceRecord> newset = new HashSet<ResourceRecord>();
				for (RRType type : exactMatch.keySet()) {
					Set<ResourceRecord> s = exactMatch.get(type);
					if (s != null) {
						synchronized (s) {
							newset.addAll(s);
						}
					}
				}
				if (newset.isEmpty() == false) {
					return new NoErrorResponse(newset);
				}
			}
			if (RRType.CNAME.equals(qtype) == false) {
				rrs = exactMatch.get(RRType.CNAME);
				if (rrs != null) {
					synchronized (rrs) {
						if (rrs.isEmpty() == false) {
							return new CNAMEResponse(rrs.first(), qtype);
						}
					}
				}
			}
			return this.nxRRSet;
		}

		for (Name qn = qname.toParent(); this.name().equals(qn) == false; qn = qn
				.toParent()) {
			ConcurrentMap<RRType, NavigableSet<ResourceRecord>> match = this.records
					.get(qn);
			if (match != null) {
				synchronized (match) {
					if (match.isEmpty() == false) {
						NavigableSet<ResourceRecord> set = match.get(RRType.NS);
						if ((set != null) && (set.isEmpty() == false)) {
							return new ReferralResponse(set);
						}
						set = match.get(RRType.DNAME);
						if ((set != null) && (set.isEmpty() == false)) {
							return new DNAMEResponse(set.first(), qname, qtype);
						}
					}
				}
			}
		}

		for (Name qn = qname; this.name().equals(qn) == false; qn = qn
				.toParent()) {
			Name wild = qn.toWildcard();
			ConcurrentMap<RRType, NavigableSet<ResourceRecord>> match = this.records
					.get(wild);
			if (match != null) {
				synchronized (match) {
					if (match.isEmpty() == false) {
						Set<ResourceRecord> matchSet = match.get(qtype);
						if (matchSet.isEmpty() == false) {
							Set<ResourceRecord> set = new HashSet<ResourceRecord>(
									matchSet.size());
							for (ResourceRecord rr : matchSet) {
								set.add(rr.toQnameRecord(qname));
							}
							return new NoErrorResponse(set);
						}
					}
				}
			}
		}

		return this.nxDomain;
	}

	// add and remove needs queuing?
	// if modify operations works on single thread, not conflict.
	public void add(ResourceRecord rr) {
		notNull(rr, "rr");
		for (;;) {
			ConcurrentMap<RRType, NavigableSet<ResourceRecord>> current = this.records
					.get(rr.name());
			if (current == null) {
				ConcurrentMap<RRType, NavigableSet<ResourceRecord>> newone = new ConcurrentSkipListMap<RRType, NavigableSet<ResourceRecord>>();
				NavigableSet<ResourceRecord> newset = new ConcurrentSkipListSet<ResourceRecord>();
				newset.add(rr);
				newone.put(rr.type(), newset);

				ConcurrentMap<RRType, NavigableSet<ResourceRecord>> prevTypes = this.records
						.putIfAbsent(rr.name(), newone);
				if (prevTypes == null) {
					break;
				}
				synchronized (prevTypes) {
					Set<ResourceRecord> prevRecs = prevTypes.putIfAbsent(
							rr.type(), newset);
					if (prevRecs == null) {
						break;
					}
					prevRecs.add(rr);
					break;
				}
			} else {
				synchronized (current) {
					Set<ResourceRecord> rrs = current.get(rr.type());
					if (rrs == null) {
						NavigableSet<ResourceRecord> newset = new ConcurrentSkipListSet<ResourceRecord>();
						newset.add(rr);
						current.put(rr.type(), newset);
						break;
					}
					if (rrs.isEmpty() == false) {
						rrs.add(rr);
						break;
					}
				}
			}
		}
	}

	public void remove(ResourceRecord rr, boolean checkSets, boolean checkMap) {
		notNull(rr, "rr");
		ConcurrentMap<RRType, NavigableSet<ResourceRecord>> current = this.records
				.get(rr.name());
		if (current != null) {
			synchronized (current) {
				NavigableSet<ResourceRecord> sets = current.get(rr.type());
				sets.remove(rr);
				if (checkSets && sets.isEmpty()) {
					current.remove(rr.type());
					if (checkMap && current.isEmpty()) {
						this.records.remove(rr.name());
					}
				}
			}
		}
	}
}
