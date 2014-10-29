package com.theisenp.harbor;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.google.common.util.concurrent.ListenableFuture;
import com.theisenp.harbor.Harbor.Listener;
import com.theisenp.harbor.filters.TypeFilter;

/**
 * Unit tests for {@link FindPeer}
 * 
 * @author patrick.theisen
 */
public class FindPeerTest {

	@Test
	public void testApplyNoInitialPeers() throws Exception {
		Harbor harbor = mock(Harbor.class);
		when(harbor.getPeers()).thenReturn(new HashSet<Peer>());
		ListenableFuture<Peer> future = new FindPeer(new MockFilter(0)).apply(harbor);

		assertThat(future.isDone()).isFalse();
	}

	@Test
	public void testApplySingleInitialPeers() throws Exception {
		Peer peer = mockPeer(0);
		Harbor harbor = mock(Harbor.class);
		when(harbor.getPeers()).thenReturn(new HashSet<>(Arrays.asList(peer)));
		ListenableFuture<Peer> future = new FindPeer(new MockFilter(0)).apply(harbor);

		assertThat(future.isDone()).isTrue();
		assertThat(future.get()).isEqualTo(peer);
	}

	@Test
	public void testApplyMultipleInitialPeers() throws Exception {
		Peer first = mockPeer(0);
		Peer second = mockPeer(1);
		Peer third = mockPeer(2);
		Harbor harbor = mock(Harbor.class);
		when(harbor.getPeers()).thenReturn(new HashSet<>(Arrays.asList(first, second, third)));
		ListenableFuture<Peer> future = new FindPeer(new MockFilter(2)).apply(harbor);

		assertThat(future.isDone()).isTrue();
		assertThat(future.get()).isEqualTo(third);
	}

	@Test
	public void testApplyFoundByListenerOnConnected() throws Exception {
		Harbor harbor = mock(Harbor.class);
		when(harbor.getPeers()).thenReturn(new HashSet<Peer>());
		ListenableFuture<Peer> future = new FindPeer(new MockFilter(0)).apply(harbor);

		assertThat(future.isDone()).isFalse();

		Peer peer = mockPeer(0);
		ArgumentCaptor<Listener> captor = ArgumentCaptor.forClass(Listener.class);
		verify(harbor).addListener(captor.capture());
		captor.getValue().onConnected(peer);

		assertThat(future.isDone()).isTrue();
		assertThat(future.get()).isEqualTo(peer);
	}

	@Test
	public void testApplyFoundByListenerOnActive() throws Exception {
		Harbor harbor = mock(Harbor.class);
		when(harbor.getPeers()).thenReturn(new HashSet<Peer>());
		ListenableFuture<Peer> future = new FindPeer(new MockFilter(0)).apply(harbor);

		assertThat(future.isDone()).isFalse();

		Peer peer = mockPeer(0);
		ArgumentCaptor<Listener> captor = ArgumentCaptor.forClass(Listener.class);
		verify(harbor).addListener(captor.capture());
		captor.getValue().onActive(peer);

		assertThat(future.isDone()).isTrue();
		assertThat(future.get()).isEqualTo(peer);
	}

	@Test
	public void testApplyFoundByListenerOnInactive() throws Exception {
		Harbor harbor = mock(Harbor.class);
		when(harbor.getPeers()).thenReturn(new HashSet<Peer>());
		ListenableFuture<Peer> future = new FindPeer(new MockFilter(0)).apply(harbor);

		assertThat(future.isDone()).isFalse();

		Peer peer = mockPeer(0);
		ArgumentCaptor<Listener> captor = ArgumentCaptor.forClass(Listener.class);
		verify(harbor).addListener(captor.capture());
		captor.getValue().onInactive(peer);

		assertThat(future.isDone()).isTrue();
		assertThat(future.get()).isEqualTo(peer);
	}

	@Test
	public void testApplyFoundByListenerOnDisconnected() throws Exception {
		Harbor harbor = mock(Harbor.class);
		when(harbor.getPeers()).thenReturn(new HashSet<Peer>());
		ListenableFuture<Peer> future = new FindPeer(new MockFilter(0)).apply(harbor);

		assertThat(future.isDone()).isFalse();

		Peer peer = mockPeer(0);
		ArgumentCaptor<Listener> captor = ArgumentCaptor.forClass(Listener.class);
		verify(harbor).addListener(captor.capture());
		captor.getValue().onDisconnected(peer);

		assertThat(future.isDone()).isTrue();
		assertThat(future.get()).isEqualTo(peer);
	}

	/**
	 * @param seed
	 * @return A {@link Peer} generated from the given seed
	 */
	private static Peer mockPeer(int seed) {
		Peer result = mock(Peer.class);
		when(result.getType()).thenReturn(String.valueOf(seed));
		return result;
	}

	/**
	 * A {@link TypeFilter} that matches a test {@link Peer} generated from a
	 * given seed
	 * 
	 * @author patrick.theisen
	 */
	private static class MockFilter extends TypeFilter {

		/**
		 * @param seed
		 */
		public MockFilter(int seed) {
			super(String.valueOf(seed));
		}
	}
}
