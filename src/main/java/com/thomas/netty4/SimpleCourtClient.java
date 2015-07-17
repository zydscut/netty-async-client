package com.thomas.netty4;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class SimpleCourtClient {
	static final boolean SSL = System.getProperty("ssl") != null;
    static String HOST = System.getProperty("host", "127.0.0.1");
    static int PORT = Integer.parseInt(System.getProperty("port", "8080"));
    static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));
    static int REQUEST_SIZE = 128;

    public static void main(String[] args) throws Exception {
    	System.out.println("CONNECTION_SIZE PORT HOST");
        // Configure SSL.git
        final SslContext sslCtx;
        if (SSL) {
            sslCtx = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        if(args.length > 0) {
        	REQUEST_SIZE = Integer.valueOf(args[0]);
        }
        if(args.length > 1) {
        	PORT = Integer.valueOf(args[1]);
        }
        if(args.length > 2) {
        	HOST = args[2];
        }
        
        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .option(ChannelOption.SO_KEEPALIVE, true)
             .option(ChannelOption.TCP_NODELAY, true)
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline pipeline = ch.pipeline();
                     if (sslCtx != null) {
                    	 pipeline.addLast(sslCtx.newHandler(ch.alloc(), HOST, PORT));
                     }
                     //p.addLast(new LoggingHandler(LogLevel.INFO));
                     pipeline.addLast(new HttpResponseDecoder());
                     pipeline.addLast(new HttpRequestEncoder());
                     pipeline.addLast(new SimpleCourtClientHandler(REQUEST_SIZE));
                 }
             });

            // Start the client.
            ChannelFuture future = b.connect(HOST, PORT).sync();
            
            execute(future.channel());
            
            // Wait until the connection is closed.
            future.channel().closeFuture().sync();
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }
    
    public static void execute(final Channel channel) {
    	ScheduledExecutorService ses = Executors.newScheduledThreadPool(NumberUtils.INTEGER_ONE);
    	ses.schedule(new Callable<Object>() {
    		@Override
    		public Object call() throws Exception {
    			
    			URI uri = new URI("http://localhost:8080");
    			
    			DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
                        uri.toASCIIString());
    			//request.
                // 构建http请求
                request.headers().set(HttpHeaders.Names.HOST, "localhost");
                request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
                request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, request.content().readableBytes());
                // 发送http请求
                channel.write(request);
                channel.flush();
                channel.closeFuture().sync();
    			return StringUtils.EMPTY;
    		}
		}, 500, TimeUnit.MILLISECONDS);
    }
}
