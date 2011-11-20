package nlp.NewsZilla.Tagger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * A class for building PCFG's from a labeled treebank
 * 
 * Assignment 3
 * 
 * @author Alex and Porter
 * 
 */
public class PCFGBuilder {

	// Members:

	// counts the occurrences of a rule, hashed first by left hand side than
	// right hand side
	protected HashMap<String, HashMap<ArrayList<String>, Double>> ruleOccurrences = new HashMap<String, HashMap<ArrayList<String>, Double>>();
	// a count of lhs's
	protected HashMap<String, Double> leftHandSideOccurrences = new HashMap<String, Double>();
	// Boolean to indicate if a given rule is lexical, hashed the same way as
	// above
	protected HashMap<String, HashMap<ArrayList<String>, Boolean>> isLexical = new HashMap<String, HashMap<ArrayList<String>, Boolean>>();
	// Storage for rule probabilities
	protected HashMap<String, HashMap<ArrayList<String>, Double>> ruleProbs = new HashMap<String, HashMap<ArrayList<String>, Double>>();
	// List of rules - easier to iterate over, also stored as GrammarRule
	protected ArrayList<GrammarRule> ruleSet = new ArrayList<GrammarRule>();

	/**
	 * Creates a new PCFG from the trees in filename, one tree per line
	 * 
	 * @param filename
	 *            the file to parse
	 */
	public PCFGBuilder(String filename) {
		ArrayList<String> sentences = null;
		try {
			// get the lines
			sentences = parseSentences(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ArrayList<ParseTree> parseTrees = new ArrayList<ParseTree>();
		// parse each line with convenient ParseTree constructor.
		for (String s : sentences) {
			parseTrees.add(new ParseTree(s));
		}

		for (ParseTree p : parseTrees) {
			// count the rules which occur in our ParseTrees
			countRules(p);

		}

		// for each rule, we calculate its probability as rule occurrence / lhs
		// occurrence
		for (Entry<String, HashMap<ArrayList<String>, Double>> left : ruleOccurrences.entrySet()) {
			String lhs = left.getKey();
			Double denom = leftHandSideOccurrences.get(lhs);

			for (Entry<ArrayList<String>, Double> right : left.getValue().entrySet()) {
				ArrayList<String> rhs = right.getKey();
				Double numerator = right.getValue();
				GrammarRule rule = new GrammarRule(lhs, rhs, isLexical.get(lhs).get(rhs));
				Double prob = numerator / denom;
				rule.setWeight(prob);
				ruleProbs.get(lhs).put(rhs, prob);
				ruleSet.add(rule);
			}

		}

	}

	/**
	 * Outputs this PCFG's rules to a file
	 * 
	 * @param filename
	 *            The file to write to.
	 */
	public void toFile(String filename) {
		File output = new File(filename);
		try {
			PrintWriter writer = new PrintWriter(output);
			for (GrammarRule rule : ruleSet) {
				writer.println(rule.toString());
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	int rulecount = 0;

	/**
	 * Counts the occurrences of each rule in a parse tree, and marks them as
	 * lexical or not in isLexical
	 * 
	 * 
	 * @param p
	 *            The ParseTree to count rules for
	 */
	private void countRules(ParseTree p) {

		String lhs = p.getLabel();

		// If we haven't seen an lhs, add it to our various hash tables
		if (!leftHandSideOccurrences.containsKey(lhs)) {
			leftHandSideOccurrences.put(lhs, 0.0);
			ruleOccurrences.put(lhs, new HashMap<ArrayList<String>, Double>());
			isLexical.put(lhs, new HashMap<ArrayList<String>, Boolean>());
			ruleProbs.put(lhs, new HashMap<ArrayList<String>, Double>());
		}
		// Add one occurrence for lhs
		incrementMap(leftHandSideOccurrences, lhs);

		ArrayList<String> rhs = p.getChildrenLabels();
		// if we haven't seen this rhs, add it to the appropriate hash tables
		if (!ruleOccurrences.get(lhs).containsKey(rhs)) {
			ruleOccurrences.get(lhs).put(rhs, 0.0);
			isLexical.get(lhs).put(rhs, false);
			ruleProbs.get(lhs).put(rhs, 0.0);
		}
		// add one more occurrence for rule as a whole
		incrementDoubleMap(ruleOccurrences, lhs, rhs);
		rulecount++;
		Boolean lexical = true;
		// This rule is lexical if its children are terminal (handles slightly
		// broader case than required where lexical rule goes to 2 or more
		// words)
		for (ParseTree child : p.getChildren()) {
			if (!child.isTerminal()) {
				countRules(child);
				lexical = false;
			}
		}
		// set lexicality of this rule
		isLexical.get(lhs).put(rhs, lexical);

	}

	/**
	 * Increment the value by one in a hash of String to double
	 * 
	 * 
	 * @param map
	 *            the HashMap to operate on
	 * @param key
	 *            the key whose value is to be incremented
	 */
	private void incrementMap(HashMap<String, Double> map, String key) {
		map.put(key, map.get(key) + 1.0);
	}

	/**
	 * Increment the value by one in a hash of String to hash of String to
	 * double
	 * 
	 * @param map
	 *            the HashMap to operate on
	 * @param firstKey
	 *            the key whose map is to be operated on
	 * @param secondKey
	 *            the key whose value is to be incremented in the sub-HashMap
	 */
	private void incrementDoubleMap(HashMap<String, HashMap<ArrayList<String>, Double>> map, String firstKey,
			ArrayList<String> secondKey) {
		double newvalue = map.get(firstKey).get(secondKey) + 1.0;
		map.get(firstKey).put(secondKey, newvalue);
	}

	/**
	 * Takes a file and returns its lines in an ArrayList
	 * 
	 * 
	 * @param filename
	 *            the file to parse
	 * @return an ArrayList<String> where each element is one line of filename
	 * @throws IOException
	 */
	protected ArrayList<String> parseSentences(String filename) throws IOException {

		File input = new File(filename);

		BufferedReader inputtext = new BufferedReader(new FileReader(input));

		ArrayList<String> sentences = new ArrayList<String>();

		while (inputtext.ready()) {
			sentences.add(inputtext.readLine());
		}

		return sentences;

	}

	/**
	 * Generates a random sentence based on this PCFG, and cleans up whitespace
	 * in the sentence.
	 * 
	 * @return the cleaned random sentence
	 */
	public String generateSentence() {
		String sent = generateSentence("S");
		sent = sent.replaceAll("\\s+", " ");
		sent = sent.replaceAll("\\s\\.", ".");
		return sent;
	}

	/**
	 * generates a random subsentence, starting with the symbol lhs.
	 * 
	 * @param lhs
	 *            the symbol to start from
	 * @return a random subsentence based on the provided lhs
	 */
	private String generateSentence(String lhs) {

		// get the rules for the provided lhs
		HashMap<ArrayList<String>, Double> rules = ruleProbs.get(lhs);
		// We select a rule by their probabilities by generating a random float
		// in [0,1] and
		// then iterating over the rules and their probabilities. We choose a
		// rule when
		// the sum of the probabilities of this rule and previous rules is >= to
		// the random float.
		Double probSum = 0.0;
		Double r = Math.random();
		for (Entry<ArrayList<String>, Double> e : rules.entrySet()) {
			ArrayList<String> rhs = e.getKey();
			probSum += e.getValue();
			if (r <= probSum && !(lhs == rhs.get(0) && rhs.size() == 1)) {

				if (isLexical.get(lhs).get(rhs)) {
					// if lexical, we just return the word.
					return rhs.get(0);
				} else {

					StringBuilder sb = new StringBuilder();
					for (String rhsElement : rhs) {
						// if not lexical, we recurse on the new symbols we got
						// from the rhs of the selected rule
						sb.append(generateSentence(rhsElement));
						sb.append(" ");
					}
					return sb.toString();
				}
			}

		}
		return null;

	}

}
