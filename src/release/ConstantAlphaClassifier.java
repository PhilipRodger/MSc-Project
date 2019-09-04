package release;


public class ConstantAlphaClassifier extends SegmentManager{

	private double alphaCutoff;
	
	// Does not change so can store it
	private int maxComplexity;
	
	public ConstantAlphaClassifier(BitImageSet source, double alphaCutoff, int segmentWidth, int segmentHeight) {
		super(source, segmentWidth, segmentHeight);
		this.alphaCutoff = alphaCutoff;
		complexityDefinition = new AlphaComplexity();
		maxComplexity = complexityDefinition.maxComplexity(segmentWidth, segmentHeight);
		conjugationMask = complexityDefinition.getConjugationMap(segmentWidth, segmentHeight);
	}

	@Override
	protected boolean meetsCriteriaForSegmentSelection(Coordinant candidate) {
		BitMap toCheck = source.extractSegment(candidate, segmentWidth, segmentHeight);
		return meetsCriteriaForPayloadEmbed(toCheck);
	}
	
	@Override
	protected boolean meetsCriteriaForPayloadEmbed(BitMap toCheck) {
		int complexity = complexityDefinition.getComplexity(toCheck);
		double alphaComplexity = getAlphaComplexity(complexity);
		if (alphaComplexity >= alphaCutoff) {
			return true;
		} else {
			return false;
		}
	}
	
	private double getAlphaComplexity(int complexity) {
		return complexity / (double) maxComplexity;
	}
}
