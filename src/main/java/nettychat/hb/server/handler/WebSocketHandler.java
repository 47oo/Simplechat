package nettychat.hb.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import nettychat.hb.processor.MsgProcessor;

public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
	private MsgProcessor processor = new MsgProcessor();
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
		processor.sendMsg(ctx.channel(), msg.text());
	}

}
