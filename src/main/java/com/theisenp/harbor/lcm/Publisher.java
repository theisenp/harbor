package com.theisenp.harbor.lcm;

import java.util.concurrent.TimeUnit;

import lcm.lcm.LCM;

import org.joda.time.Duration;

import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.theisenp.harbor.Peer;
import com.theisenp.harbor.lcmtypes.PeerMessage;

/**
 * An {@link AsyncFunction} that publishes a {@link PeerMessage} at a fixed rate
 * 
 * @author patrick.theisen
 */
public class Publisher implements AsyncFunction<LCM, Object> {
	private final ListeningScheduledExecutorService executor;
	private final long period;
	private final Peer peer;

	/**
	 * @param executor
	 * @param period
	 * @param peer
	 */
	public Publisher(ListeningScheduledExecutorService executor, Duration period, Peer peer) {
		this.executor = executor;
		this.period = period.getMillis();
		this.peer = peer;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ListenableFuture<Object> apply(LCM lcm) throws Exception {
		Runnable publish = new Publish(lcm, peer);
		TimeUnit unit = TimeUnit.MILLISECONDS;
		return (ListenableFuture<Object>) executor.scheduleAtFixedRate(publish, 0, period, unit);
	}
}
