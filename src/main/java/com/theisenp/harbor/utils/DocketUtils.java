package com.theisenp.harbor.utils;

import java.util.Map;
import java.util.Map.Entry;

import com.theisenp.harbor.Docket;
import com.theisenp.harbor.Docket.Builder;
import com.theisenp.harbor.lcmtypes.DocketMessage;

/**
 * A collection of {@link Docket} related static utilities
 * 
 * @author patrick.theisen
 */
public class DocketUtils {

	/**
	 * @param docket
	 * @return A {@link Docket} built from the given {@link DocketMessage}
	 */
	public static Docket fromMessage(DocketMessage message) {
		Builder builder = new Builder();
		builder.id(message.id);
		builder.type(message.type);
		builder.description(message.description);
		for(int i = 0; i < message.count; i++) {
			builder.protocol(message.protocols[i], message.addresses[i]);
		}
		return builder.build();
	}

	/**
	 * @param docket
	 * @return A {@link DocketMessage} built from the given {@link Docket}
	 */
	public static DocketMessage toMessage(Docket docket) {
		DocketMessage message = new DocketMessage();
		message.id = docket.getId();
		message.type = docket.getType();
		message.description = docket.getDescription();

		Map<String, String> protocols = docket.getProtocols();
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
