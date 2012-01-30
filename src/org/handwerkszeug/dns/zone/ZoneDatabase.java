package org.handwerkszeug.dns.zone;

import static org.handwerkszeug.util.Validation.notNull;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import org.handwerkszeug.dns.DNSClass;
import org.handwerkszeug.dns.Name;
import org.handwerkszeug.dns.ResourceRecord;
import org.handwerkszeug.dns.Zone;

public class ZoneDatabase {

	protected Map<ZoneDatabaseKey, Zone> zones = new ConcurrentSkipListMap<ZoneDatabaseKey, Zone>();

	public Query prepare(Name name, DNSClass dnsClass) {
		notNull(name, "name");
		notNull(dnsClass, "dnsClass");
		ZoneDatabaseKey zk = new ZoneDatabaseKey(name, dnsClass);
		Zone found = this.zones.get(zk);
		if (found != null) {
			// exact match
			return new Query(name, name, dnsClass, found, this);
		}

		Name child = name;
		// partial match
		for (int i = 0, size = this.zones.size(); i < size; i++) {
			Name p = child.toParent();
			zk.name(p);
			found = this.zones.get(zk);
			if (found == null) {
				child = p;
			} else {
				return new Query(name, p, dnsClass, found, this);
			}
		}
		// not found.
		return null;
	}

	public void add(Zone zone/* TODO ZoneConfig? */) {
		notNull(zone, "zone");
		this.zones.put(new ZoneDatabaseKey(zone), zone);
	}

	static class ZoneDatabaseKey implements Comparable<ZoneDatabaseKey> {
		Name name;
		DNSClass dnsclass;

		public ZoneDatabaseKey(Zone z) {
			this(z.name(), z.dnsClass());
		}

		public ZoneDatabaseKey(ResourceRecord rr) {
			this(rr.name(), rr.dnsClass());
		}

		public ZoneDatabaseKey(Name name, DNSClass dnsclass) {
			notNull(name, "name");
			notNull(dnsclass, "dnsclass");
			this.name = name;
			this.dnsclass = dnsclass;
		}

		public Name name() {
			return this.name;
		}

		public void name(Name name) {
			notNull(name, "name");
			this.name = name;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + this.dnsclass.hashCode();
			result = prime * result + this.name.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}
			if (other == null) {
				return false;
			}
			if (getClass() != other.getClass()) {
				return false;
			}
			return equals((ZoneDatabaseKey) other);
		}

		public boolean equals(ZoneDatabaseKey other) {
			return (this.dnsclass == other.dnsclass)
					&& this.name.equals(other.name);
		}

		@Override
		public int compareTo(ZoneDatabaseKey o) {
			if (o == null) {
				return 1;
			}
			if (equals(o)) {
				return 0;
			}
			return this.hashCode() - o.hashCode();
		}
	}
}
