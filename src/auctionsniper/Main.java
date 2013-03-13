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

public class Main { //SniperListenerは実装していない p137
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

//  public void sniperLost() {
//    SwingUtilities.invokeLater(new Runnable() {
//      public void run() {
//        ui.showStatus(MainWindow.STATUS_LOST);
//      }
//    });
//  }
//
//  public void sniperBidding() {
//    SwingUtilities.invokeLater(new Runnable() {
//      public void run() {
//        ui.showStatus(MainWindow.STATUS_BIDDING);
//      }
//    });
//  }


  private void joinAuction(XMPPConnection connection,String itemId) {
    disconnectWhenUICloses(connection);
    final Chat chat =
        connection.getChatManager().createChat(auctionId(itemId, connection), null);
    this.notToBeGCd = chat;

    Auction auction = new XMPPAuction(chat);
    chat.addMessageListener(
        new AuctionMessageTranslator(
//            connection.getUser(),  //これはp147で初めて追加されているぞ？
            new AuctionSniper(auction, new SniperStateDisplayer())));
    auction.join();
  }

  public class XMPPAuction implements Auction {
    private final Chat chat;

    public XMPPAuction(Chat chat) {
      this.chat = chat;
    }

    public void bid(int amount) {
      sendMessage(String.format(BID_COMMAND_FORMAT, amount));
    }

    public void join() {
      sendMessage(JOIN_COMMAND_FORMAT);
    }

    private void sendMessage(final String message) {
      try {
        chat.sendMessage(message);
      }catch (XMPPException e) {
        e.printStackTrace();
      }
    }
  }

  public class SniperStateDisplayer implements SniperListener {

    @Override
    public void sniperBidding() {
      showStatus(MainWindow.STATUS_BIDDING);
    }

    @Override
    public void sniperLost() {
      showStatus(MainWindow.STATUS_LOST);
    }

    @Override
    public void sniperWinning() {
      showStatus(MainWindow.STATUS_WINNING);

    }

    private void showStatus(final String status) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() { ui.showStatus(status);  }
      });
    }
  }
}
