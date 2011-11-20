package nlp.NewsZilla.Tagger;

import java.util.ArrayList;
import java.util.HashMap;

public class PartOfSpeechTagger extends PCFGBuilder {

	HashMap<String, String> partsOfSpeech = new HashMap<String, String>();

	public PartOfSpeechTagger(String filename) {
		super(filename);

		for (String lhs : ruleProbs.keySet()) {
			for (ArrayList<String> rhs : ruleProbs.get(lhs).keySet()) {
				if (isLexical.get(lhs).get(rhs)) {

				}
			}

		}

	}

}
