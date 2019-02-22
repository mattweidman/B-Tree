package tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LeafNode<K extends Comparable<K>, V> extends BTreeNode<K, V> {
	
	protected List<V> values; // max length = mk
	protected LeafNode<K, V> previous, next; // used for getRange

	public LeafNode(int maxKeys) {
		super(maxKeys);
		this.values = new ArrayList<>();
	}

	@Override
	public V get(K key) {
		int getIndex = Helpers.firstIndexGreaterOrEqual(this.keys, key);
		if (Helpers.elementAtIndexEqualsKey(this.keys, getIndex, key))
			return this.values.get(getIndex);
		return null;
	}

	@Override
	public BTreeNode<K, V> insert(K key, V value) {
		
		// find where the key should go in the sorted list
		int insertIndex = Helpers.firstIndexGreaterOrEqual(this.keys, key);
		
		// if key at index is equal to given key, replace value, don't insert
		if (Helpers.elementAtIndexEqualsKey(this.keys, insertIndex, key)) {
			// must set key too because keys are equivalent according to the compare()
			// method, but there could be different data that the compare() method ignores
			this.keys.set(insertIndex, key);
			this.values.set(insertIndex, value);
			return null;
		}
		
		// add key value pair
		this.keys.add(insertIndex, key);
		this.values.add(insertIndex, value);

		// if node doesn't have to be split, you're done
		if (this.keys.size() <= this.mc) return null;

		// when node is full, split in two
		int splitIndex = (this.mc + 1)/2;
		
		// last half goes in new node
		LeafNode<K, V> newNode = new LeafNode<K, V>(this.mc);
		newNode.keys.addAll(this.keys.subList(splitIndex, this.keys.size()));
		newNode.values.addAll(this.values.subList(splitIndex, this.values.size()));
		
		// replace this node with first half
		this.keys = new ArrayList<>(this.keys.subList(0, splitIndex));
		this.values = new ArrayList<>(this.values.subList(0, splitIndex));
		
		// create links to the new node
		newNode.next = this.next;
		newNode.previous = this;
		if (this.next != null) this.next.previous = newNode;
		this.next = newNode;
		
		return newNode;
	}
	
	@Override
	public boolean delete(K key, BTreeNode<K, V> neighbor) {
		// find the key if it exists
		int deleteIndex = Helpers.firstIndexGreaterOrEqual(this.keys, key);
		
		// if key to delete not found, return
		if (!Helpers.elementAtIndexEqualsKey(this.keys, deleteIndex, key)) return false;
		
		// before deleting anything, record the lowest value in this
		K minBeforeRemoval = this.getMin();
		
		// remove key value pair
		this.keys.remove(deleteIndex);
		this.values.remove(deleteIndex);
		
		// if leaf is not empty, return
		if (this.keys.size() > 0) return false;
		
		// if neighbor is null or doesn't have enough keys, delete this node
		if (neighbor == null || neighbor.keys.size() <= 1) {
			// remove links to this node
			if (this.previous != null) this.previous.next = this.next;
			if (this.next != null) this.next.previous = this.previous;
			
			return true;
		}
		
		LeafNode<K, V> neighborLeafNode = (LeafNode<K, V>)neighbor;
		
		// if neighbor has enough keys, take one of them

		// using the recorded min key, calculate where to remove from neighbor
		int removalLocation;
		if (neighborLeafNode.getMin().compareTo(minBeforeRemoval) < 0) {
			removalLocation = neighborLeafNode.keys.size() - 1;
		} else {
			removalLocation = 0;
		}
		
		// transfer key
		this.keys.add(neighborLeafNode.keys.get(removalLocation));
		neighborLeafNode.keys.remove(removalLocation);
		
		// transfer value
		this.values.add(neighborLeafNode.values.get(removalLocation));
		neighborLeafNode.values.remove(removalLocation);
		
		return false;
	}
	
	@Override
	public int size() {
		return this.keys.size();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Leaf: ");
		for (int i=0; i<this.keys.size(); i++) {
			sb.append(this.keys.get(i));
			sb.append(":");
			sb.append(this.values.get(i));
			sb.append(" ");
		}
		return sb.toString();
	}
	
	@Override
	public void verify(K min, K max) {
		// check that keys and values have same size
		if (this.keys.size() != this.values.size()) throw new AssertionError("Leaf node: #keys != #values");
		
		// check that keys do not exceed max children
		if (this.keys.size() > this.mc) throw new AssertionError("Leaf node: #keys > max children");
		
		// check that all keys are within range
		for (K key : this.keys) {
			if (key.compareTo(min) < 0) 
				throw new AssertionError("Leaf node: key (" + key + ") < min (" + min + ")");
			if (key.compareTo(max) >= 0)
				throw new AssertionError("Leaf node: key (" + key + ") >= max (" + max + ")");
		}
		
		// check that all keys are in order
		for (int i=0; i<this.keys.size()-1; i++) {
			if (this.keys.get(i).compareTo(this.keys.get(i+1)) >= 0) 
				throw new AssertionError("Leaf node: keys not in order");
		}
	}
	
	@Override
	public int getDepth() {
		return 0;
	}
	
	@Override
	public LeafNode<K, V> toLeafNode() {
		return this;
	}

	@Override
	public List<KeyValuePair<K, V>> getRange(K keyStart, K keyEnd) {
		LeafNode<K, V> node = this;
		List<KeyValuePair<K, V>> allEntries = new LinkedList<>();
		
		// iterative because this loop could be long and we don't want a stack overflow error
		while (node != null) {
			List<KeyValuePair<K, V>> nodeEntries = node.getEntriesInRange(keyStart, keyEnd);
			allEntries.addAll(nodeEntries);
			if (node != this && nodeEntries.size() < node.keys.size()) break;
			node = node.next;
		}
		
		return allEntries;
	}
	
	@Override
	public List<KeyValuePair<K, V>> getPage(K keyStart, int numElements) {
		LeafNode<K, V> node = this;
		List<KeyValuePair<K, V>> allEntries = new LinkedList<>();
		int elementsCounted = 0;
		int indexInLeaf = Helpers.firstIndexGreaterOrEqual(this.keys, keyStart);
		
		// if indexInLeaf >= leaf size, that means no keys were found
		if (indexInLeaf >= this.keys.size()) return new LinkedList<>();
		
		// iterative because this loop could be long and we don't want a stack overflow error
		while (node != null && elementsCounted < numElements) {
			allEntries.add(new KeyValuePair<K, V>(node.keys.get(indexInLeaf), node.values.get(indexInLeaf)));
			
			elementsCounted++;
			indexInLeaf++;
			if (indexInLeaf == node.keys.size()) {
				indexInLeaf = 0;
				node = node.next;
			}
		}
		
		return allEntries;
	}
	
	private List<KeyValuePair<K, V>> getEntriesInRange(K keyStart, K keyEnd) {
		List<KeyValuePair<K, V>> entries = new LinkedList<>();
		
		for (int i=0; i<this.keys.size(); i++) {
			K key = this.keys.get(i);
			if (keyStart.compareTo(key) <= 0 && key.compareTo(keyEnd) < 0)
				entries.add(new KeyValuePair<K, V>(key, this.values.get(i)));
		}
		
		return entries;
	}

}
