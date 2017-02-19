package nettychat.hb.protocol;

import org.msgpack.annotation.Message;

@Message
public class IMMessage {
	
	private String addr;//ip地址级端口
	private String cmd;//命令类型,如[LOGIN]
	private long time;//命令发送时间
	private String sender;//命令发送人
	private String receiver;//命令接受人
	private int online;//当前在线人数
	private String content;//聊天内容
	public IMMessage(String addr, String cmd, long time, String sender, String receiver, int online,String content) {
		super();
		this.addr = addr;
		this.cmd = cmd;
		this.time = time;
		this.sender = sender;
		this.receiver = receiver;
		this.online = online;
		this.content=content;
	}
	public IMMessage() {
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public int getOnline() {
		return online;
	}
	public void setOnline(int online) {
		this.online = online;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
