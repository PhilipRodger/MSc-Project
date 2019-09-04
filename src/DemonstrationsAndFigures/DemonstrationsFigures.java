package DemonstrationsAndFigures;

import java.awt.Color;

import release.AlphaComplexity;
import release.BitImageSet;
import release.BitMap;
import release.ComplexityMeasure;
import release.DiagonalComplexity;

public class DemonstrationsFigures {

	/**
	 * Demonstration of Conjugation using a simple Smiley bitmap. Prints to console
	 * and saves images as .png files.
	 */
	public static void saveSmileConjugateExample(ComplexityMeasure definition) {
		// Demonstration of conjugation

		// make smiley face bitmap
		BitMapForImages bitMap = new BitMapForImages(8, 8);
		bitMap.setBit(1, 1);
		bitMap.setBit(2, 1);
		bitMap.setBit(1, 2);
		bitMap.setBit(2, 2);

		bitMap.setBit(6, 1);
		bitMap.setBit(5, 1);
		bitMap.setBit(6, 2);
		bitMap.setBit(5, 2);

		bitMap.setBit(1, 4);
		bitMap.setBit(1, 5);

		bitMap.setBit(2, 5);
		bitMap.setBit(2, 6);
		bitMap.setBit(3, 6);
		bitMap.setBit(4, 6);
		bitMap.setBit(5, 5);
		bitMap.setBit(5, 6);
		bitMap.setBit(6, 4);
		bitMap.setBit(6, 5);

		// Original Smiley
		System.out.println(bitMap);
		int originalSmileComplexity = definition.getComplexity(bitMap);
		int maxPossibleComplexity = definition.maxComplexity(8, 8);
		System.out.println("Complexity = " + originalSmileComplexity + "/" + maxPossibleComplexity);
		BitImageSet.saveImage(bitMap.getBitMapImage(Color.BLACK.getRGB(), 50), "ConjugationDemo " + definition + " originalSmile"
				+ "Complexity" + originalSmileComplexity + "Of" + maxPossibleComplexity + ".png");

		// Checker Board Pattern
		BitMap checkerBoard = definition.getConjugationMap(8, 8);
		System.out.println(checkerBoard);
		int checkerBoardComplexity = definition.getComplexity(checkerBoard);
		System.out.println("Complexity = " + checkerBoardComplexity + "/" + maxPossibleComplexity);
		BitMapSetForImages.saveImage(checkerBoard.getBitMapImage(Color.BLACK.getRGB(), 50), "ConjugationDemo " + definition + " checkerBoard"
				+ "Complexity" + checkerBoardComplexity + "Of" + maxPossibleComplexity + ".png");

		// Original Smile XOR with Checker Board Pattern
		BitMap xOr =  BitMap.xOr(bitMap, checkerBoard);
		System.out.println(xOr);
		int xOrComplexity = definition.getComplexity(xOr);
		System.out.println("Complexity = " + xOrComplexity + "/" + maxPossibleComplexity);
		BitMapSetForImages.saveImage(xOr.getBitMapImage(Color.BLACK.getRGB(), 50),
				"ConjugationDemo " + definition + "  xOrSmile" + "Complexity" + xOrComplexity + "Of" + maxPossibleComplexity + ".png");

		// XOR XOR'd again with Checker Board Pattern
		BitMap xOrXor = BitMap.xOr(xOr, checkerBoard);
		System.out.println(xOrXor);
		int xOrXorComplexity = definition.getComplexity(xOrXor);
		System.out.println("Complexity = " + xOrXorComplexity + "/" + maxPossibleComplexity);
		BitMapSetForImages.saveImage(xOrXor.getBitMapImage(Color.BLACK.getRGB(), 50), "ConjugationDemo " + definition + " xOrXOrSmile"
				+ "Complexity" + xOrXorComplexity + "Of" + maxPossibleComplexity + ".png");
	}
	
	public static void saveConjugateExample(ComplexityMeasure definition) {
		BitMap checkerBoard = definition.getConjugationMap(4, 4);
		BitMapSetForImages.saveImage(checkerBoard.getBitMapImage(Color.BLACK.getRGB(), 50), "4x4" + definition.toString() + "Mask.png");
	}

	
	public static void main(String[] args) {
		saveConjugateExample(new AlphaComplexity());
		saveSmileConjugateExample(new AlphaComplexity());
		
		saveConjugateExample(new DiagonalComplexity());
		saveSmileConjugateExample(new DiagonalComplexity());
	}
}
