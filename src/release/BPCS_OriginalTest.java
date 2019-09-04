package release;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
		BPCS bpcs = new BPCS_Original(vesselImagePath);
		String expectedStegoImagePath = bpcs.embedFile(payloadPath);
		
		//clear
		bpcs = null;
		
		//extract
		bpcs = new BPCS_Original(expectedStegoImagePath);
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
		//embed 
		BPCS bpcs = new BPCS_Original(vesselImagePath);
		String expectedStegoImagePath = bpcs.embedFile(payloadPath, key);
		
		//clear
		bpcs = null;
		
		//extract
		bpcs = new BPCS_Original(expectedStegoImagePath);
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
		
		BPCS bpcs = new BPCS_Original(vesselImagePath);
		String expectedStegoImagePath = bpcs.embedFile(payloadPath, key);
		
		//clear
		bpcs = null;
		
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		// Try to extract payload using incorrect Key

		// Should fail by padding failure or content
		
		try {
			//extract
			bpcs = new BPCS_Original(expectedStegoImagePath);
			bpcs.extractFile(expectedExtractPath, incorrectKey);
		} catch (Exception e) {
			// Invalid decryption key could cause many different exceptions or none.
			// This is expected behaviour and should pass
			// Tear Down
			if (!new File(expectedStegoImagePath).delete()) {
				System.out.println("Unable to Teardown " + expectedStegoImagePath);
			}
			if (!new File(expectedExtractPath).delete()) {
				System.out.println("Unable to Teardown " + expectedExtractPath);
			}
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
