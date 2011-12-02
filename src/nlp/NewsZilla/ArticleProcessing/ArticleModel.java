package nlp.NewsZilla.ArticleProcessing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import nlp.NewsZilla.Subject.SubjectFinder;
import nlp.NewsZilla.Tagger.PartOfSpeechTagger;
import nlp.NewsZilla.VerbGram.VerbParser;

public class ArticleModel {

	ArrayList<String> headlines = new ArrayList<String>();
	ArrayList<ArrayList<String>> articles = new ArrayList<ArrayList<String>>();
	ArrayList<String> wholeArticles = new ArrayList<String>();
	HashMap<String, ArrayList<String>> sentencesByVerb = new HashMap<String, ArrayList<String>>();
	GramTree gramRoot = new GramTree("<ROOT>");
	PartOfSpeechTagger post;

	public ArticleModel(String filename, int gramDepth) {
		System.out.println("Building POST");
		post = PartOfSpeechTagger.makePOST("data/simple.parsed");
		System.out.println("Built POST");
		try {
			breakUpArticles(filename);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("Building tree");

		VerbParser verbParser = new VerbParser(post, sentencesByVerb, gramDepth);

		for (ArrayList<String> article : articles) {
			ArrayList<String> verbs = verbParser.getArticleSkeleton(article);
			for (int i = gramDepth; i < verbs.size(); i++) {

				augementTree(verbs.subList(i - gramDepth, i + 1));
				// This should get us all gram counts. Next thing to do is
				// calculate prob.
			}
		}
		System.out.println("Computing simple probabilities");
		computeProbabilities(gramRoot, 1);
		System.out.println("Probabilities computed");
		System.out.println("Printing tree");
		debugPrintTree(gramRoot, "");
		System.out.println("Printed tree");
		pw.close();
		System.out.println("Built tree");

	}

	private void computeProbabilities(GramTree root, double parentCount) {
		root.setProb(root.getCount() / parentCount);
		for (GramTree child : root.getChildren()) {
			computeProbabilities(child, root.getCount());
		}
	}

	private void debugPrintTree(GramTree root, String tabs) {
		debugPrint(tabs + root.getWord());

		debugPrint("\t" + root.getProb());

		debugPrint("\n");
		for (GramTree child : root.getChildren()) {
			debugPrintTree(child, tabs + "\t");
		}
	}

	PrintWriter pw = null;

	public void debugPrint(String s) {
		if (pw == null) {
			try {
				pw = new PrintWriter("debug.txt");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		pw.print(s);

	}

	private void augementTree(List<String> verbs) {
		GramTree root = gramRoot;
		for (String verb : verbs) {
			if (!root.hasChild(verb)) {
				root.addChild(new GramTree(verb));
			}
			root = root.getChild(verb);
			root.incrementCount();
		}
	}

	private void addToHash(String sentence, String verb) {
		if (!sentencesByVerb.containsKey(verb)) {
			sentencesByVerb.put(verb, new ArrayList<String>());
		}
		sentencesByVerb.get(verb).add(sentence);

	}

	private void breakUpArticles(String filename) throws FileNotFoundException {

		Scanner sc = new Scanner(new File(filename));

		StringBuilder sb = new StringBuilder();
		String line = sc.nextLine();
		while (sc.hasNextLine()) {
			line = sc.nextLine();
			while (!line.equals("<\\DOC>")) {
				if (!line.equals("<DOC>")) {
					if (line.contains("<TITLE>")) {
						// Pretending titles are all on a single line

						headlines.add(line.replaceAll("<.?TITLE>", ""));
					} else {

						sb.append(line.replaceAll("<P>", ""));

					}

				}
				line = sc.nextLine();
			}
			wholeArticles.add(sb.toString());
			sb = new StringBuilder();
			// System.out.println("Processed article " + wholeArticles.size());

		}

		for (int i = 0; i < wholeArticles.size(); i++) {
			stripSubjects(wholeArticles.get(i), headlines.get(i), i);
			// debugPrint(headlines.get(i));
			// debugPrint(wholeArticles.get(i));
		}

		for (String article : wholeArticles) {
			articles.add(breakIntoSentences(article));
		}
		// for (ArrayList<String> article : articles) {
		// for (String sentence : article) {
		// debugPrint(sentence);
		// }
		//
		// }

	}

	private void stripSubjects(String article, String headline, int index) {
		// System.out.println("Stripping subject " + index);

		ArrayList<String> headlineWords = new ArrayList<String>();
		ArrayList<String> headlineTags = new ArrayList<String>();
		for (String word : headline.split("\\s+")) {
			headlineWords.add(word);
			headlineTags.add(post.tag(word));
		}
		// System.out.println("Tagged headline");
		SubjectFinder sf = new SubjectFinder();
		String subject = sf.getSubject(headlineWords, headlineTags);
		// System.out.println("got subject");
		String[] articleSplit = article.split("\\s+");

		wholeArticles.set(index, removeSubjectClusters(subject, articleSplit));
		String[] headlineSplit = headline.split("\\s+");
		headlines.set(index, removeSubjectClusters(subject, headlineSplit));

	}

	private ArrayList<String> breakIntoSentences(String article) {
		return new ArrayList<String>(Arrays.asList(article.split("(?<=\\?)|(?<=\\!)|(?<=(\\. ))|(?<=(\\.''))|(?<=;)")));

	}

	private String removeSubjectClusters(String subject, String[] split) {
		for (int i = 0; i < split.length; i++) {
			if (split[i].equals(subject)) {
				split[i] = "<SUBJECT>";

				for (int j = -1; j <= 1; j++) {
					if (j != 0) {
						if (i + j < split.length && i + j >= 0 && post.tag(split[i + j]).matches("(null)|(N.*)")
								&& !split[i + j].endsWith("''")) {
							split[i + j] = "";

						}
					}
				}

			}

		}
		StringBuilder sb = new StringBuilder();
		for (String s : split) {
			if (s.length() >= 1)
				sb.append(s + " ");
		}
		return sb.toString();
	}
}
