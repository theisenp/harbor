package com.theisenp.harbor.filters;

import com.theisenp.harbor.Peer;
import com.theisenp.harbor.Peer.Status;

/**
 * A {@link Filter} that only passes {@link Peer} instances with a given
 * {@link Status}
 * 
 * @author patrick.theisen
 */
public class StatusFilter implements Filter {
	private final Status status;

	/**
	 * @param status
	 */
	public StatusFilter(Status status) {
		this.status = status;
	}

	@Override
	public boolean filter(Peer peer) {
		return peer.getStatus().equals(status);
	}
}
