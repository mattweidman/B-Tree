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
	protected List<K> keys; // max length = mk
	protected K min; // used for optimization
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
	
}
