package test.endtoend.auctionsniper;

import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.hamcrest.Matcher;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import auctionsniper.Main;

public class FakeAuctionServer{
	public static final String ITEM_ID_AS_LOGIN = "auction-%s";
	public static final String AUCTION_RESOURCE = "Auction";
	public static final String XMPP_HOSTNAME = "localhost";
	private static final String AUCTION_PASSWORD = "auction";

	private final String itemId;
	private final XMPPConnection connection;
	private Chat currentChat;

	private final SingleMessageListener messageListener = new SingleMessageListener();

	public FakeAuctionServer(String itemId) {
		this.itemId = itemId;
		this.connection = new XMPPConnection(XMPP_HOSTNAME);
	}

	public void startSellingItem() throws XMPPException {
		connection.connect();
		connection.login(String.format(ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD,AUCTION_RESOURCE);
		connection.getChatManager().addChatListener (
				new ChatManagerListener() {
					public void chatCreated(Chat chat, boolean createLocally) {
						currentChat = chat;
						chat.addMessageListener(messageListener);
					}
				}
		);
	}

	public String getItemId() {
		return itemId;
	}

	public void reportPrice(int price, int incriment, String bidder) throws XMPPException {
		currentChat.sendMessage(
				String.format("SOLVersion: 1.1; Event: PRICE; CurrentPrice: %d; "
						+ "Incriment: %d; Bidder: %s;", price, incriment, bidder));

	}

	// スナイパーから「参加」リクエストを受信したか
	public void hasRecievedJoinRequestFrom(String sniperId) throws InterruptedException {
		recievesAMessageMatching(sniperId, equalTo(Main.JOIN_COMMAND_FORMAT));
//		messageListener.receivesAMessage(is(anything()));
	}

	// 「入札」を受信したか
	public void hasRecievedBid(int bid, String sniperId) throws InterruptedException {
		recievesAMessageMatching(sniperId,
				equalTo(String.format(Main.BID_COMMAND_FORMAT, bid)));

//		assertThat(currentChat.getParticipant(), equalTo(sniperId));
//		messageListener.receivesAMessage(
//				equalTo(String.format("SOLVersion: 1.1; Commmand: BID; Price: %d", bid)));
	}

	private void recievesAMessageMatching(
			String sniperId, Matcher<? super String> messageMatcher	) throws InterruptedException {
		messageListener.receivesAMessage(messageMatcher);
		assertThat(currentChat.getParticipant(), equalTo(sniperId));
	}


	public void announceClosed() throws XMPPException {
		currentChat.sendMessage(new Message());
	}

	public void stop() {
		connection.disconnect();
	}

	public class SingleMessageListener implements MessageListener {
		private final ArrayBlockingQueue<Message> messages =
				new ArrayBlockingQueue<Message>(1);

		public void processMessage(Chat chat, Message message) {
			messages.add(message);
		}

		@SuppressWarnings("unchecked")
		public void receivesAMessage(Matcher<? super String> messageMatcher)
				throws InterruptedException {
			final Message message = messages.poll(5, TimeUnit.SECONDS);
			assertThat("Message", message, is(notNullValue()));
			assertThat(message.getBody(), messageMatcher);
		}
	}

}
