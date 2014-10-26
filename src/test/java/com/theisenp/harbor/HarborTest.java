package com.theisenp.harbor;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.joda.time.Duration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.theisenp.harbor.Harbor.Builder;

/**
 * Unit tests for {@link Harbor}
 * 
 * @author patrick.theisen
 */
public class HarborTest {
	private static final String TEST_ADDRESS = "0.0.0.0";
	private static final int TEST_PORT = 1;
	private static final int TEST_TTL = 1;
	private static final Duration TEST_PERIOD = Duration.standardSeconds(1);
	private static final Duration TEST_TIMEOUT = Duration.standardSeconds(1);
	private static final Peer TEST_PEER = mock(Peer.class);

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void testConstruct() {
		validate(new Harbor(TEST_ADDRESS, TEST_PORT, TEST_TTL, TEST_PERIOD, TEST_TIMEOUT, TEST_PEER));
	}

	@Test
	public void testConstructWithInvalidAddress() {
		thrown.expect(IllegalArgumentException.class);
		new Harbor("invalid", TEST_PORT, TEST_TTL, TEST_PERIOD, TEST_TIMEOUT, TEST_PEER);
	}

	@Test
	public void testConstructWithInvalidPort() {
		thrown.expect(IllegalArgumentException.class);
		new Harbor(TEST_ADDRESS, 0, TEST_TTL, TEST_PERIOD, TEST_TIMEOUT, TEST_PEER);
	}

	@Test
	public void testConstructWithInvalidTtl() {
		thrown.expect(IllegalArgumentException.class);
		new Harbor(TEST_ADDRESS, TEST_PORT, -1, TEST_PERIOD, TEST_TIMEOUT, TEST_PEER);
	}

	@Test
	public void testConstructWithInvalidPeriod() {
		thrown.expect(IllegalArgumentException.class);
		new Harbor(TEST_ADDRESS, TEST_PORT, TEST_TTL, Duration.ZERO, TEST_TIMEOUT, TEST_PEER);
	}

	@Test
	public void testConstructWithInvalidTimeout() {
		thrown.expect(IllegalArgumentException.class);
		new Harbor(TEST_ADDRESS, TEST_PORT, TEST_TTL, TEST_PERIOD, Duration.ZERO, TEST_PEER);
	}

	@Test
	public void testBuild() {
		Builder builder = new Builder();
		builder.address(TEST_ADDRESS);
		builder.port(TEST_PORT);
		builder.ttl(TEST_TTL);
		builder.period(TEST_PERIOD);
		builder.timeout(TEST_TIMEOUT);
		builder.peer(TEST_PEER);
		validate(builder.build());
	}

	@Test
	public void testBuildCopy() {
		validate(new Builder(new Harbor(TEST_ADDRESS, TEST_PORT, TEST_TTL, TEST_PERIOD,
				TEST_TIMEOUT, TEST_PEER)).build());
	}

	@Test
	public void testBuildDefault() {
		validateDefault(new Builder().peer(TEST_PEER).build());
	}

	@Test
	public void testBuildReset() {
		Builder builder = new Builder();
		builder.address(TEST_ADDRESS);
		builder.port(TEST_PORT);
		builder.ttl(TEST_TTL);
		builder.period(TEST_PERIOD);
		builder.timeout(TEST_TIMEOUT);
		builder.reset();
		validateDefault(builder.peer(TEST_PEER).build());
	}

	@Test
	public void testBuildWithInvalidAddress() {
		thrown.expect(IllegalArgumentException.class);
		new Builder().address("invalid");
	}

	@Test
	public void testBuildWithInvalidPort() {
		thrown.expect(IllegalArgumentException.class);
		new Builder().port(0);
	}

	@Test
	public void testBuildWithInvalidTtl() {
		thrown.expect(IllegalArgumentException.class);
		new Builder().ttl(-1);
	}

	@Test
	public void testBuildWithInvalidPeriod() {
		thrown.expect(IllegalArgumentException.class);
		new Builder().period(Duration.ZERO);
	}

	@Test
	public void testBuildWithInvalidTimeout() {
		thrown.expect(IllegalArgumentException.class);
		new Builder().timeout(Duration.ZERO);
	}

	@Test
	public void testBuildWithoutPeer() {
		thrown.expect(IllegalStateException.class);
		new Builder().build();
	}

	/**
	 * Verify that the given {@link Harbor} has the expected test data
	 * 
	 * @param harbor
	 */
	private static void validate(Harbor harbor) {
		assertThat(harbor.getAddress()).isEqualTo(TEST_ADDRESS);
		assertThat(harbor.getPort()).isEqualTo(TEST_PORT);
		assertThat(harbor.getTtl()).isEqualTo(TEST_TTL);
		assertThat(harbor.getPeriod()).isEqualTo(TEST_PERIOD);
		assertThat(harbor.getTimeout()).isEqualTo(TEST_TIMEOUT);
		assertThat(harbor.getPeer()).isEqualTo(TEST_PEER);
	}

	/**
	 * Verify that the given {@link Harbor} has the expected test data
	 * 
	 * @param harbor
	 */
	private static void validateDefault(Harbor harbor) {
		assertThat(harbor.getAddress()).isEqualTo(Harbor.DEFAULT_ADDRESS);
		assertThat(harbor.getPort()).isEqualTo(Harbor.DEFAULT_PORT);
		assertThat(harbor.getTtl()).isEqualTo(Harbor.DEFAULT_TTL);
		assertThat(harbor.getPeriod()).isEqualTo(Harbor.DEFAULT_PERIOD);
		assertThat(harbor.getTimeout()).isEqualTo(Harbor.DEFAULT_TIMEOUT);
		assertThat(harbor.getPeer()).isEqualTo(TEST_PEER);
	}
}
