package nlp.NewsZilla.ArticleProcessing;

import java.util.ArrayList;

public class GramTree {

	String word;
	ArrayList<GramTree> children = new ArrayList<GramTree>();
	Integer count = 0;
	Double prob = 0.0;

	public GramTree(String word) {
		this.word = word;
	}

	public void incrementCount() {
		count++;
	}

	public ArrayList<GramTree> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<GramTree> children) {
		this.children = children;
	}

	public void addChild(GramTree g) {
		children.add(g);
	}

	public Integer getCount() {
		return count;
	}

	public boolean hasChild(String word) {
		for (GramTree child : children) {
			if (child.getWord().equals(word)) {
				return true;
			}
		}
		return false;
	}

	public GramTree getChild(String word) {
		for (GramTree child : children) {
			if (child.getWord().equals(word)) {
				return child;
			}
		}
		return null;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Double getProb() {
		return prob;
	}

	public void setProb(Double prob) {
		this.prob = prob;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public boolean equals(Object o) {
		if (o instanceof GramTree) {
			return ((GramTree) o).getWord().equals(this.word);
		}
		return false;
	}

}
