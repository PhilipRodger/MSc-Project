package release;

import java.awt.Color;

public class RGBPixel {
	public final static int NUMBER_OF_CHANNELS = 3;
	public final static int NUMBER_OF_BITS_PER_CHANNEL = 8;
	public final static int MAX_POSSIBLE_PIXEL_VALUE = (int) Math.pow(2, NUMBER_OF_BITS_PER_CHANNEL) - 1;

	int[] rgb;

	public RGBPixel(int[] rgb) {
		this.rgb = rgb;
	}

	public int getRGBColour() {
		return makeRGB(rgb[0], rgb[1], rgb[2]);
	}

	public int getChannel(Channel colour) {
		if (colour == Channel.RED) {
			return rgb[0];
		} else if (colour == Channel.GREEN) {
			return rgb[1];
		} else {
			return rgb[2];
		}

	}

	public static int makeRGB(int red, int green, int blue) {
		return new Color(red, green, blue).getRGB();
	}

	public RGBPixel makeGrayCodePixel() {
		int[] grayRGB = rgb.clone();
		for (int i = 0; i < grayRGB.length; i++) {
			grayRGB[i] = convertBinaryToGray(rgb[i]);
		}
		return new RGBPixel(grayRGB);
	}

	// This method is copied from https://www.geeksforgeeks.org/decimal-equivalent-gray-code-inverse/
	public static int convertBinaryToGray(int input) {
		return input ^ (input >> 1);
	}

	public RGBPixel makeBinaryCodePixelFromGray() {
		int[] binaryRGB = rgb.clone();
		for (int i = 0; i < binaryRGB.length; i++) {
			binaryRGB[i] = convertGrayToBinary(rgb[i]);
		}
		return new RGBPixel(binaryRGB);
	}

	static int convertGrayToBinary(int grayInt) {
		int binaryInt = 0;
		while (grayInt != 0) {
			binaryInt ^= grayInt;
			grayInt = grayInt >> 1;
		}
		return binaryInt;
	}

	public boolean bitPresent(Channel colour, int index) {
		if (((getChannel(colour) >> index) & 1) == 1) {
			return true;
		}
		return false;
	}
}
