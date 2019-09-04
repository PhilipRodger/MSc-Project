package release;

import java.util.HashMap;

// Implementation of Modified BPCS described in:
// "An Improved BPCS Steganography Based on Dynamic Threshold"
// https://ieeexplore.ieee.org/document/5670979
public class BPCS_Modified extends BPCS {

	// Use Default Settings
	public BPCS_Modified(String vessilPath) {
		super(vessilPath);
		manager = new ModifiedAlphaComplexityClassifier(inputImage, segmentWidth, segmentHeight);
	}

	// Use Custom Settings
	public BPCS_Modified(String vessilPath, int segmentWidth, int segmentHeight) {
		super(vessilPath);
		this.segmentWidth = segmentWidth;
		this.segmentHeight = segmentHeight;
		manager = new ModifiedAlphaComplexityClassifier(inputImage, segmentWidth, segmentHeight);
	}
	
	// Use Custom Settings
	public BPCS_Modified(HashMap<String, String> params) {
		super(params);
		manager = new ModifiedAlphaComplexityClassifier(inputImage, segmentWidth, segmentHeight);
	}

	@Override
	public String toString() {
		return " Algorithim=BPCS_Modified SegmentWidth=" + segmentWidth + " SegmentHeight=" + segmentHeight;
	}
}
