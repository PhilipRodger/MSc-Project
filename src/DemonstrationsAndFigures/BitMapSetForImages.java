package DemonstrationsAndFigures;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import release.BitImageSet;
import release.Channel;

public class BitMapSetForImages extends BitImageSet {

	public BitMapSetForImages(BufferedImage input, String path, boolean convertToGreyEncoding) {
		super(input, path, convertToGreyEncoding);
		// TODO Auto-generated constructor stub
	}

	public BitMapSetForImages(BitImageSet toClone) {
		super(toClone);
		// TODO Auto-generated constructor stub
	}

	// Bellow are methods used to make technical images for report, they are not
	// used if you are just performing steganography, they are useful for
	// visualisations of bitplanes ect.
	public void saveBitPlaneImages(String baseName) {
		for (int i = 0; i < bitmaps.length; i++) {
			for (int j = 0; j < bitmaps[i].length; j++) {
				saveBitMapImage(baseName, i, j);
			}
		}
	}

	public void saveBitMapImage(String baseName, int channelIndex, int bitIndex) {
		BitMapForImages bitmap = (BitMapForImages) bitmaps[channelIndex][bitIndex];
		BufferedImage bitmapImage = bitmap.getBitMapImage(Channel.getPureColour(Channel.channelMapping(channelIndex)));
		String encoding = "BinaryEncoded";
		if (greyEncoded) {
			encoding = "GreyEncoded";
		}
		String path = baseName + Channel.channelMapping(channelIndex) + bitIndex + encoding + sourceFormat;
		saveImage(bitmapImage, path);
	}

	public static void saveImage(BufferedImage image, String path) {
		try {
			ImageIO.write(image, "png", new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
