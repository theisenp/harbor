package com.theisenp.harbor.utils;

import static java.util.regex.Pattern.compile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.Duration;

import com.theisenp.harbor.Harbor;

/**
 * A collection of {@link Harbor} related static utilities
 * 
 * @author patrick.theisen
 */
public class HarborUtils {
	private static final Pattern ADDRESS_PATTERN = compile("(\\d*)\\.(\\d*)\\.(\\d*)\\.(\\d*)");

	/**
	 * Verifies that the given address is valid
	 * 
	 * @param address
	 */
	public static void validateAddress(String address) {
		// Check for null addresses
		if(address == null) {
			String message = "The address must not be null";
			throw new IllegalArgumentException(message);
		}

		// Check for improperly formatted addresses
		Matcher matcher = ADDRESS_PATTERN.matcher(address);
		if(!matcher.matches()) {
			String message = "The address must have the format X.X.X.X";
			throw new IllegalArgumentException(message);
		}

		// Check for out of range address components
		for(int i = 1; i <= 4; i++) {
			int component = Integer.parseInt(matcher.group(i));
			if(component < 0 || component > 255) {
				String message = "All address components must be in the range [0, 255]";
				throw new IllegalArgumentException(message);
			}
		}
	}

	/**
	 * Verifies that the given port is valid
	 * 
	 * @param port
	 */
	public static void validatePort(int port) {
		if(port < 1 || port > 65535) {
			String message = "The port must be in the range [1, 65535]";
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Verifies that the given TTL is valid
	 * 
	 * @param ttl
	 */
	public static void validateTtl(int ttl) {
		if(ttl < 0) {
			String message = "The TTL must be >= 0";
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Verifies that the given period is valid
	 * 
	 * @param period
	 */
	public static void validatePeriod(Duration period) {
		if(period.getMillis() < 1) {
			String message = "The period must be >= 1 millisecond";
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Verifies that the given timeout is valid
	 * 
	 * @param timeout
	 */
	public static void validateTimeout(Duration timeout) {
		if(timeout.getMillis() < 1) {
			String message = "The timeout must be >= 1 millisecond";
			throw new IllegalArgumentException(message);
		}
	}
}
