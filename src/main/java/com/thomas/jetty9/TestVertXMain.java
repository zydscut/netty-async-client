package com.thomas.jetty9;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.websocket.client.WebSocketClient;

public class TestVertXMain {
	public static int DEFAULT_CONNECTION_TIMEOUT = 20000;

	public static void main(String[] argvs) {
		WebSocketClient webSocketClient = new WebSocketClient();
		int port = 0;
		String ip = null;
		int cnt = 0;
		try {
			ip = argvs[0];
			port = Integer.parseInt(argvs[1]);
			cnt = Integer.parseInt(argvs[2]);

			System.out.println(ip + " " + port + " " + cnt);
		} catch (Exception e) {
			System.out
					.println("Usage: java -jar  TestPush.jar <IP> <Port> <Cnt>");
			System.out.println("   IP:Vertx IP Address");
			System.out.println("   Port:Vertx port");
			System.out.println("   Cnt:WebScoket connection count");
			System.exit(1);
		}
		for (int i = 0; i < cnt; i++) {
			MyScoket socket = new MyScoket("Socket" + i, cnt);
			try {
				webSocketClient.start();
				URI uri = new URI("ws", null, ip, port,
						"/websocket/1/11F1AF738D1E8148E0AE0CB70E842F0D", null,
						null);

				webSocketClient.connect(socket, uri);

				socket.awaitOpen(DEFAULT_CONNECTION_TIMEOUT,
						TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
