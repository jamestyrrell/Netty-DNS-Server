package org.handwerkszeug.dns.server;

public class DNSServerTest {

	//@Test // Disabled as you need to be a super user to bind to port 53
	public void testMain() throws Exception {
		DNSServer.main(new String[] {});
	}
}
