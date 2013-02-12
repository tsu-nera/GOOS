package auctionsniper.ui;

import auctionsniper.SniperState;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

public class MainWindow extends JFrame {
	public static final String SNIPER_STATUS_NAME = "sniper status";
	public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
	public static final String STATUS_LOST = "Lost";
	public static final String STATUS_BIDDING = "Bidding";

	private final JLabel sniperStatus = createLabel("Joining");

	public MainWindow() {
		super("Auction Sniper");
		setName(MAIN_WINDOW_NAME);
		add(sniperStatus);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void showStatus(String status) {
		sniperStatus.setText(status);
	}

	private static JLabel createLabel(String initialTest) {
		JLabel result = new JLabel(initialTest);
		result.setName(SNIPER_STATUS_NAME);
		result.setBorder(new LineBorder(Color.BLACK));
		return result;
	}
}
