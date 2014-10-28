package com.theisenp.harbor.filters;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.theisenp.harbor.Peer;
import com.theisenp.harbor.Peer.Builder;
import com.theisenp.harbor.Peer.Status;

/**
 * Unit tests for {@link StatusFilter}
 * 
 * @author patrick.theisen
 */
public class StatusFilterTest {
	private static final Status TEST_STATUS = Status.ACTIVE;

	@Test
	public void testMatchingStatus() {
		assertThat(new StatusFilter(TEST_STATUS).filter(mockPeer(TEST_STATUS))).isTrue();
	}

	@Test
	public void testNonMatchingStatus() {
		assertThat(new StatusFilter(TEST_STATUS).filter(mockPeer(Status.INACTIVE))).isFalse();
	}

	/**
	 * @param status
	 * @return A {@link Peer} with the given status
	 */
	private static Peer mockPeer(Status status) {
		Builder builder = new Builder();
		builder.id("test-id");
		builder.type("test-type");
		builder.status(status);
		return builder.build();
	}
}
