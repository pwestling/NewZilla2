package nlp.similarity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A base class for classes which build weighted co-occurrence vectors
 * 
 * @author Porter and Alex
 * 
 */
public abstract class WeightCalculator {

	/**
	 * The SimilarityMeasurer that provides the data for our weights
	 */
	protected SimilarityMeasurer sm;

	/**
	 * Build a new WeightCalculator
	 * 
	 * @param sm
	 *            The SimilarityMeasurer to use for data
	 */
	public WeightCalculator(SimilarityMeasurer sm) {
		this.sm = sm;
	}

	/**
	 * Returns the context of the word at the provided index in the provided
	 * document
	 * 
	 * 
	 * @param document
	 *            The document to get context from
	 * @param index
	 *            The index to get the context of
	 * @return A new ArrayList<String> containing the index and the 2 words to
	 *         either side, minus one occurrence of the word at the index
	 */
	protected ArrayList<String> getContext(ArrayList<String> document, int index) {
		// We always use 2, so no need for parameter
		int width = 2;
		// Make a new list from the 2-to-either-side sublist. The mins and maxs
		// ensure we don't go out of bounds
		ArrayList<String> context = new ArrayList<String>(document.subList(Math.max(0, index - width),
				Math.min(document.size(), index + width + 1)));

		// remove one copy of the word at index, as the co-occurrences for a
		// word should not count the word itself. We use remove because order
		// doesn't really matter, we just need to reduce the count of the word
		// by 1
		context.remove(document.get(index));

		return context;

	}

	/**
	 * Return the weighted co-occurrence vector for provided word
	 * 
	 * @param word
	 *            The word to consider
	 * @return The weighted vector
	 */
	public abstract Vector weight(String word);

	/**
	 * Helper method to ensure the existence of and increment the value in a
	 * double map
	 * 
	 * @param map
	 *            the over all map
	 * @param key1
	 *            the first key
	 * @param key2
	 *            the second key
	 */
	protected void incrementDoubleMap(HashMap<String, HashMap<String, Double>> map, String key1, String key2) {
		if (!map.containsKey(key1)) {
			map.put(key1, new HashMap<String, Double>());
		}
		HashMap<String, Double> subMap = map.get(key1);
		if (!subMap.containsKey(key2)) {
			subMap.put(key2, 0.0);
		}

		subMap.put(key2, subMap.get(key2) + 1);

	}
}
