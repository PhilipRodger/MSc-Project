package release;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Statistics {
	public static double psnr_rgb(String originalPath, String modifiedPath) {
		// Unpack paths to raster images
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

		if (!inputRaster.getBounds().equals(modifiedRaster.getBounds())) {
			throw new IllegalArgumentException("Images not of the same dimentions");
		}

		// get psnr from rasters
		return psnr_rgb(inputRaster, modifiedRaster);
	}

	public static double psnr_rgb(WritableRaster inputRaster, WritableRaster modifiedRaster) {
		long squaredDiffTotal = totalSquareErrorRGB(inputRaster, modifiedRaster);

		// If no difference between original and modified image the function should
		// return to prevent a divide by 0 error.
		if (squaredDiffTotal == 0) {
			return -1;
		}

		double totalComparisonsMade = (double) (inputRaster.getHeight() * inputRaster.getWidth()
				* RGBPixel.NUMBER_OF_CHANNELS);
		double meanSquaredError = squaredDiffTotal / totalComparisonsMade;
		double psnrDecibels = 10 * Math
				.log10((RGBPixel.MAX_POSSIBLE_PIXEL_VALUE * RGBPixel.MAX_POSSIBLE_PIXEL_VALUE) / meanSquaredError);
		return psnrDecibels;
	}

	public static long totalSquareErrorRGB(WritableRaster inputRaster, WritableRaster modifiedRaster) {
		// Add up all the pixel value differences
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
					// Add Squared difference to error total.
					squaredDiffTotal += Math.abs(diff * diff);

				}
			}
		}
		return squaredDiffTotal;
	}
}
