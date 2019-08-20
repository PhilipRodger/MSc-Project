package minimal;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class BitImageSet {
	private BitMap[][] bitmaps;
	private int width;
	private int height;
	private boolean greyEncoded = false;
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public BitImageSet(BufferedImage input, boolean convertToGreyEncoding) {

		// Take note of metadata and flag.
		greyEncoded = convertToGreyEncoding;
		width = input.getWidth();
		height = input.getHeight();

		// Initialise internal representation
		bitmaps = new BitMap[RGBPixel.NUMBER_OF_CHANNELS][RGBPixel.NUMBER_OF_BITS_PER_CHANNEL];
		for (int i = 0; i < bitmaps.length; i++) {
			for (int j = 0; j < bitmaps[i].length; j++) {
				bitmaps[i][j] = new BitMap(width, height, Channel.channelMapping(i), j);
			}
		}

		// Copy info over from image.
		WritableRaster inputRaster = input.getRaster();
		for (int y = 0; y < inputRaster.getHeight(); y++) {
			for (int x = 0; x < inputRaster.getWidth(); x++) {
				// Iterate over all pixels in the input image.

				RGBPixel pixel = new RGBPixel(inputRaster.getPixel(x, y, new int[4]));
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
						// else false which is already value, no change required.
					}
				}
			}
		}
	}
	
	public BufferedImage getBufferedImage() {
		BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		// Copy info over from image.
		for (int y = 0; y < output.getHeight(); y++) {
			for (int x = 0; x < output.getWidth(); x++) {
				// Iterate over all pixels in the to create image.

				// Add bit map data to output.
				int[] pixelRGB = new int[3];
				for (int ChannelIndex = 0; ChannelIndex < RGBPixel.NUMBER_OF_CHANNELS; ChannelIndex++) {
					for (int ChannelBitIndex = RGBPixel.NUMBER_OF_BITS_PER_CHANNEL
							- 1; ChannelBitIndex >= 0; ChannelBitIndex--) {
						pixelRGB[ChannelIndex] = pixelRGB[ChannelIndex] << 1;
						if (bitmaps[ChannelIndex][ChannelBitIndex].getBit(x, y)) {
							pixelRGB[ChannelIndex]++;
						}
						// else least significant bit is 0 which is already value, no change required.
					}
				}
				RGBPixel pixel = new RGBPixel(pixelRGB);

				if (greyEncoded) {
					// Convert back to binary
					pixel = pixel.makeBinaryCodePixelFromGray();
				}
				output.setRGB(x, y, pixel.getRGBColour());

			}
		}
		return output;
	}
	
	public void convertToImage(String fileName, SupportedImageFormats sourceFormat) {
		String filePath = fileName  + "." + SupportedImageFormats.getFileExtension(sourceFormat);
		try {
			ImageIO.write(this.getBufferedImage(), SupportedImageFormats.getFileExtension(sourceFormat), new File(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static BitImageSet makeBitImageSet(String path, boolean convertToGrey) {
		BufferedImage input = null;
		try {
			input = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new BitImageSet(input, convertToGrey);
	}
	
	public ArrayList<Coordinant> getFrameCorners(BPCS algorithim) {
		ArrayList<Coordinant> candidateBitMapFrameCorners = new ArrayList<>();
		for (int i = 0; i < bitmaps.length; i++) {
			for (int j = 0; j < bitmaps[i].length; j++) {
				ArrayList<Coordinant> bitMapFrameCorners = bitmaps[i][j].getFrameCorners(algorithim);
				ArrayList<Coordinant> meetCriteria = bitmaps[i][j].getCoordinantsMatchingCriteria(algorithim, bitMapFrameCorners); 
				candidateBitMapFrameCorners.addAll(meetCriteria);
				
			}
		}
		return candidateBitMapFrameCorners;
	}
	
	public void replaceSegment(Coordinant toReplace, BitMap replacement) {
		bitmaps[Channel.channelMapping(toReplace.getChannel())][toReplace.getBitMap()].replaceCoordinantWithBitmap(toReplace, replacement);
	}

	public BitMap extractSegment(Coordinant coordanant, int segmentWidth, int segmentHeight) {
		return bitmaps[Channel.channelMapping(coordanant.getChannel())][coordanant.getBitMap()].extractSegment(coordanant, segmentWidth, segmentHeight);
	}

}