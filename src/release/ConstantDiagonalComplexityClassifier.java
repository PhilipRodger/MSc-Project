package release;

public class ConstantDiagonalComplexityClassifier extends SegmentManager{
	

		private double diagonalCutoff;
		
		// Does not change so can store it
		private int maxComplexity;
		
		public ConstantDiagonalComplexityClassifier(BitImageSet source, double diagonalCutoff, int segmentWidth, int segmentHeight) {
			super(source, segmentWidth, segmentHeight);
			this.diagonalCutoff = diagonalCutoff;
			complexityDefinition = new DiagonalComplexity();
			maxComplexity = complexityDefinition.maxComplexity(segmentWidth, segmentHeight);
			conjugationMask = complexityDefinition.getConjugationMap(segmentWidth, segmentHeight);
		}

		@Override
		protected boolean meetsCriteriaForSegmentSelection(Coordinant candidate) {
			BitMap toCheck = source.extractSegment(candidate, segmentWidth, segmentHeight);
			return meetsCriteriaForPayloadEmbed(toCheck);
		}
		
		@Override
		protected boolean meetsCriteriaForPayloadEmbed(BitMap toCheck) {
			int complexity = complexityDefinition.getComplexity(toCheck);
			double alphaComplexity = getDiagonalComplexity(complexity);
			if (alphaComplexity >= diagonalCutoff) {
				return true;
			} else {
				return false;
			}
		}
		
		public double getDiagonalComplexity(int complexity) {
			return complexity / (double) maxComplexity;
		}
}
