package tree;

/**
 * Tuple representing a key value pair.
 * @param <K> type of keys
 * @param <V> type of values
 */
public class KeyValuePair<K, V> {
	public K key;
	public V value;
	
	public KeyValuePair(K key, V value) {
		this.key = key;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "(" + key + ", " + value + ")";
	}
}
