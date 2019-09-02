package minimal;

import java.util.HashMap;

public class BPCS_Original extends BPCS {
	// Sensible Defaults
	private double alphaCutoff = 0.3;

	// Use Default Settings
	public BPCS_Original(String vessilPath) {
		super(vessilPath);
		manager = new ConstantAlphaComplexityClassifier(inputImage, alphaCutoff, segmentWidth, segmentHeight);
	}

	// Use Custom Settings
	public BPCS_Original(String vessilPath, double alphaCutoff, int segmentWidth, int segmentHeight) {
		super(vessilPath, segmentWidth, segmentHeight);
		this.alphaCutoff = alphaCutoff;
		manager = new ConstantAlphaComplexityClassifier(inputImage, alphaCutoff, segmentWidth, segmentHeight);
	}
	
	// Make from Command Line Arguments
	public BPCS_Original(HashMap<String, String> params) {
		super(params);		
		if (params.containsKey("threshold")) {
			alphaCutoff = Double.parseDouble(params.get("threshold"));
		}
		manager = new ConstantAlphaComplexityClassifier(inputImage, alphaCutoff, segmentWidth, segmentHeight);
	}
	
	@Override
	public String toString() {
		return " Algorithim=BPCS_Original AlphaCutoff=" + alphaCutoff + " SegmentWidth=" + segmentWidth
				+ " SegmentHeight=" + segmentHeight;
	}
}
