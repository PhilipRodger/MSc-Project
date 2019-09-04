package release;

public class DiagonalComplexity extends ComplexityMeasure {

	@Override
	public int getComplexity(BitMap input) {
		int complexity = 0;
		int numberOfSlices = input.getWidth() + input.getHeight() - 1;
		
		// Positive 45 degree
		int startX = 0;
		int startY = 0;
		for (int leftAndBottom = 0; leftAndBottom < numberOfSlices; leftAndBottom++) {

			boolean first = input.getBit(startX, startY);

			{
				int x = startX;
				int y = startY;
				boolean walker = first;
				while (x < input.getWidth() && x >= 0 && y >= 0 && y < input.getHeight()) {
					boolean compareTo = input.getBit(x, y);
					if (walker != compareTo) {
						walker = compareTo;
						complexity++;
					}
					x++;
					y--;
				}
			}

			startY++;
			if (startY >= input.getHeight()) {
				startX++;
				startY--;
			}
		}

		// Negative 45 degree
		startX = 0;
		startY = input.getHeight() - 1;
		for (int leftAndTop = 0; leftAndTop < numberOfSlices; leftAndTop++) {

			boolean first = input.getBit(startX, startY);
			{
				int x = startX;
				int y = startY;
				boolean walker = first;
				while (x < input.getWidth() && x >= 0 && y >= 0 && y < input.getHeight()) {
					boolean compareTo = input.getBit(x, y);
					if (walker != compareTo) {
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

	@Override
	public BitMap getConjugationMap(int frameWidth, int frameHeight) {
		BitMap conjugate = new BitMap(frameWidth, frameHeight);
		boolean topRowBlack = true;
		for (int y = 0; y < frameHeight; y++) {
			for (int x = 0; x < frameWidth; x++) {
				conjugate.setBit(x, y, topRowBlack);
			}
			if (topRowBlack) {
				topRowBlack = false;
			} else {
				topRowBlack = true;
			}
		}
		return conjugate;
	}
	@Override
	public int maxComplexity(int width, int height) {
		if (width < 2 || height < 2) {
			return 0;
		}
		return 2 * ((width - 1) * (height - 1));
	}
	
	@Override
	public String toString() {
		return "DiagonalComplexity";
	}
}
