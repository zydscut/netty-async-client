package com.thomas.netty4.websocket.client;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

public class HelloWS implements Runnable {
	private Channel channel;
	public HelloWS(Channel channel) {
		this.channel = channel;
	}
	
	@Override
	public void run() {
		while(true) {
			String dateStr = DateFormatUtils.format(new Date(), "hh:mm:ss:SSS");
			String message = String.format("producer : %1s", dateStr);
//			ByteBuf buf = Unpooled.wrappedBuffer(message.getBytes());
//			channel.writeAndFlush(Unpooled.copiedBuffer(buf));
//			System.out.println(new String(Unpooled.copiedBuffer(buf).array(), CharsetUtil.UTF_8));
			WebSocketFrame frame = new TextWebSocketFrame(message);
			channel.writeAndFlush(frame);
			try {
				Thread.sleep(1000L);
			}
			catch(Exception e) {
				//do nothing
			}
		}
	}
}
