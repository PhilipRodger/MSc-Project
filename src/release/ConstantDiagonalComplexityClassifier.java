package release;

import java.util.HashMap;

public class ConstantDiagonalComplexityClassifier extends SegmentManager{

		private double diagonalCutoff;
		
		public ConstantDiagonalComplexityClassifier(double diagonalCutoff) {
			super();
			this.diagonalCutoff = diagonalCutoff;
			complexityDefinition = new DiagonalComplexity();
		}

		public ConstantDiagonalComplexityClassifier(HashMap<String, String> params) {
			super();
			if (params.containsKey("threshold")) {
				this.diagonalCutoff = Double.parseDouble(params.get("threshold"));
			}
			complexityDefinition = new DiagonalComplexity();
			
		}

		@Override
		protected boolean meetsCriteriaForSegmentSelection(Coordinant candidate) {
			BitMap toCheck = source.extractSegment(candidate, segmentWidth, segmentHeight);
			return meetsCriteriaForPayloadEmbed(toCheck);
		}
		
		@Override
		protected boolean meetsCriteriaForPayloadEmbed(BitMap toCheck) {
			int complexity = complexityDefinition.getComplexity(toCheck);
			double diagonalComplexity = getNormalisedComplexity(complexity);
			if (diagonalComplexity >= diagonalCutoff) {
				return true;
			} else {
				return false;
			}
		}
				
		@Override
		public String toString() {
			return " Algorithim=BPCS_Diagonal DiagonalCutoff=" + diagonalCutoff + " SegmentWidth=" + segmentWidth
					+ " SegmentHeight=" + segmentHeight;
		}
}
