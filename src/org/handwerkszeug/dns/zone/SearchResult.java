package org.handwerkszeug.dns.zone;

import java.util.List;

import org.handwerkszeug.dns.ResourceRecord;

public class SearchResult {

	public enum Status {
		NXDOMAIN, SUCCESS;
	}

	List<ResourceRecord> rrs;

	Status status = Status.NXDOMAIN;

	public SearchResult(List<ResourceRecord> rrs) {
		super();
		this.rrs = rrs;
	}
}
