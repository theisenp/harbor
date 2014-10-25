package com.theisenp.harbor;

import org.joda.time.Duration;

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

	/**
	 * @param address
	 * @param port
	 * @param ttl
	 * @param periodMillis
	 * @param timeoutMillis
	 */
	public Harbor(String address, int port, int ttl, Duration period, Duration timeout) {
		this.address = address;
		this.port = port;
		this.ttl = ttl;
		this.period = period;
		this.timeout = timeout;

		HarborUtils.validateAddress(address);
		HarborUtils.validatePort(port);
		HarborUtils.validateTtl(ttl);
		HarborUtils.validatePeriod(period);
		HarborUtils.validateTimeout(timeout);
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
			return this;
		}

		/**
		 * A {@link Harbor} built from the current state
		 * 
		 * @return
		 */
		public Harbor build() {
			return new Harbor(address, port, ttl, period, timeout);
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
		public void onConnected(Docket peer);

		/**
		 * Called when a peer becomes active (either because it has just
		 * connected or because it was previously inactive)
		 * 
		 * @param peer
		 */
		public void onActive(Docket peer);

		/**
		 * Called when a peer becomes inactive
		 * 
		 * @param peer
		 */
		public void onInactive(Docket peer);

		/**
		 * Called when a peer times out and disconnects
		 * 
		 * @param peer
		 */
		public void onDisconnected(Docket peer);
	}
}
