package experiments;

import java.awt.Color;

public class RGBPixel {
	int originalRed;
	int originalGreen;
	int originalBlue;
 
	public RGBPixel(int[] rgb) {
		originalRed = rgb[0];
		originalGreen = rgb[1];
		originalBlue = rgb[2];	
	}
	public int getOriginal() {
		return makeRGB(originalRed, originalGreen, originalBlue);
	}
	
	public int getOriginalRedChannel() {
		return makeRGB(originalRed, 0, 0);
	}
	
	public int getOriginalGreenChannel() {
		return makeRGB(0, originalGreen, 0);
	}
	
	public int getOriginalBlueChannel() {
		return makeRGB(0, 0, originalBlue);
	}
	
	public static int makeRGB(int red, int green, int blue) {
		return new Color(red, green, blue).getRGB();
	}
}
