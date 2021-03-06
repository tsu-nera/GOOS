package test.endtoend.auctionsniper;

import org.junit.After;
import org.junit.Test;

public class AuctionSniperEndToEndTest {
	private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
	private final ApplicationRunner application = new ApplicationRunner();

	@Test
	public void sniperJoinnAuctionUntilAuctionCloses() throws Exception {
		auction.startSellingItem();

		application.startBiddingIn(auction);
		auction.hasRecievedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

		auction.announceClosed();
		application.showsSniperHasLostAuction();
	}

	@Test //p110
	public void sniperMakesAHigherBidButLoses() throws Exception {
		auction.startSellingItem();

		application.startBiddingIn(auction);
		auction.hasRecievedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

		auction.reportPrice(1000, 98,"order bidder");

		application.hasShownSniperIsBidding();
		auction.hasRecievedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

		auction.announceClosed();
		application.showsSniperHasLostAuction();
	}

	@Test public void //p144 14th
	sniperWinsAnAuctionByBiddingHigher() throws Exception {
    auction.startSellingItem();

    application.startBiddingIn(auction);
    auction.hasRecievedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

    auction.reportPrice(1000, 98,"order bidder");
    application.hasShownSniperIsBidding();

    auction.hasRecievedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

    auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
    application.hasShownSniperIsWinning();

    auction.announceClosed();
    application.showsSniperHasWonAuction();
	}

	@After
	public void stopAuction() {
		auction.stop();
	}

	@After
	public void stopApplication() {
		application.stop();
	}
}

