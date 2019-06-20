package experiments;

import java.awt.Color;

public enum Channel {
	RED,
	GREEN,
	BLUE;
	
	private static final int pureRed = Color.RED.getRGB();
	private static final int pureGreen = Color.GREEN.getRGB();
	private static final int pureBlue = Color.BLUE.getRGB();
	
	public static int getPureColour (Channel colour) {
		switch(colour) {
		case RED:
			return pureRed;
		case GREEN:
			return pureGreen;
		case BLUE:
			return pureBlue;
		}
		return 0;
	}
	
	public static Channel channelMapping (int channel) {
		switch(channel) {
		case 0:
			return Channel.RED;
		case 1:
			return Channel.GREEN;
		case 2:
			return Channel.BLUE;
		}
		return RED;
	}
	
	public static int channelMapping (Channel channel) {
		switch(channel) {
		case RED:
			return 0;
		case GREEN:
			return 1;
		case BLUE:
			return 2;
		}
		return -1;
	}
}
