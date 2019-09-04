package release;

public class AlphaComplexity extends ComplexityMeasure {

	public static double middleComplexity = 0.5;

	public int getComplexity(BitMap input) {		
		// Define edges of the rectangle:
		int minX = 0;
		int maxX = input.getWidth();

		int minY = 0;
		int maxY = input.getHeight();

		// Calculates the complexity of the rectange starting at the coordinant of the
		// top left hand corner and extending out the width and height of the rectangle.
		int complexityCount = 0;

		// Work out horizontal complexity
		for (int y = minY; y < maxY; y++) {
			// A single horizontal slice.
			boolean walker = input.getBit(minX, y); // inital value
			for (int x = minX + 1; x < maxX; x++) {
				boolean compareBit = input.getBit(x, y);
				if (walker != compareBit) {
					complexityCount++;
				}
			walker = compareBit;
			}
		}

		// Work out vertical complexity
		for (int x = minX; x < maxX; x++) {
			// A single horizontal slice.
			boolean walker = input.getBit(x, minY); // inital value
			for (int y = minY + 1; y < maxY; y++) {
				boolean compareBit = input.getBit(x, y);
				if (walker != compareBit) {
					complexityCount++;
				}
				walker = compareBit;
			}
		}
		return complexityCount;
	}

	public int maxComplexity(int frameWidth, int frameHeight) {
		int horizontalComplexity = frameHeight * (frameWidth - 1);
		int verticalComplexity = frameWidth * (frameHeight - 1);

		return horizontalComplexity + verticalComplexity;
	}

	@Override
	public BitMap getConjugationMap(int frameWidth, int frameHeight) {
		boolean blackTopLeft = false;
		int remainder = 0;
		if (blackTopLeft) {
			remainder = 1;
		}
		
		BitMap map = new BitMap(frameWidth, frameHeight);
		for (int y = 0; y < frameHeight; y++) {
			for (int x = 0; x < frameWidth; x++) {
				if (((y + x) % 2) == remainder) {
					map.setBit(x, y);
				}
			}
		}
		return map;
	}
	
	@Override
	public String toString() {
		return "AlphaComplexity";
	}
}
