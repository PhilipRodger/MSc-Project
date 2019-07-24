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
	long maxPossibleInfo;
	int bitsToAddressBits;
	int bitsToAddressFrame;
	int headerBitSize;

	// Data Header
	boolean firstBlockConguigate = false;
	boolean[] fileEndBit; // excluding this bit

	// Conjugation Map Header
	boolean conjugateMapOriginBlockConguigate = false;
	boolean[] conjugateMapEnd;

	ArrayList<Integer> conjugatedSegments;
	ArrayList<BitMap> replacementSegments;
	ArrayList<Coordinant> possibleMessage;
	BitImageSet vessel;
	Payload payload;

	static final boolean greyEncoding = true;
	
	public int getHeaderSize() {
		return headerBitSize;
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

	public Vessel(String vesselPath, int segmentWidth, int segmentHeight, double alphaComplexity) {
		vessel = BitImageSet.makeBitImageSet(vesselPath, greyEncoding);
		this.segmentWidth = segmentWidth;
		this.segmentHeight = segmentHeight;
		possibleMessage = vessel.getFrameCorners(segmentWidth, segmentWidth, alphaComplexity);
		maxPossibleInfo = (possibleMessage.size() * segmentWidth * segmentWidth);
		bitsToAddressBits = numberOfBitsToRepresent(maxPossibleInfo);
		bitsToAddressFrame = numberOfBitsToRepresent(possibleMessage.size());
		headerBitSize = bitsToAddressBits + 1; // End Address and current segment conjugation bit.
	}

	public void embedFile(String path) {
		payload = new Payload(path);
	}


	@Override
	public String toString() {
		return "Payload [segmentWidth=" + segmentWidth + ", segmentHeight=" + segmentHeight + ", maxPossibleInfo="
				+ maxPossibleInfo + ", bitsToAddressBits=" + bitsToAddressBits + ", bitsToAddressFrame="
				+ bitsToAddressFrame + ", firstBlockConguigate=" + firstBlockConguigate + ", fileEndBit="
				+ Arrays.toString(fileEndBit) + ", conjugateMapOriginBlockConguigate="
				+ conjugateMapOriginBlockConguigate + ", conjugateMapEnd=" + Arrays.toString(conjugateMapEnd)
				+ ", conjugatedSegments=" + conjugatedSegments + ", replacementSegments=" + replacementSegments + "]";
	}

	public static void main(String[] args) {

		String vesselPath = "lena_color.bmp";
		int segmentWidth = 8;
		int segmentHeight = 8;
		double alphaComplexity = 0.3;
		Vessel vessel = new Vessel(vesselPath, segmentWidth, segmentHeight, alphaComplexity);

		System.out.println(vessel);
		
		System.out.println("End of Main");
	}
}
