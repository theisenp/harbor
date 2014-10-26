package com.theisenp.harbor.lcm;

import java.util.concurrent.Callable;

import lcm.lcm.LCM;

import com.theisenp.harbor.utils.HarborUtils;

/**
 * A {@link Callable} that produces an {@link LCM} instance
 * 
 * @author patrick.theisen
 */
public class Initialize implements Callable<LCM> {
	private final String address;
	private final int port;
	private final int ttl;

	/**
	 * @param address
	 * @param port
	 * @param ttl
	 */
	public Initialize(String address, int port, int ttl) {
		this.address = address;
		this.port = port;
		this.ttl = ttl;
	}

	@Override
	public LCM call() throws Exception {
		return new LCM(HarborUtils.toLcmAddress(address, port, ttl));
	}
}
