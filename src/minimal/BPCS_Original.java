package minimal;

import java.util.HashMap;

public class BPCS_Original extends BPCS {
	// Sensible Defaults
	private double alphaCutoff = 0.3;
	private int segmentWidth = 8;
	private int segmentHeight = 8;

	// Use Default Settings
	public BPCS_Original(String vessilPath) {
		super(vessilPath);
		manager = new ConstantAlphaComplexityClassifier(inputImage, alphaCutoff, segmentWidth, segmentHeight);
	}

	// Use Custom Settings
	public BPCS_Original(String vessilPath, double alphaCutoff, int segmentWidth, int segmentHeight) {
		super(vessilPath);
		this.alphaCutoff = alphaCutoff;
		this.segmentWidth = segmentWidth;
		this.segmentHeight = segmentHeight;
		manager = new ConstantAlphaComplexityClassifier(inputImage, alphaCutoff, segmentWidth, segmentHeight);
	}

	public static void attemptRoundTrip(String vesselPath, String payloadPath) {
		attemptRoundTrip(vesselPath, payloadPath, defaultStegKey);
	}

	public static void attemptRoundTrip(String vesselPath, String payloadPath, String stegKey) {
		int segmentWidth = 8;
		int segmentHeight = 8;

		attemptRoundTrip(vesselPath, payloadPath, stegKey, segmentWidth, segmentHeight);
	}


	public static void attemptRoundTrip(String vesselPath, String payloadPath, String stegKey, int segmentWidth,
			int segmentHeight) {
		String extractedPath = "extracted" + payloadPath;

		BPCS_Original original = new BPCS_Original(vesselPath);
		String stegoPath = original.embedFile(payloadPath);
		original = null;

		original = new BPCS_Original(stegoPath);
		original.extractFile(extractedPath);
	}
	
	public static void runFromParams(HashMap<String, String> params) {
		if (!params.containsKey("vessel")) {
			throw new IllegalArgumentException("Must specify the input image path by vessel=path");
		}
		
		//Defaults
		double alphaCutoff = 0.3;
		int segmentWidth = 8;
		int segmentHeight = 8;
		String stegKey = defaultStegKey; 
		
		if (params.containsKey("threshold")) {
			alphaCutoff = Double.parseDouble(params.get("threshold"));
		}
		if (params.containsKey("segmentwidth")) {
			segmentWidth = Integer.parseInt(params.get("segmentwidth"));
		}
		if (params.containsKey("segmentheight")) {
			segmentHeight = Integer.parseInt(params.get("segmentheight"));
		}
		if (params.containsKey("key")) {
			stegKey = params.get("key");
		}
		
		
		
		BPCS_Original bpcs = new BPCS_Original(params.get("vessel"), alphaCutoff, segmentWidth, segmentHeight);
		
		
		if (!params.containsKey("mode")) {
			throw new IllegalArgumentException("Must specify the mode of use e.g. 'mode=embed', 'mode=extract', or 'mode=roundtrip'");
		}
		String mode = params.get("mode");
		switch (mode) {
		case "embed":
			if (!params.containsKey("payload")) {
				throw new IllegalArgumentException("Must specify the payload path you wish to embed using 'payload=[path]'");
			}
			String stegoPath = bpcs.embedFile(params.get("payload"), stegKey);
			System.out.println("Succesfully embedded, output: " + stegoPath);
			break;
		case "extract":
			if (!params.containsKey("payload")) {
				throw new IllegalArgumentException("Must specify the payload path you wish to extract using 'payload=[path]'");
			}
			bpcs.extractFile(params.get("payload"), stegKey);
			System.out.println("Succesfully Extracted payload, output: " + params.get("payload"));
			break;
		case "roundtrip":
			if (!params.containsKey("payload")) {
				throw new IllegalArgumentException("Must specify the payload path you wish to embed using 'payload=[path]'");
			}
			
			// Embed Payload Step
			stegoPath = bpcs.embedFile(params.get("payload"), stegKey);
			System.out.println("Succesfully Embedded, output: " + stegoPath);
			bpcs = null;

			// Extract Payload Step
			bpcs = new BPCS_Original(stegoPath, alphaCutoff, segmentWidth, segmentHeight);
			String extractedPath = "extracted" + params.get("payload");
			bpcs.extractFile(extractedPath, stegKey);
			System.out.println("Succesfully Extracted, output: " + extractedPath);
			break;
			
		default:
			throw new IllegalArgumentException("Must specify a valid mode of use e.g.embed, extract, or roundtrip");
		}
	}
	

	@Override
	public String toString() {
		return " Algorithim=BPCS_Original AlphaCutoff=" + alphaCutoff + " SegmentWidth=" + segmentWidth
				+ " SegmentHeight=" + segmentHeight;
	}

}
