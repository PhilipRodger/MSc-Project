package experiments;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class BitImageSet {
	BitMap[][] bitmaps;
	int width;
	int height;
	boolean greyEncoded = false;

	public BitImageSet(BufferedImage input, boolean convertToGreyEncoding) {
		// Take note of metadata and flag.
		greyEncoded = convertToGreyEncoding;
		width = input.getWidth();
		height = input.getHeight();

		// Initialise internal representation
		bitmaps = new BitMap[RGBPixel.NUMBER_OF_CHANNELS][RGBPixel.NUMBER_OF_BITS_PER_CHANNEL];
		for (int i = 0; i < bitmaps.length; i++) {
			for (int j = 0; j < bitmaps[i].length; j++) {
				bitmaps[i][j] = new BitMap(width, height);
			}
		}

		// Copy info over from image.
		WritableRaster inputRaster = input.getRaster();
		for (int y = 0; y < inputRaster.getHeight(); y++) {
			for (int x = 0; x < inputRaster.getWidth(); x++) {
				// Iterate over all pixels in the input image.

				RGBPixel pixel = new RGBPixel(inputRaster.getPixel(x, y, new int[3]));
				if (greyEncoded) {
					// Convert to grey code
					pixel = pixel.makeGrayCodePixel();
				}

				// Add pixel data to each bit map.
				for (int ChannelIndex = 0; ChannelIndex < RGBPixel.NUMBER_OF_CHANNELS; ChannelIndex++) {
					for (int ChannelBitIndex = 0; ChannelBitIndex < RGBPixel.NUMBER_OF_BITS_PER_CHANNEL; ChannelBitIndex++) {
						if (pixel.bitPresent(Channel.channelMapping(ChannelIndex), ChannelBitIndex)) {
							bitmaps[ChannelIndex][ChannelBitIndex].setBit(x, y);
						}
						//else false which is already value, no change required.
					}
				}
			}
		}
	}

	public BufferedImage getBufferedImage() {
		BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// TODO: Stuff to make the pixels right.
		return output;
	}
	
	public void saveBitMapImages(String baseName, String fileExtension) {
		for (int channelIndex = 0; channelIndex < RGBPixel.NUMBER_OF_CHANNELS; channelIndex++) {
			for (int channelBitIndex = 0; channelBitIndex < RGBPixel.NUMBER_OF_BITS_PER_CHANNEL; channelBitIndex++) {
				saveBitMapImage(baseName, fileExtension, channelIndex, channelBitIndex);
			}
		}
	}
	
	public void saveBitMapImage(String baseName, String fileExtension, int channelIndex, int bitIndex) {
		BufferedImage bitmapImage = bitmaps[channelIndex][bitIndex].getBitMapImage(Channel.getPureColour(Channel.channelMapping(channelIndex)));
		String encoding = "BinaryEncoded"; 
		if (greyEncoded) {
			encoding = "GreyEncoded";
		}
		String path = baseName + Channel.channelMapping(channelIndex) + bitIndex  + encoding + fileExtension;
		try {
			ImageIO.write(bitmapImage, "bmp", new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		BufferedImage input = null;

		try {
			input = ImageIO.read(new File("sample.bmp"));
		} catch (IOException e) {
			// TODO: handle exception
		}
		Boolean convertToGreyEncoding = true;
		BitImageSet test = new BitImageSet(input, convertToGreyEncoding);
		//test.saveBitMapImage("Testing", ".bmp", 1, 0);
		test.saveBitMapImages("Testing", ".bmp");
		System.out.println("End Of main");
	}
}
