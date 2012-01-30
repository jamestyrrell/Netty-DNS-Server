package org.handwerkszeug.yaml;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.handwerkszeug.dns.Markers;
import org.handwerkszeug.dns.nls.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.composer.Composer;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.resolver.Resolver;

public class YamlNodeAccepter<CTX> {

	static final Logger LOG = LoggerFactory.getLogger(YamlNodeAccepter.class);

	protected final YamlNodeHandler<CTX> rootHandler;

	public YamlNodeAccepter(YamlNodeHandler<CTX> root) {
		this.rootHandler = root;
	}

	public void accept(InputStream in, CTX context) {
		if (LOG.isDebugEnabled()) {
			LOG.trace(Markers.BOUNDARY, Messages.ComposeNode);
		}
		Composer composer = new Composer(new ParserImpl(new StreamReader(
				new InputStreamReader(in))), new Resolver());
		Node node = composer.getSingleNode();
		this.rootHandler.handle(node, context);
	}
}
