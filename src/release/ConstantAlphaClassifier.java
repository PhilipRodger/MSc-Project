package release;

import java.util.HashMap;

public class ConstantAlphaClassifier extends SegmentManager{

	private double alphaCutoff = 0.3;
	
	
	public ConstantAlphaClassifier(double alphaCutoff) {
		super();
		this.alphaCutoff = alphaCutoff;
		complexityDefinition = new AlphaComplexity();
	}

	public ConstantAlphaClassifier(HashMap<String, String> params) {
		super();
		if (params.containsKey("threshold")) {
			alphaCutoff = Double.parseDouble(params.get("threshold"));
		}
		complexityDefinition = new AlphaComplexity();

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
