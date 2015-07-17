package com.thomas.netty4;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class SimpleEchoClient {
	static final boolean SSL = System.getProperty("ssl") != null;
    static String HOST = System.getProperty("host", "127.0.0.1");
    static int PORT = Integer.parseInt(System.getProperty("port", "9007"));
    static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));
    static int CONNECTION_SIZE = 1024;

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
        	CONNECTION_SIZE = Integer.valueOf(args[0]);
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
             .option(ChannelOption.TCP_NODELAY, true)
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline p = ch.pipeline();
                     if (sslCtx != null) {
                         p.addLast(sslCtx.newHandler(ch.alloc(), HOST, PORT));
                     }
                     //p.addLast(new LoggingHandler(LogLevel.INFO));
                     p.addLast(new SimpleEchoClientHandler());
                 }
             });

            // Start the client.
            ChannelFuture f = b.connect(HOST, PORT).sync();
            
            int rest = 0;
            
            for(int i = 0; i < CONNECTION_SIZE; i ++) {
            	rest ++;
            	//b.connect(HOST, PORT).sync();
            	b.connect(HOST, PORT);
            	if(rest > 500) {
            		Thread.sleep(1000L);
            		rest = 0;
            	}
            }
            
            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }
}
