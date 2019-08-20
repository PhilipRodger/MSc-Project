package minimal;


public class constantAlphaComplexityClassifier extends SegmentClassifier{
	private double alphaCutoff;
	private int segmentWidth;
	private int segmentHeight;
	
	priva
	
	
	public constantAlphaComplexityClassifier(BitMap source) {
		super(source);
		//Sensible Defaults
		this.alphaCutoff = 0.3;
		this.segmentWidth = 8;
		this.segmentHeight = 8;
	}
	
	public constantAlphaComplexityClassifier(BitMap source, double alphaCutoff, int segmentWidth, int segmentHeight) {
		super(source);
		this.alphaCutoff = alphaCutoff;
		this.segmentWidth = segmentWidth;
		this.segmentHeight = segmentHeight;
	}

	@Override
	public boolean meetsCriteria(Coordinant upperLeft) {
		BitMap toCheck = source.extractSegment(upperLeft, segmentWidth, segmentHeight);
		int complexity = AlphaComplexity.getComplexity(toCheck);
	}
	
	public double getAlphaComplexity() {
		return alphaComplexity;
	}
	
	
	
	public static double getAlphaComplexity(int complexity, int maxComplexity) {
		return complexity / (double) maxComplexity;
	}

}
