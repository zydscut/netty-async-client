package com.thomas.jetty9;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CountUtil {
	private static Map<String, CountInfo> map = new HashMap<String, CountInfo>();

	private static CountInfo getCalcInfo(String key) {
		CountInfo info = null;
		synchronized (map) {
			info = (CountInfo) map.get(key);
			if (info == null) {
				info = new CountInfo();
				map.put(key, info);
			}
		}
		return info;
	}

	public static void startCount(String key) {
		CountInfo info = getCalcInfo(key);
		synchronized (info) {
			info.lastTime1.set(Long.valueOf(System.nanoTime()));
		}
	}

	public static void endCount(String key, String... args) {
		CountInfo info = getCalcInfo(key);
		synchronized (info) {
			if (info.lastTime1.get() == null) {
				return;
			}
			long lPeriod = System.nanoTime() - info.initLastTime1;
			long lPeriod1 = System.nanoTime()
					- ((Long) info.lastTime1.get()).longValue();
			if (lPeriod1 > info.maxTime1) {
				info.maxTime1 = lPeriod1;
			}
			if (lPeriod1 < info.minTime1) {
				info.minTime1 = lPeriod1;
			}
			info.totalTime1 += lPeriod1;
			info.counter1 += 1L;
			info.lastTime1.remove();

			float fSec = ((float) lPeriod + 0.0F) / 1.0E9F;
			if (fSec > 60.0F) {
				StringBuffer sb = new StringBuffer();
				sb.append(key).append(", time:").append(format(fSec));
				sb.append("sec, items").append(info.counter1);
				sb.append(", avg items per sec: ").append(
						format((float) info.counter1 / fSec));
				sb.append(", avg item time:").append(
						format((float) info.totalTime1 / 1000000.0F
								/ ((float) info.counter1 + 0.0F)));
				sb.append("ms, max item time:").append(
						format(((float) info.maxTime1 + 0.0F) / 1000000.0F));
				sb.append("ms, min item time:")
						.append(format(((float) info.minTime1 + 0.0F) / 1000000.0F))
						.append("ms");
				if (args != null) {
					for (int i = 0; i < args.length; i++) {
						sb.append(args[i]);
					}
				}
				System.err.println(sb.toString());

				info.totalTime1 = 0L;
				info.counter1 = 0L;
				info.maxTime1 = Long.MIN_VALUE;
				info.minTime1 = Long.MAX_VALUE;
				info.initLastTime1 = System.nanoTime();
			}
		}
	}

	public static void startCountOnce(String key) {
		CountInfo info = getCalcInfo(key);
		synchronized (info) {
			info.lastTime2.set(Long.valueOf(System.nanoTime()));
		}
	}

	public static void endCountOnce(String key) {
		CountInfo info = getCalcInfo(key);
		synchronized (info) {
			if (info.lastTime2.get() == null) {
				return;
			}
			long lPeriod = System.nanoTime()
					- ((Long) info.lastTime2.get()).longValue();
			float f = ((float) lPeriod + 0.0F) / 1000000.0F;
			System.out.println(key + ",  spent " + format(f) + "ms");
			info.lastTime2.remove();
		}
	}

	private static String format(float f) {
		return String.valueOf(Math.round(f * 100.0F) / 100.0F);
	}

	public static void count(String key, String... args) {
		CountInfo info = getCalcInfo(key);
		synchronized (info) {
			float fSec = ((float) (System.nanoTime() - info.lastTime) + 0.0F) / 1.0E9F;
			if (fSec > 60.0F) {
				StringBuffer sb = new StringBuffer();
				sb.append(key).append(", time:").append(format(fSec));
				sb.append("sec, items").append(info.counter);
				sb.append(", avg items per sec:").append(
						format((float) info.counter / fSec));
				if (args != null) {
					for (int i = 0; i < args.length; i++) {
						sb.append(args[i]);
					}
				}
				System.err.println(sb.toString());

				info.lastTime = System.nanoTime();
				info.counter = 0L;
			} else {
				info.counter += 1L;
			}
		}
	}

	public static class CountInfo {
		long lastTime;
		long counter;
		long initLastTime1;
		long counter1;
		long maxTime1;
		long minTime1;
		long totalTime1;
		ThreadLocal<Long> lastTime2 = new ThreadLocal<Long>();
		ThreadLocal<Long> lastTime1 = new ThreadLocal<Long>();

		public CountInfo() {
			this.lastTime = System.nanoTime();
			this.counter = 0L;

			this.initLastTime1 = System.nanoTime();
			this.counter1 = 0L;
			this.maxTime1 = Long.MIN_VALUE;
			this.minTime1 = Long.MAX_VALUE;
			this.totalTime1 = 0L;
		}
	}

	public static void main(String[] args) throws Exception {
		Random r = new Random();
		for (;;) {
			float f = r.nextFloat();
			if (f < 0.5D) {
				startCount("test1");
			}
			if (f < 0.3D) {
				startCount("test2");
			}
			Thread.sleep((int) (50.0F * f));
			if (f < 0.5D) {
				endCount("test1", new String[] { ", test0000:", "001" });
			}
			if (f < 0.3D) {
				endCount("test2", new String[0]);
			}
			count("test3", new String[0]);
			count("test4", new String[] { ", test0000:", "001" });
		}
	}
}
