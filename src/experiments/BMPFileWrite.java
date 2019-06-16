package experiments;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class BMPFileWrite {
	public static void main(String[] args) {
		// adapted from: http://www.java2s.com/Tutorials/Java/Graphics_How_to/Image/Create_BMP_format_image.htm
		System.out.println("Trying to make a bitmap image");
		BufferedImage img = map( 3200, 1600 );
        savePNG( img, "test.bmp" );
	}
	private static BufferedImage map( int sizeX, int sizeY ){
        final BufferedImage res = new BufferedImage( sizeX, sizeY, BufferedImage.TYPE_INT_RGB );
        Random r = new Random();
        for (int x = 0; x < sizeX; x++){
            for (int y = 0; y < sizeY; y++){
                res.setRGB(x, y, new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255), r.nextInt(255)).getRGB() );
            }
        }
        System.out.println(Integer.toBinaryString((new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)).getRGB())));
        System.out.println(Integer.toBinaryString((new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)).getAlpha())));
        System.out.println(Integer.toBinaryString((new Color(0, 0, 0).getRGB())));
        

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
