package minimal;

import java.util.ArrayList;

public abstract class SegmentClassifier {
	BitMap source;
	
	public SegmentClassifier(BitMap source) {
		this.source = source;
	}
	
	public abstract boolean meetsCriteria(Coordinant upperLeft);
	public ArrayList<Coordinant> meetsCriteria(ArrayList<Coordinant> candidates){
		ArrayList<Coordinant> validCoords = new ArrayList<>();
		for (Coordinant candidate : candidates) {
			if (meetsCriteria(candidate)) {
				validCoords.add(candidate);
			}
		}
		return validCoords;
	}
}
