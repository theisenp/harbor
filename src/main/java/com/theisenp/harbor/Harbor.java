package com.theisenp.harbor;

import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

import java.util.HashSet;
import java.util.Set;

import lcm.lcm.LCM;

import org.joda.time.Duration;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.theisenp.harbor.lcm.Initialize;
import com.theisenp.harbor.lcm.Publisher;
import com.theisenp.harbor.lcm.Subscribe;
import com.theisenp.harbor.lcm.Subscriber;
import com.theisenp.harbor.lcm.Unsubscribe;
import com.theisenp.harbor.utils.HarborUtils;

/**
 * An LCM based peer discovery utility
 * 
 * @author patrick.theisen
 */
public class Harbor {
	public static final String DEFAULT_ADDRESS = "239.255.76.67";
	public static final int DEFAULT_PORT = 7667;
	public static final int DEFAULT_TTL = 0;
	public static final Duration DEFAULT_PERIOD = Duration.standardSeconds(1);
	public static final Duration DEFAULT_TIMEOUT = Duration.standardSeconds(5);

	private final String address;
	private final int port;
	private final int ttl;
	private final Duration period;
	private final Duration timeout;
	private final Peer peer;

	private final Set<Listener> listeners = new HashSet<>();
	private final ListeningScheduledExecutorService executor;
	private final ListenableFuture<LCM> lcm;
	private final Subscriber subscriber;
	private ListenableFuture<Object> publisher;

	/**
	 * @param address
	 * @param port
	 * @param ttl
	 * @param periodMillis
	 * @param timeoutMillis
	 */
	public Harbor(String address, int port, int ttl, Duration period, Duration timeout, Peer peer) {
		this.address = address;
		this.port = port;
		this.ttl = ttl;
		this.period = period;
		this.timeout = timeout;
		this.peer = peer;

		// Validate the parameters
		HarborUtils.validateAddress(address);
		HarborUtils.validatePort(port);
		HarborUtils.validateTtl(ttl);
		HarborUtils.validatePeriod(period);
		HarborUtils.validateTimeout(timeout);

		// Initialize the LCM instance on a background thread
		executor = listeningDecorator(newSingleThreadScheduledExecutor());
		lcm = executor.submit(new Initialize(address, port, ttl));
		subscriber = new Subscriber(executor, timeout);
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
	 * @return
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @return
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return
	 */
	public int getTtl() {
		return ttl;
	}

	/**
	 * @return
	 */
	public Duration getPeriod() {
		return period;
	}

	/**
	 * @return
	 */
	public Duration getTimeout() {
		return timeout;
	}

	/**
	 * @return
	 */
	public Peer getPeer() {
		return peer;
	}

	/**
	 * Starts the publish and subscribe tasks
	 */
	public void open() {
		Futures.transform(lcm, new Subscribe(subscriber));
		publisher = Futures.transform(lcm, new Publisher(executor, period, peer));
	}

	/**
	 * Stops the publish and subscribe tasks
	 */
	public void close() {
		Futures.transform(lcm, new Unsubscribe(subscriber));
		// TODO: Remove all peers
		publisher.cancel(true);
	}

	/**
	 * A fluent builder for {@link Harbor}
	 * 
	 * @author patrick.theisen
	 */
	public static class Builder {
		private String address = DEFAULT_ADDRESS;
		private int port = DEFAULT_PORT;
		private int ttl = DEFAULT_TTL;
		private Duration period = DEFAULT_PERIOD;
		private Duration timeout = DEFAULT_TIMEOUT;
		private Peer peer;

		/**
		 * 
		 */
		public Builder() {
		}

		/**
		 * @param other
		 */
		public Builder(Harbor other) {
			this.address = other.address;
			this.port = other.port;
			this.ttl = other.ttl;
			this.period = other.period;
			this.timeout = other.timeout;
			this.peer = other.peer;
		}

		/**
		 * @param address
		 * @return This instance
		 */
		public Builder address(String address) {
			HarborUtils.validateAddress(address);
			this.address = address;
			return this;
		}

		/**
		 * @param port
		 * @return This instance
		 */
		public Builder port(int port) {
			HarborUtils.validatePort(port);
			this.port = port;
			return this;
		}

		/**
		 * @param ttl
		 * @return This instance
		 */
		public Builder ttl(int ttl) {
			HarborUtils.validateTtl(ttl);
			this.ttl = ttl;
			return this;
		}

		/**
		 * @param period
		 * @return This instance
		 */
		public Builder period(Duration period) {
			HarborUtils.validatePeriod(period);
			this.period = period;
			return this;
		}

		/**
		 * @param timeout
		 * @return This instance
		 */
		public Builder timeout(Duration timeout) {
			HarborUtils.validateTimeout(timeout);
			this.timeout = timeout;
			return this;
		}

		/**
		 * @param peer
		 * @return This instance
		 */
		public Builder peer(Peer peer) {
			this.peer = peer;
			return this;
		}

		/**
		 * Clears any previously set parameters
		 * 
		 * @return This instance
		 */
		public Builder reset() {
			address = DEFAULT_ADDRESS;
			port = DEFAULT_PORT;
			ttl = DEFAULT_TTL;
			period = DEFAULT_PERIOD;
			timeout = DEFAULT_TIMEOUT;
			peer = null;
			return this;
		}

		/**
		 * A {@link Harbor} built from the current state
		 * 
		 * @return
		 */
		public Harbor build() {
			validate();
			return new Harbor(address, port, ttl, period, timeout, peer);
		}

		/**
		 * Verifies that a valid {@link Harbor} can be produced from the current
		 * state
		 */
		private void validate() {
			// Check the peer
			if(peer == null) {
				String message = "You must provide a peer";
				throw new IllegalStateException(message);
			}
		}
	}

	/**
	 * A collection of {@link Harbor} related callbacks
	 * 
	 * @author patrick.theisen
	 */
	public static interface Listener {

		/**
		 * Called when a new peer connects
		 * 
		 * @param peer
		 */
		public void onConnected(Peer peer);

		/**
		 * Called when a peer becomes active (either because it has just
		 * connected or because it was previously inactive)
		 * 
		 * @param peer
		 */
		public void onActive(Peer peer);

		/**
		 * Called when a peer becomes inactive
		 * 
		 * @param peer
		 */
		public void onInactive(Peer peer);

		/**
		 * Called when a peer times out and disconnects
		 * 
		 * @param peer
		 */
		public void onDisconnected(Peer peer);
	}
}
