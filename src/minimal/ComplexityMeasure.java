package minimal;

public abstract class ComplexityMeasure {
	public abstract int getComplexity(BitMap input);

	protected abstract BitMap getConjugationMap(int frameWidth, int frameHeight);
}
