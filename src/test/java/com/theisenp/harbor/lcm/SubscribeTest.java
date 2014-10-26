package com.theisenp.harbor.lcm;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import lcm.lcm.LCM;

import org.junit.Test;

import com.theisenp.harbor.utils.LcmConstants;

/**
 * Unit tests for {@link Subscribe}
 * 
 * @author patrick.theisen
 */
public class SubscribeTest {

	@Test
	public void testApply() throws Exception {
		LCM lcm = mock(LCM.class);
		Subscriber subscriber = mock(Subscriber.class);
		new Subscribe(subscriber).apply(lcm);
		verify(lcm, times(1)).subscribe(LcmConstants.PEER_CHANNEL, subscriber);
	}
}
