package org.handwerkszeug.dns.server;

import java.net.SocketAddress;

import org.handwerkszeug.dns.DNSMessage;
import org.handwerkszeug.dns.conf.ServerConfiguration;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// this is a sandboxing code. not for testing.
public class ProxyHandler extends SimpleChannelUpstreamHandler {

	static final Logger LOG = LoggerFactory.getLogger(ForwardingHandler.class);

	protected ServerConfiguration config;
	protected ChannelFactory clientChannelFactory;

	protected Channel outboundChannel;

	public ProxyHandler(ServerConfiguration config,
			ChannelFactory clientChannelFactory) {
		super();
		this.config = config;
		this.clientChannelFactory = clientChannelFactory;
	}

	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		LOG.info("ProxyHandler#channelOpen");
		// Suspend incoming traffic until connected to the remote host.
		final Channel inboundChannel = e.getChannel();
		inboundChannel.setReadable(false);

		// Start the connection attempt.
		ClientBootstrap cb = new ClientBootstrap(this.clientChannelFactory);
		cb.setOption("broadcast", "false");
		// cb.setOption("sendBufferSize", 512);
		// cb.setOption("receiveBufferSize", 512);
		cb.getPipeline()
				.addLast("handler", new OutboundHandler(inboundChannel));
		ChannelFuture f = cb.connect(this.config.getForwarders().iterator()
				.next());

		this.outboundChannel = f.getChannel();
		f.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				if (future.isSuccess()) {
					// Connection attempt succeeded:
					// Begin to accept incoming traffic.
					inboundChannel.setReadable(true);
				} else {
					// Close the connection if the connection attempt has
					// failed.
					inboundChannel.close();
				}
			}
		});
	}

	volatile DNSMessage original = null;
	volatile Channel inboundChannel = null;
	volatile SocketAddress inboundAddr = null;

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		LOG.info("ProxyHandler#messageReceived");
		ChannelBuffer origBuffer = (ChannelBuffer) e.getMessage();
		this.original = new DNSMessage(origBuffer);
		this.inboundAddr = e.getRemoteAddress();
		this.inboundChannel = ctx.getChannel();
		LOG.info(this.original.header().toString());
		Channel ch = ctx.getChannel();
		LOG.info("addr : {} {}", e.getRemoteAddress(), ch.getLocalAddress());
		LOG.info("{}", this.original.header().toString());

		ChannelBuffer buffer = ChannelBuffers.buffer(512);
		DNSMessage newone = new DNSMessage(this.original);
		newone.write(buffer);
		this.outboundChannel.write(buffer);

		// newone.header().qr(false);
		// newone.write(buffer);
		// this.inboundChannel.write(buffer, this.inboundAddr);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getCause().printStackTrace();
		closeOnFlush(e.getChannel());
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		if (this.outboundChannel != null) {
			closeOnFlush(this.outboundChannel);
		}
	}

	private class OutboundHandler extends SimpleChannelUpstreamHandler {
		private final Channel inboundChannel;

		OutboundHandler(Channel inboundChannel) {
			this.inboundChannel = inboundChannel;
		}

		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
				throws Exception {
			LOG.info("OutboundHandler#messageReceived");
			DNSMessage original = ProxyHandler.this.original;
			ChannelBuffer buffer = (ChannelBuffer) e.getMessage();
			DNSMessage msg = new DNSMessage(buffer);
			LOG.info(msg.header().toString());
			msg.header().id(original.header().id());
			LOG.info(msg.header().toString());

			ChannelBuffer newone = ChannelBuffers.buffer(512);
			msg.write(newone);

			LOG.info("isWritable :{}", this.inboundChannel.isWritable());
			LOG.info("response : {} {}", ProxyHandler.this.inboundAddr,
					this.inboundChannel.getLocalAddress());
			newone.resetReaderIndex();
			LOG.info("readble bytes {} {} {}",
					new Object[] { newone.readableBytes(), msg.messageSize(),
							original.messageSize() });
			ProxyHandler.this.inboundChannel.write(newone,
					ProxyHandler.this.inboundAddr);
		}

		@Override
		public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
				throws Exception {
			closeOnFlush(this.inboundChannel);
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
				throws Exception {
			e.getCause().printStackTrace();
			closeOnFlush(e.getChannel());
		}
	}

	static void closeOnFlush(Channel ch) {
		if (ch.isConnected()) {
			ch.write(ChannelBuffers.EMPTY_BUFFER).addListener(
					ChannelFutureListener.CLOSE);
		}
	}

}
