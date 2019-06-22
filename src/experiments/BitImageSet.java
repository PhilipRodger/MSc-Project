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
					for (int ChannelBitIndex = RGBPixel.NUMBER_OF_BITS_PER_CHANNEL - 1; ChannelBitIndex >= 0; ChannelBitIndex--) {
						pixelRGB[ChannelIndex]  = pixelRGB[ChannelIndex] << 1;
						if (bitmaps[ChannelIndex][ChannelBitIndex].getBit(x, y)) {
							pixelRGB[ChannelIndex]++;
						}
						//else least significant bit is 0 which is already value, no change required.
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
		BufferedImage bitmapImage = bitmaps[channelIndex][bitIndex].getBitMapImage(Channel.getPureColour(Channel.channelMapping(channelIndex)));
		String encoding = "BinaryEncoded"; 
		if (greyEncoded) {
			encoding = "GreyEncoded";
		}
		String path = baseName + Channel.channelMapping(channelIndex) + bitIndex  + encoding + fileExtension;
		saveImage(bitmapImage, path);
	}
	
	public void saveBitMapImageHighlightComplexity(String baseName, String fileExtension, Channel channel, int bitIndex, int frameWidth, int frameHeight, double minimumComplexity, double maximumComplexity) {
		BufferedImage bitmapImage = bitmaps[Channel.channelMapping(channel)][bitIndex].getBitMapImageBlackReplaced(Channel.getPureColour(channel), frameWidth, frameHeight, minimumComplexity, maximumComplexity);
		String encoding = "BinaryEncoded"; 
		if (greyEncoded) {
			encoding = "GreyEncoded";
		}
		String path = baseName + channel + bitIndex + encoding + "Frame" + frameWidth + "X" + frameHeight + "MinComplexity" + minimumComplexity + "MaxComplexity" + maximumComplexity + fileExtension;
		saveImage(bitmapImage, path);
	}
	
	public void saveBMPWithRandomSegmentsAboveAlphaComplexity(String baseName, String fileExtension, int segmentWidth, int segmentHeight, double minimumComplexity) {
		int countReplacement = 0;
		for (int i = 0; i < bitmaps.length; i++) {
			for (int j = 0; j < bitmaps[i].length; j++) {
				countReplacement += bitmaps[i][j].replaceWithRandomSegmentsAboveAlphaComplexity(segmentWidth, segmentHeight, minimumComplexity);
			}
		}
		String encoding = "BinaryEncoded"; 
		if (greyEncoded) {
			encoding = "GreyEncoded";
		}
		int bitsReplaced = segmentHeight * segmentWidth * countReplacement;
		int bytes = bitsReplaced / 8;
		System.out.println(countReplacement + " x " + segmentHeight + "X" + segmentWidth + "replacements = " + bitsReplaced + "bits added or " + bytes + "bytes");
		
		
		String path = baseName + encoding + "Segment" + segmentWidth + "X" + segmentHeight + "Min"+ minimumComplexity +"Complexity" + bytes + "BytesAdded" + fileExtension;
		convertToBMPFile(path);
	}
	
	public double estimateTheoreticalMapProportion(int segmentWidth, int segmentHeight, double minimumComplexity) {
		BitImageSet copy = new BitImageSet(this);
		
		// Calculate total number of substitutions.
		int replacements = 0;
		for (int i = 0; i < bitmaps.length; i++) {
			for (int j = 0; j < bitmaps[i].length; j++) {
				// inefficient but fine to work out this
				replacements += copy.bitmaps[i][j].replaceWithRandomSegmentsAboveAlphaComplexity(segmentWidth, segmentHeight, minimumComplexity);
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
		int[] a = BitMap.getRandomComplexityDistribution(segmentWidth, segmentHeight, 1000000);
		double[] percent = distributionAsPercent(a);
		double[] complexityAlphas = BitMap.complexityAlpha(segmentWidth, segmentHeight);
		double percentageNeedingConjugation = 0.0;
		for (int i = 0; i < complexityAlphas.length; i++) {
			if(complexityAlphas[i] < minimumComplexity) {
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
	
	private void saveImage(BufferedImage image, String path) {
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
			percentage[i] = ((original[i] * 1.0) / total)*100;
		}
		return percentage;
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
		//test.saveBitMapImages("Testing", ".bmp");
		//test.convertToBMPFile("checkingReverseOnGrayConversion.bmp");
		
//		// frames
		int width = 2;
		int height = 2;
		//Channel channel = Channel.RED;
		//int channelBit = 0;
		double minComplexity = 0.35;
		
		//double maxComplexity = 0;
		
		// Make a visual version of replacement in bitmap.
		//test.saveBitMapImageHighlightComplexity("test", ".bmp", channel, channelBit, width, height, minComplexity, maxComplexity);
		
//		for (int i = 1; i < 65; i++) {
//			//Make a visual representation of what will be replaced 
//			for (int j = 0; j < 1; j++) {
//				test.saveBitMapImageHighlightComplexity("test", ".bmp", channel, j, i, i, minComplexity, maxComplexity);
//			}	
//		}
		

//		Complexity distribution of one bitmap
//		System.out.println("Bit 0 Distribution:" + Arrays.toString(test.getBitMapCompexityDistribution(width, height, channel, 0)));
//		System.out.println("Bit 1 Distribution:" + Arrays.toString(test.getBitMapCompexityDistribution(width, height, channel, 1)));
//		System.out.println("Bit 2 Distribution:" + Arrays.toString(test.getBitMapCompexityDistribution(width, height, channel, 2)));
//		System.out.println("Bit 3 Distribution:" + Arrays.toString(test.getBitMapCompexityDistribution(width, height, channel, 3)));
//		System.out.println("Bit 4 Distribution:" + Arrays.toString(test.getBitMapCompexityDistribution(width, height, channel, 4)));
//		System.out.println("Bit 5 Distribution:" + Arrays.toString(test.getBitMapCompexityDistribution(width, height, channel, 5)));
//		System.out.println("Bit 6 Distribution:" + Arrays.toString(test.getBitMapCompexityDistribution(width, height, channel, 6)));
//		System.out.println("Bit 7 Distribution:" + Arrays.toString(test.getBitMapCompexityDistribution(width, height, channel, 7)));
//
//		
////		Complexity distribution of one channel
//		System.out.println("Whole Distribution:" + Arrays.toString(test.getChannelBitMapCompexityDistribution(width, height, channel)));
//
//
//		
//		System.out.println("Random Distribution:" + Arrays.toString(BitMap.getRandomComplexityDistribution(width, height, 100000)));
//		
//		System.out.println("/////////////////////////////////////////////////////////////////////////////////////////////////////////");
		//int[] a = test.getChannelBitMapCompexityDistribution(width, height, channel);
		//int[] a = test.getBitMapCompexityDistribution(width, height, channel, 3);
//		int[] a = BitMap.getRandomComplexityDistribution(width, height, 1000000);
//		double[] percent = distributionAsPercent(a);
//		double[] complexityAlphas = BitMap.complexityAlpha(width, height);
//		for (int i = 0; i < complexityAlphas.length; i++) {
//			if(percent[i] != 0) {
//				System.out.println(String.format("Alpha Complexity Value: %f  Percent: %f %%", complexityAlphas[i], percent[i]));
//			}
//		}
		
		// Make random replacement segments when above threshold
		//test.saveBMPWithRandomSegmentsAboveAlphaComplexity("lena_sharp", ".bmp", width, height, minComplexity);
		//test.saveBitMapImages("lenasharpLSB", ".bmp");
		
		// Make a set of complexity replaced segments over a bunch of complexities and segment sizes. 
//		for (int i = 1; i <= 512; i= i*2) {
//			for (int j = 0; j < 11; j++) {
//				BitImageSet copy = new BitImageSet(test);
//				double minComplex = ((j*1.0)/10);
//				copy.saveBMPWithRandomSegmentsAboveAlphaComplexity("lena_color", ".bmp", i, i, minComplex);
//			}
//		}
		
		test.estimateTheoreticalMapProportion(width, height, minComplexity);
		
		System.out.println("End Of main");
	}
}
