package nettychat.hb.protocol;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class IMDecoder extends ByteToMessageDecoder {
	// 正则解析协议内容
	private Pattern pattern = Pattern.compile("^\\[(.*)\\](\\s\\-\\s(.*))?");

	/**
	 * 实现反序列化,解码过程
	 */
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		final int length = in.readableBytes();
		final byte[] arr = new byte[length];
		String content = new String(arr, in.readerIndex(), length);
		if (!(content == null || "".equals(content.trim()))) {
			if (!IMP.isIMP(content)) {
				ctx.channel().pipeline().remove(this);
				return;
			}
		}

		in.getBytes(in.readerIndex(), arr, 0, length);
		out.add(new MessagePack().read(arr, IMMessage.class));
		in.clear();
	}

	public IMMessage decode(String msg){
		if(msg==null||"".equals(msg.trim())){
			return null;
		}
		Matcher m = pattern.matcher(msg);
		
		String header = ""; //消息头
		String content = ""; //消息体
		if(m.matches()){
			header = m.group(1);
			content = m.group(3);
		}
		
		String [] headers = header.split("\\]\\[");
		
		//获取命令发送时间
		long time =Long.parseLong(headers[1]);
		//获取昵称
		String nickName = headers[2];
		nickName = nickName.length() < 10 ? nickName : nickName.substring(0, 9);
		String cmd = headers[0];
		if(IMP.LOGIN.getName().equals(cmd) ||
				   IMP.LOGOUT.getName().equals(cmd) ||
				   IMP.FLOWER.getName().equals(cmd)){
					IMMessage im = new IMMessage();
					im.setCmd(cmd);
					im.setTime(time);
					im.setSender(nickName);
					return im;
				}else if(IMP.CHAT.getName().equals(cmd) || 
						 IMP.SYSTEM.getName().equals(cmd)){
					IMMessage im = new IMMessage();
					im.setCmd(cmd);
					im.setTime(time);
					im.setSender(nickName);
					im.setContent(content);
					return im;
				}else{
					return null;
				}
	}
}
