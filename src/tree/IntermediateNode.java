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
		return this.children.get(Helpers.firstIndexGreater(this.keys, key)).get(key);
	}

	@Override
	public BTreeNode<K, V> insert(K key, V value) {
		// find where the key should go in the sorted list
		int insertIndex = Helpers.firstIndexGreater(this.keys, key);
		
		// insert into child node
		BTreeNode<K, V> newChildNode = this.children.get(insertIndex).insert(key, value);
		
		// set min if lowest element
		if (insertIndex == 0) this.min = this.children.get(0).min;
		
		// if no splitting is necessary, return null
		if (newChildNode == null) return null;
		
		// add key and child to lists
		this.keys.add(insertIndex, newChildNode.min);
		this.children.add(insertIndex + 1, newChildNode);
		
		// if there was room in this node for a new child, return
		if (this.keys.size() < this.mc) return null; 
		
		// split list in two (adding plus one because we split by children)
		int splitIndex = (this.mc + 1)/2;
		
		// last half goes in new node
		IntermediateNode<K, V> newNode = new IntermediateNode<K, V>(this.mc);
		newNode.children.addAll(this.children.subList(splitIndex, this.children.size()));
		newNode.min = newNode.children.get(0).min;
		newNode.keys.addAll(this.keys.subList(splitIndex, this.keys.size()));
		
		// first half goes in this node
		this.children = new ArrayList<>(this.children.subList(0, splitIndex));
		this.keys = new ArrayList<>(this.keys.subList(0, splitIndex - 1));
		
		// return new node
		return newNode;
	}
	
	@Override
	public boolean delete(K key, BTreeNode<K, V> neighbor) {
		// find where to delete
		int deleteIndex = Helpers.firstIndexGreater(this.keys, key);
		
		// delete from child, using neighbor for extra data
		BTreeNode<K, V> deleteChild = this.children.get(deleteIndex);
		BTreeNode<K, V> deleteChildNeighbor = deleteIndex == 0 
				? this.children.get(1)
				: this.children.get(deleteIndex - 1);
		boolean shouldDeleteChild = deleteChild.delete(key, deleteChildNeighbor);
		
		// reset key pointing to neighbor
		int neighborIndex = deleteIndex == 0 ? 1 : deleteIndex - 1;
		if (neighborIndex == 0) this.min = deleteChildNeighbor.min;
		else this.keys.set(neighborIndex - 1, deleteChildNeighbor.min);
		
		// delete child if necessary
		if (shouldDeleteChild) {
			if (deleteIndex == 0) {
				this.min = this.keys.get(0);
				this.keys.remove(0);
			} else {
				this.keys.remove(deleteIndex - 1);
			}
			this.children.remove(deleteIndex);
		}
		
		// if child not deleted, reset its key
		else {
			if (deleteIndex == 0) this.min = deleteChild.min;
			else this.keys.set(deleteIndex - 1, deleteChild.min);
		}
		
		this.min = this.children.get(0).min;
		
		// if there are enough children in this node, return
		if (this.children.size() >= this.mc/2) return false;
		
		// If the neighbor is null, this must be the root. Tell the data structure
		// to replace this if there is only one child.
		if (neighbor == null) return this.children.size() == 1;
		
		IntermediateNode<K, V> neighborIntermediateNode = (IntermediateNode<K, V>)neighbor;

		// if neighbor does not have enough keys, move data and tell parent to delete this node
		if (this.children.size() + neighborIntermediateNode.children.size() < this.mc) {
			if (neighborIntermediateNode.min.compareTo(this.min) < 0) {
				neighborIntermediateNode.keys.add(this.min);
				neighborIntermediateNode.keys.addAll(this.keys);
				neighborIntermediateNode.children.addAll(this.children);
			} else {
				neighborIntermediateNode.keys.add(0, neighborIntermediateNode.min);
				neighborIntermediateNode.keys.addAll(0, this.keys);
				neighborIntermediateNode.min = this.min;
				neighborIntermediateNode.children.addAll(0, this.children);
			}
			return true;
		}
		
		// if neighbor has enough keys, take one of them

		// if neighbor comes before this node, take one from the end of neighbor and move it to the beginning of this
		if (neighborIntermediateNode.min.compareTo(this.min) < 0) {
			int neighborNumChildren = neighborIntermediateNode.children.size();
			
			this.keys.add(0, this.min);
			this.min = neighborIntermediateNode.keys.get(neighborNumChildren - 2);
			this.children.add(0, neighborIntermediateNode.children.get(neighborNumChildren - 1));
			
			neighborIntermediateNode.keys.remove(neighborNumChildren - 2);
			neighborIntermediateNode.children.remove(neighborNumChildren - 1);
		} 
		
		// else, take one from the beginning of neighbor and move it to the end of this
		else {
			this.keys.add(neighborIntermediateNode.min);
			this.children.add(neighborIntermediateNode.children.get(0));
			
			neighborIntermediateNode.min = neighborIntermediateNode.keys.get(0);
			neighborIntermediateNode.keys.remove(0);
			neighborIntermediateNode.children.remove(0);
		}
		
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
		// check that number of keys is number of children - 1
		if (this.keys.size() != this.children.size() - 1) 
			throw new AssertionError("Intermediate node: #keys != #children - 1");
		
		// check that children do not exceed max children
		if (this.children.size() > this.mc) 
			throw new AssertionError("Intermediate node: #children > maxChildren");
		
		// check this.min
		if (!this.min.equals(min)) 
			throw new AssertionError("Intermediate node: expected min to be " + min + " but found " + this.min);
		
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
		K childMin = min;
		for (int i=0; i<this.keys.size(); i++) {
			K childMax = this.keys.get(i);
			this.children.get(i).verify(childMin, childMax);
			childMin = childMax;
		}
		this.children.get(this.children.size() - 1).verify(childMin, max);
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
		acc.min = keys.get(0);
		
		return acc;
	}

}
