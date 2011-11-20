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
					addRuleIfMoreProb(lhs, rhs);
				}
			}

		}
		System.out.println("Done training");

	}

	private void addRuleIfMoreProb(String lhs, ArrayList<String> rhsAL) {
		String rhs = rhsAL.get(0);

		if (partsOfSpeech.get(rhs) == null) {
			partsOfSpeech.put(rhs, lhs);
		}
		if (partsOfSpeechProbs.get(rhs) == null) {
			partsOfSpeechProbs.put(rhs, ruleProbs.get(lhs).get(rhsAL));
		}
		if (ruleProbs.get(lhs).get(rhsAL) > partsOfSpeechProbs.get(rhs)) {
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

			while (sc.hasNextLine()) {
				Scanner subsc = new Scanner(sc.nextLine());
				while (subsc.hasNext()) {
					String word = subsc.next();
					word = word.toLowerCase();
					word.replaceAll("[^a-z]", "");
					pw.print(" (" + this.tag(word) + " " + word + ")");
				}
				pw.print("\n");

			}
			pw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
