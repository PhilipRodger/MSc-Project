package minimal;

import java.util.ArrayList;

public abstract class SegmentManager {
	protected BitImageSet source;
	protected ComplexityMeasure complexityDefinition;
	protected int segmentWidth;
	protected int segmentHeight;
	protected int segmentCapacity;
	protected BitMap conjugationMask;
	protected ArrayList<Coordinant> viableSegments;
	
	private long maxPossibleInfo;
	private int bitsToAddressBits;
	
	public int getSegmentWidth() {
		return segmentWidth;
	}

	public int getSegmentHeight() {
		return segmentHeight;
	}
	
	public long getBitsToAddressBits() {
		return bitsToAddressBits;
	}
	
	public int getNumSegments() {
		return viableSegments.size();
	}

	protected abstract boolean meetsCriteria(Coordinant check);
	
	protected abstract boolean meetsCriteria(BitMap check);

	public SegmentManager(BitImageSet source,int segmentWidth, int segmentHeight) {
		this.source = source;
		this.segmentWidth = segmentWidth;
		this.segmentHeight = segmentHeight;
		segmentCapacity = segmentHeight * segmentWidth;
		getFrameCorners();
	}

	public ArrayList<Coordinant> getFrameCorners(){
		ArrayList<Coordinant> results = source.getFrameCorners(segmentWidth, segmentHeight);
		maxPossibleInfo = results.size() * segmentCapacity;
		bitsToAddressBits = util.numberOfBitsToRepresent(maxPossibleInfo);
		viableSegments = results;
		return results;
	}
	
	public ArrayList<Coordinant> meetsCriteria(ArrayList<Coordinant> candidates){
		ArrayList<Coordinant> validCoords = new ArrayList<>();
		for (Coordinant candidate : candidates) {
			if (meetsCriteria(candidate)) {
				validCoords.add(candidate);
			}
		}
		return validCoords;
	}
	
	public void conjugateMapsBelowThreshold(ArrayList<BitMap> segments) {
		conjugationMask = complexityDefinition.getConjugationMap(segmentWidth, segmentHeight);
		for (int i = 0; i < segments.size(); i++) {
			BitMap map = segments.get(i);
			if (!meetsCriteria(map)) {
				map = getConjugate(map);
				segments.set(i, map);
			}
		}
	}
	
	protected BitMap getConjugate(BitMap map) {
		return BitMap.xOr(map, conjugationMask);
	}

	public ArrayList<BitMap> extractViableSegments() {
		if (viableSegments == null) {
			viableSegments = getFrameCorners();
		}
		ArrayList<BitMap> extracts = new ArrayList<>();
		for (Coordinant coordinant : viableSegments) {
			extracts.add(source.extractSegment(coordinant, segmentWidth, segmentHeight));
		}
		return extracts;
	}

	public BitImageSet replaceWithPayload(ArrayList<BitMap> payloadSegments) {
		BitImageSet output = new BitImageSet(source);
		for (int i = 0; i < payloadSegments.size(); i++) {
			Coordinant toReplace = viableSegments.get(i);
			output.replaceSegment(toReplace, payloadSegments.get(i));
		}
		return output;
		
	}
}
