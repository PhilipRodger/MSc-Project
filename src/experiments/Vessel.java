package experiments;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Vessel {
	int segmentWidth;
	int segmentHeight;
	double alphaComplexity;
	long maxPossibleInfo;
	int bitsToAddressBits;
	int bitsToAddressFrame;
	int headerBitSize;

	ArrayList<Coordinant> viableSegments;
	BitImageSet vessel;
	Payload payload;

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
		vessel = BitImageSet.makeBitImageSet(vesselPath, greyEncoding);
	}
	
	public Vessel(String vesselPath, int segmentWidth, int segmentHeight, double alphaComplexity) {
		vessel = BitImageSet.makeBitImageSet(vesselPath, greyEncoding);
		setParameters(segmentWidth, segmentHeight, alphaComplexity);
	}
	
	public void setParameters (int segmentWidth, int segmentHeight, double alphaComplexity){
		this.segmentWidth = segmentWidth;
		this.segmentHeight = segmentHeight;
		this.alphaComplexity = alphaComplexity;
		
		viableSegments = vessel.getFrameCorners(segmentWidth, segmentWidth, alphaComplexity);
		maxPossibleInfo = (viableSegments.size() * segmentWidth * segmentWidth);
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
	
	public void saveImage(String path) {
		vessel.convertToBMPFile(path);
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

	
	public static void main(String[] args) {
		String stegImagePath = "lena_colorSECRET.bmp";
		String extractedPath = "EXTRACTED_SECRET.zip";

		int segmentWidth = 8;
		int segmentHeight = 8;
		double alphaComplexity = 0.3;
		Vessel vessel = new Vessel(stegImagePath, segmentWidth, segmentHeight, alphaComplexity);
		System.out.println(vessel.extractViableSegments().size());
		Payload payload = vessel.extractPayload();
		payload.writeFile(extractedPath);
		
		
		System.out.println("End of Main");
	}
}
