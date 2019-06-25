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
	
	boolean firstBlockConguigate = false;
	boolean[] fileEndBit;   // excluding this bit
	boolean[] conjugateMapStart;
	boolean conjugateMapOriginBlockConguigate = false;
	boolean[] conjugateMapEnd; // excluding
	
	ArrayList<Integer> conjugatedSegments;
	ArrayList<BitMap> replacementSegments;
	ArrayList<Coordinant> possibleMessage;
	ArrayList<Byte> payload;
	BitImageSet vessel;
	
	static final boolean greyEncoding = true;
	
	public Payload(String vesselPath, int segmentWidth, int segmentHeight, double alphaComplexity) {
		vessel = BitImageSet.makeBitImageSet(vesselPath, greyEncoding);
		this.segmentWidth = segmentWidth;
		this.segmentHeight = segmentHeight;
		possibleMessage = vessel.getFrameCorners(segmentWidth, segmentHeight, alphaComplexity);
		
	}
	
	public embedFile() {
		
	}
	
	private static ArrayList<Byte> getBytesFromFile(String path){
		try (FileInputStream payload = new FileInputStream(path)){
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
		try(FileOutputStream extracted = new FileOutputStream(path)){
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
	
	public static boolean bitSet (byte b, int index) {
		if (((b >> index) & 1) == 1) {
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		ArrayList<Byte> payload = getBytesFromFile("SamplePayload.zip");
		//writeFile(payload, "WooHOOO.zip");
		
		
		for (Byte bite : payload) {
			System.out.println(bite);
			for (int i = 0; i < Byte.SIZE; i++) {
			}
		}
	}
}
