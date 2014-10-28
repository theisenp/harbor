package com.theisenp.harbor.filters;

import com.theisenp.harbor.Peer;

/**
 * Passes or rejects {@link Peer} instances
 * 
 * @author patrick.theisen
 */
public interface Filter {

	/**
	 * @param peer
	 * @return True if the given {@link Peer} passes. Else, false.
	 */
	public boolean filter(Peer peer);

	/**
	 * A {@link Filter} that always passes
	 */
	public static Filter PASS = new Filter() {

		@Override
		public boolean filter(Peer peer) {
			return true;
		}
	};

	/**
	 * A {@link Filter} that always fails
	 */
	public static Filter FAIL = new Filter() {

		@Override
		public boolean filter(Peer peer) {
			return false;
		}
	};
}
