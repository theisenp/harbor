package com.theisenp.harbor;

import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.theisenp.harbor.Harbor.Listener;
import com.theisenp.harbor.filters.Filter;

/**
 * An {@link AsyncFunction} that returns the first {@link Peer} that passes the
 * given {@link Filter}
 * 
 * @author patrick.theisen
 */
public class FindPeer implements AsyncFunction<Harbor, Peer> {
	private final Filter filter;

	/**
	 * @param filter
	 */
	public FindPeer(Filter filter) {
		this.filter = filter;
	}

	@Override
	public ListenableFuture<Peer> apply(Harbor harbor) throws Exception {
		SettableFuture<Peer> result = SettableFuture.create();
		Listener listener = new FilteredListener(harbor, result);
		harbor.addListener(listener);

		synchronized(result) {
			// Check for a result already set by the listener,
			if(result.isDone()) {
				return result;
			}

			// Look for results among the existing peers
			for(Peer peer : harbor.getPeers()) {
				if(!result.isDone() && filter.filter(peer)) {
					result.set(peer);
					harbor.removeListener(listener);
					break;
				}
			}
		}

		return result;
	}

	/**
	 * A {@link Listener} that waits for a Peer that
	 * 
	 * @author patrick.theisen
	 */
	private class FilteredListener implements Listener {
		private final Harbor harbor;
		private final SettableFuture<Peer> result;

		/**
		 * @param harbor
		 * @param result
		 */
		public FilteredListener(Harbor harbor, SettableFuture<Peer> result) {
			this.harbor = harbor;
			this.result = result;
		}

		@Override
		public void onConnected(Peer peer) {
			checkPeer(peer);
		}

		@Override
		public void onActive(Peer peer) {
			checkPeer(peer);
		}

		@Override
		public void onInactive(Peer peer) {
			checkPeer(peer);
		}

		@Override
		public void onDisconnected(Peer peer) {
			checkPeer(peer);
		}

		/**
		 * Sets the result and removes the listener if the given {@link Peer}
		 * passes the {@link Filter}
		 * 
		 * @param peer
		 */
		private void checkPeer(Peer peer) {
			synchronized(result) {
				if(!result.isDone() && filter.filter(peer)) {
					result.set(peer);
					harbor.removeListener(this);
				}
			}
		}
	}
}
