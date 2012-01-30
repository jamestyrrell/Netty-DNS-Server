package org.handwerkszeug.dns.zone;

import org.handwerkszeug.dns.DNSClass;
import org.handwerkszeug.dns.Name;
import org.handwerkszeug.dns.Zone;
import org.handwerkszeug.dns.ZoneType;

public abstract class AbstractZone implements Zone {

	protected ZoneType type;

	protected DNSClass dnsClass;

	protected Name name;

	public AbstractZone(ZoneType type, Name name) {
		this(type, DNSClass.IN, name);
	}

	public AbstractZone(ZoneType type, DNSClass dnsClass, Name name) {
		this.type = type;
		this.dnsClass = dnsClass;
		this.name = name;
	}

	@Override
	public ZoneType type() {
		return this.type;
	}

	@Override
	public DNSClass dnsClass() {
		return this.dnsClass;
	}

	@Override
	public Name name() {
		return this.name;
	}

}
