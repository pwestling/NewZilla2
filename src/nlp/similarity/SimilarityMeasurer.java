package nlp.similarity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * A class for measuring the similarity of a given word to the words in a
 * corpus.
 * 
 * Assignment 5
 * 
 * @author Porter and Alex
 * 
 */
public class SimilarityMeasurer {

	/**
	 * The documents in the corpus, stored by document then by word
	 */
	ArrayList<ArrayList<String>> documents;
	/**
	 * Word counts for each unique word
	 */
	HashMap<String, Integer> wordCounts = new HashMap<String, Integer>();
	/**
	 * The number of documents that each word occurs in
	 */
	HashMap<String, Integer> documentFrequency = new HashMap<String, Integer>();
	/**
	 * The total (non-unique) words in the corpus
	 */
	int totalWordCount = 0;

	/**
	 * Construct a new SimilarityMeasurer based on the provided corpus and
	 * stoplist
	 * 
	 * 
	 * @param trainingFile
	 *            The path of the corpus, one "document" per line
	 * @param stopFile
	 *            The path of the stoplist, one word per line
	 */
	public SimilarityMeasurer(String trainingFile, String stopFile) {
		// read in data
		ArrayList<String> trainingLines = readLines(trainingFile);
		ArrayList<String> stopWords = readLines(stopFile);

		documents = new ArrayList<ArrayList<String>>();

		// Go through each "document" and break into words based on whitespace,
		// and lowercase the words
		// Then throw out stop words and words with non alphabetic characters
		for (String sentence : trainingLines) {
			ArrayList<String> words = new ArrayList<String>(Arrays.asList(sentence.split("\\s+")));
			int index = 0;
			while (index < words.size()) {
				words.set(index, words.get(index).toLowerCase());
				String word = words.get(index);
				if (stopWords.contains(word) || !word.matches("[a-z]+")) {
					words.remove(index);
				} else {
					index++;
				}

			}
			// Keeps track of whether we've already counted this document and
			// word for documentFrequency
			HashMap<String, Boolean> addedForThisDocument = new HashMap<String, Boolean>();
			// Add to wordCounts and documentFrequency as appropriate
			for (String word : words) {
				incrementMap(wordCounts, word);
				if (!addedForThisDocument.containsKey(word)) {
					incrementMap(documentFrequency, word);
					addedForThisDocument.put(word, true);
				}

			}
			totalWordCount += words.size();
			// All processing done, store the processed document
			documents.add(words);

		}
		// Print out our stats
		System.out.println(wordCounts.size() + " unique words");
		System.out.println(totalWordCount + " word occurences");
		System.out.println(documents.size() + " sentences/lines/documents");
		System.out.println();

	}

	/**
	 * A class for computing the weighted vector.
	 * 
	 * We pick a subclass of the abstract class based on how we want to compute
	 * the weighted vectors
	 */
	private WeightCalculator weighter;
	/**
	 * A class for computing the similarity of two vectors
	 * 
	 * We pick a subclass of the abstract class based on how we want to compute
	 * the similarities
	 */
	private VectorSimilarityCalculator measurer;

	/**
	 * A comparator to make sure that the most similar words appear first in our
	 * priority queue for easy retrieval
	 */
	private Comparator<SimilarityPair> pairComparer;

	/**
	 * We store this to keep from recalculating later
	 */
	Vector featureVec = null;

