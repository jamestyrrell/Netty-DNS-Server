package org.handwerkszeug.dns.conf;

import org.handwerkszeug.dns.ResourceRecord;

public interface MasterDataHandler {
	// lifecycle methods
	void initialize(ServerConfiguration conf);

	void commit();

	void rollback();

	void dispose();

	// directives
	// void do$origin(Name origin);

	// process parser
	// void do$include(String line);

	// void do$ttl(long ttl);

	// void do$unknown(String line);

	void add(ResourceRecord record);

}
