package experiments;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class BMPFileOpener {
	// I want to check I can open file in Java
	// https://stackoverflow.com/questions/16475482/how-can-i-load-a-bitmap-image-and-manipulate-individual-pixels
	
	public static void main(String[] args) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("WhiteSinglePixel.bmp"));
		} catch (IOException e) {
			// TODO: handle exception
		}
		System.out.println(image);
		System.out.println(Arrays.toString(image.getRaster().getPixel(0,0, new int[3])));
		System.out.println("/////////////////////////////////////////////////////");
		
		BufferedImage fourDifferentColourPixels = null;
		try {
			fourDifferentColourPixels = ImageIO.read(new File("FourDifferentColourPixels.bmp"));
		} catch (IOException e) {
			// TODO: handle exception
		}
		System.out.println("fourDifferentColourPixels: " + fourDifferentColourPixels);
		WritableRaster fourDifferentColourPixelsRaster = fourDifferentColourPixels.getRaster(); 
		System.out.println(Arrays.toString(fourDifferentColourPixelsRaster.getPixel(0,0, new int[3])));
		System.out.println("/////////////////////////////////////////////////////");
		for (int i = 0; i < fourDifferentColourPixelsRaster.getHeight(); i++) {
			for (int j = 0; j < fourDifferentColourPixelsRaster.getWidth(); j++) {
				System.out.println("x = "+ i +", "+ "y = " + j + ",   " + Arrays.toString(fourDifferentColourPixelsRaster.getPixel(i,j, new int[3])));
			}
		}
		
		System.out.println("/////////////////////////////////////////////////////");
		
		BufferedImage simpleFace = null;
		try {
			simpleFace = ImageIO.read(new File("SimpleFace.bmp"));
		} catch (IOException e) {
			// TODO: handle exception
		}
		System.out.println("SimpleFace: " + simpleFace);
		WritableRaster simpleFaceRaster = simpleFace.getRaster(); 
		System.out.println(Arrays.toString(simpleFaceRaster.getPixel(0,0, new int[3])));
		System.out.println("/////////////////////////////////////////////////////");
		for (int i = 0; i < simpleFaceRaster.getHeight(); i++) {
			for (int j = 0; j < simpleFaceRaster.getWidth(); j++) {
				System.out.println("x = "+ i +", "+ "y = " + j + ",   " + Arrays.toString(simpleFaceRaster.getPixel(j,i, new int[3])));
			}
		}
		// TODO: Convert integers to  unsigned bytes https://www.dei.isep.ipp.pt/~asc/tiny-papers/java-unsigned-bytes.pdf
	}
}
