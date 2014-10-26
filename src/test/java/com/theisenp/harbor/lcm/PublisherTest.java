package com.theisenp.harbor.lcm;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.TimeUnit;

import lcm.lcm.LCM;

import org.joda.time.Duration;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.theisenp.harbor.Peer;
import com.theisenp.harbor.Peer.Builder;
import com.theisenp.harbor.Peer.Status;

/**
 * Unit tests for {@link Publisher}
 * 
 * @author patrick.theisen
 */
public class PublisherTest {
	private static final Peer TEST_PEER;
	private static final Duration TEST_PERIOD = Duration.standardSeconds(1);
	static {
		Builder builder = new Builder();
		builder.id("test-id");
		builder.type("test-type");
		builder.status(Status.CONNECTED);
		TEST_PEER = builder.build();
	}

	@Test
	public void testApply() throws Exception {
		ListeningScheduledExecutorService executor = mock(ListeningScheduledExecutorService.class);
		LCM lcm = mock(LCM.class);

		new Publisher(executor, TEST_PERIOD, TEST_PEER).apply(lcm);

		ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
		ArgumentCaptor<Long> delayCaptor = ArgumentCaptor.forClass(Long.class);
		ArgumentCaptor<Long> periodCaptor = ArgumentCaptor.forClass(Long.class);
		ArgumentCaptor<TimeUnit> timeUnitCaptor = ArgumentCaptor.forClass(TimeUnit.class);
		verify(executor, times(1)).scheduleAtFixedRate(runnableCaptor.capture(),
				delayCaptor.capture(), periodCaptor.capture(), timeUnitCaptor.capture());

		assertThat(runnableCaptor.getValue()).isInstanceOf(Publish.class);
		assertThat(delayCaptor.getValue()).isEqualTo(0);
		assertThat(periodCaptor.getValue()).isEqualTo(TEST_PERIOD.getMillis());
		assertThat(timeUnitCaptor.getValue()).isEqualTo(TimeUnit.MILLISECONDS);
	}
}
