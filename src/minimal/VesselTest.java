package minimal;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import sun.security.util.Password;

class VesselTest {
	// Input
	static String vesselImagePath = "lena_color.bmp";
	static String payloadPath = "SamplePayload.zip";

	// Constants
	static String watchImagePath = "watch.png";
	Double psnrFlagIdenticalImages = -1.0;

	// Output
	static String expectedStegoImagePath = "stegolena_color.bmp";
	static String expectedExtractPath = "extractedSamplePayload.zip";

	@Test
	void noStegKeyPayload() {
		// Set up
		Vessel.attemptRoundTrip(vesselImagePath, payloadPath);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}

		try {
			assertTrue(isFileContentEqual(payloadPath, expectedExtractPath), "Should be same payload after extraction");
		} catch (IOException e) {
			fail("Unable to open files");
		}

		// Tear Down
		if (!new File(expectedStegoImagePath).delete()) {
			System.out.println("Unable to Teardown " + expectedStegoImagePath);
		}

		if (!new File(expectedExtractPath).delete()) {
			System.out.println("Unable to Teardown " + expectedExtractPath);
		}
	}
	
	
	@Test
	void stegProtectedKeyPayload() {
		String key = "secret";
		// Set up
		Vessel.attemptRoundTrip(vesselImagePath, payloadPath, key);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}

		try {
			assertTrue(isFileContentEqual(payloadPath, expectedExtractPath), "Should be same payload after extraction");
		} catch (IOException e) {
			fail("Unable to open files");
		}

		// Tear Down
		if (!new File(expectedStegoImagePath).delete()) {
			System.out.println("Unable to Teardown " + expectedStegoImagePath);
		}

		if (!new File(expectedExtractPath).delete()) {
			System.out.println("Unable to Teardown " + expectedExtractPath);
		}
	}
	
	@Test
	void invalidStegKey() {
		String key = "secret";
		String incorrectKey = "Secret";
		
		// Set up
		String stegoPath = "stego" + vesselImagePath;
		String extractedPath = "extracted" + payloadPath;
		Vessel vessel = new Vessel(vesselImagePath);
		vessel.setStegKey(key);
		vessel.embedFile(payloadPath);
		vessel.saveImage(stegoPath.replaceFirst("[.][^.]+$", ""));
		vessel = null;
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		
		// Try to extract payload using incorrect Key
		Vessel stegoImage = new Vessel(stegoPath);
		stegoImage.setStegKey(incorrectKey);
		// Should fail by padding failure or content
		
		try {
			Payload extract = stegoImage.extractPayload();
			extract.writeFile(extractedPath);
		} catch (Exception e) {
			// Invalid decryption key could cause many different exceptions or none.
			// This is expected behaviour and should pass
			return;
		}
		try {
			assertFalse(isFileContentEqual(payloadPath, expectedExtractPath), "Should not be able to extract payload with incorret key");
		} catch (IOException e) {
			fail("Unable to open files");
		}

		// Tear Down
		if (!new File(expectedStegoImagePath).delete()) {
			System.out.println("Unable to Teardown " + expectedStegoImagePath);
		}
		if (!new File(expectedExtractPath).delete()) {
			System.out.println("Unable to Teardown " + expectedExtractPath);
		}
	}

	public static boolean isFileContentEqual(String path1, String path2) throws IOException {
		byte[] original = Files.readAllBytes(Paths.get(payloadPath));
		byte[] extracted = Files.readAllBytes(Paths.get(expectedExtractPath));

		return Arrays.equals(original, extracted);
	}
}
