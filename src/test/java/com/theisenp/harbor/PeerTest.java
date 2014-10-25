package com.theisenp.harbor;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.theisenp.harbor.Peer.Builder;
import com.theisenp.harbor.Peer.Status;

/**
 * Unit tests for {@link Peer}
 * 
 * @author patrick.theisen
 */
public class PeerTest {
	private static final String TEST_ID = "test-id";
	private static final String TEST_TYPE = "test-type";
	private static final Status TEST_STATUS = Status.CONNECTED;
	private static final String TEST_DESCRIPTION = "test-description";
	private static final String TEST_PROTOCOL = "test-protocol";
	private static final String TEST_ADDRESS = "test-address";

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void testConstruct() {
		Map<String, String> protocols = mockProtocols(10);
		validate(new Peer(TEST_ID, TEST_TYPE, TEST_STATUS, TEST_DESCRIPTION, protocols), protocols);
	}

	@Test
	public void testEquals() {
		Map<String, String> protocols = mockProtocols(10);
		Peer peer = new Peer(TEST_ID, TEST_TYPE, TEST_STATUS, TEST_DESCRIPTION, protocols);

		for(Object success : enumerateEqualTo(peer)) {
			assertThat(peer.equals(success)).isTrue();
			assertThat(success.equals(peer)).isTrue();
		}

		assertThat(peer.equals(null)).isFalse();
		for(Object failure : enumerateNotEqualTo(peer)) {
			assertThat(peer.equals(failure)).isFalse();
			assertThat(failure.equals(peer)).isFalse();
		}
	}

	@Test
	public void testHashCode() {
		Map<String, String> protocols = mockProtocols(10);
		Peer peer = new Peer(TEST_ID, TEST_TYPE, TEST_STATUS, TEST_DESCRIPTION, protocols);

		for(Object success : enumerateEqualTo(peer)) {
			assertThat(peer.hashCode()).isEqualTo(success.hashCode());
		}

		for(Object failure : enumerateNotEqualTo(peer)) {
			assertThat(peer.hashCode()).isNotEqualTo(failure.hashCode());
		}
	}

	@Test
	public void testBuild() {
		Map<String, String> protocols = mockProtocols(10);
		Builder builder = new Builder();
		builder.id(TEST_ID);
		builder.type(TEST_TYPE);
		builder.status(TEST_STATUS);
		builder.description(TEST_DESCRIPTION);
		builder.protocols(protocols);
		validate(builder.build(), protocols);
	}

	@Test
	public void testBuildCopy() {
		Map<String, String> protocols = mockProtocols(10);
		Peer original = new Peer(TEST_ID, TEST_TYPE, TEST_STATUS, TEST_DESCRIPTION, protocols);
		validate(new Builder(original).build(), protocols);
	}

	@Test
	public void testBuildWithSingleProtocol() {
		Builder builder = new Builder();
		builder.id(TEST_ID);
		builder.type(TEST_TYPE);
		builder.status(TEST_STATUS);
		builder.description(TEST_DESCRIPTION);

		Map<String, String> protocols = mockProtocols(1);
		for(Entry<String, String> entry : protocols.entrySet()) {
			builder.protocol(entry.getKey(), entry.getValue());
		}

		validate(builder.build(), protocols);
	}

	@Test
	public void testBuildWithMultipleProtocol() {
		Builder builder = new Builder();
		builder.id(TEST_ID);
		builder.type(TEST_TYPE);
		builder.status(TEST_STATUS);
		builder.description(TEST_DESCRIPTION);

		Map<String, String> protocols = mockProtocols(10);
		for(Entry<String, String> entry : protocols.entrySet()) {
			builder.protocol(entry.getKey(), entry.getValue());
		}

		validate(builder.build(), protocols);
	}

	@Test
	public void testBuildWithSingleProtocolMap() {
		Builder builder = new Builder();
		builder.id(TEST_ID);
		builder.type(TEST_TYPE);
		builder.status(TEST_STATUS);
		builder.description(TEST_DESCRIPTION);

		Map<String, String> protocols = mockProtocols(10);
		builder.protocols(protocols);

		validate(builder.build(), protocols);
	}

	@Test
	public void testBuildWithMultipleProtocolMaps() {
		Builder builder = new Builder();
		builder.id(TEST_ID);
		builder.type(TEST_TYPE);
		builder.status(TEST_STATUS);
		builder.description(TEST_DESCRIPTION);

		Map<String, String> protocols = mockProtocols(10);
		for(int i = 0; i < 10; i++) {
			builder.protocols(mockProtocols(i, 1));
		}

		validate(builder.build(), protocols);
	}

	@Test
	public void testBuildWithoutId() {
		Builder builder = new Builder();
		builder.type(TEST_TYPE);
		builder.status(TEST_STATUS);
		builder.description(TEST_DESCRIPTION);
		builder.protocols(mockProtocols(10));

		thrown.expect(IllegalStateException.class);
		builder.build();
	}

