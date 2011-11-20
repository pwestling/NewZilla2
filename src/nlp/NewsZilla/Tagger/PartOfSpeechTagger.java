package nlp.NewsZilla.Tagger;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class PartOfSpeechTagger extends PCFGBuilder {

	HashMap<String, String> partsOfSpeech = new HashMap<String, String>();
	HashMap<String, Double> partsOfSpeechProbs = new HashMap<String, Double>();

	public PartOfSpeechTagger(String filename) {
		super(filename);
		System.out.println("starting POST");
		for (String lhs : ruleProbs.keySet()) {
			for (ArrayList<String> rhs : ruleProbs.get(lhs).keySet()) {
				if (isLexical.get(lhs).get(rhs)) {
					addRuleIfMoreProb(lhs, rhs.get(0));
				}
			}

		}
		System.out.println("Done training");

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

		try {
			PrintWriter pw = new PrintWriter(outputfile);
			Scanner sc = new Scanner(new File(filename));

			while (sc.hasNext()) {
				String word = sc.next();
				pw.print(this.tag(word) + " " + word);
				System.out.println(this.tag(word) + " " + word);

			}
			pw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
