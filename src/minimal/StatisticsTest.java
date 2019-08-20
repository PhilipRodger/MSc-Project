package minimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
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
	static String watchImagePath = "watch.png";
	Double psnrFlagIdenticalImages = -1.0;
	
	//Output
	static String expectedStegoImagePath = "stegolena_color.bmp";
	static String expectedExtractPath = "extractedSamplePayload.zip";
	
	@BeforeAll
	public static void init() {
		Vessel.attemptRoundTrip(vesselImagePath, payloadPath);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	void behaviourPreservationTest() {
		Double dB = Statistics.psnr_rgb(vesselImagePath, expectedStegoImagePath);
		
		// Previous Value
		Double knownGoodValue = 68.64264361793553;
		assertEquals(knownGoodValue, dB, "Should match known good value");
	}
	
	@Test
	void flagWhenNoChange() {
		// Same Image
		Double dB = Statistics.psnr_rgb(vesselImagePath, vesselImagePath);
		assertEquals(psnrFlagIdenticalImages, dB, "Should return flag value");
	}
	
	@Test
	void exceptionWhenDimentionsNotSame() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			Statistics.psnr_rgb(vesselImagePath, watchImagePath);
		});
	}
	    
	
	@AfterAll
	public static void tearDown() {
		if (!new File(expectedStegoImagePath).delete()) {
			System.out.println("Unable to Teardown " + expectedStegoImagePath);
		} 
		
		if (!new File(expectedExtractPath).delete()) {
			System.out.println("Unable to Teardown " + expectedExtractPath);
		} 
	}
}
