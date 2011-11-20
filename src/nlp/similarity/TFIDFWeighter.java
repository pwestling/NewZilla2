package nlp.similarity;

import java.util.ArrayList;
import java.util.HashMap;

public class TFIDFWeighter extends WeightCalculator {

	/**
	 * We need the term frequencies to do our calculation, so we make a
	 * TermFrequencyWeighter
	 */
	private TermFrequencyWeighter tfw;

	/**
	 * Builds a new TFIDFWeighter backed by the provided SimilarityMeasurer
	 * 
	 * @param sm
	 *            the SimilarityMeasurer to get data from
	 */
	public TFIDFWeighter(SimilarityMeasurer sm) {
		super(sm);
		tfw = new TermFrequencyWeighter(sm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nlp.similarity.WeightCalculator#weight(java.lang.String)
	 */
	@Override
	public Vector weight(String word) {
		Vector tf = tfw.weight(word);
		// Once we have the term frequencies, multiply each entry by its IDF
		for (String key : tf.keySet()) {
			ArrayList<ArrayList<String>> documents = sm.getDocuments();
			HashMap<String, Integer> documentFrequency = sm.getDocumentFrequency();
			double idf = Math.log10((double) documents.size()) - Math.log10((double) documentFrequency.get(word));
			tf.put(key, tf.get(key) * idf);
		}
		return tf;
	}

}
