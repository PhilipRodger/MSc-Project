package experiments;

import java.util.IllegalFormatException;

import javax.activation.UnsupportedDataTypeException;

public enum SupportedImageFormats {
	BMP,
	PNG;
	
	public static SupportedImageFormats getFormat(String path) {
		path = path.toLowerCase();
		if (path.endsWith("bmp")) {
			return BMP;
		} else if (path.endsWith("png")) {
			return PNG;
		} 
		throw new UnsupportedOperationException("File Format Not Supported");
	}
	
	public static String getFileExtension(SupportedImageFormats format) {
		if (format == BMP) {
			return "bmp";
		} else if (format == PNG) {
			return "png";
		}
		throw new UnsupportedOperationException("File Format Not Supported");
	}
}
