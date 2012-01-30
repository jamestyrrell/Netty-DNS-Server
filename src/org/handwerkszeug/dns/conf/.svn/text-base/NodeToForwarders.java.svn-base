package org.handwerkszeug.dns.conf;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.handwerkszeug.dns.Constants;
import org.handwerkszeug.dns.Markers;
import org.handwerkszeug.dns.NameServerContainer;
import org.handwerkszeug.dns.NameServerContainerProvider;
import org.handwerkszeug.dns.nls.Messages;
import org.handwerkszeug.yaml.DefaultHandler;
import org.handwerkszeug.yaml.SequenceHandler;
import org.handwerkszeug.yaml.YamlNodeHandler;
import org.handwerkszeug.yaml.YamlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;

import werkzeugkasten.common.util.StringUtil;

public class NodeToForwarders extends DefaultHandler<ServerConfiguration> {

	static final Logger LOG = LoggerFactory.getLogger(NodeToForwarders.class);

	protected NodeToAddress nodeToAddress;
	protected Map<NodeId, YamlNodeHandler<ServerConfiguration>> converters = new HashMap<NodeId, YamlNodeHandler<ServerConfiguration>>();

	public NodeToForwarders(NodeToAddress nodeToAddress) {
		super("forwarders");
		this.converters.put(NodeId.scalar,
				new ScalarToForwarders(nodeToAddress));
		this.converters.put(NodeId.sequence,
				new SequenceHandler<ServerConfiguration>(this));
		this.converters.put(NodeId.mapping, new MappingToForwarders(
				nodeToAddress));
	}

	@Override
	public void handle(Node node, ServerConfiguration context) {
		YamlNodeHandler<ServerConfiguration> handler = this.converters.get(node
				.getNodeId());
		if (handler == null) {
			LOG.debug(Markers.DETAIL, Messages.UnsupportedNode, node);
		} else {
			handler.handle(node, context);
		}
	}

	static class MappingToForwarders extends
			DefaultHandler<ServerConfiguration> {
		NodeToAddress nodeToAddress;

		public MappingToForwarders(NodeToAddress nodeToAddress) {
			this.nodeToAddress = nodeToAddress;
		}

		@Override
		public void handle(Node node, ServerConfiguration context) {
			this.nodeToAddress.handle(node, context.getForwarders());
		}
	}

	static class ScalarToForwarders extends DefaultHandler<ServerConfiguration> {
		NodeToAddress nodeToAddress;
		Pattern isAutoDetect = Pattern.compile("auto[_]?detect",
				Pattern.CASE_INSENSITIVE);

		public ScalarToForwarders(NodeToAddress nodeToAddress) {
			this.nodeToAddress = nodeToAddress;
		}

		@Override
		public void handle(Node node, ServerConfiguration context) {
			String value = YamlUtil.getStringValue(node);
			if ((StringUtil.isEmpty(value) == false)
					&& this.isAutoDetect.matcher(value).matches()) {
				NameServerContainerProvider provider = new NameServerContainerProvider();
				provider.initialize();
				NameServerContainer container = provider.getContainer();
				container.initialize();
				for (String s : container.nameservers()) {
					LOG.info(Markers.BOUNDARY,
							Messages.DetectedForwardingServer, s);
					context.forwarders.add(new InetSocketAddress(s,
							Constants.DEFAULT_PORT));
				}
			} else {
				this.nodeToAddress.handle(node, context.getForwarders());
			}
		}
	}
}
