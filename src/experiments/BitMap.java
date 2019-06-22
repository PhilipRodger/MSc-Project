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
	public void setBit(int x, int y, boolean value) {
		image[y][x] = value;
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
	
	public static double getAlphaComplexity(int complexity, int maxComplexity) {
		return complexity / (double) maxComplexity;
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
	
	public static int[] getComplexityDistributionAboveAlphaComplexity(int width, int height, int itterations, double minComplexity) {
		int maxComplexity = maxComplexity(width, height);
		int[] distribution = new int[maxComplexity + 1];
		for (int i = 0; i < itterations; i++) {
			BitMap random = makeRandomWithMinimumComplexity(width, height, minComplexity);
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
	
	public ArrayList<Coordinant>  getCoordinantsWithinComplexityRange(int width, int height, ArrayList<Coordinant> candidates, double minComplexityRange, double maxComplexityRange) {
		int maxComplexity = maxComplexity(width, height);
		ArrayList<Coordinant> withinRange = new ArrayList<>();
		for(Coordinant candidate: candidates) {
			int complexity = getComplexityOfSegment(candidate, width, height);
			double alphaComplexity = getAlphaComplexity(complexity, maxComplexity);
			if (minComplexityRange <= alphaComplexity && alphaComplexity <= maxComplexityRange) {
				withinRange.add(candidate);
			}
		}
		return withinRange;
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
	
	public BufferedImage getBitMapImageBlackReplaced(int pureColour, int frameWidth, int frameHeight, double minimumComplexity, double maxComplexityRange) {
		// TODO Auto-generated method stub
		BufferedImage image = getBitMapImage(pureColour);
		
		ArrayList<Coordinant> candidates = getFrameCorners(frameWidth, frameHeight);
		ArrayList<Coordinant> coordinantsInComplexityRange = getCoordinantsWithinComplexityRange(frameWidth, frameHeight, candidates , minimumComplexity, maxComplexityRange);
		for (Coordinant coordinant : coordinantsInComplexityRange) {
			replaceCoordinantWithBlack(image, coordinant, frameWidth, frameHeight);
		}
		return image;
	}
	
	public BufferedImage replaceCoordinantWithBlack(BufferedImage image, Coordinant toReplace, int frameWidth, int frameHeight) {
		int minX = toReplace.getX();
		int maxX = toReplace.getX() + frameWidth;
		int minY = toReplace.getY();
		int maxY = toReplace.y + frameHeight;
		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {
				image.setRGB(x, y, Color.BLACK.getRGB());
			}
		}
		return image;
	}
	
	public static BitMap makeCheckerBoardMap (int width, int height) {
		BitMap map = new BitMap(width, height);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (((y + x) % 2) == 0) {
					map.setBit(x, y);
				}
			}
			
		}
		return map;
	}
	public static BitMap xOr(BitMap firstBitMap, BitMap secondBitMap) {
		BitMap result = new BitMap(firstBitMap.width, firstBitMap.height);
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
	public double getAlphaComplexity() {
		int maxComplexity = maxComplexity(this.width, this.height);
		return BitMap.getAlphaComplexity(getComplexityOfSegment(new Coordinant(0, 0), this.width, this.height), maxComplexity);
	}
	
	public static BitMap makeRandomWithMinimumComplexity(int width, int height, double minimumAlphaComplexity) {
		BitMap candidate = makeRandomMap(width, height);		
		if (candidate.getAlphaComplexity() < minimumAlphaComplexity) {
			BitMap mask = makeCheckerBoardMap(width, height);
			candidate = xOr(candidate, mask);
		}
		return candidate;
	}


	
	public int replaceWithRandomSegmentsAboveAlphaComplexity(int segmentWidth, int segmentHeight, double minimumComplexity) {
		ArrayList<Coordinant> allCandidates = getFrameCorners(segmentWidth, segmentHeight);
		ArrayList<Coordinant> replace = getCoordinantsWithinComplexityRange(segmentWidth, segmentHeight, allCandidates, minimumComplexity, 1);
		for (Coordinant coordinant : replace) {
			BitMap randomSegment = makeRandomWithMinimumComplexity(segmentWidth, segmentHeight, minimumComplexity);
			replaceCoordinantWithBitmap(coordinant, randomSegment);
		}
		return replace.size();
	}
	
	public void replaceCoordinantWithBitmap(Coordinant upperLeftHandCorner, BitMap replacement) {
		for (int replacementX = 0; replacementX < replacement.width; replacementX++) {
			for (int replacementY = 0; replacementY < replacement.height; replacementY++) {
				setBit(upperLeftHandCorner.getX() + replacementX, upperLeftHandCorner.getY() + replacementY, replacement.getBit(replacementX, replacementY));
			}
		}
	}
	
	public static void main(String[] args) {

		//System.out.println(maxComplexity(8, 8));

		
		//System.out.println(Arrays.toString(bitMap.getBitMapCompexityDistribution(2, 2)));
				
		
		// Demonstration of conjugation 
//		BitMap bitMap = new BitMap(8, 8);
//		bitMap.setBit(1, 1);
//		System.out.println(bitMap);
//		System.out.println("Complexity = " + bitMap.getComplexityOfSegment(new Coordinant(0, 0), 8, 8) + "/" + maxComplexity(8, 8));
//		
//		BitMap checkerBoard = makeCheckerBoardMap(8, 8);
//		System.out.println(checkerBoard);
//		System.out.println("Complexity = " + checkerBoard.getComplexityOfSegment(new Coordinant(0, 0), 8, 8) + "/" + maxComplexity(8, 8));
//		
//		BitMap xOr = xOr(bitMap, checkerBoard);
//		System.out.println(xOr);
//		System.out.println("Complexity = " + xOr.getComplexityOfSegment(new Coordinant(0, 0), 8, 8) + "/" + maxComplexity(8, 8));
//		
//		BitMap xOrXor = xOr(xOr, checkerBoard);
//		System.out.println(xOrXor);
//		System.out.println("Complexity = " + xOrXor.getComplexityOfSegment(new Coordinant(0, 0), 8, 8) + "/" + maxComplexity(8, 8));
		
		//Testing effect on distribution when complexity cut off used. 
		System.out.println(Arrays.toString(getRandomComplexityDistribution(8, 8, 10000000)));
		System.out.println(Arrays.toString(getComplexityDistributionAboveAlphaComplexity(3024, 8, 10000000, 0.300)));

		

	}
}
