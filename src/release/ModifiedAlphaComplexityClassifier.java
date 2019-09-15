package release;

public class ModifiedAlphaComplexityClassifier extends SegmentManager{
	
	public ModifiedAlphaComplexityClassifier() {
		super();
		complexityDefinition = new AlphaComplexity();
	}

	@Override
	protected boolean meetsCriteriaForSegmentSelection(Coordinant candidate) {
		BitMap toCheck = source.extractSegment(candidate, segmentWidth, segmentHeight);
		int complexity = complexityDefinition.getComplexity(toCheck);
		double alphaComplexity = getNormalisedComplexity(complexity);
		if (alphaComplexity >= getThreshold(candidate)) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected boolean meetsCriteriaForPayloadEmbed(BitMap toCheck) {
		int complexity = complexityDefinition.getComplexity(toCheck);
		double alphaComplexity = getNormalisedComplexity(complexity);
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
	
	@Override
	public String toString() {
		return " Algorithim=BPCS_Modified SegmentWidth=" + segmentWidth + " SegmentHeight=" + segmentHeight;
	}

}
