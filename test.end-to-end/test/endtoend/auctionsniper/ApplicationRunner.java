package test.endtoend.auctionsniper;

//import static auctionsniper.ui.SnipersTableModel.textFor;
import static org.hamcrest.Matchers.containsString;
import static test.endtoend.auctionsniper.FakeAuctionServer.XMPP_HOSTNAME;

import java.io.IOException;

import javax.swing.SwingUtilities;

import auctionsniper.Main;
import auctionsniper.SniperState;
import auctionsniper.ui.MainWindow;

public class ApplicationRunner {
	public static final String SNIPER_ID = "sniper";
	public static final String SNIPER_PASSWORD = "sniper";
	private AuctionSniperDriver driver;

	public void startBiddingIn(final FakeAuctionServer auction) {
		Thread thread = new Thread("Test Application") {
			@Override public void run() {
				try {
					Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
		driver = new AuctionSniperDriver(1000);
		driver.showSniperStatus("Joining");
	}

	public void showsSniperHasLostAuction() {
		driver.showSniperStatus("Lost");
	}

	public void stop() {
		if (driver != null) {
			driver.dispose();
		}
	}
}
