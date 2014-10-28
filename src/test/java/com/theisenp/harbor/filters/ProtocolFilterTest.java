package com.theisenp.harbor.filters;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.theisenp.harbor.Peer;
import com.theisenp.harbor.Peer.Builder;
import com.theisenp.harbor.Peer.Status;

/**
 * Unit tests for {@link ProtocolFilter}
 * 
 * @author patrick.theisen
 */
public class ProtocolFilterTest {
	private static final String TEST_PROTOCOL = "test-protocol";

	@Test
	public void testMatchingProtocol() {
		assertThat(new ProtocolFilter(TEST_PROTOCOL).filter(mockPeer(TEST_PROTOCOL))).isTrue();
	}

	@Test
	public void testEmptyProtocol() {
		assertThat(new ProtocolFilter(TEST_PROTOCOL).filter(mockPeer(null))).isFalse();
	}

	@Test
	public void testNonMatchingProtocol() {
		assertThat(new ProtocolFilter(TEST_PROTOCOL).filter(mockPeer("wrong"))).isFalse();
	}

	/**
	 * @param protocol
	 * @return A {@link Peer} that supports the given protocol
	 */
	private static Peer mockPeer(String protocol) {
		Builder builder = new Builder();
		builder.id("test-id");
		builder.type("test-id");
		builder.status(Status.ACTIVE);
		if(protocol != null) {
			builder.protocol(protocol, protocol);
		}
		return builder.build();
	}
}
