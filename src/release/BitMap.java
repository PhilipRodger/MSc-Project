package release;

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
	protected int bitmapIndex;
	protected BufferedImage bitmapImage;

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public BitMap(int width, int height, Channel channel, int bitmapIndex) {
		this.width = width;
		this.height = height;
		this.channel = channel;
		this.bitmapIndex = bitmapIndex;
		image = new boolean[height][width];
		for (int i = 0; i < image.length; i++) {
			image[i] = new boolean[width];
		}
	}
	
	public BitMap(int width, int height) {
		this.width = width;
		this.height = height;
		this.bitmapIndex = -1;
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

	public ArrayList<Coordinant> getFrameCorners(int frameWidth, int frameHeight) {
		ArrayList<Coordinant> candidatesUnchecked = new ArrayList<>();
		for (int x = 0; x + frameWidth - 1 < width; x += frameWidth) {
			for (int y = 0; y + frameHeight - 1 < height; y += frameHeight) {
				candidatesUnchecked.add(new Coordinant(x, y, channel, bitmapIndex));
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
		BitMap segment = new BitMap(extractWidth, extractHeight, channel, bitmapIndex);

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

	// metadata used for returned map is copied from the first map.
	public static BitMap xOr(BitMap firstBitMap, BitMap secondBitMap) {
		BitMap result = new BitMap(firstBitMap.width, firstBitMap.height,  firstBitMap.channel,  firstBitMap.bitmapIndex);
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
	public void replacementImageCoordinantWithBitmap(Coordinant toReplace, BitMap replacement) {
		//First time should initialise the image
		if (bitmapImage == null) {
			createBitmapImage();
		}
		replaceCoordinantWithBitmap(toReplace, replacement);
		int contrastRGB = Color.BLACK.getRGB();
		
		int startX = toReplace.getX();
		int endX = startX + replacement.width;

		int startY = toReplace.getY();
		int endY = startY + replacement.height;
		
		for (int y = startY; y < endY; y++) {
			for (int x = startX; x < endX; x++) {
				bitmapImage.setRGB(x, y, contrastRGB);
			}
		}
	}
	
	private void createBitmapImage() {
		int contrastRGB  = RGBPixel.getPureRGB(channel);
		bitmapImage = intialiseBitMapImage(contrastRGB); 
	}

	public BitMap(BitMap toClone) {
		width = toClone.width;
		height = toClone.height;
		channel = toClone.channel;
		bitmapIndex = toClone.bitmapIndex;
		image = new boolean[toClone.image.length][];
		for (int i = 0; i < image.length; i++) {
			image[i] = toClone.image[i].clone();
		}
	}

	public BitMap getConjugate(boolean blackTopLeftCorner) {
		BitMap mask = makeCheckerBoardMap(width, height, blackTopLeftCorner);
		return xOr(this, mask);
	}

	
	private BufferedImage intialiseBitMapImage(int contrastRGB) {
		BufferedImage bitmapImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (getBit(x, y)) {
					bitmapImage.setRGB(x, y, contrastRGB);
				} else {
					bitmapImage.setRGB(x, y, Color.WHITE.getRGB());
				}
			}
		}
		return bitmapImage;
	}
	
	public BufferedImage getBitMapImage() {
		if (bitmapImage == null) {
			createBitmapImage();
		}
		return bitmapImage;
	}

	public BufferedImage getBitMapImage(int contrastRGB, int scale) {
		BufferedImage bitmapImage = new BufferedImage(width * scale, height * scale, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int colourToRepresentBit = 0;
				if (getBit(x, y)) {
					colourToRepresentBit = contrastRGB;
				} else {
					colourToRepresentBit = Color.WHITE.getRGB();
				}

				for (int outputY = y * scale; outputY < height * scale; outputY++) {
					for (int outputX = x * scale; outputX < width * scale; outputX++) {
						bitmapImage.setRGB(outputX, outputY, colourToRepresentBit);
					}
				}
			}
		}
		return bitmapImage;
	}
}
