package com.thomas.netty_async_client;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;

public class AsyncCompletionHandlerAdapter extends AsyncCompletionHandler<Response> {
	Request request;
	
	public AsyncCompletionHandlerAdapter(Request request) {
		this.request = request;
	}
	
	@Override
	public Response onCompleted(Response response)throws Exception {
		try {
			String nowStr = DateFormatUtils.format(new Date(), "hh:mm:ss:SSS");
	        System.out.println("---------------" + nowStr + "---------------");
			System.out.println(request.getUrl() + "?" + StringUtils.join(request.getQueryParams(), "&"));
			System.out.println(response.getResponseBody());
			System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
		} finally {
		}
		return response;
	}
}
