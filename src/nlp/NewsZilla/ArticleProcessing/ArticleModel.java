package nlp.NewsZilla.ArticleProcessing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ArticleModel {

	ArrayList<String> headlines = new ArrayList<String>();
	ArrayList<ArrayList<String>> articles = new ArrayList<ArrayList<String>>();

	public ArticleModel(String filename) {

	}

	private void breakUpArticles(String filename) throws FileNotFoundException {

		Scanner sc = new Scanner(new File(filename));
		ArrayList<String> wholeArticles = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		String line = sc.nextLine();
		while (sc.hasNextLine()) {
			while (!line.equals("<\\DOC>")) {
				if (!line.equals("<DOC>")) {
					if (line.contains("<TITLE>")) {
						// Pretending titles are all on a single line

						headlines.add(line.replaceAll("<.?TITLE>", ""));
					} else {

						sb.append(line);

					}

				}
			}
			wholeArticles.add(sb.toString());
			sb = new StringBuilder();
		}

		for (int i = 0; i < wholeArticles.size(); i++) {
			stripSubjects(wholeArticles.get(i), headlines.get(i));
		}

		for (String article : wholeArticles) {
			articles.add(breakIntoSentences(article));
		}

	}

	private void stripSubjects(String string, String string2) {

	}

	private ArrayList<String> breakIntoSentences(String article) {
		
		article.split("(\\?)|(\\!)|(. )|(.\"))
		
		
	}
}
