package tree;

import java.util.List;

/**
 * A map that lets you insert, delete, get, and get a list of values sorted by key.
 * @param <K> type of keys
 * @param <V> type of values
 */
public interface SortedMap<K extends Comparable<K>, V> {
	
	/**
	 * Insert a key value pair.
	 */
	public void insert(K key, V value);
	
	/**
	 * Delete a key value pair.
	 */
	public void delete(K key);
	
	/**
	 * Given a key, get a value. Return null if not found.
	 */
	public V get(K key);
	
	/**
	 * Given a starting key and an ending key, get all key value pairs starting with
	 * keyStart and ending with keyEnd, inclusive. Returns empty if nothing found.
	 */
	public List<KeyValuePair<K, V>> getRange(K keyStart, K keyEnd);
	
	/**
	 * Given a starting key and a number of elements return, get all key value pairs
	 * starting with keyStart. If there are not enough pairs after keyStart, returns
	 * the longest possible list it can. Returns empty if nothing found.
	 */
	public List<KeyValuePair<K, V>> getPage(K keyStart, int numElements);
	
	/**
	 * Gets the first numValues elements in this sorted map. If numValues is greater
	 * than the size of this data structure, returns the entire data structure.
	 */
	public List<KeyValuePair<K, V>> getPage(int numElements);
	
	/**
	 * Get the number of key value pairs in this map.
	 */
	public int size();

}
