package nlp.similarity;

/**
 * Calculates the Euclidean distance based similarity between two vectors
 * 
 * @author Porter and Alex
 * 
 */
public class EuclidianCalculator extends VectorSimilarityCalculator {

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
			sum += Math.pow(first.get(key) - second.get(key), 2);
		}
		for (String key : second.keySet()) {
			if (!first.containsKey(key)) {
				sum += Math.pow(first.get(key) - second.get(key), 2);
			}
		}
		return Math.sqrt(sum);
	}

}
