package org.handwerkszeug.dns.conf;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.SocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.handwerkszeug.dns.Zone;
import org.handwerkszeug.util.AddressUtil;
import org.handwerkszeug.yaml.DefaultHandler;
import org.handwerkszeug.yaml.MappingHandler;
import org.handwerkszeug.yaml.YamlNodeAccepter;
import org.handwerkszeug.yaml.YamlNodeHandler;
import org.handwerkszeug.yaml.YamlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.nodes.Node;

import werkzeugkasten.common.util.Streams;

public class ServerConfigurationImpl implements ServerConfiguration {

	static final Logger LOG = LoggerFactory
			.getLogger(ServerConfigurationImpl.class);

	protected Set<SocketAddress> bindingHosts = new HashSet<SocketAddress>();

	protected Set<SocketAddress> forwarders = new HashSet<SocketAddress>();

	protected List<Zone> zones = new ArrayList<Zone>();

	protected int threadPoolSize = 10;

	public ServerConfigurationImpl() {
	}

	public void load(final URL url) {
		new Streams.using<BufferedInputStream, Exception>() {
			@Override
			public BufferedInputStream open() throws Exception {
				return new BufferedInputStream(url.openStream());
			}

			@Override
			public void handle(BufferedInputStream stream) throws Exception {
				load(stream);
			}

			@Override
			public void happen(Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	public void load(InputStream in) {
		YamlNodeHandler<ServerConfiguration> root = createRootHandler();
		YamlNodeAccepter<ServerConfiguration> accepter = new YamlNodeAccepter<ServerConfiguration>(
				root);
		accepter.accept(in, this);
	}

	protected YamlNodeHandler<ServerConfiguration> createRootHandler() {
		MappingHandler<ServerConfiguration> root = new MappingHandler<ServerConfiguration>();
		final NodeToAddress node2addr = new NodeToAddress();
		root.add(new DefaultHandler<ServerConfiguration>("bindingHosts") {
			@Override
			public void handle(Node node, ServerConfiguration context) {
				node2addr.handle(node, context.getBindingHosts());
			}
		});
		root.add(new NodeToForwarders(node2addr));
		// TODO logging
		root.add(new DefaultHandler<ServerConfiguration>("threadPoolSize") {
			@Override
			public void handle(Node node, ServerConfiguration conf) {
				String value = YamlUtil.getStringValue(node);
				conf.setThreadPoolSize(AddressUtil.toInt(value, 10));
			}
		});
		return root;
	}

	@Override
    public Set<SocketAddress> getBindingHosts() {
		return this.bindingHosts;
	}

	@Override
    public int getThreadPoolSize() {
		return this.threadPoolSize;
	}

	@Override
    public Set<SocketAddress> getForwarders() {
		return this.forwarders;
	}

    @Override
    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }
}
