package minimal;

import java.util.ArrayList;

public abstract class BPCS{
	protected SegmentManager manager;
	protected BitImageSet inputImage;
	protected BitImageSet outputImage;
	protected Payload payload;
	public static final boolean CONVERT_TO_GRAY = true;
	// Sensible default
	public static final String defaultStegKey = "This is a default steganographic key";

	public BPCS(String vessilPath) {
		inputImage = BitImageSet.makeBitImageSet(vessilPath, CONVERT_TO_GRAY);
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
			throw new AssertionError("Payload is too big for vessel");
		}

		ArrayList<BitMap> payloadSegments = payload.getSegments();
		
		BitImageSet output = manager.replaceWithPayload(payloadSegments);
		double psnr = Statistics.psnr_rgb(inputImage.getBufferedImage().getRaster(), output.getBufferedImage().getRaster());
		String filename = String.format(" %s PSNR=%.2fdB", toString(), psnr);
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

}