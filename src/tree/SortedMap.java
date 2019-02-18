package tree;

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
	public Iterable<Entry<K, V>> getRange(K keyStart, K keyEnd);
	
	/**
	 * Given a starting key and a number of elements return, get all key value pairs
	 * starting with keyStart. If there are not enough pairs after keyStart, returns
	 * the longest possible list it can. Returns empty if nothing found.
	 */
	public Iterable<Entry<K, V>> getRange(K keyStart, int numValues);
	
	/**
	 * Get the number of key value pairs in this map.
	 */
	public int size();
	
	/**
	 * Tuple representing a key value pair.
	 * @param <EK> type of keys
	 * @param <EV> type of values
	 */
	public class Entry<EK, EV> {
		public EK key;
		public EV value;
	}

}
