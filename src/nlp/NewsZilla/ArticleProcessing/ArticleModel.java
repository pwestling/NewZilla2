package nlp.NewsZilla.ArticleProcessing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import nlp.NewsZilla.Subject.SubjectFinder;
import nlp.NewsZilla.Tagger.PartOfSpeechTagger;
import nlp.NewsZilla.VerbGram.VerbParser;
import nlp.similarity.CosineCalculator;
import nlp.similarity.Vector;
import nlp.similarity.VectorSimilarityCalculator;

public class ArticleModel {

	ArrayList<String> headlines = new ArrayList<String>();
	ArrayList<ArrayList<String>> articles = new ArrayList<ArrayList<String>>();
	ArrayList<String> wholeArticles = new ArrayList<String>();
	HashMap<String, ArrayList<String>> sentencesByVerb = new HashMap<String, ArrayList<String>>();
	GramTree gramRoot = new GramTree("<ROOT>");
	PartOfSpeechTagger post;
	int gramDepth;
	SubjectFinder sf = new SubjectFinder();
	Random rand = new Random();

	public ArticleModel(String filename, int gramDepth) {
		this.gramDepth = gramDepth;
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
		// debugPrintTree(gramRoot, "");
		System.out.println("Printed tree");
		pw.close();
		System.out.println("Built tree");

	}

	public String makeArticle(String subject, String verb) {

		if (!gramRoot.hasChild(verb)) {
			System.out.println("Verb " + verb + " not in data");
			return "";
		}

		ArrayList<String> verbs = new ArrayList<String>();
		for (int i = 0; i < gramDepth - 1; i++) {
			verbs.add("<START>");
		}

		String nextVerb = verb;
		while (!nextVerb.equals("<END>") && (verbs.size() - gramDepth) < 7) {
			verbs.add(nextVerb);

			List<String> gram = verbs.subList(verbs.size() - gramDepth, verbs.size());
			GramTree curNode = gramRoot;
			for (int i = 0; i < gram.size(); i++) {
				curNode = curNode.getChild(gram.get(i));
				if (curNode == null) {
					System.out.println(gram);
					return "";
				}
			}
			ArrayList<GramTree> children = curNode.getChildren();
			Double r = Math.random();
			Double probSum = children.get(0).getProb();
			String thisVerb = children.get(0).getWord();

			for (int i = 1; i < children.size() && r >= probSum; i++) {
				thisVerb = children.get(i).getWord();
				probSum += children.get(i).getProb();
			}
			nextVerb = thisVerb;

		}

		StringBuilder sb = new StringBuilder();
		ArrayList<String> choosenSentences = new ArrayList<String>();

		Double addSubjects = 1.0;
		for (String currentVerb : verbs) {
			if (!currentVerb.equals("<START>")) {
				ArrayList<String> sentences = sentencesByVerb.get(currentVerb);
				String sentence = getNextSentence(choosenSentences, sentences);
				System.out.println("Sentence picked was: " + sentence);
				if (!sentence.contains("<SUBJECT>")) {

					if (rand.nextDouble() < addSubjects) {

						String thisSubject = sf.getSubject(sentence, post);
						sentence = removeSubjectClusters(thisSubject, sentence.split("\\s+"));
						addSubjects = -1.0;
					}

				}
				choosenSentences.add(sentence);
			}
		}
		for (String s : choosenSentences) {
			sb.append(s + "\n");
		}

		return sb.toString().replaceAll("<SUBJECT>", subject);
	}

	private String getNextSentence(ArrayList<String> choosenSentences, ArrayList<String> sentences) {
		if (choosenSentences.size() >= 1) {
			String previousSentence = choosenSentences.get(choosenSentences.size() - 1);

			Vector previousSentenceVec = Vector.makeVector(previousSentence);

			Double bestSim = 9999.9;
			String bestSentence = "This sentence is AWESOME!!!!!!";
			for (String sentence : sentences) {

				Vector sentenceVec = Vector.makeVector(sentence);

				VectorSimilarityCalculator vSim = new CosineCalculator();

				Double sim = vSim.vectorSimilarity(previousSentenceVec, sentenceVec);
				if (sim < bestSim) {
					bestSim = sim;
					bestSentence = sentence;
				}

			}

			return bestSentence;
		} else {
			return sentences.get(rand.nextInt(sentences.size()));
		}

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
		for (ArrayList<String> article : articles) {
			for (String sentence : article) {
				if (sentence.contains("<SUBJECT>")) {
					debugPrint(sentence);
					debugPrint("\n");
				}
			}

		}

	}

	private void stripSubjects(String article, String headline, int index) {
		// System.out.println("Stripping subject " + index);

		// System.out.println("Tagged headline");

		String subject = sf.getSubject(headline);
		// System.out.println("got subject");
		String[] articleSplit = article.split("\\s+");

		wholeArticles.set(index, removeSubjectClusters(subject, articleSplit));
		String[] headlineSplit = headline.split("\\s+");
		headlines.set(index, removeSubjectClusters(subject, headlineSplit));

	}

	private ArrayList<String> breakIntoSentences(String article) {
		return new ArrayList<String>(Arrays.asList(article
				.split("((?<=\\?)|(?<=\\!)|(?<=([^A-Z]\\. ))|(?<=([^A-Z]\\.''))|(?<=;))")));

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
