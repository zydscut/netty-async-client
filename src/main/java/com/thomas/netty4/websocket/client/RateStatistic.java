package com.thomas.netty4.websocket.client;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.AbstractScheduledService;

public class RateStatistic extends AbstractScheduledService {
	
	static final private Logger logger = LoggerFactory.getLogger(GriffinWebSocketClientHandler.class);
	
	public volatile int totalCount = 0;
	public volatile long totalSum = 0L;
	public int lastCount = 0;
	public long lastSum = 0;
    
	public void onChange(int count, int sum) {
		totalCount += count;
		totalSum += sum;
	}
	
	@Override
	protected void startUp() throws Exception {
		// TODO
	}

	@Override
	protected void shutDown() throws Exception {
		// TODO
	}
	
	@Override
	protected void runOneIteration() throws Exception {
		int ccount = totalCount;
		long csum = totalSum;
		logger.info("receive count : " + (ccount - lastCount));
		logger.info("receive sizee : " + (csum - lastSum));
		lastCount = ccount;
		lastSum = csum;
	}

	@Override
	protected Scheduler scheduler() {
		return Scheduler.newFixedRateSchedule(0, 1, TimeUnit.SECONDS);
	}
}
