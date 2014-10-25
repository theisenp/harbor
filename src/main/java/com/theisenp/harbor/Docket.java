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
public class Docket {
	private static final String PATTERN = "%s [%s - %s]: {%s}";

	private final String id;
	private final String type;
	private final String description;
	private final ImmutableMap<String, String> protocols;

	/**
	 * @param id
	 * @param type
	 * @param description
	 * @param protocols
	 */
	public Docket(String id, String type, String description, Map<String, String> protocols) {
		this.id = id;
		this.type = type;
		this.description = description;
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
		if(obj == null || !(obj instanceof Docket)) {
			return false;
		}

		Docket other = (Docket) obj;
		EqualsBuilder builder = new EqualsBuilder();
		builder.append(id, other.id);
		builder.append(type, other.type);
		builder.append(description, other.description);
		builder.append(protocols, other.protocols);
		return builder.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(id);
		builder.append(type);
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
		return String.format(PATTERN, id, type, description, builder.toString());
	}

	/**
	 * A fluent builder for {@link Docket}
	 * 
	 * @author patrick.theisen
	 */
	public static class Builder {
		private String id;
		private String type;
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
		public Builder(Docket other) {
			this.id = other.id;
			this.type = other.type;
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
		 * @return A {@link Docket} built from the current state
		 */
		public Docket build() {
			validate();
			return new Docket(id, type, description, protocols);
		}

		/**
		 * Verifies that a valid {@link Docket} can be produced from the current
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

			// Check the description
			if(description == null) {
				String message = "You cannot set the description to null. Leave it blank instead.";
				throw new IllegalStateException(message);
			}
		}
	}
}
