package com.theisenp.harbor.lcm;

import lcm.lcm.LCM;

import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.ListenableFuture;
import com.theisenp.harbor.utils.LcmConstants;

/**
 * An {@link AsyncFunction} that attaches a {@link Subscriber} to an {@link LCM}
 * instance
 * 
 * @author patrick.theisen
 */
public class Subscribe implements AsyncFunction<LCM, Void> {
	private final Subscriber subscriber;

	/**
	 * @param subscriber
	 */
	public Subscribe(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	@Override
	public ListenableFuture<Void> apply(LCM lcm) throws Exception {
		lcm.subscribe(LcmConstants.PEER_CHANNEL, subscriber);
		return null;
	}
}
