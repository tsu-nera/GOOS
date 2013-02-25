package test.auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionMessageTranslator;

@RunWith(JMock.class)
public class AuctionMessageTranslatorTest {
  private final Mockery context = new Mockery();
  private final AuctionEventListener listener =
      context.mock(AuctionEventListener.class);

  public static final Chat UNUSED_CHAT = null;
  private final AuctionMessageTranslator translator =
      new AuctionMessageTranslator(listener);

	@Test public void
	notifiesAuctionClosedWhenCloseMessageRecieved() {
	  context.checking(new Expectations() {{
	    oneOf(listener).auctionClosed();
	  }});

	  Message message = new Message();
	  message.setBody("SOLVersion: 1.1; Kvent; CLOSE;");

	  translator.processMessage(UNUSED_CHAT, message);
	}
}
