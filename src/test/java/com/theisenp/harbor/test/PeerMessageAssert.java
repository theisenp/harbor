package com.theisenp.harbor.test;

import java.util.HashMap;
import java.util.Map;

import org.fest.assertions.Assertions;
import org.fest.assertions.GenericAssert;

import com.theisenp.harbor.lcmtypes.PeerMessage;

/**
 * A set of assertions for {@link PeerMessage}
 * 
 * @author patrick.theisen
 */
public class PeerMessageAssert extends GenericAssert<PeerMessageAssert, PeerMessage> {

	/**
	 * @param actual
	 */
	protected PeerMessageAssert(PeerMessage actual) {
		super(PeerMessageAssert.class, actual);
	}

	/**
	 * An entry point for {@link PeerMessage} assertions
	 * 
	 * @param actual
	 * @return A {@link PeerMessageAssert} wrapped around the given
	 * {@link PeerMessage}
	 */
	public static PeerMessageAssert assertThat(PeerMessage actual) {
		return new PeerMessageAssert(actual);
	}

	@Override
	public PeerMessageAssert isEqualTo(PeerMessage expected) {
		// Check the fixed size fields
		Assertions.assertThat(actual.id).isEqualTo(expected.id);
		Assertions.assertThat(actual.type).isEqualTo(expected.type);
		Assertions.assertThat(actual.description).isEqualTo(expected.description);
		Assertions.assertThat(actual.count).isEqualTo(expected.count);

		// Check the mapped protocols
		Map<String, String> actualProtocols = new HashMap<>();
		Map<String, String> expectedProtocols = new HashMap<>();
		for(int i = 0; i < actual.count; i++) {
			actualProtocols.put(actual.protocols[i], actual.addresses[i]);
			expectedProtocols.put(expected.protocols[i], expected.addresses[i]);
		}
		Assertions.assertThat(actualProtocols).isEqualTo(expectedProtocols);

		return this;
	}
}
