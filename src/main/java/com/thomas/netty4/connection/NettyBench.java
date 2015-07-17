package com.thomas.netty4.connection;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;
import org.asynchttpclient.netty.NettyAsyncHttpProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thomas.netty4.websocket.client.RateStatistic;

public class NettyBench {
	private final static Logger logger = LoggerFactory.getLogger(NettyBench.class);
	private static final boolean SSL = System.getProperty("ssl") != null;
    private static String URL = "127.0.0.1";
	private static URI uri = null;
    private static int CONCURRENCY = NumberUtils.INTEGER_ONE;
    private static volatile int REQUEST_SIZE = NumberUtils.INTEGER_ONE;
    private static boolean KEEPALIVED = false;
    private ExecutorService execsrv;
    
    private static Semaphore semaphore;
    
    RateStatistic rs = new RateStatistic();

    NettyBench() {
    	rs.startAsync();
    	semaphore = new Semaphore(REQUEST_SIZE);
    	execsrv = Executors.newFixedThreadPool(CONCURRENCY);
    	execute();
    }
    
    public void execute() {
    	//Request request = new RequestBuilder(HttpMethod.GET.name()).setUrl(URL).build();
    	execsrv.submit(new Callable<Object>() {
    		@Override
    		public Object call() throws Exception {
    			RequestBuilder rbuilder = new RequestBuilder(HttpMethod.GET.name());
	            rbuilder.setUrl(URL);
	            if(KEEPALIVED) {
	            	rbuilder.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
	            }
	            else {
	            	rbuilder.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
	            }
	            Request request = rbuilder.build();
	            
	            SyncBeginCompletionHandler handler = new SyncBeginCompletionHandler(execsrv, request, KEEPALIVED, semaphore);
	            handler.execute();
                return StringUtils.EMPTY;
    		}
		});
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
	
	public static void main(String[] args) throws Exception {
    	logger.info("URL REQUEST_SIZE CONCURRENCY KEEPALIVED");
        // Configure SSL.git
        final SslContext sslCtx;
        if (SSL) {
            sslCtx = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        if(args.length > 0) {
        	URL = args[0];
        	uri = new URI(URL);
        }
        if(args.length > 1) {
        	REQUEST_SIZE = Integer.valueOf(args[1]);
        }
        if(args.length > 2) {
        	CONCURRENCY = Integer.valueOf(args[2]);
        }
        if(args.length > 3) {
        	KEEPALIVED = Boolean.valueOf(args[3]);
        }
        
        new NettyBench();
    }
	
	class SyncBeginCompletionHandler extends AsyncCompletionHandler<Response> {
		AsyncHttpClient client;
		Request request;
		boolean keepalive;
		Semaphore sema;
		ExecutorService exec;
		SyncBeginCompletionHandler _handler;
		public SyncBeginCompletionHandler(ExecutorService exec, Request request, boolean keepalive, Semaphore sema) {
			_handler = this;
			this.keepalive = keepalive;
			this.request = request;
			this.sema = sema;
			this.exec = exec;
			initClient();
		}
		
		private void initClient() {
			if(keepalive) {
				client = getAsyncHttpClient(null);
			}
		}
		
		private void prepareClient() {
			if(!keepalive) {
				client = getAsyncHttpClient(null);
			}
		}
		
		private void postClient() {
			if(!keepalive) {
				if(!client.isClosed()) {
					client.close();
				}
			}
		}
		
		public void execute() throws Exception {
			sema.acquire(1);
			prepareClient();
			exec.submit(new Callable<String>() {
				@Override
				public String call() throws Exception {
					client.executeRequest(request, _handler);
					return StringUtils.EMPTY;
				}
			});
		}
		
		@Override
		public Response onCompleted(Response response)throws Exception {
			rs.onChange(1, response.getResponseBodyAsBytes().length);
			postClient();
			_handler.execute();
			return response;
		}
		
		@Override
		public void onThrowable(Throwable t) {
			super.onThrowable(t);
			logger.error(StringUtils.EMPTY, t);
			try {
				_handler.execute();
			}
			catch(Exception e) {
				logger.error(StringUtils.EMPTY, e);
			}
		}
		
		@Override
		protected void finalize() throws Throwable {
			if(! client.isClosed()) {
				client.close();
			}
			super.finalize();
		}
	}
}
