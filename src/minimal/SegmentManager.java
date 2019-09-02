package minimal;

import java.util.ArrayList;
import java.util.Iterator;

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
		if (viableSegments == null) {
			getViableFrameCorners();
		}
		return viableSegments.size();
	}
	
	public ArrayList<Coordinant> getViableSegments() {
		return viableSegments;
	}

	protected abstract boolean meetsCriteriaForSegmentSelection(Coordinant check);
	
	protected abstract boolean meetsCriteriaForPayloadEmbed(BitMap check);

	public SegmentManager(BitImageSet source,int segmentWidth, int segmentHeight) {
		this.source = source;
		this.segmentWidth = segmentWidth;
		this.segmentHeight = segmentHeight;
		segmentCapacity = segmentHeight * segmentWidth;
	}

	public ArrayList<Coordinant> getViableFrameCorners(){
		ArrayList<Coordinant> allCorners = source.getFrameCorners(segmentWidth, segmentHeight);
		viableSegments = new ArrayList<>();
		for (Coordinant coordinant : allCorners) {
			if (meetsCriteriaForSegmentSelection(coordinant)) {
				viableSegments.add(coordinant);
			}
		}
		maxPossibleInfo = viableSegments.size() * segmentCapacity;
		bitsToAddressBits = util.numberOfBitsToRepresent(maxPossibleInfo);
		return viableSegments;
	}
	
	public void conjugateMapsBelowThreshold(ArrayList<BitMap> segments) {
		conjugationMask = complexityDefinition.getConjugationMap(segmentWidth, segmentHeight);
		for (int i = 0; i < segments.size(); i++) {
			BitMap map = segments.get(i);
			if (!meetsCriteriaForPayloadEmbed(map)) {
				BitMap mapConj = getConjugate(map);
				segments.set(i, mapConj);
			}
		}
	}
	
	protected BitMap getConjugate(BitMap map) {
		return BitMap.xOr(map, conjugationMask);
	}

	public ArrayList<BitMap> extractViableSegments() {
		if (viableSegments == null) {
			viableSegments = getViableFrameCorners();
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

	public long getMaxPayloadBytes() {
		return maxPossibleInfo/8;
	}
}
