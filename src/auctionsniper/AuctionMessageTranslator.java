//見本だと、auctionsniper.xmppにあるけども
package auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

public class AuctionMessageTranslator implements MessageListener {
  private final AuctionEventListener listener;

  public AuctionMessageTranslator(AuctionEventListener listener) {
    //この処理は本にのってない。常識なのか？
    this.listener = listener;
  }

  public void processMessage(Chat chat, Message message) {
    listener.auctionClosed();
  }

}
