package release;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;


class BPCS_OriginalTest {
	// Input
	static String vesselImagePath = "lena_color.bmp";
	static String payloadPath = "SamplePayload.zip";

	// Constants
	static String watchImagePath = "watch.png";
	Double psnrFlagIdenticalImages = -1.0;

	// Output
	static String expectedExtractPath = "extractedSamplePayload.zip";

	@Test
	void noStegKeyPayload() {
		//embed 
		BPCS bpcs = new BPCS(vesselImagePath);
		bpcs.setSegmentManager(new Original(0.3));
		ArrayList<String> expectedStegoImagePath = bpcs.embedFile(payloadPath);
		
		//clear
		bpcs = null;
		
		//extract
		bpcs = new BPCS(expectedStegoImagePath.get(0));
		bpcs.setSegmentManager(new Original(0.3));
		bpcs.extractFile(expectedExtractPath);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}

		try {
			assertTrue(isFileContentEqual(payloadPath, expectedExtractPath), "Should be same payload after extraction");
		} catch (IOException e) {
			fail("Unable to open files");
		}

		// Tear Down
		tearDown(expectedStegoImagePath);
		tearDown(expectedExtractPath);
	}
	
	
	@Test
	void stegProtectedKeyPayload() {
		String key = "secret";
		//embed 
		BPCS bpcs = new BPCS(vesselImagePath);
		bpcs.setSegmentManager(new Original(0.3));
		ArrayList<String> expectedStegoImagePath = bpcs.embedFile(payloadPath, key);
		
		//clear
		bpcs = null;
		
		//extract
		bpcs = new BPCS(expectedStegoImagePath.get(0));
		bpcs.setSegmentManager(new Original(0.3));
		bpcs.extractFile(expectedExtractPath, key);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}

		try {
			assertTrue(isFileContentEqual(payloadPath, expectedExtractPath), "Should be same payload after extraction");
		} catch (IOException e) {
			fail("Unable to open files");
		}

		// Tear Down
		tearDown(expectedStegoImagePath);
		tearDown(expectedExtractPath);
	}
	
	@Test
	void invalidStegKey() {
		String key = "secret";
		String incorrectKey = "Secret";
		
		BPCS bpcs = new BPCS(vesselImagePath);
		bpcs.setSegmentManager(new Original(0.3));
		ArrayList<String> expectedStegoImagePath = bpcs.embedFile(payloadPath, key);
		
		//clear
		bpcs = null;
		
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		// Try to extract payload using incorrect Key

		// Should fail by padding failure or content
		
		try {
			//extract
			bpcs = new BPCS(expectedStegoImagePath.get(0));
			bpcs.setSegmentManager(new Original(0.3));
			bpcs.extractFile(expectedExtractPath, incorrectKey);
		} catch (Exception e) {
			// Invalid decryption key could cause many different exceptions or none.
			// This is expected behaviour and should pass
			// Tear Down
			tearDown(expectedStegoImagePath);
			tearDown(expectedExtractPath);
			return;
		}
		try {
			assertFalse(isFileContentEqual(payloadPath, expectedExtractPath), "Should not be able to extract payload with incorret key");
		} catch (IOException e) {
			fail("Unable to open files");
		}

		// Tear Down
		tearDown(expectedStegoImagePath);
		tearDown(expectedExtractPath);
	}
	
//	@Test
//	void replacementImage() {
//		BPCS bpcs = new BPCS(vesselImagePath);
//		bpcs.setSegmentManager(new ConstantAlphaClassifier(0.3));
//		String expectedStegoImagePath = bpcs.visualizeEmbedFile(payloadPath);
//	}
	
	@Test
	void maxReplacementImage() {
		BPCS bpcs = new BPCS(vesselImagePath, 8, 8);
		bpcs.setSegmentManager(new ConstantDiagonalComplexityClassifier(0.3));
		bpcs.saveReplacementImages(true);
		ArrayList<String> paths = bpcs.maxEmbed();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch blocks
			e.printStackTrace();
		}
		tearDown(paths);
	}

	public static boolean isFileContentEqual(String path1, String path2) throws IOException {
		byte[] original = Files.readAllBytes(Paths.get(payloadPath));
		byte[] extracted = Files.readAllBytes(Paths.get(expectedExtractPath));

		return Arrays.equals(original, extracted);
	}
	
	public static void tearDown(ArrayList<String> path) {
		for (String string : path) {
			tearDown(string);
		}
	}
	
	public static void tearDown(String path) {
		if (!new File(path).delete()) {
			System.out.println("Unable to Teardown " + path);
		}
	}
}
