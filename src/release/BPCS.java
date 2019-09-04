package release;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class BPCS{
	protected SegmentManager manager;
	protected BitImageSet inputImage;
	protected BitImageSet outputImage;
	protected Payload payload;
	
	// Sensible Defaults
	protected int segmentWidth = 8;
	protected int segmentHeight = 8;
	
	public static final boolean CONVERT_TO_GRAY = true;
	// Sensible default
	public static final String defaultStegKey = "This is a default steganographic key";

	public BPCS(String vessilPath) {
		inputImage = BitImageSet.makeBitImageSet(vessilPath, CONVERT_TO_GRAY);
	}
	
	public BPCS(String vessilPath, int segmentWidth, int segmentHeight) {
		inputImage = BitImageSet.makeBitImageSet(vessilPath, CONVERT_TO_GRAY);
		this.segmentWidth = segmentWidth;
		this.segmentHeight = segmentHeight;
	}
	
	public BPCS(HashMap<String, String> params) {
		if (!params.containsKey("vessel")) {
			throw new IllegalArgumentException("Must specify the input image path by vessel=path");
		}
		inputImage = BitImageSet.makeBitImageSet(params.get("vessel"), CONVERT_TO_GRAY);
		
		if (params.containsKey("segmentwidth")) {
			segmentWidth = Integer.parseInt(params.get("segmentwidth"));
		}
		if (params.containsKey("segmentheight")) {
			segmentHeight = Integer.parseInt(params.get("segmentheight"));
		}
	}

	public String embedFile(String path) {
		return embedFile(path, defaultStegKey);
	}
	
	public String embedFile(String payloadPath, String stegKey) {
		Payload payload = new Payload(payloadPath, manager);
		try {
			payload.performEncryptDecrypt(stegKey, CipherMode.ENCRYPT);
		} catch (Exception e) {
			System.out.println("Unable to encrypt file, embedding file without encrypting");
			e.printStackTrace();
		}
		payload.segmentisePayload();

		if (payload.getNumberOfSegments() > manager.getNumSegments()) {
			// Payload is too big for vessel
			throw new AssertionError("Payload is too big for vessel estimated max payload = " + manager.getMaxPayloadBytes() + " bytes");
		}

		ArrayList<BitMap> payloadSegments = payload.getSegments();
		
		BitImageSet output = manager.replaceWithPayload(payloadSegments);
		double psnr = Statistics.psnr_rgb(inputImage.getBufferedImage().getRaster(), output.getBufferedImage().getRaster());
		String filename = String.format(" %s PSNR=%.2fdB Max_Payload=%dbytes", toString(), psnr, manager.getMaxPayloadBytes());
		return output.saveImage(filename);
	}
	
	public void extractFile(String extractPath){
		extractFile(extractPath, defaultStegKey);
	}

	public void extractFile(String extractPath, String stegKey){
		Payload payload = new Payload(manager);
		try {
			payload.performEncryptDecrypt(stegKey, CipherMode.DECRYPT);
			payload.writeFile(extractPath);	
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to extract file, please check settings and stego-key supplied match those used to embed the file");
		}
	}
	
	@Override
	public String toString() {
		return " Algorithim=BPCS SegmentWidth=" + segmentWidth
				+ " SegmentHeight=" + segmentHeight;
	}

}