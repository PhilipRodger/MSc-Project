package experiments;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

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
	
	public ArrayList<Coordinant> getFrameCorners(int frameWidth, int frameHeight) {
		ArrayList<Coordinant> candidatesUnchecked = new ArrayList<>();
		for (int x = 0; x + frameWidth - 1 < width; x += frameWidth) {
			for (int y = 0; y + frameHeight - 1 < height; y+= frameHeight) {
				candidatesUnchecked.add(new Coordinant(x, y));
			}
		}
		return candidatesUnchecked;
	}
	
	public int getComplexityOfSegment(Coordinant upperLeftHandCorner, int frameWidth, int frameHeight) {
		// Define edges of the rectangle:
		int minX = upperLeftHandCorner.getX();
		int maxX = minX + frameWidth;
		
		int minY = upperLeftHandCorner.getY();
		int maxY = minY + frameHeight;
		
		// Calculates the complexity of the rectange starting at the coordinant of the top left hand corner and extending out the width and height of the rectangle.
		int complexityCount = 0;
		
		// Work out horizontal complexity
		for (int y = minY; y < maxY; y++) {
			// A single horizontal slice.
			boolean walker = getBit(minX, y); //inital value
			for (int x = minX + 1; x < maxX; x++) {
				boolean compareBit = getBit(x, y);
				if (walker != compareBit) {
					complexityCount++;
				}
				walker = compareBit;
			}
		}
		
		// Work out vertical complexity
		for (int x = minX; x < maxX; x++) {
			// A single horizontal slice.
			boolean walker = getBit(x, minY); //inital value
			for (int y = minY + 1; y < maxY; y++) {
				boolean compareBit = getBit(x, y);
				if (walker != compareBit) {
					complexityCount++;
				}
				walker = compareBit;
			}
		}
		return complexityCount;
	}
	
	
	public static int maxComplexity(int frameWidth, int frameHeight) {
		int horizontalComplexity = frameHeight * (frameWidth - 1);
		int verticalComplexity = frameWidth * (frameHeight - 1);

		return horizontalComplexity + verticalComplexity;
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
	
	public int[] getBitMapCompexityDistribution(int width, int height) {
		ArrayList<Coordinant> candidates = getFrameCorners(width, height);
		return getComplexityDistribution(width, height, candidates);
	}
	
	public static int[] getRandomComplexityDistribution(int width, int height, int itterations) {
		int maxComplexity = maxComplexity(width, height);
		int[] distribution = new int[maxComplexity + 1];
		for (int i = 0; i < itterations; i++) {
			BitMap random = makeRandomMap(width, height);
			int complexity = random.getComplexityOfSegment(new Coordinant(0, 0), width, height);
			distribution[complexity]++;
		}
		return distribution;
	}
	
	public int[] getComplexityDistribution(int width, int height, ArrayList<Coordinant> candidates) {
		int maxComplexity = maxComplexity(width, height);
		int[] distribution = new int[maxComplexity + 1];
		for(Coordinant candidate: candidates) {
			int complexity = getComplexityOfSegment(candidate, width, height);
			distribution[complexity]++;
		}
		return distribution;
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
	
	public static double[] complexityAlpha(int width, int height) {
		int maxPossibleComplexity = maxComplexity(width, height);
		double[] alphaValuesforDistribution = new double[maxPossibleComplexity];
		for (int i = 0; i < alphaValuesforDistribution.length; i++) {
			alphaValuesforDistribution[i] = (i * 1.0) / maxPossibleComplexity;
		}
		return alphaValuesforDistribution;
	}
	
	public static void main(String[] args) {
		BitMap b = new BitMap(8, 8);
		b.setBit(1, 1);
		System.out.println(b);
		
		System.out.println(Arrays.toString(b.getBitMapCompexityDistribution(2, 2)));
		System.out.println(maxComplexity(3, 3));
		
		//System.out.println(Arrays.toString(getRandomComplexityDistribution(3, 2, 10000000)));
	}
}
