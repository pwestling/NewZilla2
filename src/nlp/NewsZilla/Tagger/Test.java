package nlp.NewsZilla.Tagger;

public class Test {
	public static void main(String[] args) {

		PartOfSpeechTagger post = new PartOfSpeechTagger("data/simple.parsed");

		post.tagFile("data/articles.processed", "tagged.tag");

	}
}
