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
	
	public int getDiagonalComplexityOfSegment(Coordinant upperLeftHandCorner, int frameWidth, int frameHeight) {
		//int borderComplexity = getComplexityOfSegment(upperLeftHandCorner, frameWidth, frameHeight);
		//double diagonalComplexityContribution = (1/Math.sqrt(2)); // possible complexity of a diagonal change
		int complexity = 0;
		int numberOfSlices = frameWidth + frameHeight - 1;
		//System.out.println("Positive 45 degree");
		// Positive 45 degree
		int startX = 0;
		int startY = 0;
		for (int leftAndBottom = 0; leftAndBottom < numberOfSlices; leftAndBottom++) {

			boolean first = getBit(startX + upperLeftHandCorner.getX(), startY + upperLeftHandCorner.getY());
			
			{
				int x = startX;
				int y = startY;
				boolean walker = first;
				while(x < frameWidth && x >= 0 && y >=0 && y < frameHeight) {
					boolean compareTo = getBit(x + upperLeftHandCorner.getX(), y + upperLeftHandCorner.getY());
					if(walker != compareTo) {
						//System.out.println("Change: " + (x-1) + ", " + (y + 1) + " to "+  x + ", " + y);
						walker = compareTo;
						complexity++;
					}
					x++;
					y--;
				}
			}
			
			startY++;
			if(startY >= frameHeight) {
				startX++;
				startY--;
			}
		}
		
		//System.out.println("Negative 45 degree");
		startX = 0;
		startY = frameHeight - 1;
		for (int leftAndTop = 0; leftAndTop < numberOfSlices; leftAndTop++) {

			boolean first = getBit(startX + upperLeftHandCorner.getX(), startY + upperLeftHandCorner.getY());
			{
				int x = startX;
				int y = startY;
				boolean walker = first;
				while(x < frameWidth && x >= 0 && y >=0 && y < frameHeight) {
					boolean compareTo = getBit(x + upperLeftHandCorner.getX(), y + upperLeftHandCorner.getY());
					if(walker != compareTo) {
						//System.out.println("Change: " + (x-1) + ", " + (y + 1) + " to "+  x + ", " + y);
						walker = compareTo;
						complexity++;
					}
					x++;
					y++;
				}
			}
			
			startY--;
			if(startY < 0) {
				startX++;
				startY++;
			}
		}
		
		return complexity;
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
	
	public static int[] getRandomComplexityDistribution(int width, int height, int itterations, int timeOutSeconds) {
		int maxComplexity = maxComplexity(width, height);
		int[] distribution = new int[maxComplexity + 1];
		int timeInMilli = timeOutSeconds * 1000;
		long startTime = System.currentTimeMillis();
		boolean timedout = false;
		for (int i = 0; i < itterations && !timedout; i++) {
			BitMap random = makeRandomMap(width, height);
			int complexity = random.getComplexityOfSegment(new Coordinant(0, 0), width, height);
			distribution[complexity]++;
		    timedout = (System.currentTimeMillis() > (startTime + timeInMilli));

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
	
	public BitMap(BitMap toClone) {
		width = toClone.width;
		height = toClone.height;
		image = new boolean[toClone.image.length][];
		for (int i = 0; i < image.length; i++) {
			image[i] = toClone.image[i].clone();
		}
	}
	public static int[] getComplexityDistrobutionEdgePlusDiagonal(int width, int height, int itterations) {
		Coordinant upperLeft = new Coordinant(0,0);
		double diagonalComplexityContribution = (1/Math.sqrt(2)); // possible complexity of a diagonal change

		int[] distribution = new int[width*height*3];
		for (int i = 0; i < itterations; i++) {
			BitMap map =  makeRandomMap(width, height);
			int diagonalComplex = (int) Math.round((map.getDiagonalComplexityOfSegment(upperLeft, width, height) * diagonalComplexityContribution));
			int edgeComplex = map.getComplexityOfSegment(upperLeft, width, height);
			int totalComplexity = edgeComplex + diagonalComplex;
			distribution[totalComplexity]++;
		}
		
		return distribution;
	}
	
	public static void printComplexityComparison(int width, int height, int itterations) {
		// Trying to find out diagonal complexity:
				Coordinant upperLeft = new Coordinant(0,0);
				double diagonalComplexityContribution = (1/Math.sqrt(2)); // possible complexity of a diagonal change
				//double diagonalComplexityContribution = 1; // possible complexity of a diagonal change

				// getting extremes of diagonal complexity
				BitMap minDiagonal =  makeRandomMap(width, height);
				int minD = (int) ( minDiagonal.getDiagonalComplexityOfSegment(upperLeft, width, height) * diagonalComplexityContribution);
				BitMap maxDiagonal =  makeRandomMap(width, height);
				int maxD = (int) ( maxDiagonal.getDiagonalComplexityOfSegment(upperLeft, width, height) * diagonalComplexityContribution);

				// getting extremes of Normal Ege complexity
				BitMap minEdge =  makeRandomMap(width, height);
				int minE = minEdge.getComplexityOfSegment(upperLeft, width, height);
				BitMap maxEdge =  makeRandomMap(width, height);
				int maxE = maxEdge.getComplexityOfSegment(upperLeft, width, height);

				// getting extremes of combined complexity
				BitMap minTotal =  makeRandomMap(width, height);
				int minT = minTotal.getComplexityOfSegment(upperLeft, width, height) +(int) ( minTotal.getDiagonalComplexityOfSegment(upperLeft, width, height) * diagonalComplexityContribution);
				BitMap maxTotal =  makeRandomMap(width, height);
				int maxT = maxTotal.getComplexityOfSegment(upperLeft, width, height) +  (int)( maxTotal.getDiagonalComplexityOfSegment(upperLeft, width, height) * diagonalComplexityContribution);

				for (int i = 0; i < itterations; i++) {
					BitMap map =  makeRandomMap(width, height);
					int diagonalComplex = (int) Math.round((map.getDiagonalComplexityOfSegment(upperLeft, width, height) * diagonalComplexityContribution));
					int edgeComplex = map.getComplexityOfSegment(upperLeft, width, height);
					int totalComplexity = edgeComplex + diagonalComplex ;
					
					// New extreme check for diagonal
					if (diagonalComplex > maxD) {
						maxDiagonal = map;
						maxD = diagonalComplex;
					} else if (diagonalComplex < minD) {
						minDiagonal = map;
						minD = diagonalComplex;
					}
					
					// New extreme check for edge
					if (edgeComplex > maxE) {
						maxEdge = map;
						maxE = edgeComplex;
					} else if (edgeComplex < minE) {
						minEdge = map;
						minE = edgeComplex;
					}
					
					// New extreme check for total
					if (totalComplexity > maxT) {
						maxTotal = map;
						maxT = totalComplexity;
					} else if (totalComplexity < minT) {
						minTotal = map;
						minT = totalComplexity;
					} 
				}
				System.out.println("Minimum Diagonal Complexity: " + minD);
				System.out.println(minDiagonal);
				
				System.out.println("Maximum Diagonal Complexity: " + maxD);
				System.out.println(maxDiagonal);
				
				System.out.println("Minimum Edge Complexity: " + minE);
				System.out.println(minEdge);
				
				System.out.println("Maximum Edge Complexity: " + maxE);
				System.out.println(maxEdge);
				
				System.out.println("Minimum Total Complexity: " + minT);
				System.out.println(minTotal);
				
				System.out.println("Maximum Total Complexity: " + maxT);
				System.out.println(maxTotal);
	}
	
	public static void printRandomDistribution(int segmentWidth, int segmentHeight) {
		int[] a = BitMap.getRandomComplexityDistribution(segmentWidth, segmentHeight, 1000000, 60);
		double[] percent = BitImageSet.distributionAsPercent(a);
		double[] complexityAlphas = BitMap.complexityAlpha(segmentWidth, segmentHeight);
		for (int i = 0; i < complexityAlphas.length; i++) {
			if (percent[i] != 0) {
				System.out.println(complexityAlphas[i] + "\t" + percent[i]);
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

		
		//printRandomDistribution(2, 2);


//		
//		BitMap bitMap = makeRandomMap(width, height);
//		System.out.println(bitMap);
//		System.out.println("Diagonal Complexity: " + bitMap.getDiagonalComplexityOfSegment(new Coordinant(0, 0), width, height));
//		System.out.println("Alpha Complexity: " + bitMap.getComplexityOfSegment(new Coordinant(0, 0), width, height));

		//Compare evaluation of complexity using diagonal complexity, edge complexity and a combined value 
		//printComplexityComparison(8, 8, 200000);
		int width = 8;
		int height = 8;
		int itterations = 100000;
		int[] combined = getComplexityDistrobutionEdgePlusDiagonal(width, height, itterations);
		double[] combinedPercent = BitImageSet.distributionAsPercent(combined);
		for (int i = 0; i < combinedPercent.length; i++) {
			if (combinedPercent[i] > .2) {
				System.out.println(i + ": " + combinedPercent[i] + "%");
			}
		}
		int[] edgeDistribution = getRandomComplexityDistribution(width, height, itterations, 10000);
		double[] edgePercent = BitImageSet.distributionAsPercent(edgeDistribution);
		for (int i = 0; i < edgePercent.length; i++) {
			if (edgePercent[i] > .2) {
				System.out.println(i + ": " + edgePercent[i] + "%");
			}
		}
	}
}
