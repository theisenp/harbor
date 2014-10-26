package com.theisenp.harbor.utils;

import java.util.Map;
import java.util.Map.Entry;

import com.theisenp.harbor.Peer;
import com.theisenp.harbor.Peer.Builder;
import com.theisenp.harbor.Peer.Status;
import com.theisenp.harbor.lcmtypes.PeerMessage;

/**
 * A collection of {@link Peer} related static utilities
 * 
 * @author patrick.theisen
 */
public final class PeerUtils {

	/**
	 * Private constructor to prevent instantiations/extensions
	 */
	private PeerUtils() {
	}

	/**
	 * @param message
	 * @param status
	 * @return A {@link Peer} built from the given {@link PeerMessage} and
	 * {@link Status}
	 */
	public static Peer fromMessage(PeerMessage message, Status status) {
		Builder builder = new Builder();
		builder.id(message.id);
		builder.type(message.type);
		builder.status(status);
		builder.description(message.description);
		for(int i = 0; i < message.count; i++) {
			builder.protocol(message.protocols[i], message.addresses[i]);
		}
		return builder.build();
	}

	/**
	 * @param peer
	 * @return A {@link PeerMessage} built from the given {@link Peer}
	 */
	public static PeerMessage toMessage(Peer peer) {
		PeerMessage message = new PeerMessage();
		message.id = peer.getId();
		message.type = peer.getType();
		message.description = peer.getDescription();

		Map<String, String> protocols = peer.getProtocols();
		message.count = protocols.size();
		message.protocols = new String[message.count];
		message.addresses = new String[message.count];

		int i = 0;
		for(Entry<String, String> entry : protocols.entrySet()) {
			message.protocols[i] = entry.getKey();
			message.addresses[i++] = entry.getValue();
		}

		return message;
	}
}
