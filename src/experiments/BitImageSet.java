package experiments;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class BitImageSet {
	BitMap[][] bitmaps;
	int width;
	int height;
	boolean greyEncoded = false;

	public final static int MAX_COMPLEXITY = 1;

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

		// make deep copy of bitmaps
		for (int i = 0; i < bitmaps.length; i++) {
			bitmaps[i] = new BitMap[toClone.bitmaps[i].length];
			for (int j = 0; j < bitmaps[i].length; j++) {
				bitmaps[i][j] = new BitMap(toClone.bitmaps[i][j]);
			}
		}
	}

	public void convertToBMPFile(String path) {
		BufferedImage image = getBufferedImage();
		saveImage(image, path);
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

	public int[] getChannelBitMapCompexityDistribution(int width, int height, Channel channel) {
		int[][] bitIndexComplexities = new int[RGBPixel.NUMBER_OF_BITS_PER_CHANNEL][];
		for (int i = 0; i < bitIndexComplexities.length; i++) {
			bitIndexComplexities[i] = getBitMapCompexityDistribution(width, height, channel, i);
		}
		int[] summedComplexities = new int[bitIndexComplexities[0].length];

		for (int i = 0; i < summedComplexities.length; i++) {
			int sum = 0;
			for (int j = 0; j < bitIndexComplexities.length; j++) {
				sum += bitIndexComplexities[j][i];
			}
			summedComplexities[i] = sum;
		}
		return summedComplexities;
	}

	public void printBitComplexityDistributions(int width, int height) {
		int[][] bitIndexComplexities = new int[RGBPixel.NUMBER_OF_BITS_PER_CHANNEL][];

		for (int i = 0; i < bitIndexComplexities.length; i++) {
			// loop over each bit map of equal significance
			bitIndexComplexities[i] = getBitMapCompexityDistribution(width, height, Channel.channelMapping(0), i);
			for (int channel = 1; channel < Channel.values().length; channel++) {
				int[] tempComplexity = getBitMapCompexityDistribution(width, height, Channel.channelMapping(channel),
						i);
				bitIndexComplexities[i] = addArray(bitIndexComplexities[i], tempComplexity);
			}
		}

//		double[] percent = distributionAsPercent(a);
		double[] complexityAlphas = BitMap.complexityAlpha(width, height);
		System.out.println("Alpha Complexity	Bit 8	Bit 7	Bit 6	Bit 5	Bit 4	Bit 3	Bit 2	Bit 1");
		for (int i = 0; i < complexityAlphas.length; i++) {
			System.out.println(String.format("%f	%d	%d	%d	%d	%d	%d	%d	%d", complexityAlphas[i],
					bitIndexComplexities[7][i], bitIndexComplexities[6][i], bitIndexComplexities[5][i],
					bitIndexComplexities[4][i], bitIndexComplexities[3][i], bitIndexComplexities[2][i],
					bitIndexComplexities[1][i], bitIndexComplexities[0][i]));
		}
	}

	private void printBitComplexityDistribution(int width, int height) {
		double[] complexityAlphas = BitMap.complexityAlpha(width, height);
		int[] bitIndexComplexities = new int[complexityAlphas.length];

		for (int i = 0; i < RGBPixel.NUMBER_OF_BITS_PER_CHANNEL; i++) {
			// loop over each bit map of equal significance
			for (int channel = 0; channel < Channel.values().length; channel++) {
				int[] tempComplexity = getBitMapCompexityDistribution(width, height, Channel.channelMapping(channel), i);
				bitIndexComplexities = addArray(bitIndexComplexities, tempComplexity);
			}
		}

		double[] percent = distributionAsPercent(bitIndexComplexities);
		System.out.println("Alpha Complexity	Frequency");
		for (int i = 0; i < complexityAlphas.length; i++) {
			System.out.println(String.format("%f	%f", complexityAlphas[i], percent[i]));
		}
	}

	public static int[] addArray(int[] first, int[] second) {
		if (first.length != second.length) {
			throw new IllegalArgumentException("Can't add two arrays if they are not the same size");
		}
		int[] result = new int[first.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = first[i] + second[i];
		}
		return result;
	}

	public int[] getBitMapCompexityDistribution(int width, int height, Channel channel, int bitIndex) {
		return bitmaps[Channel.channelMapping(channel)][bitIndex].getBitMapCompexityDistribution(width, height);
	}

	public void saveBitMapImages(String baseName, String fileExtension) {
		for (int channelIndex = 0; channelIndex < RGBPixel.NUMBER_OF_CHANNELS; channelIndex++) {
			for (int channelBitIndex = 0; channelBitIndex < RGBPixel.NUMBER_OF_BITS_PER_CHANNEL; channelBitIndex++) {
				saveBitMapImage(baseName, fileExtension, channelIndex, channelBitIndex);
			}
		}
	}

	public void saveBitMapImage(String baseName, String fileExtension, int channelIndex, int bitIndex) {
		BufferedImage bitmapImage = bitmaps[channelIndex][bitIndex]
				.getBitMapImage(Channel.getPureColour(Channel.channelMapping(channelIndex)));
		String encoding = "BinaryEncoded";
		if (greyEncoded) {
			encoding = "GreyEncoded";
		}
		String path = baseName + Channel.channelMapping(channelIndex) + bitIndex + encoding + fileExtension;
		saveImage(bitmapImage, path);
	}

	public void saveBitMapImageHighlightComplexity(String baseName, String fileExtension, Channel channel, int bitIndex,
			int frameWidth, int frameHeight, double minimumComplexity, double maximumComplexity) {
		BufferedImage bitmapImage = bitmaps[Channel.channelMapping(channel)][bitIndex].getBitMapImageBlackReplaced(
				Channel.getPureColour(channel), frameWidth, frameHeight, minimumComplexity, maximumComplexity);
		String encoding = "BinaryEncoded";
		if (greyEncoded) {
			encoding = "GreyEncoded";
		}
		String path = baseName + channel + bitIndex + encoding + "Frame" + frameWidth + "X" + frameHeight
				+ "MinComplexity" + minimumComplexity + "MaxComplexity" + maximumComplexity + fileExtension;
		saveImage(bitmapImage, path);
	}

	public void saveBMPWithRandomSegmentsAboveAlphaComplexity(String baseName, String fileExtension, int segmentWidth,
			int segmentHeight, double minimumComplexity) {
		int countReplacement = 0;
		for (int i = 0; i < bitmaps.length; i++) {
			for (int j = 0; j < bitmaps[i].length; j++) {
				countReplacement += bitmaps[i][j].replaceWithRandomSegmentsAboveAlphaComplexity(segmentWidth,
						segmentHeight, minimumComplexity);
			}
		}
		String encoding = "BinaryEncoded";
		if (greyEncoded) {
			encoding = "GreyEncoded";
		}
		int bitsReplaced = segmentHeight * segmentWidth * countReplacement;
		int bytes = bitsReplaced / 8;
		System.out.println(countReplacement + " x " + segmentHeight + "X" + segmentWidth + "replacements = "
				+ bitsReplaced + "bits added or " + bytes + "bytes");

		String path = baseName + encoding + "Min" + minimumComplexity + "Complexity" + segmentWidth + "X"
				+ segmentHeight + "Segment" + bytes + "BytesAdded" + fileExtension;
		convertToBMPFile(path);
	}

	public double estimateTheoreticalMapProportion(int segmentWidth, int segmentHeight, double minimumComplexity) {
		System.out.println("//////////////////////////////////////////////////////////////////");
		System.out.println(segmentHeight + "x" + segmentWidth + " alpha complexity cutoff of " + minimumComplexity
				+ " in a " + width + "x" + height + "image.\n");
		BitImageSet copy = new BitImageSet(this);

		// Calculate total number of substitutions.
		int replacements = 0;
		for (int i = 0; i < bitmaps.length; i++) {
			for (int j = 0; j < bitmaps[i].length; j++) {
				// inefficient but fine to work out this
				replacements += copy.bitmaps[i][j].replaceWithRandomSegmentsAboveAlphaComplexity(segmentWidth,
						segmentHeight, minimumComplexity);
			}
		}
		System.out.println("Total replacements: " + replacements);

		// Calculate minimum number of bits to represent each replacement.
		int temp = replacements;
		int numberOfBitsRequired = 0;
		while (temp > 0) {
			temp = temp / 2;
			numberOfBitsRequired++;
		}
		System.out.println("Minimum number of bits to represent each replacement:" + numberOfBitsRequired);

		// Estimate proportion of segments that require conjugation mapping.
		int[] a = BitMap.getRandomComplexityDistribution(segmentWidth, segmentHeight, 1000000, 60, 2);
		double[] percent = distributionAsPercent(a);
		double[] complexityAlphas = BitMap.complexityAlpha(segmentWidth, segmentHeight);
		double percentageNeedingConjugation = 0.0;
		for (int i = 0; i < complexityAlphas.length; i++) {
			if (complexityAlphas[i] < minimumComplexity) {
				percentageNeedingConjugation += percent[i];
			}
		}
		System.out.println("Estimated segment % requiring conjugation: " + percentageNeedingConjugation);

		double numberOfConjugates = (percentageNeedingConjugation / 100) * replacements;
		System.out.println("Estimated number of conjugates in image: " + numberOfConjugates);

		double totalSpaceNeededForConjMap = numberOfConjugates * numberOfBitsRequired;
		System.out.println("Estimated bits for map: " + totalSpaceNeededForConjMap);

		double totalSegmentReplaceSpace = segmentWidth * segmentHeight * replacements;
		System.out.println("Total replaceable bits: " + totalSegmentReplaceSpace);

		double useableSpace = totalSegmentReplaceSpace - totalSpaceNeededForConjMap;
		System.out.println("Usable space: " + useableSpace);

		double proportionUsable = useableSpace / totalSegmentReplaceSpace;
		System.out.println("Usable proportion: " + proportionUsable);

		double percentUsable = proportionUsable * 100;
		System.out.println("Usable percentage: " + percentUsable);

		return proportionUsable;

	}

	public static void saveImage(BufferedImage image, String path) {
		try {
			ImageIO.write(image, "bmp", new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static double[] distributionAsPercent(int[] original) {
		int total = 0;
		for (int i = 0; i < original.length; i++) {
			total += original[i];
		}

		double[] percentage = new double[original.length];
		for (int i = 0; i < original.length; i++) {
			percentage[i] = ((original[i] * 1.0) / total) * 100;
		}
		return percentage;
	}

	public void saveBitMapReplacementImages(String baseName, String fileExtension, int segmentWidth, int segmentHeight,
			double minComplexity, double maxComplexity) {
		for (int i = 0; i < RGBPixel.NUMBER_OF_BITS_PER_CHANNEL; i++) {
			for (int j = 0; j < RGBPixel.NUMBER_OF_CHANNELS; j++) {
				saveBitMapImageHighlightComplexity(baseName, fileExtension, Channel.channelMapping(j), i, segmentWidth,
						segmentHeight, minComplexity, maxComplexity);
			}
		}
	}

	public static BitImageSet makeBitImageSet(String path, boolean convertToGrey) {
		BufferedImage input = null;
		try {
			input = ImageIO.read(new File(path));
		} catch (IOException e) {
			// TODO: handle exception
		}
		return new BitImageSet(input, convertToGrey);
	}

	public ArrayList<Coordinant> getFrameCorners(int segmentWidth, int segmentHeight, double alphaComplexityCutoff) {
		ArrayList<Coordinant> allBitMapFrameCorners = new ArrayList<>();
		for (int i = 0; i < bitmaps.length; i++) {
			for (int j = 0; j < bitmaps[i].length; j++) {
				ArrayList<Coordinant> bitMapFrameCorners = bitmaps[i][j].getFrameCorners(segmentWidth, segmentHeight);
				ArrayList<Coordinant> complexEnough = bitmaps[i][j].getCoordinantsWithinComplexityRange(segmentWidth,
						segmentHeight, bitMapFrameCorners, alphaComplexityCutoff, MAX_COMPLEXITY);
				for (Coordinant coordinant : complexEnough) {
					// Add info about bitmap it came from.
					coordinant.setChannel(Channel.channelMapping(i));
					coordinant.setBitMap(j);

					// Then add to the list
					allBitMapFrameCorners.add(coordinant);
				}
			}
		}

		return allBitMapFrameCorners;
	}

	// TODO
	// public writeBit(Co) {

	// }

	public static void main(String[] args) {

		Boolean convertToGreyEncoding = true;
		BitImageSet test = BitImageSet.makeBitImageSet("lena_colour.bmp", convertToGreyEncoding);
		// test.saveBitMapImage("Testing", ".bmp", 1, 0);
		// test.saveBitMapImages("Testing", ".bmp");
		// test.convertToBMPFile("checkingReverseOnGrayConversion.bmp");

//		// frames
		int width = 8;
		int height = 8;
		Channel channel = Channel.RED;
		// int channelBit = 0;
		double minComplexity = .4;

		double maxComplexity = 1;
		
		
		//test.saveBMPWithRandomSegmentsAboveAlphaComplexity("lena_colour", ".bmp", 512, 512, 0.3);
		//test.saveBMPWithRandomSegmentsAboveAlphaComplexity("sample", ".bmp", 4032, 3024, 0.2);
		//test.saveBMPWithRandomSegmentsAboveAlphaComplexity("sample", ".bmp", 4032, 3024, 0.0);

		// test.printBitComplexityDistributions(width, height);
		test.printBitComplexityDistribution(width, height);

		// BitMap.printRandomDistribution(width, height, 10000000, minComplexity);

		//

		// Make a visual version of replacement in bitmap.
		// test.saveBitMapReplacementImages("BitMap", ".bmp", 2, 2, minComplexity,
		// maxComplexity);

//
//
//		
//		System.out.println("Random Distribution:" + Arrays.toString(BitMap.getRandomComplexityDistribution(width, height, 100000)));
//		

		// Make random replacement segments when above threshold
		// test.saveBMPWithRandomSegmentsAboveAlphaComplexity("lena_sharp", ".bmp",
		// width, height, minComplexity);
		// test.saveBitMapImages("lenasharpLSB", ".bmp");

		// Make a set of complexity replaced segments over a bunch of complexities and
		// segment sizes.
//		for (int i = 1; i <= 512; i= i*2) {
//			for (int j = 0; j < 11; j++) {
//				BitImageSet copy = new BitImageSet(test);
//				double minComplex = ((j*1.0)/10);
//				copy.saveBMPWithRandomSegmentsAboveAlphaComplexity("lena_color", ".bmp", i, i, minComplex);
//			}
//		}

		// Print out estimate size of Conjugate map required:
//		for (int i = 1; i <= 512; i= i*2) {
//			for (int j = 1; j < 11; j++) {
//				BitImageSet copy = new BitImageSet(test);
//				double minComplex = ((j*1.0)/10);
//				copy.estimateTheoreticalMapProportion(i, i, minComplex);
//
//			}
//		}

		// test.saveBitMapImages("", ".bmp");
		// test.saveBitMapReplacementImages("", ".bmp", 8, 8, 0.3, 1);
		// test.saveBMPWithRandomSegmentsAboveAlphaComplexity("", ".bmp", 8, 8, 0.3);
		// test.saveBitMapImages("dog", ".bmp");
		System.out.println("End Of main");
	}
}
