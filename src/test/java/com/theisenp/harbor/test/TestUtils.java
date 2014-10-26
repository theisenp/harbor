package com.theisenp.harbor.test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lcm.lcm.LCMDataInputStream;
import lcm.lcm.LCMEncodable;

/**
 * A collection of test related static utilities
 * 
 * @author patrick.theisen
 */
public class TestUtils {

	/**
	 * @param message
	 * @return An {@link LCMDataInputStream} wrapped around the given
	 * {@link LCMEncodable}
	 * @throws IOException
	 */
	public static LCMDataInputStream wrap(LCMEncodable message) {
		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			message.encode(new DataOutputStream(output));
			return new LCMDataInputStream(output.toByteArray());
		}
		catch(IOException exception) {
			throw new RuntimeException(exception);
		}
	}
}
