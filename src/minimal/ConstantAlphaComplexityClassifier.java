package minimal;


public class ConstantAlphaComplexityClassifier extends SegmentManager{

	private double alphaCutoff;
	
	// Does not change so can store it
	private int maxComplexity;
	
	public ConstantAlphaComplexityClassifier(BitImageSet source, double alphaCutoff, int segmentWidth, int segmentHeight) {
		super(source, segmentWidth, segmentHeight);
		this.alphaCutoff = alphaCutoff;
		complexityDefinition = new AlphaComplexity();
		maxComplexity = AlphaComplexity.maxComplexity(segmentWidth, segmentHeight);
		conjugationMask = complexityDefinition.getConjugationMap(segmentWidth, segmentHeight);
	}

	@Override
	protected boolean meetsCriteria(Coordinant candidate) {
		BitMap toCheck = source.extractSegment(candidate, segmentWidth, segmentHeight);
		return meetsCriteria(toCheck);
	}
	
	@Override
	protected boolean meetsCriteria(BitMap toCheck) {
		int complexity = complexityDefinition.getComplexity(toCheck);
		double alphaComplexity = getAlphaComplexity(complexity);
		if (alphaComplexity >= alphaCutoff) {
			return true;
		} else {
			return false;
		}
	}
	
	public double getAlphaComplexity(int complexity) {
		return complexity / (double) maxComplexity;
	}
}
