package tree;

import java.util.LinkedList;
import java.util.List;

public class BTreeSortedMap<K extends Comparable<K>, V> implements SortedMap<K, V> {
	
	// Root of B tree. Null if map is empty.
	private BTreeNode<K, V> root;
	
	private final int mc;
	
	public BTreeSortedMap(int maxKeys) {
		this.mc = maxKeys;
	}

	@Override
	public void insert(K key, V value) {
		// if tree is empty, make the root into a leaf and add pair
		if (root == null) {
			root = new LeafNode<K, V>(this.mc);
			root.isRoot = true;
			root.insert(key, value);
			return;
		}
		
		// insert into existing tree and get new node if one was made
		BTreeNode<K, V> newNode = root.insert(key, value);
		if (newNode == null) return;
		
		// figure out which node (root or newNode) goes first
		BTreeNode<K, V> firstChild, secondChild;
		if (root.getMin().compareTo(newNode.getMin()) < 0) {
			firstChild = root;
			secondChild = newNode;
		} else {
			firstChild = newNode;
			secondChild = root;
		}
		
		// add children to new root
		IntermediateNode<K, V> newRoot = new IntermediateNode<K, V>(this.mc);
		newRoot.keys.add(firstChild.getMin());
		newRoot.keys.add(secondChild.getMin());
		newRoot.children.add(firstChild);
		newRoot.children.add(secondChild);
		
		// replace current root
		this.root.isRoot = false;
		newRoot.isRoot = true;
		this.root = newRoot;
	}

	@Override
	public void delete(K key) {
		// if root is null, nothing you can do
		if (this.root == null) return;
		
		// delete
		if (this.root.delete(key, null)) {
			
			// if intermediate node only has one child, replace it with its child 
			if (this.root instanceof IntermediateNode) {
				this.root = ((IntermediateNode<K, V>) this.root).children.get(0);
			} 
			
			// if leaf node is empty, replace it with null
			else {
				this.root = null;
			}
		}
	}

	@Override
	public V get(K key) {
		return root == null ? null : root.get(key);
	}

	@Override
	public List<KeyValuePair<K, V>> getRange(K keyStart, K keyEnd) {
		if (this.root == null) return new LinkedList<>();
		return this.root.getRange(keyStart, keyEnd);
	}

	@Override
	public List<KeyValuePair<K, V>> getPage(K keyStart, int numElements) {
		if (this.root == null) return new LinkedList<>();
		return this.root.getPage(keyStart, numElements);
	}

	@Override
	public List<KeyValuePair<K, V>> getPage(int numElements) {
		if (root == null) return new LinkedList<>();
		return this.getPage(this.root.getMin(), numElements);
	}
	
	@Override 
	public int size() {
		return this.root == null ? 0 : this.root.size();
	}
	
	@Override
	public String toString() {
		return root == null ? "" : root.toString();
	}
	
	/**
	 * Used for testing purposes. Throws an assertion error if invalid.
	 * @param min minimum value (inclusive)
	 * @param max maximum value (exclusive)
	 */
	protected void verify(K min, K max) {
		if (this.root != null) {
			this.root.verify(min, max);
			this.root.getDepth();
		}
	}

}
