package com.theisenp.harbor.filters;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.theisenp.harbor.Peer;

/**
 * Unit tests for {@link DisjunctiveFilter}
 * 
 * @author patrick.theisen
 */
public class DisjunctiveFilterTest {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void testConstructVarargsEmpty() {
		thrown.expect(IllegalArgumentException.class);
		new DisjunctiveFilter();
	}

	@Test
	public void testConstructVarargs() {
		Filter filter = new DisjunctiveFilter(Filter.PASS, Filter.PASS);
		assertThat(filter.filter(mock(Peer.class))).isTrue();
	}

	@Test
	public void testConstructCollectionEmpty() {
		thrown.expect(IllegalArgumentException.class);
		new DisjunctiveFilter(new ArrayList<Filter>());
	}

	@Test
	public void testConstructCollection() {
		Filter filter = new DisjunctiveFilter(Arrays.asList(Filter.PASS, Filter.PASS));
		assertThat(filter.filter(mock(Peer.class))).isTrue();
	}

	@Test
	public void testSingleFilterFail() {
		Filter filter = new DisjunctiveFilter(Filter.FAIL);
		assertThat(filter.filter(mock(Peer.class))).isFalse();
	}

	@Test
	public void testSingleFilterPass() {
		Filter filter = new DisjunctiveFilter(Filter.PASS);
		assertThat(filter.filter(mock(Peer.class))).isTrue();
	}

	@Test
	public void testMultipleFiltersAllFail() {
		Filter filter = new DisjunctiveFilter(Filter.FAIL, Filter.FAIL);
		assertThat(filter.filter(mock(Peer.class))).isFalse();
	}

	@Test
	public void testMultipleFiltersSingleFail() {
		Filter filter = new DisjunctiveFilter(Filter.PASS, Filter.FAIL);
		assertThat(filter.filter(mock(Peer.class))).isTrue();
	}

	@Test
	public void testMultipleFiltersAllPass() {
		Filter filter = new DisjunctiveFilter(Filter.PASS, Filter.PASS);
		assertThat(filter.filter(mock(Peer.class))).isTrue();
	}
}
