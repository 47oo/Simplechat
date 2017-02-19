package nettychat.hb.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import nettychat.hb.server.handler.Httphandler;
import nettychat.hb.server.handler.WebSocketHandler;

/**
 * 聊天服务器
 * 
 * @author hp
 *
 */
public class ChatServer {
	// 默认端口号
	private int port = 2222;
	public ChatServer(){
		
	}
	public ChatServer(int port){
		this.port = port;
	}
	public void start(){
		//主从模式,创建主线程
		EventLoopGroup boss = new NioEventLoopGroup();
		//创建子线程,工作线程
		EventLoopGroup worker = new NioEventLoopGroup();
		//创建Netty Socket引导程序
		ServerBootstrap sb= new ServerBootstrap();
		//默认分配1024个工作线程
		sb.group(boss,worker).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024)
		.childHandler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel arg0) throws Exception {
				//获取工作流,流水线
				ChannelPipeline cp = arg0.pipeline();
				
				//========对Http协议支持
				//http请求解码
				cp.addLast(new HttpServerCodec());
				//将一个http请求或响应变成一个fullhttprequest对象
				cp.addLast(new HttpObjectAggregator(64*1024));
				//这个是用来处理文件流
				cp.addLast(new ChunkedWriteHandler());
				//处理HTTP请求的业务逻辑
				cp.addLast(new Httphandler());
				
				//==支持对webSocket协议的支持
				cp.addLast(new WebSocketServerProtocolHandler("/simplechat/ws"));
				//实现处理WebSocket逻辑的handler处理
				cp.addLast(new WebSocketHandler());
			}
		});
		
		//采用同步方式监听客户端链接
		try {
			ChannelFuture f = sb.bind(port).sync();
			System.out.println("服务端已经启动,监听端口: "+port);
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			worker.shutdownGracefully();
			boss.shutdownGracefully();
		}
	}
	public static void main(String[] args) {
		new ChatServer().start();
	}
}
