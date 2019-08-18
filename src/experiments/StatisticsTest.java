package experiments;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
class StatisticsTest {
	//Input
	static String vesselImagePath = "lena_color.bmp";
	static String payloadPath = "SamplePayload.zip";
	
	//Constants
	Double psnrFlagIdenticalImages = -1.0;
	
	//Output
	static String expectedStegoImagePath = "stegolena_color.bmp";
	static String expectedExtractPath = "extractedSamplePayload.zip";
	
	@BeforeAll
	public static void init() {
		Vessel.attemptRoundTrip(vesselImagePath, payloadPath, 8, 8, 0.3);
		System.out.println("Setup Done");
	}
	
	@Test
	void behaviourPreservationTest() {
		Double dB = Statistics.psnr_rgb(vesselImagePath, expectedStegoImagePath);
		
		// Previous Value
		Double knownGoodValue = 68.74193679858286;
		assertEquals(knownGoodValue, dB, "Should match known good value");
	}
	
	@Test
	void flagWhenNoChange() {
		// Same Image
		Double dB = Statistics.psnr_rgb(vesselImagePath, vesselImagePath);
		assertEquals(psnrFlagIdenticalImages, dB, "Should return flag value");
	}
	
	@AfterAll
	public static void tearDown() {
		File stegoImage = new File(expectedStegoImagePath);
		File expectedExtract = new File(expectedExtractPath);
		if (stegoImage.delete() && expectedExtract.delete()) {
			System.out.println("Teardown Successful");
		} else {
			System.out.println("Unable to Teardown");
		}
	}

}
