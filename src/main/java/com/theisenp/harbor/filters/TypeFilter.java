package com.theisenp.harbor.filters;

import com.theisenp.harbor.Peer;

/**
 * A {@link Filter} that only passes {@link Peer} instances of a given type
 * 
 * @author patrick.theisen
 */
public class TypeFilter implements Filter {
	private final String type;

	/**
	 * @param type
	 */
	public TypeFilter(String type) {
		this.type = type;
	}

	@Override
	public boolean filter(Peer peer) {
		return peer.getType().equals(type);
	}
}
