package com.theisenp.harbor.lcm;

import static com.theisenp.harbor.test.TestUtils.wrap;
import static com.theisenp.harbor.utils.LcmConstants.PEER_CHANNEL;
import static com.theisenp.harbor.utils.PeerUtils.toMessage;
import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import lcm.lcm.LCMDataInputStream;

import org.joda.time.Duration;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.theisenp.harbor.Harbor.Listener;
import com.theisenp.harbor.Peer;
import com.theisenp.harbor.Peer.Builder;
import com.theisenp.harbor.Peer.Status;

/**
 * Unit tests for {@link Subscriber}
 * 
 * @author patrick.theisen
 */
public class SubscriberTest {
	private static final Peer TEST_SELF = mockPeer(0, Status.ACTIVE);

	private ListeningScheduledExecutorService executor;

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() {
		executor = MoreExecutors.listeningDecorator(Executors.newSingleThreadScheduledExecutor());
	}

	@After
	public void tearDown() {
		executor.shutdownNow();
	}

	@Test
	public void testMessageOnWrongChannel() {
		Peer peer = mockPeer(1, Status.CONNECTED);
		Subscriber subscriber = new Subscriber(executor, Duration.standardSeconds(1), TEST_SELF);

		thrown.expect(RuntimeException.class);
		subscriber.messageReceived(null, PEER_CHANNEL + "-invalid", wrap(toMessage(peer)));
	}

	@Test
	public void testMessageWithInvalidFormat() {
		Subscriber subscriber = new Subscriber(executor, Duration.standardSeconds(1), TEST_SELF);

		thrown.expect(RuntimeException.class);
		subscriber.messageReceived(null, PEER_CHANNEL, new LCMDataInputStream(new byte[0]));
	}

	@Test
	public void testMessageFromSelf() {
		Listener listener = mock(Listener.class);
		Subscriber subscriber = new Subscriber(executor, Duration.standardSeconds(1), TEST_SELF);

		subscriber.addListener(listener);
		subscriber.messageReceived(null, PEER_CHANNEL, wrap(toMessage(TEST_SELF)));

		assertThat(subscriber.getPeers()).isEmpty();
		verify(listener, times(0)).onConnected(any(Peer.class));
		verify(listener, times(0)).onActive(any(Peer.class));
	}

	@Test
	public void testAddSinglePeer() {
		Listener listener = mock(Listener.class);
		Subscriber subscriber = new Subscriber(executor, Duration.standardSeconds(1), TEST_SELF);

		subscriber.addListener(listener);
		Peer peer = mockPeer(1, Status.CONNECTED);
		subscriber.messageReceived(null, PEER_CHANNEL, wrap(toMessage(peer)));

		assertThat(subscriber.getPeers()).containsOnly(asActive(peer));
		verify(listener, times(1)).onConnected(peer);
		verify(listener, times(1)).onActive(asActive(peer));
	}

	@Test
	public void testAddMultiplePeers() {
		Listener listener = mock(Listener.class);
		Subscriber subscriber = new Subscriber(executor, Duration.standardSeconds(1), TEST_SELF);

		subscriber.addListener(listener);
		List<Peer> peers = new ArrayList<>();
		for(int i = 1; i <= 10; i++) {
			Peer peer = mockPeer(i, Status.CONNECTED);
			subscriber.messageReceived(null, PEER_CHANNEL, wrap(toMessage(peer)));
			peers.add(peer);
		}

		assertThat(subscriber.getPeers()).hasSize(peers.size());
		for(Peer peer : peers) {
			assertThat(subscriber.getPeers()).contains(asActive(peer));
			verify(listener, times(1)).onConnected(peer);
			verify(listener, times(1)).onActive(asActive(peer));
		}
	}

	@Test
	public void testUpdateActive() {
		Peer peer = mockPeer(1, Status.CONNECTED);
		Listener listener = mock(Listener.class);
		Subscriber subscriber = new Subscriber(executor, Duration.standardSeconds(1), TEST_SELF);

		subscriber.messageReceived(null, PEER_CHANNEL, wrap(toMessage(peer)));
		subscriber.addListener(listener);
		subscriber.messageReceived(null, PEER_CHANNEL, wrap(toMessage(peer)));

		assertThat(subscriber.getPeers()).containsOnly(asActive(peer));
		verify(listener, times(0)).onConnected(any(Peer.class));
		verify(listener, times(0)).onActive(any(Peer.class));
	}

