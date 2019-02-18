package tree;

import java.util.ArrayList;
import java.util.List;

public class LeafNode<K extends Comparable<K>, V> extends BTreeNode<K, V> {
	
	protected List<V> values; // max length = mk

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
		
		// set min if lowest element
		if (insertIndex == 0) this.min = key;
		
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
		newNode.min = newNode.keys.get(0);
		
		// replace this node with first half
		this.keys = new ArrayList<>(this.keys.subList(0, splitIndex));
		this.values = new ArrayList<>(this.values.subList(0, splitIndex));
		
		return newNode;
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
		
		// check this.min
		if (!this.min.equals(min)) 
			throw new AssertionError("Intermediate node: expected min to be " + min + " but found " + this.min);
		
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
		
		// check that this is at least half full
		if (!this.isRoot && this.keys.size() < this.mc/2) 
			throw new AssertionError("Leaf node: #keys < maxChidlren/2");
	}
	
	@Override
	public int getDepth() {
		return 0;
	}

}
