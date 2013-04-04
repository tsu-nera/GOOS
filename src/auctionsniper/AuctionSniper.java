package auctionsniper;

import auctionsniper.ui.MainWindow;

public class AuctionSniper implements AuctionEventListener {
  private boolean isWinnging = false;
  private final SniperListener sniperListener;
  private final Auction auction;

  public AuctionSniper(Auction auction, SniperListener sniperListener) {
    this.sniperListener = sniperListener;
    this.auction = auction;
  }

  public void auctionClosed() {
    if(isWinnging) {
      sniperListener.sniperWon();
    }
    else {
      sniperListener.sniperLost();
    }
  }

  public void currentPrice(int price, int increment, PriceSource priceSource) {
    isWinnging = priceSource == priceSource.FromSniper;
    if(isWinnging) {
      sniperListener.sniperWinning();
    }
    else {
      auction.bid(price + increment);
      sniperListener.sniperBidding();
    }
  }
}