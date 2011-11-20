package nlp.similarity;

import java.util.Comparator;

public class SimilarityPair {

	private String word;
	private Double similarity;

	public SimilarityPair(String word, double similarity) {
		super();
		this.word = word;
		this.similarity = similarity;
	}

	public String getWord() {
		return word;
	}

	public Double getSimilarity() {
		return similarity;
	}

	/**
	 * compares SimilarityPairs which are based on Cosine
	 * 
	 * When sorted the cosine closest to 1 will be first
	 * 
	 * @author Porter and Alex
	 * 
	 */
	static class CosineComparator implements Comparator<SimilarityPair> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(SimilarityPair arg0, SimilarityPair arg1) {
			return -arg0.getSimilarity().compareTo(arg1.getSimilarity());
		}

	}

	/**
	 * compares SimilarityPairs which are based on Euclidean or L1 distance
	 * 
	 * When sorted the least distance will be first
	 * 
	 * @author Porter and Alex
	 * 
	 */
	static class EuclideanOrL1Comparator implements Comparator<SimilarityPair> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(SimilarityPair arg0, SimilarityPair arg1) {
			return arg0.getSimilarity().compareTo(arg1.getSimilarity());
		}

	}

}
