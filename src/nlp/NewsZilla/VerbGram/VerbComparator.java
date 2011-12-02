package nlp.NewsZilla.VerbGram;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map.Entry;

public class VerbComparator implements Comparator<Entry<String, String>> {

	static final ArrayList<String> verbOrder = new ArrayList<String>() {
		{
			add("VB");
			add("VBN");
			add("VBD");
			add("VBP");
			add("VBZ");
			add("VBG");

		}
	};

	@Override
	public int compare(Entry<String, String> v1, Entry<String, String> v2) {
		Integer rankV1 = verbOrder.indexOf(v1.getValue());
		Integer rankV2 = verbOrder.indexOf(v2.getValue());
		if (rankV1 == -1)
			rankV1 = 999;
		if (rankV2 == -1)
			rankV2 = 999;
		return rankV1.compareTo(rankV2);
	}
}
