package experiments;

public class Coordinant {
	private int x;
	private int y;
	private Channel channel;
	private int bitMap;
	private BitMap origin;
	
	public Coordinant(int x, int y, Channel channel, int bitMap, BitMap origin) {
		this.x = x;
		this.y = y;
		this.channel = channel;
		this.bitMap = bitMap;
		this.origin = origin;
	}

	public Coordinant(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public int getBitMap() {
		return bitMap;
	}

	public void setBitMap(int bitMap) {
		this.bitMap = bitMap;
	}

	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
}
