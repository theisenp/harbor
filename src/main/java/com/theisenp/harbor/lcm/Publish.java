package com.theisenp.harbor.lcm;

import lcm.lcm.LCM;

import com.theisenp.harbor.Peer;
import com.theisenp.harbor.lcmtypes.PeerMessage;
import com.theisenp.harbor.utils.LcmConstants;
import com.theisenp.harbor.utils.PeerUtils;

/**
 * A {@link Runnable} that publishes a {@link PeerMessage}
 * 
 * @author patrick.theisen
 */
public class Publish implements Runnable {
	private final LCM lcm;
	private final PeerMessage message;

	/**
	 * @param lcm
	 * @param peer
	 */
	public Publish(LCM lcm, Peer peer) {
		this.lcm = lcm;
		this.message = PeerUtils.toMessage(peer);
	}

	@Override
	public void run() {
		lcm.publish(LcmConstants.PEER_CHANNEL, message);
	}
}
