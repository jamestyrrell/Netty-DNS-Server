package org.handwerkszeug.yaml;

import org.handwerkszeug.dns.Markers;
import org.handwerkszeug.dns.nls.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class SequenceHandler<CTX> extends DefaultHandler<CTX> {

	static final Logger LOG = LoggerFactory.getLogger(SequenceHandler.class);

	protected YamlNodeHandler<CTX> handler;

	public SequenceHandler(YamlNodeHandler<CTX> handler) {
		this.handler = handler;
	}

	public SequenceHandler(String name, YamlNodeHandler<CTX> handler) {
		super(name);
		this.handler = handler;
	}

	@Override
	public void handle(Node node, CTX context) {
		if (node instanceof SequenceNode) {
			SequenceNode sn = (SequenceNode) node;
			for (Node n : sn.getValue()) {
				this.handler.handle(n, context);
			}
		} else {
			LOG.debug(Markers.DETAIL, Messages.InvalidParameter, new Object[] {
					"SequenceHandler#handle", SequenceNode.class, node });
		}
	}
}