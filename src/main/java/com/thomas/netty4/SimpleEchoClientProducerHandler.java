/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.thomas.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * Handler implementation for the echo client.  It initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 */
public class SimpleEchoClientProducerHandler extends ChannelInboundHandlerAdapter {
	public static Map<Integer, Integer> messages = new HashMap<Integer, Integer>();
	public static long begin = System.currentTimeMillis();
	public static long last = System.currentTimeMillis();
	public static long response = 0;
    private ByteBuf firstMessage;

    public ByteBuf genMessage() {
    	firstMessage = Unpooled.buffer(SimpleEchoClient.SIZE);
        for (int i = 0; i < firstMessage.capacity(); i ++) {
            firstMessage.writeByte((byte) i);
        }
        return firstMessage;
    }
    
    /**
     * Creates a client-side handler.
     */
    public SimpleEchoClientProducerHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        //ctx.writeAndFlush(firstMessage);
    	(new Thread(new Hello(ctx.channel()))).start();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //ctx.write(msg);
        //int port = ((InetSocketAddress)ctx.channel().localAddress()).getPort();
//        Integer number = messages.get(port);
//        if(number != null) {
//        	messages.put(port, number ++);
//        }
        response ++;
        long current = System.currentTimeMillis();
        if(current - last > 1000) {
        	last = current;
        	String print = String.format("%1s sec %2s response", (current - begin)/1000, response);
        	System.out.println(print);
        }
        
        //System.out.println("receipt:" + String.valueOf(msg));
        //TODO statistic
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
       ctx.flush();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    	super.channelUnregistered(ctx);
    	System.out.println("writability unregistered");
    }
    
    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx)
    		throws Exception {
    	super.channelWritabilityChanged(ctx);
    	System.out.println("writability changed");
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	super.channelInactive(ctx);
    	System.out.println("writability inactive");
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
    
    class Hello implements Runnable {
    	private Channel channel;
    	public Hello(Channel channel) {
    		this.channel = channel;
    	}
    	
    	@Override
		public void run() {
    		while(true) {
    			String dateStr = DateFormatUtils.format(new Date(), "hh:mm:ss:SSS");
    			String message = String.format("producer : %1s", dateStr);
    			ByteBuf buf = Unpooled.wrappedBuffer(message.getBytes());
    			channel.writeAndFlush(Unpooled.copiedBuffer(buf));
    			System.out.println(new String(Unpooled.copiedBuffer(buf).array(), CharsetUtil.UTF_8));
    			try {
    				Thread.sleep(1000L);
    			}
    			catch(Exception e) {
    				//do nothing
    			}
    		}
		}
	};
}
