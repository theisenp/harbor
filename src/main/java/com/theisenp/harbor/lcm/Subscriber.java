package com.theisenp.harbor.lcm;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import lcm.lcm.LCM;
import lcm.lcm.LCMDataInputStream;
import lcm.lcm.LCMSubscriber;

import org.joda.time.Duration;

import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.theisenp.harbor.Harbor.Listener;
import com.theisenp.harbor.Peer;
import com.theisenp.harbor.Peer.Builder;
import com.theisenp.harbor.Peer.Status;
import com.theisenp.harbor.lcmtypes.PeerMessage;
import com.theisenp.harbor.utils.LcmConstants;
import com.theisenp.harbor.utils.PeerUtils;

/**
 * An {@link LCMSubscriber} that tracks the states of know peers on the network
 * 
 * @author patrick.theisen
 */
public class Subscriber implements LCMSubscriber {
	private final ListeningScheduledExecutorService executor;
	private final Map<String, Peer> peers = new HashMap<>();
	private final Map<String, Future<?>> timeouts = new HashMap<>();
	private final Set<Listener> listeners = new HashSet<>();
	private final long delay;

	/**
	 * @param executor
	 * @param timeout
	 */
	public Subscriber(ListeningScheduledExecutorService executor, Duration timeout) {
		this.executor = executor;
		this.delay = timeout.getMillis();
	}

	/**
	 * Adds the given {@link Listener} to the set of those that will be notified
	 * when peers change status
	 * 
	 * @param listener
	 */
	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes the given {@link Listener} from the set of those that will be
	 * notified when peers change status
	 * 
	 * @param listener
	 */
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	/**
	 * Clear all known peers
	 */
	public void clear() {
		synchronized(peers) {
			peers.clear();
			for(Future<?> timeout : timeouts.values()) {
				timeout.cancel(true);
			}
			timeouts.clear();
		}
	}

	/**
	 * @return The current set of known peers
	 */
	public Set<Peer> getPeers() {
		synchronized(peers) {
			return new HashSet<>(peers.values());
		}
	}

	@Override
	public void messageReceived(LCM lcm, String channel, LCMDataInputStream stream) {
		// Check the channel
		if(!channel.equals(LcmConstants.PEER_CHANNEL)) {
			String message = "Received message on unexpected channel: " + channel;
			throw new RuntimeException(message);
		}

		// Handle the message
		try {
			handlePeerMessage(new PeerMessage(stream));
		}
		catch(IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	/**
	 * Handles the given {@link PeerMessage} by either adding a new {@link Peer}
	 * or updating an existing one
	 * 
	 * @param message
	 */
	private void handlePeerMessage(PeerMessage message) {
		Peer peer = PeerUtils.fromMessage(message, Status.ACTIVE);
		synchronized(peers) {
			if(!peers.containsKey(peer.getId())) {
				add(peer);
			}
			else {
				update(peer);
			}
		}
	}

	/**
	 * Adds the given {@link Peer} as newly and actively connected
	 * 
	 * @param peer
	 */
	private void add(Peer peer) {
		String id = peer.getId();
		peers.put(id, peer);
		timeouts.put(id, executor.schedule(new Deactivate(id), delay, MILLISECONDS));
		notifyConnected(new Builder(peer).status(Status.CONNECTED).build());
		notifyActive(peer);
	}

	/**
	 * Notifies all registered listeners of the connected peer
	 */
	private void notifyConnected(Peer peer) {
		for(Listener listener : listeners) {
			listener.onConnected(peer);
		}
	}

	/**
	 * Notifies all registered listeners of the active peer
	 */
	private void notifyActive(Peer peer) {
		for(Listener listener : listeners) {
			listener.onActive(peer);
		}
	}

	/**
	 * Notifies all registered listeners of the inactive peer
	 */
	private void notifyInactive(Peer peer) {
		for(Listener listener : listeners) {
			listener.onInactive(peer);
		}
	}

	/**
	 * Notifies all registered listeners of the disconnected peer
	 */
	private void notifyDisconnected(Peer peer) {
		for(Listener listener : listeners) {
			listener.onDisconnected(peer);
		}
	}

	/**
	 * Updates the given {@link Peer} to reflect its renewed activity
	 * 
	 * @param peer
	 */
	private void update(Peer peer) {
		String id = peer.getId();
		Peer previous = peers.get(id);
		switch(previous.getStatus()) {
			case ACTIVE:
				timeouts.get(id).cancel(true);
				timeouts.put(id, executor.schedule(new Deactivate(id), delay, MILLISECONDS));
				break;
			case INACTIVE:
				peers.put(peer.getId(), peer);
				timeouts.get(id).cancel(true);
				timeouts.put(id, executor.schedule(new Deactivate(id), delay, MILLISECONDS));
				notifyActive(peer);
				break;
			default:
				String error = "Unexpected peer status: " + previous.getStatus();
				throw new RuntimeException(error);
		}
	}

	/**
	 * A {@link Runnable} that deactivates a {@link Peer}
	 * 
	 * @author patrick.theisen
	 */
	private class Deactivate implements Runnable {
		private final String id;

		/**
		 * @param id
		 */
		public Deactivate(String id) {
			this.id = id;
		}

		@Override
		public void run() {
			synchronized(peers) {
				Peer peer = new Builder(peers.get(id)).status(Status.INACTIVE).build();
				peers.put(id, peer);
				timeouts.put(id, executor.schedule(new Disconnect(id), delay, MILLISECONDS));
				notifyInactive(peer);
			}
		}
	}

	/**
	 * A {@link Runnable} that disconnects a {@link Peer}
	 * 
	 * @author patrick.theisen
	 */
	private class Disconnect implements Runnable {
		private final String id;

		/**
		 * @param id
		 */
		public Disconnect(String id) {
			this.id = id;
		}

		@Override
		public void run() {
			synchronized(peers) {
				Peer peer = new Builder(peers.get(id)).status(Status.DISCONNECTED).build();
				peers.remove(id);
				timeouts.remove(id);
				notifyDisconnected(peer);
			}
		}
	}
}
