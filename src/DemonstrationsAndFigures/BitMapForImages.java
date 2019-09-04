package DemonstrationsAndFigures;

import java.awt.Color;
import java.awt.image.BufferedImage;

import release.BitMap;
import release.Channel;

public class BitMapForImages extends BitMap{
	public BitMapForImages(BitMap toClone) {
		super(toClone);
	}

	public BitMapForImages(int width, int height) {
		super(width, height);
	}

	public BitMapForImages(int width, int height, Channel channel, int bitmap) {
		super(width, height, channel, bitmap);
	}
}
