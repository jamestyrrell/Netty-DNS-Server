package org.handwerkszeug.dns.client;

public class DNSClientTest {

	//@Test // Disabled as the test does nothing
	public void main() throws Exception {
		DNSClient.main(new String[] { "iana.org", "AAAA", "IN" });
		// DNSClient.main(new String[] { "@127.0.0.1", "iana.org", "AAAA", "IN"
		// });
	}
}
