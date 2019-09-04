package release;

public abstract class ComplexityMeasure {
	public abstract int getComplexity(BitMap input);
	public abstract int maxComplexity(int width, int height);
	public abstract BitMap getConjugationMap(int frameWidth, int frameHeight);
}
