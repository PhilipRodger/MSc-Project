package experiments;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class BMPFileCopyPixelByPixel {
	public static void main(String[] args) {
		BufferedImage input = null;

		try {
			input = ImageIO.read(new File("sample.bmp"));
		} catch (IOException e) {
			// TODO: handle exception
		}
		BufferedImage output = map( input.getWidth(), input.getHeight() );
		
		WritableRaster simpleFaceRaster = input.getRaster();
		for (int y = 0; y < simpleFaceRaster.getHeight(); y++) {
			for (int x = 0; x < simpleFaceRaster.getWidth(); x++) {
				RGBPixel pixel = new RGBPixel(simpleFaceRaster.getPixel(x, y, new int[3]));
				output.setRGB(x, y, pixel.getChannel(Channel.GREEN));
				
			}
		}
        savePNG( output, "test.bmp" );

	}
	
	private static BufferedImage map( int sizeX, int sizeY ){
        final BufferedImage res = new BufferedImage( sizeX, sizeY, BufferedImage.TYPE_INT_RGB );
        return res;
    }
	
	 private static void savePNG( final BufferedImage bi, final String path ){
	        try {
	            RenderedImage rendImage = bi;
	            ImageIO.write(rendImage, "bmp", new File(path));
	            //ImageIO.write(rendImage, "PNG", new File(path));
	            //ImageIO.write(rendImage, "jpeg", new File(path));
	        } catch ( IOException e) {
	            e.printStackTrace();
	        }
	    }
}
