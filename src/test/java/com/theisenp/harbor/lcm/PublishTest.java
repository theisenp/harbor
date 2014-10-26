package com.theisenp.harbor.lcm;

import static com.theisenp.harbor.test.PeerMessageAssert.assertThat;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import lcm.lcm.LCM;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.theisenp.harbor.Peer;
import com.theisenp.harbor.Peer.Builder;
import com.theisenp.harbor.Peer.Status;
import com.theisenp.harbor.lcmtypes.PeerMessage;
import com.theisenp.harbor.utils.PeerUtils;

/**
 * Unit tests for {@link Publish}
 * 
 * @author patrick.theisen
 */
public class PublishTest {
	private static final Peer TEST_PEER;
	static {
		Builder builder = new Builder();
		builder.id("test-id");
		builder.type("test-type");
		builder.status(Status.CONNECTED);
		TEST_PEER = builder.build();
	}

	@Test
	public void testRun() {
		LCM lcm = mock(LCM.class);
		new Publish(lcm, TEST_PEER).run();

		ArgumentCaptor<String> channelCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<PeerMessage> messageCaptor = ArgumentCaptor.forClass(PeerMessage.class);
		verify(lcm, times(1)).publish(channelCaptor.capture(), messageCaptor.capture());
		assertThat(channelCaptor.getValue()).isEqualTo(Publish.CHANNEL);
		assertThat(messageCaptor.getValue()).isEqualTo(PeerUtils.toMessage(TEST_PEER));
	}
}
