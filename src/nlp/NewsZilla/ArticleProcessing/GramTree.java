package nlp.NewsZilla.ArticleProcessing;

import java.io.Serializable;
import java.util.ArrayList;

public class GramTree implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String word;
	private ArrayList<GramTree> children = new ArrayList<GramTree>();
	private Integer count = 0;
	private Double prob = 0.0;
	private GramTree parent = null;

	public GramTree getParent() {
		return parent;
	}

	public void setParent(GramTree parent) {
		this.parent = parent;
	}

	public GramTree(String word) {
		this.word = word;
	}

	public GramTree(String word, GramTree parent) {
		this(word);
		this.setParent(parent);
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
