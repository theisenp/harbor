package com.theisenp.harbor.filters;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.theisenp.harbor.Peer;
import com.theisenp.harbor.Peer.Builder;
import com.theisenp.harbor.Peer.Status;

/**
 * Unit tests for {@link TypeFilter}
 * 
 * @author patrick.theisen
 */
public class TypeFilterTest {
	private static final String TEST_TYPE = "test-type";

	@Test
	public void testMatchingType() {
		assertThat(new TypeFilter(TEST_TYPE).filter(mockPeer(TEST_TYPE))).isTrue();
	}

	@Test
	public void testNonMatchingType() {
		assertThat(new TypeFilter(TEST_TYPE).filter(mockPeer("wrong"))).isFalse();
	}

	/**
	 * @param type
	 * @return A {@link Peer} with the given type
	 */
	private static Peer mockPeer(String type) {
		Builder builder = new Builder();
		builder.id("test-id");
		builder.type(type);
		builder.status(Status.ACTIVE);
		return builder.build();
	}
}
