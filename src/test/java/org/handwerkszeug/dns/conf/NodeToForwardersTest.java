package org.handwerkszeug.dns.conf;

import static org.junit.Assert.assertTrue;

import java.io.StringReader;

import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Node;

public class NodeToForwardersTest {

	Yaml yaml;

	@Before
	public void setUp() {
		this.yaml = new Yaml();
	}

	@Test
	public void testHandle() {
		testHandle("AutoDetect");
		testHandle("127.0.0.1");
		testHandle("- autodetect\n- 127.0.0.1");
	}

	protected void testHandle(String data) {
		Node node = this.yaml.compose(new StringReader(data));
		NodeToForwarders target = new NodeToForwarders(new NodeToAddress());
		ServerConfigurationImpl context = new ServerConfigurationImpl();
		target.handle(node, context);

		assertTrue(0 < context.getForwarders().size());
	}
}
