package org.handwerkszeug.yaml;

import org.yaml.snakeyaml.nodes.Node;

public interface YamlNodeHandler<CTX> {

	String getNodeName();

	void handle(Node node, CTX context);
}