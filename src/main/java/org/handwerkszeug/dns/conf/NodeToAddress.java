package org.handwerkszeug.dns.conf;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.handwerkszeug.dns.Constants;
import org.handwerkszeug.dns.Markers;
import org.handwerkszeug.dns.nls.Messages;
import org.handwerkszeug.util.AddressUtil;
import org.handwerkszeug.yaml.DefaultHandler;
import org.handwerkszeug.yaml.SequenceHandler;
import org.handwerkszeug.yaml.YamlNodeHandler;
import org.handwerkszeug.yaml.YamlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

import werkzeugkasten.common.util.StringUtil;

/**
 * <p>
 * convert Node to Address
 * </p>
 * you can use following examples.
 * <p>
 * <blockquote>
 * 
 * <pre>
 * #sequence to addresses
 *   - address : 127.0.0.1
 *     port : 53
 *   - 127.0.0.1 : 53
 *   - 127.0.0.1:53
 *  #mapping to address
 *   127.0.0.1 : 53
 *   address : 127.0.0.1
 *   port : 53
 *  #scalar to address
 *    127.0.0.1:53
 * </pre>
 * 
 * </blockquote>
 * </p>
 * 
 * @author taichi
 */
public class NodeToAddress extends DefaultHandler<Set<SocketAddress>> {

	static final Logger LOG = LoggerFactory.getLogger(NodeToAddress.class);
	Map<NodeId, YamlNodeHandler<Set<SocketAddress>>> converters = new HashMap<NodeId, YamlNodeHandler<Set<SocketAddress>>>();

	public NodeToAddress() {
		this.converters.put(NodeId.scalar, new ScalarToAddress());
		this.converters.put(NodeId.mapping, new MappingToAddress());
		this.converters.put(NodeId.sequence,
				new SequenceHandler<Set<SocketAddress>>(this));
	}

	@Override
	public void handle(Node node, Set<SocketAddress> context) {
		YamlNodeHandler<Set<SocketAddress>> handler = this.converters.get(node
				.getNodeId());
		if (handler == null) {
			LOG.debug(Markers.DETAIL, Messages.UnsupportedNode, node);
		} else {
			handler.handle(node, context);
		}
	}

	static class ScalarToAddress extends DefaultHandler<Set<SocketAddress>> {
		protected int defaultPort = Constants.DEFAULT_PORT;

		public ScalarToAddress() {
		}

		public ScalarToAddress(int port) {
			this.defaultPort = port;
		}

		@Override
		public void handle(Node node, Set<SocketAddress> context) {
			if (node instanceof ScalarNode) {
				ScalarNode sn = (ScalarNode) node;
				SocketAddress addr = AddressUtil.convertTo(sn.getValue(),
						this.defaultPort);
				if (addr != null) {
					context.add(addr);
				} else {
					LOG.debug(Markers.DETAIL, Messages.InvalidAddressFormat,
							sn.getValue());
				}
			}
		}
	}

	static class MappingToAddress extends DefaultHandler<Set<SocketAddress>> {

		protected int defaultPort = Constants.DEFAULT_PORT;

		public MappingToAddress() {
		}

		public MappingToAddress(int port) {
			this.defaultPort = port;
		}

		@Override
		public void handle(Node node, Set<SocketAddress> context) {
			if (node instanceof MappingNode) {
				MappingNode mn = (MappingNode) node;
				String[] ary = new String[2];
				for (NodeTuple nt : mn.getValue()) {
					String key = YamlUtil.getStringValue(nt.getKeyNode());
					String value = YamlUtil.getStringValue(nt.getValueNode());
					if ("address".equalsIgnoreCase(key)) {
						ary[0] = value;
					} else if ("port".equalsIgnoreCase(key)) {
						ary[1] = value;
					} else {
						ary[0] = key;
						ary[1] = value;
						break;
					}
				}
				if (StringUtil.isEmpty(ary[0]) == false) {
					SocketAddress addr = AddressUtil.convertTo(ary[0],
							AddressUtil.toInt(ary[1], this.defaultPort));
					if (addr != null) {
						context.add(addr);
					} else {
						LOG.debug(Markers.DETAIL,
								Messages.InvalidAddressFormat, mn.getValue());
					}
				}
			}
		}
	}
}
