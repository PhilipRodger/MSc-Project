package experiments;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.BitSet;

public class BitMap {
	int width;
	int height;
	boolean[][] image;

	public BitMap(int width, int height) {
		this.width = width;
		this.height = height;
		image = new boolean[height][width];
		for (int i = 0; i < image.length; i++) {
			image[i] = new boolean[width];
		}
	}

	public void setBit(int x, int y) {
		image[y][x] = true;
	}

	public boolean getBit(int x, int y) {
		return image[y][x];
	}

	public BufferedImage getBitMapImage(int contrastRGB) {
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

	public static void main(String[] args) {
		BitMap b = new BitMap(5000, 4000);
		b.setBit(0, 0);
		b.setBit(3, 3);
		System.out.println(b);
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
}
