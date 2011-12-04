package nlp.NewsZilla.Subject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import nlp.NewsZilla.Tagger.PartOfSpeechTagger;

public class SubjectFinder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getSubject(String sentence, PartOfSpeechTagger post) {
		ArrayList<String> words = new ArrayList<String>(Arrays.asList(sentence.split("\\s+")));
		ArrayList<String> tags = new ArrayList<String>();
		for (String word : words) {
			tags.add(post.tag(word));
		}
		return getSubject(words, tags);
	}

	public String getSubject(String taggedHeadline) {

		String[] wordsSplit = taggedHeadline.split("\\)");
		ArrayList<String> words = new ArrayList<String>();
		ArrayList<String> tags = new ArrayList<String>();
		for (int i = 0; i < wordsSplit.length; i++) {
			wordsSplit[i] = wordsSplit[i].replaceAll("\\(", "");
			String[] split = wordsSplit[i].trim().split(" ");

			words.add(split[1]);
			tags.add(split[0]);

		}

		return getSubject(words, tags);
	}

	public String getSubject(ArrayList<String> words, ArrayList<String> tags) {
		for (int i = 0; i < tags.size(); i++) {
			if (tags.get(i).matches("NNP.*")) {
				return words.get(i);
			}
		}
		for (int i = 0; i < tags.size(); i++) {
			if (tags.get(i).matches("null")) {
				return words.get(i);
			}
		}
		for (int i = 0; i < tags.size(); i++) {
			if (tags.get(i).matches("NN.*")) {
				return words.get(i);
			}
		}
		return words.get(0);

	}

}
