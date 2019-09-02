package minimal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


public class Payload {
	private ArrayList<Byte> data;
	private ArrayList<BitMap> segments;
	private SegmentManager manager;
	private long nextBit;
	private boolean nextBitNeedsNewSegment;
	private int segmentWidth;
	private int segmentHeight;
	private int segmentCapacity;
	private long totalPossibleCapacity;
	private int nextInternalSegmentIndex;
	private int nextSegmentIndex;
	private long lastDataBit;
	private BitMap currentSegment;

	public Payload(String path, SegmentManager manager) {
		this.manager = manager;
		data = getBytesFromFile(path);
	}

	public Payload(SegmentManager manager) {
		this.manager = manager;
		unsegmentisePayload();
	}

	public int getNumberOfSegments() {
		return segments.size();
	}

	public ArrayList<BitMap> getSegments() {
		return segments;
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
			System.out.println("Unable to find file " + path);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// If failure to read file.
		return null;
	}

	public void segmentisePayload() {
		// Initialise segment info
		clearSegments();
		manager.getViableFrameCorners();
		this.segmentWidth = manager.getSegmentWidth();
		this.segmentHeight = manager.getSegmentHeight();
		segmentCapacity = segmentHeight * segmentWidth;
		nextInternalSegmentIndex = 0;

		// Clear Space for Data Header
		writeHeader();
		
		// Write Data
		writeData();

		// Conjugate Maps below complexity threshold
		manager.conjugateMapsBelowThreshold(segments);
	}

	public void unsegmentisePayload() {

		// Initialise segment info
		data = new ArrayList<>();
		segments = manager.extractViableSegments();
		segmentWidth = manager.getSegmentWidth();
		segmentHeight = manager.getSegmentHeight();
		segmentCapacity = segmentWidth * segmentHeight;


		unconjugateMappedSegments();

		readHeader();
		while (nextBit <= lastDataBit - 1) {
			readByte();
		}
	}

	private void clearSegments() {
		segments = new ArrayList<>();
		nextBitNeedsNewSegment = true;
		nextBit = 0;
	}

	private void writeHeader() {
		long datalength = Byte.SIZE * data.size();
		lastDataBit = datalength + manager.getBitsToAddressBits();
		manager.getViableFrameCorners();
		makeNewSegment();

		for (int i = 0; i < manager.getBitsToAddressBits(); i++) {
			if (((lastDataBit >> i) & 1) == 1) {
				bitToSegmentWriter(true);
			} else {
				bitToSegmentWriter(false);
			}
		}
	}

	private void readHeader() {
		lastDataBit = 0;
		getNewSegment();

		for (int i = 0; i < manager.getBitsToAddressBits(); i++) {
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


	private boolean getBitFromByte(byte b, int index) {
		if (((b >> index) & 1) == 1) {
			return true;
		} else {
			return false;
		}
	}
	
	private void writeByte(byte toWrite) {
		for (int i = 0; i < Byte.SIZE; i++) {
			bitToSegmentWriter(getBitFromByte(toWrite, i));
		}
	}

	private void readByte() {
		byte b = 0;
		for (int i = 0; i < Byte.SIZE; i++) {
			if (readBit()) {
				b += (1 << i);
			}
		}
		data.add(b);
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

		if (nextInternalSegmentIndex >= segmentCapacity) {
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

		if (nextInternalSegmentIndex >= segmentCapacity) {
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
		currentSegment.setBit(nextInternalSegmentIndex % segmentWidth, nextInternalSegmentIndex / segmentWidth);
	}

	private boolean getCurrentSegmentIndex() {
		return currentSegment.getBit(nextInternalSegmentIndex % segmentWidth, nextInternalSegmentIndex / segmentWidth);
	}


	public void writeFile(String path) {
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
				segments.set(i, manager.getConjugate(segment));
			}
		}
	}
	
	
	public void performEncryptDecrypt(String stegKey, CipherMode mode) throws Exception {
		// Get Cipher initialised on Steganographic Key
		SecretKeySpec key = getKey(stegKey);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        
        // Set Cipher for encryption or decryption
        switch (mode) {
		case ENCRYPT:
	        cipher.init(Cipher.ENCRYPT_MODE, key);
	        break;
		case DECRYPT:
	        cipher.init(Cipher.DECRYPT_MODE, key);
	        break;
		default:
			throw new IllegalArgumentException("Cipher mode not defined.");
			}
        
        // Convert Payload to Primative Array 
        byte[] primative = new byte[data.size()];
        for (int i = 0; i < primative.length; i++) {
			primative[i] = data.get((int) i);
		}
                
        // Perform cipher  
        byte[] encrypted = cipher.doFinal(primative);
        
        // Clear old payload and convert array to list
		data = new ArrayList<>();
         for (int i = 0; i < encrypted.length; i++) {
			data.add(encrypted[i]);
         }
	}

	private SecretKeySpec getKey(String stegKey) throws Exception {
		if (stegKey == null) {
			stegKey = "DefaultBCNFStegKey";
		}
		byte[] digest;
		MessageDigest digester = MessageDigest.getInstance("SHA-256");
		digest = digester.digest(stegKey.getBytes("UTF-8"));
		return new SecretKeySpec(digest, "AES");

	}


	// Copied/adapted from
	// https://stackoverflow.com/questions/11528898/convert-byte-to-binary-in-java
	public String toBinary(byte b) {
		StringBuilder sb = new StringBuilder(Byte.SIZE);
		for (int i = 0; i < Byte.SIZE; i++)
			sb.append((b << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
		return sb.toString();
	}
}
