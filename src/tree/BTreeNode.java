package tree;

import java.util.ArrayList;
import java.util.List;

/**
 * A node in a B tree.
 * @param <K> type of keys
 * @param <V> type of values
 */
public abstract class BTreeNode<K extends Comparable<K>, V> {
	
	public final int mc;
	protected List<K> keys; // max length = mc
	protected boolean isRoot; // used for testing
	
	public BTreeNode(int maxChildren) {
		this.mc = maxChildren;
		this.keys = new ArrayList<>();
		this.isRoot = false;
	}
	
	/**
	 * Given a key, get the value at that key. Returns null if not found.
	 * @param key key to look for
	 * @return value found or null if not found
	 */
	public abstract V get(K key);
	
	/**
	 * Insert a key value pair into the node.
	 * @param key key to insert
	 * @param value value associated with key
	 * @return new node to be inserted in the parent node if necessary
	 */
	public abstract BTreeNode<K, V> insert(K key, V value);
	
	/**
	 * Delete a key value pair.
	 * @param key key to delete
	 * @param neighbor sibling node to take data from if this node needs more;
	 * must be same type as this
	 * @return whether this node should be deleted by parent
	 */
	public abstract boolean delete(K key, BTreeNode<K, V> neighbor);
	
	/**
	 * Get a sorted list of all key value pairs in this map that lie within 
	 * a certain range. List is expected to start under this node, but does
	 * not have to end under it - leaves are linked together so traversal over
	 * all intermediate nodes is not necessary.
	 * @param keyStart first key to find
	 * @param keyEnd key to end on
	 * @return list of key value pairs where the first one is under this leaf
	 */
	public abstract List<KeyValuePair<K, V>> getRange(K keyStart, K keyEnd);
	
	/**
	 * Get a sorted list of all key value pairs in this map of a certain length,
	 * starting with a certain key. List is expected to start under this node, but 
	 * does not have to end under it - leaves are linked together so traversal over
	 * all intermediate nodes is not necessary.
	 * @param keyStart first key to find
	 * @param numValues number of pairs
	 * @return list of key value pairs where the first one is under this leaf
	 */
	public abstract List<KeyValuePair<K, V>> getPage(K keyStart, int numElements);
	
	/**
	 * @return number of key value pairs in tree with this node as root
	 */
	public abstract int size();
	
	/**
	 * Used for testing purposes. Throws assertion error if invalid.
	 * @param min minimum value (inclusive)
	 * @param max maximum value (exclusive)
	 */
	protected abstract void verify(K min, K max);
	
	/**
	 * Used for testing purposes. Throws an assertion error if two sibling
	 * nodes have different depths.
	 * @return depth of tree starting with this as root
	 */
	protected abstract int getDepth();
	
	/**
	 * Convert the entire tree into a single leaf node, ignoring size constraints.
	 * @return all of the data in a single node
	 */
	protected abstract LeafNode<K, V> toLeafNode();
	
	/**
	 * Convenience method to get the lowest key. Runs in constant time.
	 * @return lowest key
	 */
	protected K getMin() {
		return this.keys.get(0);
	}
	
}
