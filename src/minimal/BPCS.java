package minimal;

import java.util.ArrayList;

public class BPCS{

	public int getFrameWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getFrameHeight() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public int getHeaderSize() {
		return headerBitSize;
	}

	public int getSegmentWidth() {
		return segmentWidth;
	}

	public int getSegmentHeight() {
		return segmentHeight;
	}

	public double getAlphaComplexity() {
		return alphaComplexity;
	}
	
	public static int maxComplexity(int frameWidth, int frameHeight) {
		int horizontalComplexity = frameHeight * (frameWidth - 1);
		int verticalComplexity = frameWidth * (frameHeight - 1);

		return horizontalComplexity + verticalComplexity;
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
	public static double getAlphaComplexity(int complexity, int maxComplexity) {
		return complexity / (double) maxComplexity;
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
	
	public double getAlphaComplexity() {
		int maxComplexity = maxComplexity(this.width, this.height);
		return BitMap.getAlphaComplexity(getComplexityOfSegment(new Coordinant(0, 0), this.width, this.height),
				maxComplexity);
	}
	
	public long getBitsToAddressBits() {
		return bitsToAddressBits;
	}

	public void setStegKey(String stegKey) {
		this.stegKey = stegKey;
	}

	public static int numberOfBitsToRepresent(long numberOfPossibilities) {
		int numberOfBitsRequired = 0;
		long maxPosibilitiesRepresentable = 1;
		while (maxPosibilitiesRepresentable < numberOfPossibilities) {
			maxPosibilitiesRepresentable = maxPosibilitiesRepresentable * 2;
			numberOfBitsRequired++;
		}
		return numberOfBitsRequired;
	}
}