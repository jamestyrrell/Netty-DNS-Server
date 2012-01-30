package org.handwerkszeug.yaml;

import java.util.HashMap;
import java.util.Map;

import org.handwerkszeug.dns.Markers;
import org.handwerkszeug.dns.nls.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;

public class MappingHandler<CTX> extends DefaultHandler<CTX> {

	static final Logger LOG = LoggerFactory.getLogger(MappingHandler.class);

	Map<String, YamlNodeHandler<CTX>> handlers = new HashMap<String, YamlNodeHandler<CTX>>();

	public MappingHandler() {
	}

	public MappingHandler(String name) {
		super(name);
	}

	public void add(YamlNodeHandler<CTX> handler) {
		if (handler != null) {
			this.handlers.put(handler.getNodeName().toLowerCase(), handler);
		}
	}

	@Override
	public void handle(Node node, CTX context) {
		if (node instanceof MappingNode) {
			MappingNode mn = (MappingNode) node;
			for (NodeTuple nt : mn.getValue()) {
				String key = YamlUtil.getStringValue(nt.getKeyNode());
				YamlNodeHandler<CTX> h = this.handlers.get(key.toLowerCase());
				if (h != null) {
					Node value = nt.getValueNode();
					h.handle(value, context);
				} else {
					LOG.debug(Markers.DETAIL, Messages.UnsupportedAttribute,
							key);
				}
			}
		} else {
			LOG.debug(Markers.DETAIL, Messages.InvalidParameter, new Object[] {
					"MappingHandler#handle", MappingNode.class, node });
		}
	}
}