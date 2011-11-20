package nlp.similarity;

import java.util.ArrayList;

/**
 * A class to interface SimilarityMeasurer with the command line
 * 
 * @author Porter and Alex
 * 
 */
public class Main {

	/**
	 * Build a SimilarityMeasurer based on the provided corpus and stoplist,
	 * then find the most similar words for each word in the input file using
	 * the provided methods
	 * 
	 * @param args
	 *            should be in the form <stoplist> <sentences> <input_file>
	 * 
	 *            stoplist has the stop words, one per line
	 * 
	 *            sentences has the corpus, one document per line
	 * 
	 *            input file has the words to find similar words for, in the
	 *            form
	 * 
	 *            <word> <weighting> <sim_measure>
	 * 
	 *            one per line
	 * 
	 * 
	 */
	public static void main(String[] args) {

		// build the SimMeasurer
		SimilarityMeasurer sm = new SimilarityMeasurer(args[1], args[0]);
		ArrayList<String> readLines = SimilarityMeasurer.readLines(args[2]);

		// print similarities for each line in input_file
		for (String line : readLines) {
			String[] parts = line.split("\\s+");
			sm.printSimilarities(parts[0], parts[1], parts[2]);

		}

	}

}
