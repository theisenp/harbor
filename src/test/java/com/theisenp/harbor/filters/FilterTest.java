package com.theisenp.harbor.filters;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import com.theisenp.harbor.Peer;

/**
 * Unit tests for {@link Filter}
 * 
 * @author patrick.theisen
 */
public class FilterTest {

	@Test
	public void testPass() {
		assertThat(Filter.PASS.filter(mock(Peer.class))).isTrue();
	}

	@Test
	public void testFail() {
		assertThat(Filter.FAIL.filter(mock(Peer.class))).isFalse();
	}
}
