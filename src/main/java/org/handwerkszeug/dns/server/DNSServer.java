package org.handwerkszeug.dns.server;

import java.io.File;
import java.net.MalformedURLException;
import java.net.SocketAddress;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.handwerkszeug.dns.Markers;
import org.handwerkszeug.dns.conf.ServerConfiguration;
import org.handwerkszeug.dns.conf.ServerConfigurationImpl;
import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.jboss.netty.util.ExternalResourceReleasable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import werkzeugkasten.common.util.Disposable;
import werkzeugkasten.common.util.Initializable;

public class DNSServer implements Initializable, Disposable {

	protected static Logger LOG = LoggerFactory.getLogger(DNSServer.class);

	protected ServerConfiguration config;
	protected ChannelFactory serverChannelFactory;
	protected ChannelFactory clientChannelFactory;
	protected ConnectionlessBootstrap bootstrap;
	protected ChannelGroup group;

	public static void main(String[] args) throws Exception {
		ServerConfiguration conf = parseArgs(args);
		final DNSServer server = new DNSServer(conf);
		server.initialize();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				server.dispose();
			}
		});

		server.process();
	}

	public static ServerConfiguration parseArgs(String[] args) throws Exception {
		ServerConfigurationImpl config = new ServerConfigurationImpl();
		URL from = null;
		if ((args != null) && (0 < args.length)) {
			from = readFrom(args[0]);
		}
		if (from == null) {
			from = readFrom("named.yml");
		}
		if (from == null) {
			LOG.info("read named.default.yml from ClassLoader");
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			from = cl.getResource("named.default.yml");
		}
		if (from == null) {
			throw new IllegalStateException("configuration file is not found.");
		}
		config.load(from);
		return config;
	}

	public static URL readFrom(String path) throws MalformedURLException {
		File f = new File(path);
		LOG.info("read from {}", f.getAbsolutePath());
		if (f.exists()) {
			if (f.canRead()) {
				return f.toURI().toURL();
			} else {
				LOG.info("{} cannot read", f.getAbsolutePath());
			}
		} else {
			LOG.info("{} is not exists", f.getAbsolutePath());
		}
		return null;
	}

	public DNSServer(ServerConfiguration config) {
		this.config = config;
	}

	@Override
	public void initialize() {
		LOG.debug(Markers.LIFECYCLE, "initialize server");
		ExecutorService executor = Executors.newFixedThreadPool(this.config
				.getThreadPoolSize());
		// TODO need TCP?
		this.clientChannelFactory = new NioDatagramChannelFactory(executor);
		// TODO TCP and/or UDP
		this.serverChannelFactory = new NioDatagramChannelFactory(executor);
		ChannelPipelineFactory pipelineFactory = new DNSServerPipelineFactory(
				this.config, this.clientChannelFactory);

		this.bootstrap = new ConnectionlessBootstrap(this.serverChannelFactory);
		this.bootstrap.setPipelineFactory(pipelineFactory);

		this.group = new DefaultChannelGroup();
	}

	public void process() {
		for (SocketAddress sa : this.config.getBindingHosts()) {
			LOG.info(Markers.BOUNDARY, "binding {}", sa);
			this.group.add(this.bootstrap.bind(sa));
		}
	}

	@Override
	public void dispose() {
		try {
			this.group.close().awaitUninterruptibly();
		} finally {
			dispose(this.clientChannelFactory);
			dispose(this.serverChannelFactory);
		}
	}

	protected void dispose(ExternalResourceReleasable releasable) {
		try {
			releasable.releaseExternalResources();
		} catch (Exception e) {
			LOG.error(e.getLocalizedMessage(), e);
		}
	}
}
