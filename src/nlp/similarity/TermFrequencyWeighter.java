package nlp.similarity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * Builds co-occurrence vectors based on term frequency
 * 
 * @author Porter and Alex
 * 
 */
public class TermFrequencyWeighter extends WeightCalculator {

	/**
	 * Builds a new TermFrequencyWeighter backed by the provided
	 * SimilarityMeasurer
	 * 
	 * @param sm
	 *            the SimilarityMeasurer to get data from
	 */
	public TermFrequencyWeighter(SimilarityMeasurer sm) {
		super(sm);
	}

	/**
	 * We store the co-occurences so that they only get calculated once
	 */
	HashMap<String, HashMap<String, Double>> coOccurances;

	/*
	 * (non-Javadoc)
	 * 
	 * @see nlp.similarity.WeightCalculator#weight(java.lang.String)
	 */
	@Override
	public Vector weight(String word) {
		// if the co-occurrences have not be computed, compute them
		if (coOccurances == null) {

			coOccurances = new HashMap<String, HashMap<String, Double>>();
			ArrayList<ArrayList<String>> documents = sm.getDocuments();

			// For each word in the corpus, add to its co-occurrences based on
			// the words in its 2-to-either-side context. This way we calculate
			// all co-occurrence
			// vectors in a single pass
			for (ArrayList<String> document : documents) {
				for (int i = 0; i < document.size(); i++) {
					String thisFeature = document.get(i);
					if (!coOccurances.containsKey(thisFeature))
						coOccurances.put(thisFeature, new HashMap<String, Double>());
					ArrayList<String> context = getContext(document, i);
					for (String contextWord : context) {
						incrementDoubleMap(coOccurances, thisFeature, contextWord);
					}
				}
			}

		}

		// make a vector out of the counts we already computed
		return new Vector(coOccurances.get(word));

	}
}
