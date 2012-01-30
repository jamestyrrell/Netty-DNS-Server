package org.handwerkszeug.yaml;

import org.handwerkszeug.dns.conf.ServerConfiguration;
import org.handwerkszeug.yaml.DefaultHandler;
import org.handwerkszeug.yaml.MappingHandler;
import org.handwerkszeug.yaml.SequenceHandler;
import org.handwerkszeug.yaml.YamlNodeAccepter;
import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.nodes.Node;

public class YamlNodeAccepterTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testAccept() throws Exception {
		ServerConfiguration conf = new ServerConfiguration();
		MappingHandler<ServerConfiguration> root = new MappingHandler<ServerConfiguration>();
		root.add(new SequenceHandler<ServerConfiguration>("bindingHosts",
				new DefaultHandler<ServerConfiguration>() {
					@Override
					public void handle(Node node, ServerConfiguration conf) {
						System.out.println(node);
					}
				}));
		root.add(new DefaultHandler<ServerConfiguration>("logging") {
			@Override
			public void handle(Node node, ServerConfiguration conf) {
				System.out.println(node);
			}
		});
		root.add(new DefaultHandler<ServerConfiguration>("threadPoolSize") {
			@Override
			public void handle(Node node, ServerConfiguration conf) {
				System.out.println(node);
			}
		});

		YamlNodeAccepter<ServerConfiguration> accepter = new YamlNodeAccepter<ServerConfiguration>(
				root);
		accepter.accept(YamlNodeAccepter.class
				.getResourceAsStream("/named.default.yml"), conf);
	}
}
