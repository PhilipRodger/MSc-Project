package minimal;

public class AlphaComplexity {
	public static int getComplexity (BitMap map) {
		// Define edges of the rectangle:
				int minX = 0;
				int maxX = map.getWidth() - 1;

				int minY = 0;
				int maxY = map.getHeight() - 1;

				// Calculates the complexity of the rectange starting at the coordinant of the
				// top left hand corner and extending out the width and height of the rectangle.
				int complexityCount = 0;

				// Work out horizontal complexity
				for (int y = minY; y < maxY; y++) {
					// A single horizontal slice.
					boolean walker = map.getBit(minX, y); // inital value
					for (int x = minX + 1; x < maxX; x++) {
						boolean compareBit = map.getBit(x, y);
						if (walker != compareBit) {
							complexityCount++;
						}
						walker = compareBit;
					}
				}

				// Work out vertical complexity
				for (int x = minX; x < maxX; x++) {
					// A single horizontal slice.
					boolean walker = map.getBit(x, minY); // inital value
					for (int y = minY + 1; y < maxY; y++) {
						boolean compareBit = map.getBit(x, y);
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
}
