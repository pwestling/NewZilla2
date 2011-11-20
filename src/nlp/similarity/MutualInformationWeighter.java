package nlp.similarity;

import java.util.HashMap;

public class MutualInformationWeighter extends WeightCalculator {

	/**
	 * We need the term frequencies to do our calculation, so we make a
	 * TermFrequencyWeighter
	 */
	private TermFrequencyWeighter tfw;

	/**
	 * Builds a new MutualInformationWeighter backed by the provided
	 * SimilarityMeasurer
	 * 
	 * @param sm
	 *            the SimilarityMeasurer to get data from
	 */
	public MutualInformationWeighter(SimilarityMeasurer sm) {
		super(sm);
		tfw = new TermFrequencyWeighter(sm);
	}

	@Override
	public Vector weight(String word) {
		Vector tf = tfw.weight(word);

		int totalWordCount = sm.getTotalWordCount();
		HashMap<String, Integer> wordCounts = sm.getWordCounts();

		// Once we have the term frequencies, replace each entry by the PMI
		// between the entry and the word
		for (String key : tf.keySet()) {
			double featureProb = (double) wordCounts.get(key) / (double) totalWordCount;
			double wordProb = (double) wordCounts.get(word) / (double) totalWordCount;
			double wordAndFeatureProb = tf.get(key) / (double) totalWordCount;

			tf.put(key, Math.log10(wordAndFeatureProb) - (Math.log10(featureProb) + Math.log10(wordProb)));
		}

		return tf;
	}

}
