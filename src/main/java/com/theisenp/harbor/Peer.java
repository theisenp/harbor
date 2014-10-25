package com.theisenp.harbor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.collect.ImmutableMap;

/**
 * An immutable description of a service and the protocols that it supports
 * 
 * @author patrick.theisen
 */
public class Peer {
	private static final String PATTERN = "%s, %s, %s, %s: {%s}";

	private final String id;
	private final String type;
	private final Status status;
	private final String description;
	private final ImmutableMap<String, String> protocols;

	/**
	 * @param id
	 * @param type
	 * @param description
	 * @param status
	 * @param protocols
	 */
	public Peer(String id, String type, Status status, String description,
			Map<String, String> protocols) {
		this.id = id;
		this.type = type;
		this.description = description;
		this.status = status;
		this.protocols = ImmutableMap.copyOf(protocols);
	}

	/**
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * @return
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return
	 */
	public ImmutableMap<String, String> getProtocols() {
		return protocols;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof Peer)) {
			return false;
		}

		Peer other = (Peer) obj;
		EqualsBuilder builder = new EqualsBuilder();
		builder.append(id, other.id);
		builder.append(type, other.type);
		builder.append(status, other.status);
		builder.append(description, other.description);
		builder.append(protocols, other.protocols);
		return builder.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(id);
		builder.append(type);
		builder.append(status);
		builder.append(description);
		builder.append(protocols);
		return builder.toHashCode();
	}

	@Override
	public String toString() {
		int count = 0;
		StringBuilder builder = new StringBuilder();
		for(Entry<String, String> entry : protocols.entrySet()) {
			builder.append("(");
			builder.append(entry.getKey());
			builder.append(", ");
			builder.append(entry.getValue());
			builder.append(")");
			if(count++ < protocols.size()) {
				builder.append(", ");
			}
		}
		return String.format(PATTERN, id, type, status, description, builder.toString());
	}

	/**
	 * A fluent builder for {@link Peer}
	 * 
	 * @author patrick.theisen
	 */
	public static class Builder {
		private String id;
		private String type;
		private Status status;
		private String description = "";
		private Map<String, String> protocols = new HashMap<>();

		/**
		 * 
		 */
		public Builder() {
		}

		/**
		 * @param other
		 */
		public Builder(Peer other) {
			this.id = other.id;
			this.type = other.type;
			this.status = other.status;
			this.description = other.description;
			this.protocols.putAll(other.protocols);
		}

		/**
		 * @param id
		 * @return This instance
		 */
		public Builder id(String id) {
			this.id = id;
			return this;
		}

		/**
		 * @param type
		 * @return This instance
		 */
		public Builder type(String type) {
			this.type = type;
			return this;
		}
		
		/**
		 * @param status
		 * @return This instance
		 */
		public Builder status(Status status) {
			this.status = status;
			return this;
		}

		/**
		 * @param description
		 * @return This instance
		 */
		public Builder description(String description) {
			this.description = description;
			return this;
		}

		/**
		 * @param protocol
		 * @param address
		 * @return This instance
		 */
		public Builder protocol(String protocol, String address) {
			this.protocols.put(protocol, address);
			return this;
		}

		/**
		 * @param protocols
		 * @return This instance
		 */
		public Builder protocols(Map<String, String> protocols) {
			this.protocols.putAll(protocols);
			return this;
		}

		/**
		 * Clears any previously set parameters
		 * 
		 * @return This instance
		 */
		public Builder reset() {
			this.id = null;
			this.type = null;
			this.status = null;
			this.description = "";
			this.protocols.clear();
			return this;
		}

		/**
		 * Clears any previously added protocols
		 * 
		 * @return This instance
		 */
		public Builder resetProtocols() {
			this.protocols.clear();
			return this;
		}

		/**
		 * @return A {@link Peer} built from the current state
		 */
		public Peer build() {
			validate();
			return new Peer(id, type, status, description, protocols);
		}

		/**
		 * Verifies that a valid {@link Peer} can be produced from the current
		 * state
		 */
		private void validate() {
			// Check the ID
			if(id == null) {
				String message = "You must provide an ID";
				throw new IllegalStateException(message);
			}

			// Check the type
			if(type == null) {
				String message = "You must provide a type";
				throw new IllegalStateException(message);
			}
			
			// Check the status
			if(status == null) {
				String message = "You must provide a status";
				throw new IllegalStateException(message);
			}

			// Check the description
			if(description == null) {
				String message = "You cannot set the description to null. Leave it blank instead.";
				throw new IllegalStateException(message);
			}
		}
	}

	/**
	 * The supported {@link Peer} statuses
	 * 
	 * @author patrick.theisen
	 */
	public static enum Status {
		CONNECTED, ACTIVE, INACTIVE, DISCONNECTED;
	}
}
