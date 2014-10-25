package com.theisenp.harbor.utils;

import static org.fest.assertions.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Test;

import com.theisenp.harbor.Docket;
import com.theisenp.harbor.Docket.Builder;
import com.theisenp.harbor.lcmtypes.DocketMessage;

/**
 * Unit tests for {@link DocketUtils}
 * 
 * @author patrick.theisen
 */
public class DocketUtilsTest {
	private static final String TEST_ID = "test-id";
	private static final String TEST_TYPE = "test-type";
	private static final String TEST_DESCRIPTION = "test-description";
	private static final String TEST_FIRST_PROTOCOL = "test-protocol-1";
	private static final String TEST_FIRST_ADDRESS = "test-address-1";
	private static final String TEST_SECOND_PROTOCOL = "test-protocol-2";
	private static final String TEST_SECOND_ADDRESS = "test-address-2";

	@Test
	public void testFromMessage() {
		assertThat(DocketUtils.fromMessage(mockDocketMessage())).isEqualTo(mockDocket());
	}

	@Test
	public void testToMessage() {
		assertThat(equals(DocketUtils.toMessage(mockDocket()), mockDocketMessage())).isTrue();
	}

	/**
	 * @param actual
	 * @param expected
	 * @return True if the given {@link DocketMessage} is equal to the expected
	 * one. The comparison ignores the order of protocols.
	 */
	private static boolean equals(DocketMessage actual, DocketMessage expected) {
		// Check the fixed size fileds
		EqualsBuilder builder = new EqualsBuilder();
		builder.append(actual.id, expected.id);
		builder.append(actual.type, expected.type);
		builder.append(actual.description, expected.description);
		builder.append(actual.count, expected.count);
		if(!builder.isEquals()) {
			return false;
		}

		// Check the mapped fields
		Map<String, String> actualProtocols = new HashMap<>();
		Map<String, String> expectedProtocols = new HashMap<>();
		for(int i = 0; i < actual.count; i++) {
			actualProtocols.put(actual.protocols[i], actual.addresses[i]);
			expectedProtocols.put(expected.protocols[i], expected.addresses[i]);
		}
		return actualProtocols.equals(expectedProtocols);
	}

	/**
	 * @return A {@link Docket} generated from the test data
	 */
	private static Docket mockDocket() {
		Builder builder = new Docket.Builder();
		builder.id(TEST_ID);
		builder.type(TEST_TYPE);
		builder.description(TEST_DESCRIPTION);
		builder.protocol(TEST_FIRST_PROTOCOL, TEST_FIRST_ADDRESS);
		builder.protocol(TEST_SECOND_PROTOCOL, TEST_SECOND_ADDRESS);
		return builder.build();
	}

	/**
	 * @return A {@link DocketMessage} generated from the test data
	 */
	private static DocketMessage mockDocketMessage() {
		DocketMessage result = new DocketMessage();
		result.id = TEST_ID;
		result.type = TEST_TYPE;
		result.description = TEST_DESCRIPTION;
		result.count = 2;
		result.protocols = new String[] { TEST_FIRST_PROTOCOL, TEST_SECOND_PROTOCOL };
		result.addresses = new String[] { TEST_FIRST_ADDRESS, TEST_SECOND_ADDRESS };
		return result;
	}
}
