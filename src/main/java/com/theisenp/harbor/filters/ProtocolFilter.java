package com.theisenp.harbor.filters;

import com.theisenp.harbor.Peer;

/**
 * A {@link Filter} that only passes {@link Peer} instances that support a given
 * protocol
 * 
 * @author patrick.theisen
 */
public class ProtocolFilter implements Filter {
	private final String protocol;

	/**
	 * @param protocol
	 */
	public ProtocolFilter(String protocol) {
		this.protocol = protocol;
	}

	@Override
	public boolean filter(Peer peer) {
		return peer.getProtocols().containsKey(protocol);
	}
}
