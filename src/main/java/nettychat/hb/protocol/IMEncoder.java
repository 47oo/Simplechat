package nettychat.hb.protocol;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * IM协议编码器,服务端向客户端输出,要编码
 * 
 * @author hp
 *
 */
public class IMEncoder extends MessageToByteEncoder<IMMessage> {
	// 实现序列化过程
	@Override
	protected void encode(ChannelHandlerContext ctx, IMMessage msg, ByteBuf out) throws Exception {
		out.writeBytes(new MessagePack().write(msg));
	}

	/**
	 * 把IMMessage对象解析称为我们IMP协议字符串,方便输出到客户端
	 * 
	 * @param msg
	 * @return
	 */
	public String encode(IMMessage msg) {
		if (msg == null) {
			return "";
		}
		String prex = "[" + msg.getCmd() + "][" + msg.getTime() + "]";
		if (IMP.LOGIN.getName().equals(msg.getCmd()) || IMP.CHAT.getName().equals(msg.getCmd())
				|| IMP.FLOWER.getName().equals(msg.getCmd()) || IMP.LOGOUT.getName().equals(msg.getCmd())) {
			prex += ("[" + msg.getSender() + "]");
		} else if (IMP.SYSTEM.getName().equals(msg.getCmd())) {
			prex += ("[" + msg.getOnline() + "]");
		}
		if (!(msg.getContent() == null || "".equals(msg.getContent().trim()))) {
			prex += (" - " + msg.getContent());
		}
		return prex;
	}
}
