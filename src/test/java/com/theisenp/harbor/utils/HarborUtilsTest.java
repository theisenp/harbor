package com.theisenp.harbor.utils;

import static org.fest.assertions.Assertions.assertThat;

import org.joda.time.Duration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.theisenp.harbor.Harbor;

/**
 * Unit tests for {@link HarborUtils}
 * 
 * @author patrick.theisen
 */
public class HarborUtilsTest {
	private static final String TEST_ADDRESS = Harbor.DEFAULT_ADDRESS;
	private static final int TEST_PORT = Harbor.DEFAULT_PORT;
	private static final int TEST_TTL = Harbor.DEFAULT_TTL;
	private static final Duration TEST_PERIOD = Harbor.DEFAULT_PERIOD;
	private static final Duration TEST_TIMEOUT = Harbor.DEFAULT_TIMEOUT;

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void testToLcmAddress() {
		String actual = HarborUtils.toLcmAddress(TEST_ADDRESS, TEST_PORT, TEST_TTL);
		String expected = "udpm://" + TEST_ADDRESS + ":" + TEST_PORT + "?ttl=" + TEST_TTL;
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void testToLcmAddressInvalidAddress() {
		thrown.expect(IllegalArgumentException.class);
		HarborUtils.toLcmAddress("invalid", TEST_PORT, TEST_TTL);
	}

	@Test
	public void testToLcmAddressInvalidPort() {
		thrown.expect(IllegalArgumentException.class);
		HarborUtils.toLcmAddress(TEST_ADDRESS, 0, TEST_TTL);
	}

	@Test
	public void testToLcmAddressInvalidTtl() {
		thrown.expect(IllegalArgumentException.class);
		HarborUtils.toLcmAddress(TEST_ADDRESS, TEST_PORT, -1);
	}

	@Test
	public void testValidateAddress() {
		HarborUtils.validateAddress(TEST_ADDRESS);
	}

	@Test
	public void testValidateAddressInvalidFormat() {
		thrown.expect(IllegalArgumentException.class);
		HarborUtils.validateAddress("invalid");
	}

	@Test
	public void testValidateAddressInvalidComponent() {
		thrown.expect(IllegalArgumentException.class);
		HarborUtils.validateAddress("256.256.256.256");
	}

	@Test
	public void testValidatePort() {
		HarborUtils.validatePort(TEST_PORT);
	}

	@Test
	public void testValidatePortTooLow() {
		thrown.expect(IllegalArgumentException.class);
		HarborUtils.validatePort(0);
	}

	@Test
	public void testValidatePortTooHigh() {
		thrown.expect(IllegalArgumentException.class);
		HarborUtils.validatePort(65536);
	}

	@Test
	public void testValidateTtl() {
		HarborUtils.validateTtl(TEST_TTL);
	}

	@Test
	public void testValidateTtlInvalid() {
		thrown.expect(IllegalArgumentException.class);
		HarborUtils.validateTtl(-1);
	}

	@Test
	public void testValidatePeriod() {
		HarborUtils.validatePeriod(TEST_PERIOD);
	}

	@Test
	public void testValidatePeriodInvalid() {
		thrown.expect(IllegalArgumentException.class);
		HarborUtils.validatePeriod(Duration.ZERO);
	}

	@Test
	public void testValidateTimeout() {
		HarborUtils.validateTimeout(TEST_TIMEOUT);
	}

	@Test
	public void testValidateTimeoutInvalid() {
		thrown.expect(IllegalArgumentException.class);
		HarborUtils.validateTimeout(Duration.ZERO);
	}
}
