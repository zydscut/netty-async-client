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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler implementation for the echo client.  It initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 */
public class SimpleEchoClientHandler extends ChannelInboundHandlerAdapter {
	public static Map<Integer, Integer> messages = new HashMap<Integer, Integer>();
	public static long begin = System.currentTimeMillis();
	public static long last = System.currentTimeMillis();
	public static long response = 0;
    //private final ByteBuf firstMessage;

    /**
     * Creates a client-side handler.
     */
    public SimpleEchoClientHandler() {
        //firstMessage = Unpooled.buffer(SimpleEchoClient.SIZE);
//        for (int i = 0; i < firstMessage.capacity(); i ++) {
//            firstMessage.writeByte((byte) i);
//        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        //ctx.writeAndFlush(firstMessage);
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
        //ByteBuf buf = ((ByteBuf)msg);
        ////System.out.println("consume : " + Unpooled.copiedBuffer(bbuf));
        //System.out.println("consume : " + new String(Unpooled.copiedBuffer(buf).array(), CharsetUtil.UTF_8));
        //TODO statistic
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
       ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
