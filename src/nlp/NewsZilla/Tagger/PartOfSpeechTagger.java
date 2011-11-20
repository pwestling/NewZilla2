package nlp.NewsZilla.Tagger;

import java.util.ArrayList;
import java.util.HashMap;

public class PartOfSpeechTagger extends PCFGBuilder {

	HashMap<String, String> partsOfSpeech = new HashMap<String, String>();
	HashMap<String, Double> partsOfSpeechProbs = new HashMap<String, Double>();

	public PartOfSpeechTagger(String filename) {
		super(filename);

		for (String lhs : ruleProbs.keySet()) {
			for (ArrayList<String> rhs : ruleProbs.get(lhs).keySet()) {
				if (isLexical.get(lhs).get(rhs)) {
					addRuleIfMoreProb(lhs, rhs.get(0));
				}
			}

		}

	}

	private void addRuleIfMoreProb(String lhs, String rhs) {

		if (partsOfSpeech.get(rhs) == null) {
			partsOfSpeech.put(rhs, lhs);
			partsOfSpeechProbs.put(rhs, ruleProbs.get(lhs).get(rhs));
		} else if (ruleProbs.get(lhs).get(rhs) > partsOfSpeechProbs.get(rhs)) {
			partsOfSpeech.put(rhs, lhs);
			partsOfSpeechProbs.put(rhs, ruleProbs.get(lhs).get(rhs));
		}

	}

	public String tag(String word) {
		return partsOfSpeech.get(word);
	}

	public void tagFile(String filename, String outputfile) {

	}

}
