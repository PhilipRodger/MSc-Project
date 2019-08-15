package experiments;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Statistics {
	public static double psnr_rgb(String originalPath, String modifiedPath) {
		BufferedImage input = null;
		BufferedImage modified = null;
		try {
			input = ImageIO.read(new File(originalPath));
			modified = ImageIO.read(new File(modifiedPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		WritableRaster inputRaster = input.getRaster();
		WritableRaster modifiedRaster = modified.getRaster();
		
		long squaredDiffTotal = 0;
		
		for (int y = 0; y < inputRaster.getHeight(); y++) {
			for (int x = 0; x < inputRaster.getWidth(); x++) {
				// Iterate over all pixels in the input image.

				RGBPixel originalPixel = new RGBPixel(inputRaster.getPixel(x, y, new int[4]));
				RGBPixel modifiedPixel = new RGBPixel(modifiedRaster.getPixel(x, y, new int[4]));
				
				for (int i = 0; i < RGBPixel.NUMBER_OF_CHANNELS; i++) {
					// Iterate over all channels in the RGB image
					int originalIntensity = originalPixel.getChannel(Channel.channelMapping(i));
					int modifiedlIntensity = modifiedPixel.getChannel(Channel.channelMapping(i));
					
					int diff = originalIntensity - modifiedlIntensity;
					// Add Squared differance to error total.
					squaredDiffTotal += Math.abs(diff * diff);

				}
			}
		}	
		double meanSquaredError = squaredDiffTotal / (double)(inputRaster.getHeight() * inputRaster.getWidth() * RGBPixel.NUMBER_OF_CHANNELS);
		double psnrDecibels = 10*Math.log10((RGBPixel.MAX_POSSIBLE_PIXEL_VALUE * RGBPixel.MAX_POSSIBLE_PIXEL_VALUE) / meanSquaredError);
		return psnrDecibels;
	}
	public static void main(String[] args) {
		String originalPath = "PSNR-example-base.png";
		String modifiedPath = "1024px-PSNR-example-comp-90.jpg";
		System.out.println(psnr_rgb(originalPath, modifiedPath));
		System.out.println("End of Main");
	}
}
