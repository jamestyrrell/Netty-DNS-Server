package org.handwerkszeug.yaml;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class YamlUtil {

	public static String getStringValue(Node node) {
		if (node instanceof ScalarNode) {
			ScalarNode sn = (ScalarNode) node;
			return sn.getValue();
		}
		return null;
	}
}
