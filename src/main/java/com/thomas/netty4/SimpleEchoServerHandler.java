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
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Handler implementation for the echo server.
 */
@Sharable
public class SimpleEchoServerHandler extends ChannelInboundHandlerAdapter {
	public static Map<String, Channel> channels = new HashMap<String, Channel>();
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    	ByteBuf buf = ((ByteBuf)msg);
    	//ctx.writeAndFlush(Unpooled.copiedBuffer(buf));
    	//System.out.println("exchange : " + Unpooled.copiedBuffer(bbuf));
    	String message = new String(Unpooled.copiedBuffer(buf).array(), CharsetUtil.UTF_8);
    	System.out.println("exchange:" + message);
    	ctx.writeAndFlush(Unpooled.copiedBuffer(buf));
    	
    	if(message.startsWith("producer")) {
    		Iterator<Entry<String, Channel>> citerator = channels.entrySet().iterator();
    		while(citerator.hasNext()) {
    			Entry<String, Channel> hpch = citerator.next();
    			String hp = hpch.getKey();
    			//Channel channel = channels.get(hp);
    			Channel channel = hpch.getValue();
    			
    			if(channel == null) {
    				channels.remove(hp);
    				System.out.println("remove damn null channel : " + hp);
    				System.out.println("connection leave, " + channels.size() + " connections counted");
    			}
    			else if(! channel.isActive()) {
    				channels.remove(hp);
    				System.out.println("remove damn dead channel : " + hp);
    				System.out.println("connection leave, " + channels.size() + " connections counted");
    			}
    			else {
//    				int random = RandomUtils.nextInt(0, 3);
//    				if(random == 0) {
//    					channel.writeAndFlush(Unpooled.copiedBuffer(buf));
    					//soon run out of memory
//    					channel.write(Unpooled.copiedBuffer(buf));
//    				}
    				channel.writeAndFlush(Unpooled.copiedBuffer(buf));
    				//soon run out of memory
//    				channel.write(Unpooled.copiedBuffer(buf));
    			}
    		}
    	}
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	//int port = ((InetSocketAddress)ctx.channel().localAddress()).getPort();
    	InetSocketAddress address = (InetSocketAddress)ctx.channel().remoteAddress();
    	String host = address.getHostName();
    	int port = address.getPort();
    	channels.put(host + ":" + port, ctx.channel());
    	System.out.println("connection active, " + channels.size() + " connections counted");
    }
    
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    	//int port = ((InetSocketAddress)ctx.channel().localAddress()).getPort();
    	InetSocketAddress address = (InetSocketAddress)ctx.channel().remoteAddress();
    	String host = address.getHostName();
    	int port = address.getPort();
    	channels.remove(host + ":" + port);
    	System.out.println("connection leave, " + channels.size() + " connections counted");
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        //cause.printStackTrace();
        ctx.close();
    }
}
