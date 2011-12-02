package nlp.NewsZilla.Tagger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class PartOfSpeechTagger extends PCFGBuilder {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HashMap<String, String> partsOfSpeech = new HashMap<String, String>();
	HashMap<String, Double> partsOfSpeechProbs = new HashMap<String, Double>();

	public static PartOfSpeechTagger makePOST(String filename) {
		File storedPOST = new File("post.serial");
		System.out.println("Serial File exists? " + storedPOST.exists());
		if (storedPOST.exists()) {
			try {
				FileInputStream fi = new FileInputStream(storedPOST);
				ObjectInputStream os = new ObjectInputStream(fi);
				PartOfSpeechTagger post = (PartOfSpeechTagger) os.readObject();
				return post;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			PartOfSpeechTagger post = new PartOfSpeechTagger(filename);

			System.out.println("Writing serial");
			try {
				FileOutputStream fo = new FileOutputStream(storedPOST);
				ObjectOutputStream os = new ObjectOutputStream(fo);
				os.writeObject(post);
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Wrote serial");

			return post;
		}
		return null;
	}

	private PartOfSpeechTagger(String filename) {
		super(filename);

		System.out.println("starting POST");
		for (String lhs : ruleProbs.keySet()) {
			for (ArrayList<String> rhs : ruleProbs.get(lhs).keySet()) {
				if (isLexical.get(lhs).get(rhs)) {
					addRuleIfMoreProb(lhs, rhs);
				}
			}

		}
		leftHandSideOccurrences = null;
		partsOfSpeechProbs = null;
		ruleOccurrences = null;
		ruleProbs = null;
		isLexical = null;
		ruleSet = null;

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
		String tag = partsOfSpeech.get(word.replaceAll("[\\.'\",\\?!]", ""));
		if (tag != null) {
			return tag;
		} else {
			return "null";
		}
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
					word = word.replaceAll("[^a-z-<>]", "");
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
