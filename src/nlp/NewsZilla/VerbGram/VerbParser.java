package nlp.NewsZilla.VerbGram;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import nlp.NewsZilla.Tagger.PartOfSpeechTagger;

/**
 * Given a parsed article as an ArrayList<Entry<String, String>> the
 * getArticleSkeleton method of this class returns the 'verb skeleton'
 * of the article as an ArrayList<Entry<String, String>>.
 * @author Armaan
 *
 */
public class VerbParser {
	

	PartOfSpeechTagger post;
	HashMap<String, ArrayList<String>> sentencesByVerb;
	int gramDepth;
	
	public VerbParser(PartOfSpeechTagger post, HashMap<String, ArrayList<String>> sentencesByVerb, int gramDepth) {
		this.post = post;
		this.sentencesByVerb = sentencesByVerb;
		this.gramDepth = gramDepth;
	}
	
	/**
	 * Takes a parsed article, finds the 'main' verb for each sentence and
	 * returns the 'verb skeleton'
	 * @param article
	 * @return
	 */
	public ArrayList<String> getArticleSkeleton(ArrayList<String> article) {
		ArrayList<String> verbSkeleton = new ArrayList<String>();
		for (int i = 0; i < gramDepth - 1; i++) {
			verbSkeleton.add("<START>");
		}
		for (String sentence : article) {
			verbSkeleton.add(getMainVerb(sentence));
		}
		return verbSkeleton;
	}
	
	/**
	 * Gets the main verb in a given sentence
	 * and hashes it by that verb.
	 * @param sentence
	 * @return
	 */
	public String getMainVerb(String sentence) {
		PriorityQueue<Entry<String, String>> verbQueue = new PriorityQueue<Entry<String, String>>(10,
				new VerbComparator());
		for (String word : sentence.split(" ")) {
			String pos = post.tag(word);
			Entry<String, String> verbPOSEntry = new AbstractMap.SimpleEntry<String, String>(word, pos);
			verbQueue.add(verbPOSEntry);
		}
		Entry<String, String> topVerbEntry = verbQueue.poll();
		String verb;
		if (topVerbEntry == null) {
			verb = "<NULL>";
		} else {
			verb = topVerbEntry.getKey();
		}
		if (!sentencesByVerb.containsKey(verb)) {
			sentencesByVerb.put(verb, new ArrayList<String>());
		}
		sentencesByVerb.get(verb).add(sentence);
		return verb;
	}

}