	@Test
	public void testBuildWithoutType() {
		Builder builder = new Builder();
		builder.id(TEST_ID);
		builder.status(TEST_STATUS);
		builder.description(TEST_DESCRIPTION);
		builder.protocols(mockProtocols(10));

		thrown.expect(IllegalStateException.class);
		builder.build();
	}

	@Test
	public void testBuildWithoutStatus() {
		Builder builder = new Builder();
		builder.id(TEST_ID);
		builder.type(TEST_TYPE);
		builder.description(TEST_DESCRIPTION);
		builder.protocols(mockProtocols(10));

		thrown.expect(IllegalStateException.class);
		builder.build();
	}

	@Test
	public void testBuildWithoutDescription() {
		Builder builder = new Builder();
		builder.id(TEST_ID);
		builder.type(TEST_TYPE);
		builder.status(TEST_STATUS);
		builder.protocols(mockProtocols(10));
		assertThat(builder.build().getDescription()).isEmpty();
	}

	@Test
	public void testBuildWithNullDescription() {
		Builder builder = new Builder();
		builder.id(TEST_ID);
		builder.type(TEST_TYPE);
		builder.status(TEST_STATUS);
		builder.description(null);
		builder.protocols(mockProtocols(10));

		thrown.expect(IllegalStateException.class);
		builder.build();
	}

	@Test
	public void testBuildWithoutProtocols() {
		Builder builder = new Builder();
		builder.id(TEST_ID);
		builder.type(TEST_TYPE);
		builder.status(TEST_STATUS);
		builder.description(TEST_DESCRIPTION);
		assertThat(builder.build().getProtocols()).isEmpty();
	}

	@Test
	public void testBuildReset() {
		Builder builder = new Builder();
		builder.id(TEST_ID);
		builder.type(TEST_TYPE);
		builder.status(TEST_STATUS);
		builder.description(TEST_DESCRIPTION);
		builder.protocols(mockProtocols(10));
		builder.reset();

		thrown.expect(IllegalStateException.class);
		builder.build();
	}

	@Test
	public void testBuildResetProtocols() {
		Builder builder = new Builder();
		builder.id(TEST_ID);
		builder.type(TEST_TYPE);
		builder.status(TEST_STATUS);
		builder.description(TEST_DESCRIPTION);
		builder.protocols(mockProtocols(10));
		builder.resetProtocols();
		assertThat(builder.build().getProtocols()).isEmpty();
	}

	/**
	 * @param peer
	 * @return A list of objects expected to compare equal to the given
	 * {@link Peer}
	 */
	private static List<Object> enumerateEqualTo(Peer peer) {
		List<Object> result = new ArrayList<>();
		result.add(peer);
		result.add(new Builder(peer).build());
		return result;
	}

	/**
	 * @param peer
	 * @return A list of objects expected to compare not equal to the given
	 * {@link Peer}
	 */
	private static List<Object> enumerateNotEqualTo(Peer peer) {
		String wrongId = peer.getId() + "-wrong";
		String wrongType = peer.getType() + "-wrong";
		Status wrongStatus = peer.getStatus() == Status.ACTIVE ? Status.INACTIVE : Status.ACTIVE;
		String wrongDescription = peer.getDescription() + "-wrong";

		List<Object> result = new ArrayList<>();
		result.add(new Object());
		result.add(new Builder(peer).id(wrongId).build());
		result.add(new Builder(peer).type(wrongType).build());
		result.add(new Builder(peer).status(wrongStatus).build());
		result.add(new Builder(peer).description(wrongDescription).build());
		result.add(new Builder(peer).protocol("wrong-protocol", "wrong-address").build());
		return result;
	}

	/**
	 * @param count
	 * @return A map with the given number of protocols generated from the
	 * default seed of 0
	 */
	private static Map<String, String> mockProtocols(int count) {
		return mockProtocols(0, count);
	}

	/**
	 * @param seed
	 * @param count
	 * @return A map with the given number of protocols generated from the given
	 * seed
	 */
	private static Map<String, String> mockProtocols(int seed, int count) {
		Map<String, String> result = new HashMap<>();
		for(int i = seed; i < seed + count; i++) {
			result.put(TEST_PROTOCOL + i, TEST_ADDRESS + i);
		}
		return result;
	}

	/**
	 * Verifies that the given {@link Peer} contains the expected test data
	 * 
	 * @param peer
	 * @param protocols
	 */
	private static void validate(Peer peer, Map<String, String> protocols) {
		assertThat(peer.getId()).isEqualTo(TEST_ID);
		assertThat(peer.getType()).isEqualTo(TEST_TYPE);
		assertThat(peer.getStatus()).isEqualTo(TEST_STATUS);
		assertThat(peer.getDescription()).isEqualTo(TEST_DESCRIPTION);
		assertThat(peer.getProtocols()).isEqualTo(protocols);
	}
}
