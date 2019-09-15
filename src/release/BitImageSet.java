package release;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;



public class BitImageSet {
	protected BitMap[][] bitmaps;
	protected int width;
	protected int height;
	protected boolean greyEncoded = false;
	protected String fileName;
	protected SupportedImageFormats sourceFormat;
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public boolean getBit(Channel c, int bitmapIndex, int x, int y) {
		return bitmaps[Channel.channelMapping(c)][bitmapIndex].getBit(x, y);
	}

	public String getFileName() {
		return fileName;
	}

	public BitImageSet(BufferedImage input, String path, boolean convertToGreyEncoding) {
		sourceFormat = SupportedImageFormats.getFormat(path);
		fileName = path.replaceFirst("[.][^.]+$", "");

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
	
	public BitImageSet(BitImageSet toClone) {
		width = toClone.width;
		height = toClone.height;
		greyEncoded = toClone.greyEncoded;
		bitmaps = new BitMap[toClone.bitmaps.length][];
		fileName = toClone.fileName;
		sourceFormat = toClone.sourceFormat;


		// make deep copy of bitmaps
		for (int i = 0; i < bitmaps.length; i++) {
			bitmaps[i] = new BitMap[toClone.bitmaps[i].length];
			for (int j = 0; j < bitmaps[i].length; j++) {
				bitmaps[i][j] = new BitMap(toClone.bitmaps[i][j]);
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
	
	public void convertToImage(String addionalInfo) {
		convertToImage(addionalInfo, sourceFormat);
	}
	
	public String convertToImage(String addionalInfo, SupportedImageFormats sourceFormat) {
		String filePath = fileName +  addionalInfo  + "." + SupportedImageFormats.getFileExtension(sourceFormat);
		try {
			ImageIO.write(this.getBufferedImage(), SupportedImageFormats.getFileExtension(sourceFormat), new File(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filePath;
	}
	
	public static BitImageSet makeBitImageSet(String path, boolean convertToGrey) {
		BufferedImage input = null;
		
		try {
			input = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new BitImageSet(input, path, convertToGrey);
	}
	
	public ArrayList<Coordinant> getFrameCorners(int segmentWidth, int segmentHeight) {
		ArrayList<Coordinant> allBitMapFrameCorners = new ArrayList<>();
		for (int i = 0; i < bitmaps[0].length; i++) {
			for (int j = 0; j < bitmaps.length; j++) {
				ArrayList<Coordinant> bitMapFrameCorners = bitmaps[j][i].getFrameCorners(segmentWidth, segmentHeight);
				for (Coordinant coordinant : bitMapFrameCorners) {
					// Add info about bitmap it came from.
					coordinant.setChannel(Channel.channelMapping(j));
					coordinant.setBitMap(i);

					// Then add to the list
					allBitMapFrameCorners.add(coordinant);
				}
			}
		}
		return allBitMapFrameCorners;
	}
	
	public void replaceSegment(Coordinant toReplace, BitMap replacement) {
		bitmaps[Channel.channelMapping(toReplace.getChannel())][toReplace.getBitMap()].replaceCoordinantWithBitmap(toReplace, replacement);
	}
	
	public void addSegmentToReplacementImage(Coordinant toReplace, BitMap replacement) {
		bitmaps[Channel.channelMapping(toReplace.getChannel())][toReplace.getBitMap()].replacementImageCoordinantWithBitmap(toReplace, replacement);
	}

	public BitMap extractSegment(Coordinant coordanant, int segmentWidth, int segmentHeight) {
		return bitmaps[Channel.channelMapping(coordanant.getChannel())][coordanant.getBitMap()].extractSegment(coordanant, segmentWidth, segmentHeight);
	}
	
	public String saveImage(String fileName) {
		return convertToImage(fileName, sourceFormat);
	}
	
	public static void saveImage(BufferedImage image, String path) {
		try {
			ImageIO.write(image, "png", new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Bellow are methods used to make technical images for report, they are not
		// used if you are just performing steganography, they are useful for
		// visualisations of bitplanes ect.
		public ArrayList<String> saveBitPlaneImages(String baseName) {
			ArrayList<String> paths = new ArrayList<>();
			for (int i = 0; i < bitmaps.length; i++) {
				for (int j = 0; j < bitmaps[i].length; j++) {
					paths.add(saveBitMapImage(baseName + fileName, i, j));
				}
			}
			paths.add(saveImage(baseName));
			return paths;
		}

		public String saveBitMapImage(String baseName, int channelIndex, int bitIndex) {
			BitMap bitmap =  bitmaps[channelIndex][bitIndex];
			BufferedImage bitmapImage = bitmap.getBitMapImage();
			String encoding = "BinaryEncoded";
			if (greyEncoded) {
				encoding = "GreyEncoded";
			}
			String path = baseName + Channel.channelMapping(channelIndex) + bitIndex + encoding + ".png";
			saveImage(bitmapImage, path);
			return path;
		}
}