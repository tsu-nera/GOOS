package auctionsniper;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.xmlpull.v1.XmlPullParserException;

import auctionsniper.ui.MainWindow;

public class Main implements AuctionEventListener {
	@SuppressWarnings("unused") private Chat notToBeGCd;

	private static final int ARG_HOSTNAME = 0;
	private static final int ARG_USERNAME = 1;
	private static final int ARG_PASSWORD = 2;
	private static final int ARG_ITEM_ID = 3;

	public static final String AUCTION_RESOURCE = "Auction";
	public static final String ITEM_ID_AS_LOGIN = "auction-%s";
	public static final String AUCTION_ID_FORMAT =
			ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

	public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
	public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";

	private MainWindow ui;

	public Main() throws Exception {
		startUserInterface();
	}

	public static void main(String... args) throws Exception {
		Main main = new Main();
		main.joinAuction(
				connection(args[ARG_HOSTNAME], args[ARG_USERNAME],
						   args[ARG_PASSWORD]), args[ARG_ITEM_ID]);
	}

	private void joinAuction(XMPPConnection connection,String itemId) throws XMPPException {
		disconnectWhenUICloses(connection);
		
		Chat chat = connection.getChatManager().createChat(
		auctionId(itemId, connection),
		new AuctionMessageTranslator(this));
//		new MessageListener() {
//			public void processMessage(Chat aChat, Message message) {
//				SwingUtilities.invokeLater(new Runnable() {
//					public void run() {
//						ui.showStatus(MainWindow.STATUS_LOST);
//					}
//				});
//			}
//		});
		chat.sendMessage(JOIN_COMMAND_FORMAT);
    this.notToBeGCd = chat;
	}

	private void disconnectWhenUICloses(final XMPPConnection connection) {
		ui.addWindowListener(new WindowAdapter() {
			@Override public void windowClosed(WindowEvent e) {
				connection.disconnect();
			}
		});
	}

	private static String auctionId(String itemId, XMPPConnection connection) {
		return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
	}

	private void startUserInterface() throws Exception {
	SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				ui = new MainWindow();
			}
		});
	}

	private static XMPPConnection
	connection(String hostname, String username, String password)
		throws XMPPException
	{
		XMPPConnection connection = new XMPPConnection(hostname);
		connection.connect();
		connection.login(username, password, AUCTION_RESOURCE);

		return connection;
	}

  public void auctionClosed() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        ui.showStatus(MainWindow.STATUS_LOST);
      }
    });
  }
}

