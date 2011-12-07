package nlp.NewsZilla.Evaluation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;

import nlp.NewsZilla.ArticleProcessing.ArticleModel;
import nlp.NewsZilla.Subject.SubjectFinder;
import nlp.similarity.CosineCalculator;
import nlp.similarity.Vector;
import nlp.similarity.VectorSimilarityCalculator;

public class PairwiseSimilarity {

	String basinRoot = "http://www.cs.middlebury.edu/~wwestlin/articlegen/";
	SubjectFinder sf;
	ArticleModel model;
	ArrayList<ArrayList<Double>> scores;

	public PairwiseSimilarity() throws FileNotFoundException, MalformedURLException {
		model = new ArticleModel("data/articles.processed", 2);
		sf = new SubjectFinder();

		ArrayList<ArrayList<String>> articles = model.articles;
		ArrayList<String> headlines = model.headlines;

		scores = new ArrayList<ArrayList<Double>>();

		try {
			PrintWriter pw;
			pw = new PrintWriter(new FileWriter(new File("data/pairwiseSimilarityScores")));
			for (int i = 0; i < articles.size(); i++) {
				System.out.println("article " + i);
				ArrayList<Double> singleHeadlineScore = getSimilarities(headlines.get(i), articles.get(i));
				if (singleHeadlineScore != null) {
					for (Double score : singleHeadlineScore) {
						System.out.print(score + "\t");
						pw.print(score + "\t");
					}
					System.out.println();
					pw.println();
				}
			}
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void printScores() {
		try {
			System.out.println(scores.size());
			PrintWriter pw = new PrintWriter(new FileWriter(new File("data/pairwiseSimilarityScores")));
			for (ArrayList<Double> singleHeadlineScore : scores) {
				for (Double score : singleHeadlineScore) {
					System.out.print(score + "\t");
					pw.print(score + "\t");
				}
				System.out.println();
				pw.println();
			}
			pw.close();
		} catch (IOException e) {
			System.out.println("couldn't find file to write scores to");
		}
	}

	public ArrayList<Double> getSimilarities(String headline, ArrayList<String> article) {
		String subject = sf.getSubject(headline, model.post);
		if (subject == null) {
			return null;
		}
		String verb = model.verbParser.getMainVerb(headline);
		ArrayList<String> generatedSentences = model.getChosenSentences(subject, verb);
		if (generatedSentences == null) {
			return null;
		}
		int size = Math.min(generatedSentences.size(), article.size());
		ArrayList<Double> similarities = new ArrayList<Double>();
		VectorSimilarityCalculator vsim = new CosineCalculator();
		Vector firstOriginal = Vector.makeVector(article.get(0));
		Vector firstGenerated = Vector.makeVector(generatedSentences.get(0));
		for (int i = 1; i < size; i++) {
			Vector currentOriginal = Vector.makeVector(article.get(i));
			double originalSimilarity = vsim.vectorSimilarity(firstOriginal, currentOriginal);

			Vector currentGenerated = Vector.makeVector(generatedSentences.get(i));
			double generatedSimilarity = vsim.vectorSimilarity(firstGenerated, currentGenerated);

			similarities.add(originalSimilarity - generatedSimilarity);
		}
		return similarities;
	}

	public static void main(String[] args) throws FileNotFoundException, MalformedURLException {
		PairwiseSimilarity sim = new PairwiseSimilarity();
	}
}
