package org.handwerkszeug.dns.server;

import org.handwerkszeug.dns.conf.ServerConfiguration;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

public class DNSServerPipelineFactory implements ChannelPipelineFactory {

	protected ServerConfiguration config;
	protected ChannelFactory clientChannelFactory;

	protected DNSMessageDecoder decoder = new DNSMessageDecoder();

	public DNSServerPipelineFactory(ServerConfiguration config,
			ChannelFactory clientChannelFactory) {
		this.config = config;
		this.clientChannelFactory = clientChannelFactory;
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline result = Channels.pipeline();
		result.addLast("decoder", this.decoder);
		result.addLast("fowarder", new ForwardingHandler(this.config,
				this.clientChannelFactory));
		return result;
	}

}
