package experiments;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.text.Segment;

public class Payload {
	public static final int BITS_PER_BYTE = 8;
	ArrayList<Byte> data;
	ArrayList<BitMap> segments;
	long nextBit;
	boolean nextBitNeedsNewSegment;
	int segmentWidth;
	int segmentHeight;
	int segmentCapacity;
	int nextInternalSegmentIndex;
	int nextSegmentIndex;
	double complexityCutoff;
	
	long lastDataBit;
	
	BitMap currentSegment;

	public Payload(String path) {
		data = getBytesFromFile(path);
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
	
	private void segmentisePayload(Vessel vessel, int segmentWidth, int segmentHeight, double complexityCutoff) {
		System.out.println(vessel.bitsToAddressBits);
		// Initialise segment info
		clearSegments();
		this.complexityCutoff = complexityCutoff;
		this.segmentWidth = segmentWidth;
		this.segmentHeight = segmentHeight;
		segmentCapacity = segmentHeight * segmentWidth;
		nextInternalSegmentIndex = 0;
		
		// Clear Space for Data Header
		writeHeader(vessel);

		// Write Data
		writeData();
		
		// Conjugate Maps below complexity threshold
		conjugateMapsBelowThreshold();
	}
	
	public void unsegmentisePayload(Vessel vessel, int segmentWidth, int segmentHeight, double complexityCutoff) {
		data = new ArrayList<>();
		// Initialise segment info
		ArrayList<BitMap> segmentsCopy = segments;
		clearSegments();
		segments = segmentsCopy;
		this.complexityCutoff = complexityCutoff;
		this.segmentWidth = segmentWidth;
		this.segmentHeight = segmentHeight;
		segmentCapacity = segmentHeight * segmentWidth;
		nextInternalSegmentIndex = 0;
		nextSegmentIndex = 0;
		

		unconjugateMappedSegments();
		
		
		readHeader(vessel);
		while (nextBit<=lastDataBit-1) {
			readByte();
		}
		
		for (Byte byte1 : data) {
			System.out.println(byte1);
		}
	}


	private void clearSegments() {
		segments = new ArrayList<>();
		nextBitNeedsNewSegment = true;
		nextBit = 0;
	}
	
	private void writeHeader(Vessel toWrite) {
		long datalength = BITS_PER_BYTE * data.size();
		lastDataBit = datalength + toWrite.bitsToAddressBits;
		makeNewSegment();

		
		for (int i = 0; i < toWrite.bitsToAddressBits; i++) {
			if (((lastDataBit >> i) & 1) == 1) {
				bitToSegmentWriter(true);
			} else {
				bitToSegmentWriter(false);

			}
		}	
	}

	private void readHeader(Vessel toRead) {
		lastDataBit = 0;
		getNewSegment();

		for (int i = 0; i < toRead.bitsToAddressBits; i++) {
			if (readBit()) {
				lastDataBit = lastDataBit + (1 << i);
			} 
		}
	}
	
	private void writeData() {
		for (Byte byte1 : data) {
			writeByte(byte1);
		}
	}
	
	private void writeByte(byte toWrite){
		for (int i = 0; i < BITS_PER_BYTE; i++) {
			bitToSegmentWriter(getBitFromByte(toWrite, i));
		}
	}
	
	private void readByte() {
		byte b = 0;
		for (int i = 0; i < BITS_PER_BYTE; i++) {
			if (readBit()) {
				b += (1 << i);
			}
		}
		data.add(b);			
	}
	
	private boolean getBitFromByte(byte b, int index) {
		if (((b >> index) & 1) == 1) {
			return true;
		} else {
			return false;
		}
	}

	private void bitToSegmentWriter(boolean bit) {
		if (nextBitNeedsNewSegment) {
			makeNewSegment();
		}	
		
		if (bit) {
			setCurrentSegmentIndex();
		}
		nextBit++;
		nextInternalSegmentIndex++;
		
		if(nextInternalSegmentIndex >= segmentCapacity) {
			nextBitNeedsNewSegment = true;
		}
	}
	
	private boolean readBit() {
		if (nextBitNeedsNewSegment) {
			getNewSegment();
		}
		
		boolean result = getCurrentSegmentIndex();
		 
		nextBit++;
		nextInternalSegmentIndex++;
		
		if(nextInternalSegmentIndex >= segmentCapacity) {
			nextBitNeedsNewSegment = true;
		}
		return result;
	}
	

	private void makeNewSegment() {
		currentSegment = new BitMap(segmentWidth, segmentHeight);
		segments.add(currentSegment);
		nextInternalSegmentIndex = 1;
		nextBitNeedsNewSegment = false;
	}
	
	private void getNewSegment() {
		currentSegment = segments.get(nextSegmentIndex);
		nextSegmentIndex++;
		nextInternalSegmentIndex = 1;
		nextBitNeedsNewSegment = false;

	}

	
	private void setCurrentSegmentIndex() {
		currentSegment.setBit(nextInternalSegmentIndex%segmentWidth, nextInternalSegmentIndex/segmentWidth);
	}
	private boolean getCurrentSegmentIndex() {
		return currentSegment.getBit(nextInternalSegmentIndex%segmentWidth, nextInternalSegmentIndex/segmentWidth);
	}
	
	private void conjugateMapsBelowThreshold() {
		for (int i = 0; i < segments.size(); i++) {
			BitMap map = segments.get(i);
			if (map.getAlphaComplexity() < complexityCutoff) {
				map = map.getConjugate();
				segments.set(i, map);
			}
		}
	}

	private void writeFile(String path) {
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
	
	private void unconjugateMappedSegments() {
		for (int i = 0; i < segments.size(); i++) {
			BitMap segment = segments.get(i); 
			if (segment.getBit(0, 0)) {
				segments.set(i, segment.getConjugate());
			}
		}
	}
	
	
	
	

	public static void main(String[] args) {
		Payload payload = new Payload("SamplePayload.zip");
		String vesselPath = "lena_color.bmp";
		int segmentWidth = 8;
		int segmentHeight = 8;
		double alphaComplexity = 0.3;
		
		
		Vessel vessel = new Vessel(vesselPath, segmentWidth, segmentHeight, alphaComplexity);
		payload.segmentisePayload(vessel, segmentWidth, segmentHeight, alphaComplexity);
		
		payload.unsegmentisePayload(vessel, segmentWidth, segmentHeight, alphaComplexity);
		System.out.println("End of Main");
	}
	
	
	// Copied/adapted from https://stackoverflow.com/questions/11528898/convert-byte-to-binary-in-java
	public String toBinary(byte b)
	{
	    StringBuilder sb = new StringBuilder(Byte.SIZE);
	    for( int i = 0; i < Byte.SIZE ; i++ )
	        sb.append((b << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
	    return sb.toString();
	}
}
