package com.thomas.netty_async_client;

import io.netty.handler.codec.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.FluentCaseInsensitiveStringsMap;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Param;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;
import org.asynchttpclient.netty.NettyAsyncHttpProvider;

public class AsyncClientHttpCourt {
//	private List<ListenableFuture<Response>> list = new ArrayList<ListenableFuture<Response>>();
	private ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
	private ScheduledExecutorService lses = Executors.newScheduledThreadPool(1);
	AsyncClientHttpCourt() throws Exception {
		try {
			for (int i = 0; i < 50; i++) {
				long delay = i * 100 + 100;
				final int index = i;
				ScheduledFuture<Object> sfuture = ses.schedule(
						new Callable<Object>() {
							@Override
							public Object call() throws Exception {
								welcomeQuery(Version.V1_0, index);
								reservationQuery(Version.V1_0, index);
								return StringUtils.EMPTY;
							}
						}, delay, TimeUnit.MILLISECONDS);
				Object xxx = sfuture.get();
				System.out.println(xxx);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// httpclient.close();
		}
	}

	public static void main(final String[] args) throws Exception {
		new AsyncClientHttpCourt();
	}

	public void query(Request request) throws Exception {
		System.out.println("Executing request " + request.getUrl());

		try {
			AsyncHttpClient client = getAsyncHttpClient(null);
			ListenableFuture<Response> lfuture = client.executeRequest(request,
					new AsyncCompletionHandlerAdapter(request));
			
			//lfuture.addListener(new ListenableFutureListener(lfuture), lses);

			//list.add(lfuture);
			//lfuture.get();
		}
		finally {
			//do nothing
		}
	}

	public void reservationQuery(Version ver, int index) throws Exception {
		String url = "http://%1s:8080/httpdemo/reservationQuery/";
		String ip = "localhost";
		url = String.format(url, ip);

		FluentCaseInsensitiveStringsMap h = new FluentCaseInsensitiveStringsMap();
		h.add("Content-Type", "application/x-www-form-urlencoded");

		List<Param> params = new ArrayList<Param>();
		Param courtName = new Param("courtName", "ten");
		Param clientType = new Param("clientType", "mobile");
		Param idx = new Param("index", String.valueOf(index));
		params.add(courtName);
		params.add(clientType);
		params.add(idx);

		// Request request = new
		// RequestBuilder("POST").setUrl(url).setHeaders(h).setFormParams(m).build();
		Request request = new RequestBuilder(HttpMethod.POST.name())
				.setUrl(url).setHeaders(h).setFormParams(params).build();
		query(request);

	}

	public void welcomeQuery(Version ver, int index) throws Exception {
		String url = "http://%1s:8080/httpdemo/welcome/";
		String ip = "localhost";
		url = String.format(url, ip);
		List<Param> params = new ArrayList<Param>();
		Param courtName = new Param("courtName", "ten");
		Param clientType = new Param("clientType", "mobile");
		Param idx = new Param("index", String.valueOf(index));
		params.add(courtName);
		params.add(clientType);
		params.add(idx);
		String query = StringUtils.join(params, "&");
		url = url + "?" + query;
		// Request request = new RequestBuilder("GET").setUrl(url).build();
		Request request = new RequestBuilder(HttpMethod.GET.name()).setUrl(url)
				.build();

		query(request);
	}

	public AsyncHttpClient getAsyncHttpClient(AsyncHttpClientConfig config) {
		return nettyProvider(config);
	}

	public AsyncHttpClient nettyProvider(AsyncHttpClientConfig config) {
		if (config == null) {
			config = new AsyncHttpClientConfig.Builder().build();
		}
		return new DefaultAsyncHttpClient(new NettyAsyncHttpProvider(config),
				config);
	}

}