	/**
	 * Print out the ten most similar words in the corpus to the provided word,
	 * using the provided weighting and vector similarity measure
	 * 
	 * 
	 * @param feature
	 *            The word to compare to the corpus
	 * @param weighting
	 *            The weighting scheme in string form. Possibilities are "PMI"
	 *            "TF" and "TFIDF"
	 * @param measure
	 *            The vector similarity measure to employ. Possibilities are
	 *            "L1" "EUCLIDEAN" and "COSINE"
	 */
	public void printSimilarities(String feature, String weighting, String measure) {

		// Create the appropriate WeightCalculator
		if (weighting.equals("PMI")) {
			weighter = new MutualInformationWeighter(this);
		} else if (weighting.equals("TFIDF")) {
			weighter = new TFIDFWeighter(this);
		} else if (weighting.equals("TF")) {
			weighter = new TermFrequencyWeighter(this);
		}
		// create the appropriate VectorSimilarityCalculator and Comparator
		if (measure.equals("L1")) {
			measurer = new L1Calculator();
			pairComparer = new SimilarityPair.EuclideanOrL1Comparator();
		} else if (measure.equals("EUCLIDEAN")) {
			measurer = new EuclidianCalculator();
			pairComparer = new SimilarityPair.EuclideanOrL1Comparator();
		} else if (measure.equals("COSINE")) {
			measurer = new CosineCalculator();
			pairComparer = new SimilarityPair.CosineComparator();
		}

		// We store and simultaneously sort the calculated similarities in this
		PriorityQueue<SimilarityPair> similarities = new PriorityQueue<SimilarityPair>(wordCounts.size(), pairComparer);
		// reset featureVec to make sure it gets recalculated for the new word
		featureVec = null;

		// compare similarity of provided word to every other word in the corpus
		for (String word : wordCounts.keySet()) {

			// ignore words with few occurrences and the word itself (a trivial
			// similarity, the vectors will always be identical)
			if (wordCounts.get(word) > 2 && !word.equals(feature)) {

				similarities.add(new SimilarityPair(word, similarity(word, feature)));

			}
		}
		System.out.println("SIM: " + feature + " " + weighting + " " + measure);

		// Print our top 10 similarities
		for (int i = 0; i < 10; i++) {
			SimilarityPair simPair = similarities.remove();

			System.out.println(simPair.getWord() + "\t" + simPair.getSimilarity());
		}

	}

	/**
	 * Compute the similarity between two supplied words
	 * 
	 * @param word
	 *            The first word
	 * @param feature
	 *            The second word
	 * @return The similarity between the two words
	 */
	private double similarity(String word, String feature) {
		// Get the co-occurence vectors
		Vector wordVec = weighter.weight(word);
		// feature will be the same for each run of printSimilarities, so we
		// only calculate it once
		if (featureVec == null)
			featureVec = weighter.weight(feature);
		// get sim between vectors
		double result = measurer.vectorSimilarity(wordVec, featureVec);
		return result;
	}

	/**
	 * Helper function to ensure the existence of and then increment the value
	 * in a map
	 * 
	 * @param map
	 *            The map to increment
	 * @param key
	 *            The key whose value should be incremented
	 */
	private void incrementMap(HashMap<String, Integer> map, String key) {
		if (!map.containsKey(key)) {
			map.put(key, 0);
		}
		map.put(key, map.get(key) + 1);

	}

	/**
	 * Puts each line of the provided file into an entry of an ArrayList
	 * 
	 * @param filename
	 *            The file to read
	 * @return An ArrayList<String> with each line of filename as one entry
	 */
	public static ArrayList<String> readLines(String filename) {
		try {
			File input = new File(filename);

			BufferedReader inputtext = new BufferedReader(new FileReader(input));

			ArrayList<String> sentences = new ArrayList<String>();

			while (inputtext.ready()) {
				sentences.add(inputtext.readLine());
			}
			return sentences;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Getter for documents
	 * 
	 * @return this.documents
	 */
	public ArrayList<ArrayList<String>> getDocuments() {
		return documents;
	}

	/**
	 * Getter for wordCounts
	 * 
	 * @return this.wordCounts
	 */
	public HashMap<String, Integer> getWordCounts() {
		return wordCounts;
	}

	/**
	 * Getter for documentFrequency
	 * 
	 * @return this.documentFrequency
	 */
	public HashMap<String, Integer> getDocumentFrequency() {
		return documentFrequency;
	}

	/**
	 * Getter for totalWordCount
	 * 
	 * @return this.totalWordCount
	 */
	public int getTotalWordCount() {
		return totalWordCount;
	}

}
