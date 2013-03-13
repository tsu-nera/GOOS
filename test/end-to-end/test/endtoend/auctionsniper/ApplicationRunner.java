package test.endtoend.auctionsniper;

import static test.endtoend.auctionsniper.FakeAuctionServer.XMPP_HOSTNAME;

import auctionsniper.Main;
import auctionsniper.ui.MainWindow;

public class ApplicationRunner {
	public static final String SNIPER_ID = "sniper";
	public static final String SNIPER_PASSWORD = "sniper";
	public static final String SNIPER_XMPP_ID = SNIPER_ID + "@" + XMPP_HOSTNAME + "/Auction";

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

	public void hasShownSniperIsBidding() {
		driver.showSniperStatus(MainWindow.STATUS_BIDDING);
	}

	public void showSniperHasLostAuction() {
    driver.showSniperStatus(MainWindow.STATUS_LOST);
	}

  public void hasShownSniperIsWinning() {
    driver.showSniperStatus(MainWindow.STATUS_WINNING);
  }

  public void showsSniperHasWonAuction() {
    driver.showSniperStatus(MainWindow.STATUS_WON);
  }
}
