package minimal;

import java.util.HashMap;

public class BPCS_Diagonal extends BPCS {
	// Sensible Defaults
	private double diagonalCutoff = 0.3;

	// Use Default Settings
	public BPCS_Diagonal(String vessilPath) {
		super(vessilPath);
		manager = new ConstantDiagonalComplexityClassifier(inputImage, diagonalCutoff, segmentWidth, segmentHeight);
	}

	// Use Custom Settings
	public BPCS_Diagonal(String vessilPath, double cutoff, int segmentWidth, int segmentHeight) {
		super(vessilPath, segmentWidth, segmentHeight);
		this.diagonalCutoff = cutoff;
		manager = new ConstantDiagonalComplexityClassifier(inputImage, cutoff, segmentWidth, segmentHeight);
	}
	
	// Make from Command Line Arguments
	public BPCS_Diagonal(HashMap<String, String> params) {
		super(params);		
		if (params.containsKey("threshold")) {
			diagonalCutoff = Double.parseDouble(params.get("threshold"));
		}
		manager = new ConstantDiagonalComplexityClassifier(inputImage, diagonalCutoff, segmentWidth, segmentHeight);
	}
	
	@Override
	public String toString() {
		return " Algorithim=BPCS_Diagonal DiagonalCutoff=" + diagonalCutoff + " SegmentWidth=" + segmentWidth
				+ " SegmentHeight=" + segmentHeight;
	}
}
