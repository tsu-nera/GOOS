package test.auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.junit.Test;

import auctionsniper.AuctionMessageTranslator;

public class AuctionMessageTranslatorTest {
  public static final Chat UNUSED_CHAT = null;
  private final AuctionMessageTranslator translator = new AuctionMessageTranslator();

	@Test public void
	notifiesAuctionClosedWhenCloseMessageRecieved() {
	  Message message = new Message();
	  message.setBody("SOLVersion: 1.1; Kvent; CLOSE;");

	  translator.processMessage(UNUSED_CHAT, message);
	}
}
