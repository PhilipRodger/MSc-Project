package minimal;

public abstract class ComplexityMeasure {
	public abstract int getComplexity(BitMap input);

	public abstract BitMap getConjugationMap(int frameWidth, int frameHeight);
}
