package minimal;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;


public class BitMap {
	protected int width;
	protected int height;
	protected boolean[][] image;
	protected Channel channel;
	protected int bitmap;

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public BitMap(int width, int height, Channel channel, int bitmap) {
		this.width = width;
		this.height = height;
		this.channel = channel;
		this.bitmap = bitmap;
		image = new boolean[height][width];
		for (int i = 0; i < image.length; i++) {
			image[i] = new boolean[width];
		}
	}
	
	public BitMap(int width, int height) {
		this.width = width;
		this.height = height;
		this.bitmap = -1;
		image = new boolean[height][width];
		for (int i = 0; i < image.length; i++) {
			image[i] = new boolean[width];
		}
	}

	public void setBit(int x, int y) {
		image[y][x] = true;
	}

	public void setBit(int x, int y, boolean value) {
		image[y][x] = value;
	}

	public boolean getBit(int x, int y) {
		return image[y][x];
	}

	public ArrayList<Coordinant> getFrameCorners(int frameWidth, int frameHeight, Channel channel, int bitMap) {
		ArrayList<Coordinant> candidatesUnchecked = new ArrayList<>();
		for (int x = 0; x + frameWidth - 1 < width; x += frameWidth) {
			for (int y = 0; y + frameHeight - 1 < height; y += frameHeight) {
				candidatesUnchecked.add(new Coordinant(x, y, channel, bitMap));
			}
		}
		return candidatesUnchecked;
	}

	public BitMap extractSegment(Coordinant upperLeft, int extractWidth, int extractHeight) {

		// Define edges of the rectangle:
		int minX = upperLeft.getX();
		int maxX = minX + extractWidth;

		int minY = upperLeft.getY();
		int maxY = minY + extractHeight;


		// Copy contents into the new segment
		BitMap segment = new BitMap(extractWidth, extractHeight, channel, bitmap);

		for (int y = minY; y < maxY; y++) {
			for (int x = minX; x < maxX; x++) {
				if (getBit(x, y)) {
					segment.setBit(x - minX, y - minY);
				}
			}
		}
		return segment;
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < image.length; i++) {
			for (int j = 0; j < image[i].length; j++) {
				if (image[i][j]) {
					str.append("1 ");
				} else {
					str.append("0 ");
				}
			}
			str.append("\n");
		}
		return str.toString();
	}

	public static BitMap makeRandomMap(int width, int height) {
		BitMap map = new BitMap(width, height);
		Random r = new Random();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (r.nextBoolean()) {
					map.setBit(x, y);
				}
			}
		}
		return map;
	}

	public static BitMap makeCheckerBoardMap(int width, int height, boolean blackTopLeft) {
		int remainder = 0;
		if (blackTopLeft) {
			remainder = 1;
		}
		BitMap map = new BitMap(width, height);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (((y + x) % 2) == remainder) {
					map.setBit(x, y);
				}
			}

		}
		return map;
	}

	//TODO: metadata used for returned map is copied from the first map.
	public static BitMap xOr(BitMap firstBitMap, BitMap secondBitMap) {
		BitMap result = new BitMap(firstBitMap.width, firstBitMap.height,  firstBitMap.channel,  firstBitMap.bitmap);
		for (int x = 0; x < firstBitMap.width; x++) {
			for (int y = 0; y < firstBitMap.height; y++) {
				boolean first = firstBitMap.getBit(x, y);
				boolean second = secondBitMap.getBit(x, y);
				if (Boolean.logicalXor(first, second)) {
					result.setBit(x, y);
				}
			}
		}
		return result;
	}

	public void replaceCoordinantWithBitmap(Coordinant upperLeftHandCorner, BitMap replacement) {
		for (int replacementX = 0; replacementX < replacement.width; replacementX++) {
			for (int replacementY = 0; replacementY < replacement.height; replacementY++) {
				setBit(upperLeftHandCorner.getX() + replacementX, upperLeftHandCorner.getY() + replacementY,
						replacement.getBit(replacementX, replacementY));
			}
		}
	}

	public BitMap(BitMap toClone) {
		width = toClone.width;
		height = toClone.height;
		image = new boolean[toClone.image.length][];
		for (int i = 0; i < image.length; i++) {
			image[i] = toClone.image[i].clone();
		}
	}

	public BitMap getConjugate(boolean blackTopLeftCorner) {
		BitMap mask = makeCheckerBoardMap(width, height, blackTopLeftCorner);
		return xOr(this, mask);
	}

	public ArrayList<Coordinant> getCoordinantsMatchingCriteria(BPCS algorithim,
			ArrayList<Coordinant> bitMapFrameCorners) {
		// TODO Auto-generated method stub
		return null;
	}
}
