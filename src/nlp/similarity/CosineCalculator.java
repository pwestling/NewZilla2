package nlp.similarity;

/**
 * Calculates the cosine based similarity between two vectors
 * 
 * @author Porter and Alex
 * 
 */
public class CosineCalculator extends VectorSimilarityCalculator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nlp.similarity.VectorSimilarityCalculator#vectorSimilarityImpl(nlp.similarity
	 * .Vector, nlp.similarity.Vector)
	 */
	@Override
	protected double vectorSimilarityImpl(Vector first, Vector second) {
		double dotProduct = 0;
		for (String key : first.keySet()) {
			dotProduct += first.get(key) * second.get(key);
		}
		double magFirst = magnitude(first);
		double magSecond = magnitude(second);
		double d = dotProduct / (magFirst * magSecond);

		// We get NaN if a document is only 1 word, so we handle this here
		return Double.isNaN(d) ? 0 : d;
	}

}
