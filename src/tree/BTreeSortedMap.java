package tree;

public class BTreeSortedMap<K extends Comparable<K>, V> implements SortedMap<K, V> {
	
	// Root of B tree. Null if map is empty.
	private BTreeNode<K, V> root;
	
	private final int maxKeys;
	
	public BTreeSortedMap(int maxKeys) {
		this.maxKeys = maxKeys;
	}

	@Override
	public void insert(K key, V value) {
		// if tree is empty, make the root into a leaf and add pair
		if (root == null) {
			root = new LeafNode<K, V>(this.maxKeys);
			root.isRoot = true;
			root.insert(key, value);
			return;
		}
		
		// insert into existing tree and get new node if one was made
		BTreeNode<K, V> newNode = root.insert(key, value);
		if (newNode == null) return;
		
		// figure out which node (root or newNode) goes first
		BTreeNode<K, V> firstChild, secondChild;
		if (root.min.compareTo(newNode.min) < 0) {
			firstChild = root;
			secondChild = newNode;
		} else {
			firstChild = newNode;
			secondChild = root;
		}
		
		// add children to new root
		IntermediateNode<K, V> newRoot = new IntermediateNode<K, V>(this.maxKeys);
		newRoot.min = firstChild.min;
		newRoot.keys.add(secondChild.min);
		newRoot.children.add(firstChild);
		newRoot.children.add(secondChild);
		
		// replace current root
		this.root.isRoot = false;
		newRoot.isRoot = true;
		this.root = newRoot;
	}

	@Override
	public void delete(K key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public V get(K key) {
		return root == null ? null : root.get(key);
	}

	@Override
	public Iterable<Entry<K, V>> getRange(K keyStart, K keyEnd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Entry<K, V>> getRange(K keyStart, int numValues) {
		// TODO Auto-generated method stub
		return null;
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
