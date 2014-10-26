package com.theisenp.harbor.lcm;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import lcm.lcm.LCM;

import org.junit.Test;

import com.theisenp.harbor.utils.LcmConstants;

/**
 * Unit tests for {@link Unsubscribe}
 * 
 * @author patrick.theisen
 */
public class UnsubscribeTest {

	@Test
	public void testApply() throws Exception {
		LCM lcm = mock(LCM.class);
		Subscriber subscriber = mock(Subscriber.class);
		new Unsubscribe(subscriber).apply(lcm);
		verify(lcm, times(1)).unsubscribe(LcmConstants.PEER_CHANNEL, subscriber);
	}
}
