package com.opaigc.server.infrastructure.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 描述
 *
 * @author huhongda@fiture.com
 * @date 2023/4/10
 */

public class IPLimiter {
	private final Map<String, AtomicLong> ipCounter = new ConcurrentHashMap<>();
	private final long limit;

	public IPLimiter(long limit) {
		this.limit = limit;
	}

	public boolean isRequestAllowed(String clientIP) {
		AtomicLong count = ipCounter.computeIfAbsent(clientIP, k -> new AtomicLong(0));
		return count.incrementAndGet() <= limit;
	}
}
