package com.theisenp.harbor.utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Unit tests for {@link HarborUtils}
 * 
 * @author patrick.theisen
 */
public class HarborUtilsTest {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void testValidateAddress() {
		HarborUtils.validateAddress("0.0.0.0");
	}

	@Test
	public void testValidateAddressNull() {
		thrown.expect(IllegalArgumentException.class);
		HarborUtils.validateAddress(null);
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
		HarborUtils.validatePort(7667);
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
		HarborUtils.validateTtl(0);
	}

	@Test
	public void testValidateTtlInvalid() {
		thrown.expect(IllegalArgumentException.class);
		HarborUtils.validateTtl(-1);
	}
}
