package com.thomas.netty_async_client;

import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

public class ListenableFutureListener implements Runnable {
	ListenableFuture<Response> future;
	public ListenableFutureListener(ListenableFuture<Response> future) {
		this.future = future;
	}
	
	@Override
	public void run() {
		try {
			System.out.println(future.get());			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
