package nlp.similarity;

/**
 * Calculates the L1 distance based similarity between two vectors
 * 
 * @author Porter and Alex
 * 
 */
public class L1Calculator extends VectorSimilarityCalculator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nlp.similarity.VectorSimilarityCalculator#vectorSimilarityImpl(nlp.similarity
	 * .Vector, nlp.similarity.Vector)
	 */
	@Override
	protected double vectorSimilarityImpl(Vector first, Vector second) {
		double sum = 0;
		for (String key : first.keySet()) {
			sum += Math.abs(first.get(key) - second.get(key));
		}
		for (String key : second.keySet()) {
			if (!first.containsKey(key)) {
				sum += Math.abs(first.get(key) - second.get(key));
			}
		}
		return sum;
	}

}
