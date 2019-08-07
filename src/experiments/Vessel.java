package experiments;

import java.util.ArrayList;

public class Vessel {
	private int segmentWidth;
	private int segmentHeight;
	private double alphaComplexity;
	private long maxPossibleInfo;
	private int bitsToAddressBits;
	private int bitsToAddressFrame;
	private int headerBitSize;
	private SupportedImageFormats sourceFormat;
	private ArrayList<Coordinant> viableSegments;
	private BitImageSet vessel;
	private Payload payload;

	static final boolean greyEncoding = true;

	public int getHeaderSize() {
		return headerBitSize;
	}
	
	public int getSegmentWidth() {
		return segmentWidth;
	}

	public int getSegmentHeight() {
		return segmentHeight;
	}

	public double getAlphaComplexity() {
		return alphaComplexity;
	}
	
	public int getWidth() {
		return vessel.getWidth();
	}
	
	public int getHeight() {
		return vessel.getHeight();
	}
	
	public long getBitsToAddressBits() {
		return bitsToAddressBits;
	}
	
	public static int numberOfBitsToRepresent(long numberOfPossibilities) {
		int numberOfBitsRequired = 0;
		long maxPosibilitiesRepresentable = 1;
		while (maxPosibilitiesRepresentable < numberOfPossibilities) {
			maxPosibilitiesRepresentable = maxPosibilitiesRepresentable * 2;
			numberOfBitsRequired++;
		}
		return numberOfBitsRequired;
	}

	public Vessel(String vesselPath) {
		sourceFormat = SupportedImageFormats.getFormat(vesselPath);
		vessel = BitImageSet.makeBitImageSet(vesselPath, greyEncoding);
	}
	
	public Vessel(String vesselPath, int segmentWidth, int segmentHeight, double alphaComplexity) {
		this(vesselPath);
		setParameters(segmentWidth, segmentHeight, alphaComplexity);
	}
	
	public void setParameters (int segmentWidth, int segmentHeight, double alphaComplexity){
		this.segmentWidth = segmentWidth;
		this.segmentHeight = segmentHeight;
		this.alphaComplexity = alphaComplexity;
		
		viableSegments = vessel.getFrameCorners(segmentWidth, segmentHeight, alphaComplexity);
		maxPossibleInfo = (viableSegments.size() * (segmentWidth * segmentHeight - 1)); // 1 bit per segment for map
		bitsToAddressBits = numberOfBitsToRepresent(maxPossibleInfo);
		bitsToAddressFrame = numberOfBitsToRepresent(viableSegments.size());
		headerBitSize = bitsToAddressBits + 1; // End Address and current segment conjugation bit
	}

	public void embedFile(String path) {	
		payload = new Payload(path);
		payload.segmentisePayload(this, segmentWidth, segmentHeight, alphaComplexity);
		
		if (payload.getNumberOfSegments() > viableSegments.size()) {
			// Payload is too big for vessel
			throw new AssertionError("Payload is too big for vessel");
		}
		
		ArrayList<BitMap> payloadSegments = payload.getSegments();
		for (int i = 0; i < payloadSegments.size(); i++) {
			Coordinant toReplace = viableSegments.get(i);
			vessel.replaceSegment(toReplace, payloadSegments.get(i));
		}
	}
	
	public void saveImage(String fileName) {
		vessel.convertToImage(fileName, sourceFormat);
	}
	
	@Override
	public String toString() {
		return "Payload [segmentWidth=" + segmentWidth + ", segmentHeight=" + segmentHeight + ", maxPossibleInfo="
				+ maxPossibleInfo + ", bitsToAddressBits=" + bitsToAddressBits + ", bitsToAddressFrame="
				+ bitsToAddressFrame + "]";
	}
	
	public void generateSampleStegImage() {
		String vesselPath = "lena_color.bmp";
		String payloadPath = "SamplePayload.zip";
		String stegImagePath = "lena_colorSECRET.bmp";
		
		int segmentWidth = 8;
		int segmentHeight = 8;
		double alphaComplexity = 0.3;
		
		Vessel vessel = new Vessel(vesselPath, segmentWidth, segmentHeight, alphaComplexity);
		
		vessel.embedFile(payloadPath);
		vessel.saveImage(stegImagePath);
	}
	
	public Payload extractPayload() {
		ArrayList<BitMap> extractedSegments = extractViableSegments();
		Payload payload = new Payload(this);
		return payload;
	}
	
	public ArrayList<BitMap> extractViableSegments(){
		ArrayList<BitMap> extractedSegments = new ArrayList<>();
		for (Coordinant coordanant: viableSegments) {
			extractedSegments.add(vessel.extractSegment(coordanant, segmentWidth, segmentHeight));
		}
		return extractedSegments;
	}
	
	public static void attemptRoundTrip(String vesselPath, String payloadPath) {
		int segmentWidth = 8;
		int segmentHeight = 8;
		double alphaComplexity = 0.3;
		
		attemptRoundTrip(vesselPath, payloadPath, segmentWidth, segmentHeight, alphaComplexity);
	}
	
	public static void attemptRoundTrip(String vesselPath, String payloadPath, 	int segmentWidth, int segmentHeight, double alphaComplexity ) {
		String stegoPath = "stego" + vesselPath;
		String extractedPath = "extracted" + payloadPath;
	
		
		Vessel vessel = new Vessel(vesselPath, segmentWidth, segmentHeight, alphaComplexity);
		vessel.embedFile(payloadPath);
		vessel.saveImage(stegoPath.replaceFirst("[.][^.]+$", ""));
		vessel = null;
		
		
		Vessel stegoImage = new Vessel(stegoPath, segmentWidth, segmentHeight, alphaComplexity);
		Payload extract = stegoImage.extractPayload();
		extract.writeFile(extractedPath);
	}

	public static void main(String[] args) {
		attemptRoundTrip("airbusemb.png", "Report.zip", 8, 8, 0.3);
		System.out.println("End of Main");
	}


}
