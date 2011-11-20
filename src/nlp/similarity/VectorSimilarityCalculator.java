package nlp.similarity;

/**
 * 
 * A base class for classes which calculate the similarity between two vectors
 * 
 * 
 * @author Porter and Alex
 * 
 */
public abstract class VectorSimilarityCalculator {

	/**
	 * Compute the similarity between two vectors after normalizing both by
	 * their Euclidean length
	 * 
	 * @param first
	 *            The first vector
	 * @param second
	 *            The second vector
	 * @return The similarity between the two vectors
	 */
	public double vectorSimilarity(Vector first, Vector second) {
		return vectorSimilarityImpl(normalize(first), normalize(second));
	}

	/**
	 * A helper method for subclasses to override without removing the
	 * normalization part of vectorSimilarity
	 * 
	 * @param first
	 *            The first vector
	 * @param second
	 *            The second vector
	 * @return the similarity between the two vectors
	 */
	protected abstract double vectorSimilarityImpl(Vector first, Vector second);

	/**
	 * Helper function to calculate the magnitude of a vector
	 * 
	 * @param v
	 *            The vector
	 * @return The magnitude of the vector
	 */
	public static double magnitude(Vector v) {
		// Profiler.start("mag");
		double mag = 0;
		for (String key : v.keySet()) {
			mag += Math.pow(v.get(key), 2);
		}
		// Profiler.stop("mag");
		return Math.sqrt(mag);
	}

	/**
	 * Returns a Euclidean-length normalized copy of the provided vector
	 * 
	 * @param v
	 *            the vector to normalize
	 * @return the normalized vector
	 */
	public static Vector normalize(Vector v) {
		double magnitude = magnitude(v);
		Vector newVec = new Vector(v);
		for (String key : v.keySet()) {
			newVec.put(key, v.get(key) / magnitude);
		}
		return newVec;

	}

}
