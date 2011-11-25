package nlp.NewsZilla.VerbGram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.PriorityQueue;

/**
 * Given a parsed article as an ArrayList<Entry<String, String>> the
 * getArticleSkeleton method of this class returns the 'verb skeleton'
 * of the article as an ArrayList<Entry<String, String>>.
 * @author Armaan
 *
 */
public class VerbParser {
	
	PriorityQueue<Entry<String, String>> verbQueue = new PriorityQueue<Entry<String, String>>(10,
			new VerbComparator());
	ArrayList<Entry<String, String>> skeleton = new ArrayList<Entry<String, String>>();
	
	/**
	 * Takes a parsed article, finds the 'main' verb for each sentence and
	 * returns the 'skeleton'
	 * @param article
	 * @return
	 */
	public ArrayList<Entry<String, String>> getArticleSkeleton(ArrayList<Entry<String, String>> article) {
		ArrayList<Entry<String, String>> skeleton = new ArrayList<Entry<String, String>>();
		Iterator<Entry<String, String>> articleIterator = skeleton.iterator();
		while (articleIterator.hasNext()) {
			Entry<String, String> word = articleIterator.next();
			if (word.getKey().equals(".")) {
				Entry<String, String> verb = verbQueue.peek();
				if (verb != null) {
					skeleton.add(verb);
				}
				verbQueue.clear();
			}
		}
		return skeleton;
	}

}
