package nlp.NewsZilla.ArticleProcessing;

public class Main {

	public static void main(String[] args) {
		System.out.println("Start");
		ArticleModel am = new ArticleModel("data/articles.processed", 2);
		System.out.println("End");

	}
}