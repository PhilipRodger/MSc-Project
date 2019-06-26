package experiments;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Payload {
	int segmentWidth;
	int segmentHeight;
	long maxPossibleInfo;
	int bitsToAddressBits;
	int bitsToAddressFrame;

	boolean firstBlockConguigate = false;
	boolean[] fileEndBit; // excluding this bit
	boolean[] conjugateMapStart;
	boolean conjugateMapOriginBlockConguigate = false;
	boolean[] conjugateMapEnd; // excluding

	ArrayList<Integer> conjugatedSegments;
	ArrayList<BitMap> replacementSegments;
	ArrayList<Coordinant> possibleMessage;
	ArrayList<Byte> payload;
	BitImageSet vessel;

	static final boolean greyEncoding = true;
	
	public void firstBlockConguigate() {
		
	}

	public static int numberOfBitsToRepresent(long numberOfPossibilities) {
		int numberOfBitsRequired = 0;
		while (numberOfPossibilities > 0) {
			numberOfPossibilities = numberOfPossibilities / 2;
			numberOfBitsRequired++;
		}
		return numberOfBitsRequired;
	}

	public Payload(String vesselPath, int segmentWidth, int segmentHeight, double alphaComplexity) {
		vessel = BitImageSet.makeBitImageSet(vesselPath, greyEncoding);
		this.segmentWidth = segmentWidth;
		this.segmentHeight = segmentHeight;
		possibleMessage = vessel.getFrameCorners(segmentWidth, segmentWidth, alphaComplexity);
		maxPossibleInfo = possibleMessage.size() * segmentWidth * segmentWidth;
		bitsToAddressBits = numberOfBitsToRepresent(maxPossibleInfo);
		bitsToAddressFrame = numberOfBitsToRepresent(possibleMessage.size());

	}

	public void embedFile(String path) {
		payload = getBytesFromFile(path);
	}

	private static ArrayList<Byte> getBytesFromFile(String path) {
		try (FileInputStream payload = new FileInputStream(path)) {
			int readByte = payload.read();
			ArrayList<Byte> data = new ArrayList<>();
			while (readByte != -1) {
				data.add((byte) readByte);
				readByte = payload.read();
			}
			return data;
		} catch (FileNotFoundException e) {
			System.out.println("Unable to find file");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// If failure to read file.
		return null;
	}

	private static void writeFile(ArrayList<Byte> data, String path) {
		try (FileOutputStream extracted = new FileOutputStream(path)) {
			for (Byte out : data) {
				extracted.write(out);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Unable to find file");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Payload(String path) {
		payload = getBytesFromFile(path);
	}

	public static boolean bitSet(byte b, int index) {
		if (((b >> index) & 1) == 1) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		// ArrayList<Byte> payload = getBytesFromFile("SamplePayload.zip");
		// writeFile(payload, "WooHOOO.zip");

//		for (Byte bite : payload) {
//			System.out.println(bite);
//			for (int i = 0; i < Byte.SIZE; i++) {
//			}
//		}

		// System.out.println(numberOfBitsToRepresent(2));
		String vesselPath = "lena_color.bmp";
		int segmentWidth = 8;
		int segmentHeight = 8;
		double alphaComplexity = 0.3;
		Payload vessel = new Payload(vesselPath, segmentWidth, segmentHeight, alphaComplexity);
		System.out.println();
	}
}
