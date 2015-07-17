package com.thomas.jetty9;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.msgpack.MessagePack;
import org.msgpack.type.Value;

@WebSocket(maxTextMessageSize = 268435456)
public class MyScoket {
	private static int totalCount = 0;
	private int index;
	private String id;
	protected boolean connected = false;
	private int cntcal;
	private final DateFormat dtFormat = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");
	protected CountDownLatch openLatch = new CountDownLatch(1);

	public MyScoket(String id, int cnt) {
		this.id = id;
		totalCount += 1;
		this.index = totalCount;
		this.cntcal = (cnt / 10);
		if (this.cntcal == 0) {
			this.cntcal = 1;
		}
	}

	@OnWebSocketMessage
	public void onMessage(String msg) {
		//for test only
		System.out.println(msg);
	}

	@OnWebSocketMessage
	public void onMessage(byte[] buf, int offset, int length) {
		MessagePack msgp = new MessagePack();
		try {
			Value val = msgp.read(buf);
			System.out.println(val);
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
		CountUtil.count("onMessage" + length, new String[0]);
	}

	@OnWebSocketConnect
	public void onOpen(Session session) {
		this.connected = true;
		this.openLatch.countDown();
	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		System.out.println(this.dtFormat.format(new Date(System
				.currentTimeMillis())) + " " + this.id + " onClose");
		this.connected = false;
	}

	public boolean awaitOpen(int duration, TimeUnit unit)
			throws InterruptedException {
		boolean res = this.openLatch.await(duration, unit);
		if (this.connected) {
			if ((this.index % this.cntcal == 0)
					|| (this.index == this.cntcal * 10)) {
				System.out.println(this.id + " - Connection established "
						+ this.index);
			}
		} else if ((this.index % this.cntcal == 0)
				|| (this.index == this.cntcal * 10)) {
			System.out.println(this.id
					+ " - Cannot connect to the remote server " + this.index);
		}
		return res;
	}
}
