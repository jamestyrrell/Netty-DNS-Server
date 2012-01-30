package org.handwerkszeug.dns.server;

import org.handwerkszeug.dns.DNSMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

@Sharable
public class DNSMessageDecoder extends OneToOneDecoder {

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		if ((msg instanceof ChannelBuffer) == false) {
			return msg;

		}
		return new DNSMessage(ChannelBuffer.class.cast(msg));
	}

}
