package com.theisenp.harbor.utils;

import lcm.lcm.LCM;

/**
 * A collection of {@link LCM} related constants
 * 
 * @author patrick.theisen
 */
public final class LcmConstants {
	public static final String ADDRESS_FORMAT = "udpm://%s:%d?ttl=%d";
	public static final String PEER_CHANNEL = "harbor-peers";

	/**
	 * Private constructor to prevent instantiations/extensions
	 */
	private LcmConstants() {
	}
}
