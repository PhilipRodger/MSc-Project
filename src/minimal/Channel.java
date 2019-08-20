package minimal;

public enum Channel {
	RED, GREEN, BLUE;

	public static Channel channelMapping(int channel) {
		switch (channel) {
		case 0:
			return Channel.RED;
		case 1:
			return Channel.GREEN;
		case 2:
			return Channel.BLUE;
		}
		return RED;
	}

	public static int channelMapping(Channel channel) {
		switch (channel) {
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
