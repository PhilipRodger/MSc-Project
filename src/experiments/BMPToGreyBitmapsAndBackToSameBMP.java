package experiments;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class BMPToGreyBitmapsAndBackToSameBMP {
	public static void main(String[] args) {
		BufferedImage input = null;

		try {
			input = ImageIO.read(new File("sample.bmp"));
		} catch (IOException e) {
			// TODO: handle exception
		}
		BufferedImage[][] bitmaps = new BufferedImage[RGBPixel.NUMBER_OF_CHANNELS][RGBPixel.NUMBER_OF_BITS_PER_CHANNEL];
		
		for (int ChannelIndex = 0; ChannelIndex < RGBPixel.NUMBER_OF_CHANNELS; ChannelIndex++) {
			for (int ChannelBitIndex = 0; ChannelBitIndex < RGBPixel.NUMBER_OF_BITS_PER_CHANNEL; ChannelBitIndex++) {
				bitmaps[ChannelIndex][ChannelBitIndex] = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_RGB);
			}
		}

		WritableRaster simpleFaceRaster = input.getRaster();
		for (int y = 0; y < simpleFaceRaster.getHeight(); y++) {
			for (int x = 0; x < simpleFaceRaster.getWidth(); x++) {
				RGBPixel pixel = new RGBPixel(simpleFaceRaster.getPixel(x, y, new int[3]));
				
				// Convert to grey code
				pixel = pixel.makeGrayCodePixel();
				
				
				for (int ChannelIndex = 0; ChannelIndex < RGBPixel.NUMBER_OF_CHANNELS; ChannelIndex++) {
					for (int ChannelBitIndex = 0; ChannelBitIndex < RGBPixel.NUMBER_OF_BITS_PER_CHANNEL; ChannelBitIndex++) {
						if (pixel.bitPresent(Channel.channelMapping(ChannelIndex), ChannelBitIndex)) {
							bitmaps[ChannelIndex][ChannelBitIndex].setRGB(x, y, Channel.getPureColour(Channel.channelMapping(ChannelIndex)));
						} else {
							bitmaps[ChannelIndex][ChannelBitIndex].setRGB(x, y, Color.WHITE.getRGB());
						}
					}
				}
			}
		}
		
		
		saveBitMaps(bitmaps, "GreyTest.bmp");
	}

	
	
	private static void saveBitMaps(BufferedImage[][] bitmaps, String path) {
		for (int ChannelIndex = 0; ChannelIndex < RGBPixel.NUMBER_OF_CHANNELS; ChannelIndex++) {
			String colourPath = Channel.channelMapping(ChannelIndex).toString() + path;
			saveBitMaps(bitmaps[ChannelIndex], colourPath);
			System.out.println("Saved bitmap images");
		}
	}
	
	private static void saveBitMaps(BufferedImage[] bitmaps, String path) {
		for (int ChannelBitIndex = 0; ChannelBitIndex < RGBPixel.NUMBER_OF_BITS_PER_CHANNEL; ChannelBitIndex++) {
			String colourPath = ChannelBitIndex + path;
			savePNG(bitmaps[ChannelBitIndex], colourPath);
		}
	}
	
	private static void savePNG(final BufferedImage bi, final String path) {
		try {
			RenderedImage rendImage = bi;
			ImageIO.write(rendImage, "bmp", new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
