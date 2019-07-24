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

	public static int getMaxDiagonalComplexity(int width, int height) {
		if (width < 2 || height < 2) {
			return 0;
		}
		return 2 * ((width - 1) * (height - 1));
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

	public ArrayList<Coordinant> getFrameCorners(int frameWidth, int frameHeight) {
		ArrayList<Coordinant> candidatesUnchecked = new ArrayList<>();
		for (int x = 0; x + frameWidth - 1 < width; x += frameWidth) {
			for (int y = 0; y + frameHeight - 1 < height; y += frameHeight) {
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

		// Calculates the complexity of the rectange starting at the coordinant of the
		// top left hand corner and extending out the width and height of the rectangle.
		int complexityCount = 0;

		// Work out horizontal complexity
		for (int y = minY; y < maxY; y++) {
			// A single horizontal slice.
			boolean walker = getBit(minX, y); // inital value
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
			boolean walker = getBit(x, minY); // inital value
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

	public double getAdjustedComplexityOfSegment(Coordinant upperLeftHandCorner, int frameWidth, int frameHeight) {
		double alpha = getAlphaComplexity(getComplexityOfSegment(upperLeftHandCorner, frameWidth, frameHeight),
				maxComplexity(frameWidth, frameHeight));
		alpha *= 2;
		return 1 - Math.abs(1 - alpha);
	}

	public int getDiagonalComplexityOfSegment(Coordinant upperLeftHandCorner, int frameWidth, int frameHeight) {
		// int borderComplexity = getComplexityOfSegment(upperLeftHandCorner,
		// frameWidth, frameHeight);
		// double diagonalComplexityContribution = (1/Math.sqrt(2)); // possible
		// complexity of a diagonal change
		int complexity = 0;
		int numberOfSlices = frameWidth + frameHeight - 1;
		// System.out.println("Positive 45 degree");
		// Positive 45 degree
		int startX = 0;
		int startY = 0;
		for (int leftAndBottom = 0; leftAndBottom < numberOfSlices; leftAndBottom++) {

			boolean first = getBit(startX + upperLeftHandCorner.getX(), startY + upperLeftHandCorner.getY());

			{
				int x = startX;
				int y = startY;
				boolean walker = first;
				while (x < frameWidth && x >= 0 && y >= 0 && y < frameHeight) {
					boolean compareTo = getBit(x + upperLeftHandCorner.getX(), y + upperLeftHandCorner.getY());
					if (walker != compareTo) {
						// System.out.println("Change: " + (x-1) + ", " + (y + 1) + " to "+ x + ", " +
						// y);
						walker = compareTo;
						complexity++;
					}
					x++;
					y--;
				}
			}

			startY++;
			if (startY >= frameHeight) {
				startX++;
				startY--;
			}
		}

		// System.out.println("Negative 45 degree");
		startX = 0;
		startY = frameHeight - 1;
		for (int leftAndTop = 0; leftAndTop < numberOfSlices; leftAndTop++) {

			boolean first = getBit(startX + upperLeftHandCorner.getX(), startY + upperLeftHandCorner.getY());
			{
				int x = startX;
				int y = startY;
				boolean walker = first;
				while (x < frameWidth && x >= 0 && y >= 0 && y < frameHeight) {
					boolean compareTo = getBit(x + upperLeftHandCorner.getX(), y + upperLeftHandCorner.getY());
					if (walker != compareTo) {
						// System.out.println("Change: " + (x-1) + ", " + (y + 1) + " to "+ x + ", " +
						// y);
						walker = compareTo;
						complexity++;
					}
					x++;
					y++;
				}
			}

			startY--;
			if (startY < 0) {
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

	public static int[] getRandomComplexityDistribution(int width, int height, int itterations, int timeOutSeconds, double minimumCuttoff) {
		int maxComplexity = maxComplexity(width, height);
		int[] distribution = new int[maxComplexity + 1];
		int timeInMilli = timeOutSeconds * 1000;
		long startTime = System.currentTimeMillis();
		boolean timedout = false;
		for (int i = 0; i < itterations && !timedout; i++) {
			BitMap random = makeRandomWithMinimumComplexity(width, height, minimumCuttoff);
			int complexity = random.getComplexityOfSegment(new Coordinant(0, 0), width, height);
			distribution[complexity]++;
			timedout = (System.currentTimeMillis() > (startTime + timeInMilli));

		}
		return distribution;
	}
	
	public static void printRandomDistribution(int width, int height, int itterations, double complexityCutoff) {
		int[] complexities = getRandomComplexityDistribution(width, height, itterations, 999999, complexityCutoff);
		double[] percent = BitImageSet.distributionAsPercent(complexities);

		double[] complexityAlphas = BitMap.complexityAlpha(width, height);
		System.out.println("Alpha Complexity	Frequency (%)");
		for (int i = 0; i < complexityAlphas.length; i++) {
				System.out.println(String.format("%f	%f", complexityAlphas[i], percent[i]));
		}
	}

	public static int[] getComplexityDistributionAboveAlphaComplexity(int width, int height, int itterations,
			double minComplexity) {
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
		for (Coordinant candidate : candidates) {
			int complexity = getComplexityOfSegment(candidate, width, height);
			distribution[complexity]++;
		}
		return distribution;
	}

	public ArrayList<Coordinant> getCoordinantsWithinComplexityRange(int width, int height,
			ArrayList<Coordinant> candidates, double minComplexityRange, double maxComplexityRange) {
		int maxComplexity = maxComplexity(width, height);
		ArrayList<Coordinant> withinRange = new ArrayList<>();
		for (Coordinant candidate : candidates) {
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
		double[] alphaValuesforDistribution = new double[maxPossibleComplexity + 1];
		for (int i = 0; i < alphaValuesforDistribution.length; i++) {
			alphaValuesforDistribution[i] = (i * 1.0) / maxPossibleComplexity;
		}
		return alphaValuesforDistribution;
	}

	public BufferedImage getBitMapImageBlackReplaced(int pureColour, int frameWidth, int frameHeight,
			double minimumComplexity, double maxComplexityRange) {
		// TODO Auto-generated method stub
		BufferedImage image = getBitMapImage(pureColour);

		ArrayList<Coordinant> candidates = getFrameCorners(frameWidth, frameHeight);
		ArrayList<Coordinant> coordinantsInComplexityRange = getCoordinantsWithinComplexityRange(frameWidth,
				frameHeight, candidates, minimumComplexity, maxComplexityRange);
		for (Coordinant coordinant : coordinantsInComplexityRange) {
			replaceCoordinantWithBlack(image, coordinant, frameWidth, frameHeight);
		}
		return image;
	}

	public BufferedImage replaceCoordinantWithBlack(BufferedImage image, Coordinant toReplace, int frameWidth,
			int frameHeight) {
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

	public static BitMap makeCheckerBoardMap(int width, int height) {
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
		return BitMap.getAlphaComplexity(getComplexityOfSegment(new Coordinant(0, 0), this.width, this.height),
				maxComplexity);
	}

	public static BitMap makeRandomWithMinimumComplexity(int width, int height, double minimumAlphaComplexity) {
		BitMap candidate = makeRandomMap(width, height);
		if (candidate.getAlphaComplexity() < minimumAlphaComplexity) {
			BitMap mask = makeCheckerBoardMap(width, height);
			candidate = xOr(candidate, mask);
		}
		return candidate;
	}

	public int replaceWithRandomSegmentsAboveAlphaComplexity(int segmentWidth, int segmentHeight,
			double minimumComplexity) {
		ArrayList<Coordinant> allCandidates = getFrameCorners(segmentWidth, segmentHeight);
		ArrayList<Coordinant> replace = getCoordinantsWithinComplexityRange(segmentWidth, segmentHeight, allCandidates,
				minimumComplexity, 1);
		for (Coordinant coordinant : replace) {
			BitMap randomSegment = makeRandomWithMinimumComplexity(segmentWidth, segmentHeight, minimumComplexity);
			replaceCoordinantWithBitmap(coordinant, randomSegment);
		}
		return replace.size();
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
	
	public BitMap getConjugate() {
		BitMap mask = makeCheckerBoardMap(width, height);
		return  xOr(this, mask);
	}

	public static int[] getComplexityDistrobutionEdgePlusDiagonal(int width, int height, int itterations) {
		Coordinant upperLeft = new Coordinant(0, 0);
		double diagonalComplexityContribution = 1; // possible complexity of a diagonal change

		int[] distribution = new int[width * height * 3];
		for (int i = 0; i < itterations; i++) {
			BitMap map = makeRandomMap(width, height);
			int diagonalComplex = (int) Math.round(
					(map.getDiagonalComplexityOfSegment(upperLeft, width, height) * diagonalComplexityContribution));
			int edgeComplex = map.getComplexityOfSegment(upperLeft, width, height);
			int totalComplexity = edgeComplex + diagonalComplex;
			distribution[totalComplexity]++;
		}

		return distribution;
	}

	public static void saveScatterPlotOfComplexities(int width, int height, int itterations, int scatterplotDimention) {
		BufferedImage scatterPlot = new BufferedImage(scatterplotDimention, scatterplotDimention,
				BufferedImage.TYPE_INT_RGB);
		// Trying to find out diagonal complexity:
		Coordinant upperLeft = new Coordinant(0, 0);

		int scatterplotDimentionIndex = scatterplotDimention - 1;
		for (int x = 0; x < scatterplotDimention; x++) {
			for (int y = 0; y < scatterplotDimention; y++) {
				scatterPlot.setRGB(x, y, Color.WHITE.getRGB());
			}
		}
		int maxPossibleDiagonalComplexity = getMaxDiagonalComplexity(width, height);
		int maxPossibleEdgeComplexity = maxComplexity(width, height);

		for (int i = 0; i < itterations; i++) {
			BitMap map = makeRandomMap(width, height);
			double diagonalComplex = (((double) map.getDiagonalComplexityOfSegment(upperLeft, width, height))
					/ maxPossibleDiagonalComplexity);
			double edgeComplex = ((double) map.getComplexityOfSegment(upperLeft, width, height))
					/ maxPossibleEdgeComplexity;
			scatterPlot.setRGB((int) (edgeComplex * scatterplotDimentionIndex),
					(int) (diagonalComplex * scatterplotDimentionIndex), Color.BLACK.getRGB());
		}
		BitImageSet.saveImage(scatterPlot, "XIsEdgeYDiagonalComplexity.bmp");
	}
	
	public static void saveSmileConjugateExample() {
		// Demonstration of conjugation
		
		//make smiley face bitmap
				BitMap bitMap = new BitMap(8, 8);
				bitMap.setBit(1, 1);
				bitMap.setBit(2, 1);
				bitMap.setBit(1, 2);
				bitMap.setBit(2, 2);
				
				bitMap.setBit(6, 1);
				bitMap.setBit(5, 1);
				bitMap.setBit(6, 2);
				bitMap.setBit(5, 2);
				
				bitMap.setBit(1, 4);
				bitMap.setBit(1, 5);
				
				bitMap.setBit(2, 5);
				bitMap.setBit(2, 6);
				//bitMap.setBit(3, 5);
				bitMap.setBit(3, 6);
				//bitMap.setBit(4, 5);
				bitMap.setBit(4, 6);
				bitMap.setBit(5, 5);
				bitMap.setBit(5, 6);
				bitMap.setBit(6, 4);
				bitMap.setBit(6, 5);

				System.out.println(bitMap);
				System.out.println("Complexity = " + bitMap.getComplexityOfSegment(new Coordinant(0, 0), 8, 8) + "/" + maxComplexity(8, 8));
				BitImageSet.saveImage(bitMap.getBitMapImage(Color.BLACK.getRGB(), 50), "originalSmile"+"Complexity" + bitMap.getComplexityOfSegment(new Coordinant(0, 0), 8, 8) + "Of" + maxComplexity(8, 8)+".bmp");
				
				BitMap checkerBoard = makeCheckerBoardMap(8, 8);
				System.out.println(checkerBoard);
				System.out.println("Complexity = " + checkerBoard.getComplexityOfSegment(new Coordinant(0, 0), 8, 8) + "/" + maxComplexity(8, 8));
				BitImageSet.saveImage(checkerBoard.getBitMapImage(Color.BLACK.getRGB(), 50), "checkerBoard"+"Complexity" + checkerBoard.getComplexityOfSegment(new Coordinant(0, 0), 8, 8) + "Of"+ maxComplexity(8, 8)+".bmp");

				
				BitMap xOr = xOr(bitMap, checkerBoard);
				System.out.println(xOr);
				System.out.println("Complexity = " + xOr.getComplexityOfSegment(new Coordinant(0, 0), 8, 8) + "-" + maxComplexity(8, 8));
				BitImageSet.saveImage(xOr.getBitMapImage(Color.BLACK.getRGB(), 50), "xOrSmile"+"Complexity" + xOr.getComplexityOfSegment(new Coordinant(0, 0), 8, 8) + "Of"+ maxComplexity(8, 8)+".bmp");

				
				BitMap xOrXor = xOr(xOr, checkerBoard);
				System.out.println(xOrXor);
				System.out.println("Complexity = " + xOrXor.getComplexityOfSegment(new Coordinant(0, 0), 8, 8) + "/" + maxComplexity(8, 8));
				BitImageSet.saveImage(xOrXor.getBitMapImage(Color.BLACK.getRGB(), 50), "xOrXOrSmile"+"Complexity" + xOrXor.getComplexityOfSegment(new Coordinant(0, 0), 8, 8) + "Of" + maxComplexity(8, 8)+".bmp");
	}

	public static void saveComplexityComparison(int width, int height, int itterations, int outPutScale) {

		// Trying to find out diagonal complexity:
		Coordinant upperLeft = new Coordinant(0, 0);
		int maxPossibleDiagonalComplexity = getMaxDiagonalComplexity(width, height);
		int maxPossibleEdgeComplexity = maxComplexity(width, height);
		int maxPossibleComplexityTotal = maxPossibleDiagonalComplexity + maxPossibleEdgeComplexity;

		// getting starting values of complexity
		BitMap dumby = makeRandomMap(width, height);
		int dumbyDiagonalComplexity = dumby.getDiagonalComplexityOfSegment(upperLeft, width, height);
		int dumbyEdgeComplexity = dumby.getComplexityOfSegment(upperLeft, width, height);

		BitMap minDiagonal, maxDiagonal, minEdge, maxEdge, minTotal, maxTotal;
		minDiagonal = maxDiagonal = minEdge = maxEdge = minTotal = maxTotal = dumby;
		double minD = (((double) dumbyDiagonalComplexity) / maxPossibleDiagonalComplexity);
		double maxD = minD;
		// getting extremes of Normal Edge complexity
		double minE = ((double) dumbyEdgeComplexity) / maxPossibleEdgeComplexity;
		double maxE = minE;

		// getting extremes of combined complexity
		double minT = ((double) dumbyDiagonalComplexity + dumbyEdgeComplexity) / maxPossibleComplexityTotal;
		double maxT = minT;

		for (int i = 0; i < itterations; i++) {
			BitMap map = makeRandomMap(width, height);
			int mapDiagonalComplexity = map.getDiagonalComplexityOfSegment(upperLeft, width, height);
			int mapEdgeComplexity = map.getComplexityOfSegment(upperLeft, width, height);

			double diagonalComplex = ((double) mapDiagonalComplexity) / maxPossibleDiagonalComplexity;
			double edgeComplex = ((double) mapEdgeComplexity) / maxPossibleEdgeComplexity;
			double totalComplexity = ((double) mapDiagonalComplexity + mapEdgeComplexity) / maxPossibleComplexityTotal;

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
		BitImageSet.saveImage(minDiagonal.getBitMapImage(Color.BLACK.getRGB(), outPutScale),
				"MinimumDiagonalComplexity" + minD + "Over" + itterations + "RandomMaps.bmp");

		System.out.println("Maximum Diagonal Complexity: " + maxD);
		System.out.println(maxDiagonal);
		BitImageSet.saveImage(maxDiagonal.getBitMapImage(Color.BLACK.getRGB(), outPutScale),
				"MaximumDiagonalComplexity" + maxD + "Over" + itterations + "RandomMaps.bmp");

		System.out.println("Minimum Edge Complexity: " + minE);
		System.out.println(minEdge);
		BitImageSet.saveImage(minEdge.getBitMapImage(Color.BLACK.getRGB(), outPutScale),
				"MinimumEdgeComplexity" + minE + "Over" + itterations + "RandomMaps.bmp");

		System.out.println("Maximum Edge Complexity: " + maxE);
		System.out.println(maxEdge);
		BitImageSet.saveImage(maxEdge.getBitMapImage(Color.BLACK.getRGB(), outPutScale),
				"MaximumEdgeComplexity" + maxE + "Over" + itterations + "RandomMaps.bmp");

		System.out.println("Minimum Total Complexity: " + minT);
		System.out.println(minTotal);
		BitImageSet.saveImage(minTotal.getBitMapImage(Color.BLACK.getRGB(), outPutScale),
				"MinimumTotalComplexity" + minT + "Over" + itterations + "RandomMaps.bmp");

		System.out.println("Maximum Total Complexity: " + maxT);
		System.out.println(maxTotal);
		BitImageSet.saveImage(maxTotal.getBitMapImage(Color.BLACK.getRGB(), outPutScale),
				"MaximumTotalComplexity" + maxT + "Over" + itterations + "RandomMaps.bmp");
	}

	public static void printRandomDistribution(int segmentWidth, int segmentHeight) {
		int[] a = BitMap.getRandomComplexityDistribution(segmentWidth, segmentHeight, 1000000, 60, 2);
		double[] percent = BitImageSet.distributionAsPercent(a);
		double[] complexityAlphas = BitMap.complexityAlpha(segmentWidth, segmentHeight);
		for (int i = 0; i < complexityAlphas.length; i++) {
			if (percent[i] != 0) {
				System.out.println(complexityAlphas[i] + "\t" + percent[i]);
			}

		}
	}

	public static void main(String[] args) {

		// System.out.println(maxComplexity(8, 8));

		// System.out.println(Arrays.toString(bitMap.getBitMapCompexityDistribution(2,
		// 2)));

		saveSmileConjugateExample();


		// Testing effect on distribution when complexity cut off used.

		// printRandomDistribution(2, 2);

//		
//		BitMap bitMap = makeRandomMap(width, height);
//		System.out.println(bitMap);
//		System.out.println("Diagonal Complexity: " + bitMap.getDiagonalComplexityOfSegment(new Coordinant(0, 0), width, height));
//		System.out.println("Alpha Complexity: " + bitMap.getComplexityOfSegment(new Coordinant(0, 0), width, height));

		// Compare evaluation of complexity using diagonal complexity, edge complexity
		// and a combined value
		// printComplexityComparison(8, 8, 200000);
//		int width = 8;
//		int height = 8;
//		int itterations = 100000;
//		int[] combined = getComplexityDistrobutionEdgePlusDiagonal(width, height, itterations);
//		double[] combinedPercent = BitImageSet.distributionAsPercent(combined);
//		for (int i = 0; i < combinedPercent.length; i++) {
//			if (combinedPercent[i] > .2) {
//				System.out.println(i + ": " + combinedPercent[i] + "%");
//			}
//		}
//		int[] edgeDistribution = getRandomComplexityDistribution(width, height, itterations, 10000);
//		double[] edgePercent = BitImageSet.distributionAsPercent(edgeDistribution);
//		for (int i = 0; i < edgePercent.length; i++) {
//			if (edgePercent[i] > .2) {
//				System.out.println(i + ": " + edgePercent[i] + "%");
//			}
//		}
//		for (int i = 0; i < 50; i++) {
//			saveComplexityComparison(8, 8, 10000000, 50);	
//		}
		// saveScatterPlotOfComplexities(8, 8, 500,100);

		BitMap map = makeRandomMap(8, 8);
		map = makeCheckerBoardMap(4, 4);
		System.out.println(map);
		//System.out.println(map.getAdjustedComplexityOfSegment(new Coordinant(0, 0), 8, 8));
		//BitImageSet.saveImage(map.getBitMapImage(Color.BLACK.getRGB(), 50), "checkerbord.bmp");
	}
}
