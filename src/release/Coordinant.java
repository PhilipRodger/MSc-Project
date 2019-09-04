package release;


public class Coordinant {
	private int x;
	private int y;
	private Channel channel;
	private int bitMap;
	
	public Coordinant(int x, int y, Channel channel2, int bitMap) {
		this.x = x;
		this.y = y;
		this.channel = channel2;
		this.bitMap = bitMap;
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