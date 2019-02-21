package tree;

import java.util.ArrayList;
import java.util.List;

public class IntermediateNode<K extends Comparable<K>, V> extends BTreeNode<K, V> {
	
	protected List<BTreeNode<K, V>> children; // max length = mk + 1

	public IntermediateNode(int maxChildren) {
		super(maxChildren);
		this.children = new ArrayList<>();
	}

	@Override
	public V get(K key) {
		return this.children.get(Helpers.chooseChildFromKeys(this.keys, key)).get(key);
	}

	@Override
	public BTreeNode<K, V> insert(K key, V value) {
		// find where the key should go in the sorted list
		int insertIndex = Helpers.chooseChildFromKeys(this.keys, key);
		
		// insert into child node and set its key correctly
		BTreeNode<K, V> insertChild = this.children.get(insertIndex);
		BTreeNode<K, V> newChildNode = insertChild.insert(key, value);
		this.keys.set(insertIndex, insertChild.getMin());
		
		// if no splitting is necessary, return null
		if (newChildNode == null) return null;
		
		// add key and new child to lists
		this.keys.add(insertIndex + 1, newChildNode.getMin());
		this.children.add(insertIndex + 1, newChildNode);
		
		// if there was room in this node for a new child, return
		if (this.keys.size() <= this.mc) return null; 
		
		// split list in two (adding plus one because we split by children)
		int splitIndex = (this.mc + 1)/2;
		
		// last half goes in new node
		IntermediateNode<K, V> newNode = new IntermediateNode<K, V>(this.mc);
		newNode.children.addAll(this.children.subList(splitIndex, this.children.size()));
		newNode.keys.addAll(this.keys.subList(splitIndex, this.keys.size()));
		
		// first half goes in this node
		this.children = new ArrayList<>(this.children.subList(0, splitIndex));
		this.keys = new ArrayList<>(this.keys.subList(0, splitIndex));
		
		// return new node
		return newNode;
	}
	
	@Override
	public boolean delete(K key, BTreeNode<K, V> neighbor) {
		// find where to delete
		int deleteIndex = Helpers.chooseChildFromKeys(this.keys, key);
		
		// delete from child, using neighbor for extra data
		BTreeNode<K, V> deleteChild = this.children.get(deleteIndex);
		BTreeNode<K, V> deleteChildNeighbor = deleteIndex == 0 
				? this.children.get(1)
				: this.children.get(deleteIndex - 1);
		boolean shouldDeleteChild = deleteChild.delete(key, deleteChildNeighbor);
		
		// reset key pointing to neighbor
		int neighborIndex = deleteIndex == 0 ? 1 : deleteIndex - 1;
		this.keys.set(neighborIndex, deleteChildNeighbor.getMin());
		
		// delete child if necessary
		if (shouldDeleteChild) {
			this.children.remove(deleteIndex);
			this.keys.remove(deleteIndex);
		}
		
		// if child not deleted, reset its key
		else this.keys.set(deleteIndex, deleteChild.getMin());
		
		// if there are enough children in this node, return
		if (this.children.size() >= this.mc/2) return false;
		
		// If the neighbor is null, this must be the root. Tell the data structure
		// to replace this if there is only one child.
		if (neighbor == null) return this.children.size() == 1;
		
		IntermediateNode<K, V> neighborIntermediateNode = (IntermediateNode<K, V>)neighbor;

		// if neighbor does not have enough keys, move data and tell parent to delete this node
		if (this.children.size() + neighborIntermediateNode.children.size() < this.mc) {
			if (neighborIntermediateNode.getMin().compareTo(this.getMin()) < 0) {
				neighborIntermediateNode.keys.addAll(this.keys);
				neighborIntermediateNode.children.addAll(this.children);
			} else {
				neighborIntermediateNode.keys.addAll(0, this.keys);
				neighborIntermediateNode.children.addAll(0, this.children);
			}
			return true;
		}
		
		// if neighbor has enough keys, take one of them

		// calculate where to insert to this and remove from neighbor
		int insertionLocation, removalLocation;
		if (neighborIntermediateNode.getMin().compareTo(this.getMin()) < 0) {
			insertionLocation = 0;
			removalLocation = neighborIntermediateNode.keys.size() - 1;
		} else {
			insertionLocation = this.keys.size();
			removalLocation = 0;
		}
		
		// transfer key
		this.keys.add(insertionLocation, neighborIntermediateNode.keys.get(removalLocation));
		neighborIntermediateNode.keys.remove(removalLocation);
		
		// transfer value
		this.children.add(insertionLocation, neighborIntermediateNode.children.get(removalLocation));
		neighborIntermediateNode.children.remove(removalLocation);
		
		return false;
	}
	
	@Override
	public int size() {
		int count = 0;
		for (BTreeNode<K, V> node : this.children) {
			count += node.size();
		}
		return count;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		// add list of keys at top
		for (K key : this.keys) {
			sb.append(key);
			sb.append(" ");
		}
		
		// add each child on a new line, indenting everything by one tab
		for (BTreeNode<K, V> child : children) {
			sb.append("\n\t");
			sb.append(child.toString().replaceAll("\n", "\n\t"));
		}
		
		return sb.toString();
	}
	
	@Override
	public void verify(K min, K max) {
		// check that number of keys == number of children
		if (this.keys.size() != this.children.size()) 
			throw new AssertionError("Intermediate node: #keys != #children");
		
		// check that children do not exceed max children
		if (this.children.size() > this.mc) 
			throw new AssertionError("Intermediate node: #children > maxChildren");
		
		// check that all keys are within range
		for (K key : this.keys) {
			if (key.compareTo(min) < 0) 
				throw new AssertionError("Intermediate node: key (" + key + ") < min (" + min + ")");
			if (key.compareTo(max) >= 0) 
				throw new AssertionError("Intermediate node: key (" + key + ") >= max (" + max + ")");
		}
		
		// check that all keys are in order
		for (int i=0; i<this.keys.size()-1; i++) {
			if (this.keys.get(i).compareTo(this.keys.get(i+1)) >= 0) 
				throw new AssertionError("Intermediate node: keys not in order");
		}
		
		// check that this is at least half full
		if (!this.isRoot && this.children.size() < this.mc/2) 
			throw new AssertionError("Intermediate node: #children < maxChildren/2");
		
		// the root should have at least two children if it is not a leaf
		if (this.isRoot && this.children.size() < 2) 
			throw new AssertionError("Intermediate node: #children in root < 2");
		
		// check that all children are valid
		for (int i=0; i<this.keys.size() - 1; i++)
			this.children.get(i).verify(this.keys.get(i), this.keys.get(i + 1));
		this.children.get(this.children.size() - 1).verify(this.keys.get(this.keys.size() - 1), max);
	}
	
	@Override
	public int getDepth() {
		int depthFound = -1;
		for (BTreeNode<K, V> child : this.children) {
			int childDepth = child.getDepth();
			if (depthFound == -1) depthFound = childDepth;
			if (childDepth != depthFound) throw new AssertionError("Sibling depths not equal");
		}
		return depthFound;
	}
	
	@Override
	public LeafNode<K, V> toLeafNode() {
		List<K> keys = new ArrayList<>();
		List<V> values = new ArrayList<>();
		
		for (BTreeNode<K, V> child : this.children) {
			LeafNode<K, V> leaf = child.toLeafNode();
			keys.addAll(leaf.keys);
			values.addAll(leaf.values);
		}
		
		LeafNode<K, V> acc = new LeafNode<>(this.mc);
		acc.keys = keys;
		acc.values = values;
		
		return acc;
	}

}
