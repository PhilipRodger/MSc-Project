package minimal;

public class ModifiedAlphaComplexityClassifier extends SegmentManager{
	// Does not change so can store it
	private int maxComplexity;
	
	public ModifiedAlphaComplexityClassifier(BitImageSet source, int segmentWidth, int segmentHeight) {
		super(source, segmentWidth, segmentHeight);
		complexityDefinition = new AlphaComplexity();
		maxComplexity = AlphaComplexity.maxComplexity(segmentWidth, segmentHeight);
		conjugationMask = complexityDefinition.getConjugationMap(segmentWidth, segmentHeight);
	}

	@Override
	protected boolean meetsCriteriaForSegmentSelection(Coordinant candidate) {
		BitMap toCheck = source.extractSegment(candidate, segmentWidth, segmentHeight);
		int complexity = complexityDefinition.getComplexity(toCheck);
		double alphaComplexity = getAlphaComplexity(complexity);
		if (alphaComplexity >= getThreshold(candidate)) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected boolean meetsCriteriaForPayloadEmbed(BitMap toCheck) {
		int complexity = complexityDefinition.getComplexity(toCheck);
		double alphaComplexity = getAlphaComplexity(complexity);
		if (alphaComplexity > AlphaComplexity.middleComplexity) {
			return true;
		} else {
			return false;
		}
	}
	
	public static double getThreshold(Coordinant toCheck) {
		int bitIndex = toCheck.getBitMap();
		switch (bitIndex) {
		case 0:
			return 0.0;
		case 1:
			return 0.0;
		case 2:
			return 0.40;
		case 3:
			return 0.425;
		case 4:
			return 0.45;
		case 5:
			return 0.475;
		default:
			return 2;
		}
	}
	
	public double getAlphaComplexity(int complexity) {
		return complexity / (double) maxComplexity;
	}

}