	@Test
	public void testUpdateInactive() throws InterruptedException {
		Peer peer = mockPeer(1, Status.CONNECTED);
		final Subscriber subscriber = new Subscriber(executor, Duration.millis(10), TEST_SELF);

		final CountDownLatch latch = new CountDownLatch(1);
		subscriber.messageReceived(null, PEER_CHANNEL, wrap(toMessage(peer)));
		subscriber.addListener(new Listener.Adapter() {
			@Override
			public void onActive(Peer peer) {
				assertThat(subscriber.getPeers()).containsOnly(asActive(peer));
				latch.countDown();
			}

			@Override
			public void onInactive(Peer peer) {
				assertThat(subscriber.getPeers()).containsOnly(asInactive(peer));
				subscriber.messageReceived(null, PEER_CHANNEL, wrap(toMessage(peer)));
			}
		});

		if(!latch.await(1, TimeUnit.SECONDS)) {
			fail("Peer was never reactivated");
		}
	}

	@Test
	public void testRemoveListener() {
		Peer peer = mockPeer(1, Status.CONNECTED);
		Listener listener = mock(Listener.class);
		Subscriber subscriber = new Subscriber(executor, Duration.standardSeconds(1), TEST_SELF);

		subscriber.addListener(listener);
		subscriber.removeListener(listener);
		subscriber.messageReceived(null, PEER_CHANNEL, wrap(toMessage(peer)));

		verify(listener, times(0)).onConnected(any(Peer.class));
		verify(listener, times(0)).onActive(any(Peer.class));
	}

	@Test
	public void testTimeoutInactive() throws InterruptedException {
		Peer peer = mockPeer(1, Status.CONNECTED);
		final Subscriber subscriber = new Subscriber(executor, Duration.millis(10), TEST_SELF);

		final CountDownLatch latch = new CountDownLatch(1);
		subscriber.messageReceived(null, PEER_CHANNEL, wrap(toMessage(peer)));
		subscriber.addListener(new Listener.Adapter() {
			@Override
			public void onInactive(Peer peer) {
				assertThat(subscriber.getPeers()).containsOnly(asInactive(peer));
				latch.countDown();
			}
		});

		if(!latch.await(1, TimeUnit.SECONDS)) {
			fail("Peer was never deactivated");
		}
	}

	@Test
	public void testTimeoutDisconnected() throws InterruptedException {
		Peer peer = mockPeer(1, Status.CONNECTED);
		final Subscriber subscriber = new Subscriber(executor, Duration.millis(10), TEST_SELF);

		final CountDownLatch latch = new CountDownLatch(1);
		subscriber.messageReceived(null, PEER_CHANNEL, wrap(toMessage(peer)));
		subscriber.addListener(new Listener.Adapter() {
			@Override
			public void onDisconnected(Peer peer) {
				assertThat(subscriber.getPeers()).isEmpty();
				latch.countDown();
			}
		});

		if(!latch.await(1, TimeUnit.SECONDS)) {
			fail("Peer was never disconnected");
		}
	}

	@Test
	public void testClear() {
		Subscriber subscriber = new Subscriber(executor, Duration.standardSeconds(1), TEST_SELF);

		for(int i = 1; i <= 10; i++) {
			Peer peer = mockPeer(i, Status.CONNECTED);
			subscriber.messageReceived(null, PEER_CHANNEL, wrap(toMessage(peer)));
		}

		subscriber.clear();
		assertThat(subscriber.getPeers()).isEmpty();
	}

	/**
	 * @param peer
	 * @return The given {@link Peer} with an active {@link Status}
	 */
	private static Peer asActive(Peer peer) {
		return new Builder(peer).status(Status.ACTIVE).build();
	}

	/**
	 * @param peer
	 * @return The given {@link Peer} with an inactive {@link Status}
	 */
	private static Peer asInactive(Peer peer) {
		return new Builder(peer).status(Status.INACTIVE).build();
	}

	/**
	 * @param seed
	 * @param status
	 * @return A {@link Peer} generated from the given seed and status
	 */
	private static Peer mockPeer(int seed, Status status) {
		String value = String.valueOf(seed);
		Builder builder = new Builder();
		builder.id(value);
		builder.type(value);
		builder.status(status);
		return builder.build();
	}
}
