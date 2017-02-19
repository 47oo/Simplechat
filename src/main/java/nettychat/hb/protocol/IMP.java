package nettychat.hb.protocol;

public enum IMP {
	//系统消息
	SYSTEM("SYSTEM"),
	//登录命令
	LOGIN("LOGIN"),
	//登出指令
	LOGOUT("LOGOUT"),
	//聊天
	CHAT("CHAT"),
	//鲜花效果
	FLOWER("FLOWER");
	private String name;
	IMP(String name){
		this.name= name;
	}
	/**
	 * 判断是不是协议支持的命令
	 * @param msg
	 * @return
	 */
	public static boolean isIMP(String msg){
		return msg.matches("^\\[(SYSTEM|LOGIN|LOGOUT|CHAT|FLOWER)\\]");
		
	}
	public String getName(){
		return name;
	}
}

