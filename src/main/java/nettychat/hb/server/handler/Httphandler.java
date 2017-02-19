package nettychat.hb.server.handler;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedNioFile;

public class Httphandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	// class路径
	private URL basepath = getClass().getProtectionDomain().getCodeSource().getLocation();
	// 静态文件的存放目录
	private final String webroot = "webroot";

	@SuppressWarnings("deprecation")
	@Override
	protected void channelRead0(ChannelHandlerContext arg0, FullHttpRequest arg1) throws Exception {
		// 一个URI对应一个资源,只解析静态资源
		String uri = arg1.getUri();
		String resource = uri.equals("/simplechat") ? "chat.html" : uri;
		RandomAccessFile file = null;
		try {
			file = new RandomAccessFile(getResource(resource), "r");
		} catch (Exception e) {
			// 继续下一次请求，服务端不报错
			arg0.fireChannelRead(arg1.retain());
			return;
		}

		HttpResponse response = new DefaultHttpResponse(arg1.getProtocolVersion(), HttpResponseStatus.OK);
		String contextType = "text/html;";
		if (uri.endsWith(".css")) {
			contextType = "text/css;";
		} else if (uri.endsWith(".js")) {
			contextType = "text/javascript;";
		} else if (uri.toLowerCase().matches("(jpg|png|gif|ico)$")) {
			String ext = uri.substring(uri.lastIndexOf("."));
			contextType = "image/" + ext + ";";
		}
		response.headers().set(HttpHeaders.Names.CONTENT_TYPE, contextType + "charset=utf-8");
		boolean keepAlive = HttpHeaders.isKeepAlive(arg1);
		if (keepAlive) {
			response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length());
			// 响应头添加为长连接信息
			response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		}
		arg0.write(response);
		arg0.write(new ChunkedNioFile(file.getChannel()));

		// 如果不是长连接，然后文件也全部输出完毕了，那么就关闭连接
		ChannelFuture future = arg0.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
		if (!keepAlive) {
			future.addListener(ChannelFutureListener.CLOSE);
		}

		file.close();
	}

	/**
	 * 从磁盘中找到一个静态资源
	 * 
	 * @param uri
	 * @return
	 * @throws URISyntaxException
	 */
	private File getResource(String uri) throws URISyntaxException {
		String path = basepath.toURI() + webroot + "/" + uri;
		path = !path.contains("file:") ? path : path.substring(5);
		path = path.replaceAll("//", "/");
		return new File(path);
	}

}
