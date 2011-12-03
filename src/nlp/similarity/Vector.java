package nlp.similarity;

import java.util.HashMap;
import java.util.Map;

/**
 * A class for representing sparse co-occurrence vectors
 * 
 * @author Porter and Alex
 * 
 */
@SuppressWarnings("serial")
public class Vector extends HashMap<String, Double> {

	/**
	 * Create a new Vector with the values of the provided Vector
	 * 
	 * @param v
	 */
	public Vector(Vector v) {
		super(v);
	}

	/**
	 * Build a vector with the keys and values of the provided Map
	 * 
	 * @param map
	 */
	public Vector(Map<String, Double> map) {
		super(map);
	}

	/**
	 * Build an empty vector
	 */
	public Vector() {
		super();
	}

	/**
	 * Wraps the get of HashMap to return 0 instead of null for keys that don't
	 * exist in the map, so that we don't have to store instances of 0
	 * co-occurrences. Otherwise identical
	 * 
	 * @param key
	 * @return
	 */
	public Double get(String key) {
		Double d = super.get(key);
		return d == null ? 0 : d;
	}

	public static Vector makeVector(String sentence) {
		HashMap<String, Double> map = new HashMap<String, Double>();

		String[] split = sentence.split("\\s+");
		for (String s : split) {
			if (!map.containsKey(s)) {

				map.put(s, 0.0);

			}

			map.put(s, map.get(s) + 1);
		}

		return new Vector(map);

	}

}
