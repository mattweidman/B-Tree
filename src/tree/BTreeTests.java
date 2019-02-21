package tree;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class BTreeTests {

	@Test
	public void testInsert() {
		BTreeSortedMap<Integer, Integer> map = new BTreeSortedMap<>(5);
		
		// size 0
		assertEquals("", map.toString());
		map.verify(0, 0);
		
		// size 1
		map.insert(5, 5);
		assertEquals("Leaf: 5:5 ", map.toString());
		map.verify(5, 6);
		
		// saturate 1 node
		map.insert(3, 3);
		map.insert(2, 2);
		map.insert(4, 4);
		map.insert(1, 1);
		assertEquals("Leaf: 1:1 2:2 3:3 4:4 5:5 ", map.toString());
		map.verify(1, 6);
		
		// add one level
		map.insert(6, 6);
		assertEquals("1 4 \n"
				+ "\tLeaf: 1:1 2:2 3:3 \n"
				+ "\tLeaf: 4:4 5:5 6:6 ",
				map.toString());
		map.verify(1, 7);
		
		// saturate one node
		map.insert(7, 7);
		map.insert(8, 8);
		assertEquals("1 4 \n"
				+ "\tLeaf: 1:1 2:2 3:3 \n"
				+ "\tLeaf: 4:4 5:5 6:6 7:7 8:8 ",
				map.toString());
		map.verify(1, 9);
		
		// add a child node
		map.insert(9, 9);
		assertEquals("1 4 7 \n"
				+ "\tLeaf: 1:1 2:2 3:3 \n"
				+ "\tLeaf: 4:4 5:5 6:6 \n"
				+ "\tLeaf: 7:7 8:8 9:9 ",
				map.toString());
		map.verify(1, 10);
		
		// saturate root node
		for (int i=10; i<=17; i++) {
			map.insert(i, i);
		}
		assertEquals("1 4 7 10 13 \n"
				+ "\tLeaf: 1:1 2:2 3:3 \n"
				+ "\tLeaf: 4:4 5:5 6:6 \n"
				+ "\tLeaf: 7:7 8:8 9:9 \n"
				+ "\tLeaf: 10:10 11:11 12:12 \n"
				+ "\tLeaf: 13:13 14:14 15:15 16:16 17:17 ",
				map.toString());
		map.verify(1, 18);
		
		// split intermediate node
		map.insert(18, 18);
		assertEquals("1 10 \n"
				+ "\t1 4 7 \n"
				+ "\t\tLeaf: 1:1 2:2 3:3 \n"
				+ "\t\tLeaf: 4:4 5:5 6:6 \n"
				+ "\t\tLeaf: 7:7 8:8 9:9 \n"
				+ "\t10 13 16 \n"
				+ "\t\tLeaf: 10:10 11:11 12:12 \n"
				+ "\t\tLeaf: 13:13 14:14 15:15 \n"
				+ "\t\tLeaf: 16:16 17:17 18:18 ",
				map.toString());
		map.verify(1, 19);
	}
	
	@Test
	public void testLeafDelete() {
		BTreeSortedMap<Integer, Integer> map = new BTreeSortedMap<>(5);
		
		// build up to 5 nodes
		for (int i=1; i<=5; i++) map.insert(i, i);
		assertEquals("Leaf: 1:1 2:2 3:3 4:4 5:5 ", map.toString());
		map.verify(1, 6);
		
		// delete middle of leaf
		map.delete(3);
		assertEquals("Leaf: 1:1 2:2 4:4 5:5 ", map.toString());
		map.verify(1, 6);
		
		// delete end of leaf
		map.delete(5);
		assertEquals("Leaf: 1:1 2:2 4:4 ", map.toString());
		map.verify(1, 5);
		
		// delete beginning of leaf
		map.delete(1);
		assertEquals("Leaf: 2:2 4:4 ", map.toString());
		map.verify(2, 5);
		
		// delete the rest of leaf
		map.delete(2);
		map.delete(4);
		assertEquals("", map.toString());
		map.verify(0, 0);
		
		// build up to 1 intermediate node
		for (int i=1; i<=6; i++) map.insert(i, i);
		assertEquals("1 4 \n"
				+ "\tLeaf: 1:1 2:2 3:3 \n"
				+ "\tLeaf: 4:4 5:5 6:6 ",
				map.toString());
		map.verify(1, 7);
		
		// remove from beginning a leaf
		map.delete(1);
		assertEquals("2 4 \n"
				+ "\tLeaf: 2:2 3:3 \n"
				+ "\tLeaf: 4:4 5:5 6:6 ",
				map.toString());
		map.verify(2, 7);
		
		// remove from second position in leaf
		map.delete(3);
		assertEquals("2 4 \n"
				+ "\tLeaf: 2:2 \n"
				+ "\tLeaf: 4:4 5:5 6:6 ",
				map.toString());
		map.verify(2, 7);
		
		// empty a leaf, transferring right to left
		map.delete(2);
		assertEquals("4 5 \n"
				+ "\tLeaf: 4:4 \n"
				+ "\tLeaf: 5:5 6:6 ",
				map.toString());
		map.verify(4, 7);
		
		// remove from right leaf
		map.delete(5);
		assertEquals("4 6 \n"
				+ "\tLeaf: 4:4 \n"
				+ "\tLeaf: 6:6 ",
				map.toString());
		map.verify(4, 7);
		
		// force root to contract back to a leaf
		map.delete(6);
		assertEquals("Leaf: 4:4 ", map.toString());
		map.verify(4, 5);
		
		// fill tree back up
		map.delete(4);
		for (int i=1; i<=6; i++) map.insert(i, i);
		assertEquals("1 4 \n"
				+ "\tLeaf: 1:1 2:2 3:3 \n"
				+ "\tLeaf: 4:4 5:5 6:6 ",
				map.toString());
		map.verify(1, 7);
		
		// force a transfer from left to right
		map.delete(6);
		map.delete(4);
		map.delete(5);
		assertEquals("1 3 \n"
				+ "\tLeaf: 1:1 2:2 \n"
				+ "\tLeaf: 3:3 ",
				map.toString());
		map.verify(1, 4);
	}
	
	@Test
	public void testIntermediateDelete() {
		BTreeSortedMap<Integer, Integer> map = new BTreeSortedMap<>(5);
		
		// make a 2-layered tree
		for (int i=1; i<=18; i++) map.insert(i, i);
		assertEquals("1 10 \n"
				+ "\t1 4 7 \n"
				+ "\t\tLeaf: 1:1 2:2 3:3 \n"
				+ "\t\tLeaf: 4:4 5:5 6:6 \n"
				+ "\t\tLeaf: 7:7 8:8 9:9 \n"
				+ "\t10 13 16 \n"
				+ "\t\tLeaf: 10:10 11:11 12:12 \n"
				+ "\t\tLeaf: 13:13 14:14 15:15 \n"
				+ "\t\tLeaf: 16:16 17:17 18:18 ",
				map.toString());
		map.verify(1, 19);
		
		// delete nodes so the right side is almost ready to swap
		for (Integer x : new Integer[] {11, 12, 14, 15, 17, 18}) map.delete(x);
		assertEquals("1 10 \n"
				+ "\t1 4 7 \n"
				+ "\t\tLeaf: 1:1 2:2 3:3 \n"
				+ "\t\tLeaf: 4:4 5:5 6:6 \n"
				+ "\t\tLeaf: 7:7 8:8 9:9 \n"
				+ "\t10 13 16 \n"
				+ "\t\tLeaf: 10:10 \n"
				+ "\t\tLeaf: 13:13 \n"
				+ "\t\tLeaf: 16:16 ",
				map.toString());
		map.verify(1, 17);
		
		// remove a leaf
		map.delete(10);
		assertEquals("1 13 \n"
				+ "\t1 4 7 \n"
				+ "\t\tLeaf: 1:1 2:2 3:3 \n"
				+ "\t\tLeaf: 4:4 5:5 6:6 \n"
				+ "\t\tLeaf: 7:7 8:8 9:9 \n"
				+ "\t13 16 \n"
				+ "\t\tLeaf: 13:13 \n"
				+ "\t\tLeaf: 16:16 ",
				map.toString());
		map.verify(1, 17);
		
		// add to left side so a merge is not possible
		map.insert(-1, -1);
		map.insert(-2, -2);
		map.insert(-3, -3);
		assertEquals("-3 13 \n"
				+ "\t-3 1 4 7 \n"
				+ "\t\tLeaf: -3:-3 -2:-2 -1:-1 \n"
				+ "\t\tLeaf: 1:1 2:2 3:3 \n"
				+ "\t\tLeaf: 4:4 5:5 6:6 \n"
				+ "\t\tLeaf: 7:7 8:8 9:9 \n"
				+ "\t13 16 \n"
				+ "\t\tLeaf: 13:13 \n"
				+ "\t\tLeaf: 16:16 ",
				map.toString());
		map.verify(-3, 17);
		
		// delete from the right to force a left to right swap
		map.delete(16);
		assertEquals("-3 7 \n"
				+ "\t-3 1 4 \n"
				+ "\t\tLeaf: -3:-3 -2:-2 -1:-1 \n"
				+ "\t\tLeaf: 1:1 2:2 3:3 \n"
				+ "\t\tLeaf: 4:4 5:5 6:6 \n"
				+ "\t7 13 \n"
				+ "\t\tLeaf: 7:7 8:8 9:9 \n"
				+ "\t\tLeaf: 13:13 ",
				map.toString());
		map.verify(-3, 14);
		
		// fill the right side back up
		for (int i=10; i<=18; i++) map.insert(i, i);
		assertEquals("-3 7 \n"
				+ "\t-3 1 4 \n"
				+ "\t\tLeaf: -3:-3 -2:-2 -1:-1 \n"
				+ "\t\tLeaf: 1:1 2:2 3:3 \n"
				+ "\t\tLeaf: 4:4 5:5 6:6 \n"
				+ "\t7 10 13 16 \n"
				+ "\t\tLeaf: 7:7 8:8 9:9 \n"
				+ "\t\tLeaf: 10:10 11:11 12:12 \n"
				+ "\t\tLeaf: 13:13 14:14 15:15 \n"
				+ "\t\tLeaf: 16:16 17:17 18:18 ",
				map.toString());
		map.verify(-3, 19);
		
		// empty most of left side
		for (Integer x : new Integer[] {-3, -2, -1, 2, 3, 5, 6}) map.delete(x);
		assertEquals("1 7 \n"
				+ "\t1 4 \n"
				+ "\t\tLeaf: 1:1 \n"
				+ "\t\tLeaf: 4:4 \n"
				+ "\t7 10 13 16 \n"
				+ "\t\tLeaf: 7:7 8:8 9:9 \n"
				+ "\t\tLeaf: 10:10 11:11 12:12 \n"
				+ "\t\tLeaf: 13:13 14:14 15:15 \n"
				+ "\t\tLeaf: 16:16 17:17 18:18 ",
				map.toString());
		map.verify(1, 19);
		
		// force a right to left swap
		map.delete(4);
		assertEquals("1 10 \n"
				+ "\t1 7 \n"
				+ "\t\tLeaf: 1:1 \n"
				+ "\t\tLeaf: 7:7 8:8 9:9 \n"
				+ "\t10 13 16 \n"
				+ "\t\tLeaf: 10:10 11:11 12:12 \n"
				+ "\t\tLeaf: 13:13 14:14 15:15 \n"
				+ "\t\tLeaf: 16:16 17:17 18:18 ",
				map.toString());
		map.verify(1, 19);
		
		// force a right to left merge
		map.delete(1);
		map.delete(7);
		map.delete(8);
		assertEquals("9 10 13 16 \n"
				+ "\tLeaf: 9:9 \n"
				+ "\tLeaf: 10:10 11:11 12:12 \n"
				+ "\tLeaf: 13:13 14:14 15:15 \n"
				+ "\tLeaf: 16:16 17:17 18:18 ",
				map.toString());
		map.verify(9, 19);
		
		// cause a split again
		for (int i=-2; i<=8; i++) map.insert(i, i);
		assertEquals("-2 10 \n"
				+ "\t-2 1 4 7 \n"
				+ "\t\tLeaf: -2:-2 -1:-1 0:0 \n"
				+ "\t\tLeaf: 1:1 2:2 3:3 \n"
				+ "\t\tLeaf: 4:4 5:5 6:6 \n"
				+ "\t\tLeaf: 7:7 8:8 9:9 \n"
				+ "\t10 13 16 \n"
				+ "\t\tLeaf: 10:10 11:11 12:12 \n"
				+ "\t\tLeaf: 13:13 14:14 15:15 \n"
				+ "\t\tLeaf: 16:16 17:17 18:18 ",
				map.toString());
		map.verify(-2, 19);
		
		// almost empty the right side
		for (int i=9; i<=18; i++) map.delete(i);
		assertEquals("-2 7 \n"
				+ "\t-2 1 4 \n"
				+ "\t\tLeaf: -2:-2 -1:-1 0:0 \n"
				+ "\t\tLeaf: 1:1 2:2 3:3 \n"
				+ "\t\tLeaf: 4:4 5:5 6:6 \n"
				+ "\t7 8 \n"
				+ "\t\tLeaf: 7:7 \n"
				+ "\t\tLeaf: 8:8 ",
				map.toString());
		map.verify(-2, 13);
		
		// force a left to right merge
		map.delete(8);
		assertEquals("-2 1 4 7 \n"
				+ "\tLeaf: -2:-2 -1:-1 0:0 \n"
				+ "\tLeaf: 1:1 2:2 3:3 \n"
				+ "\tLeaf: 4:4 5:5 6:6 \n"
				+ "\tLeaf: 7:7 ",
				map.toString());
		map.verify(-2, 8);
	}
	
	@Test
	public void randomInsertTestCases() {
		this.runRandomInsertTestCase(-1000, 1000, 1000, 4, false);
	}
	
	@Test
	public void randomDeleteTestCases() {
		this.runRandomDeleteTestCase(-1000, 1000, 1000, 1000, 4, false);
	}
	
	public void runRandomDeleteTestCase(int rangeMin, int rangeMax, int mapSize, int numDelete, 
			int maxChildren, boolean printTree) {
		BTreeSortedMap<Integer, String> treeMap = new BTreeSortedMap<>(maxChildren);
		Map<Integer, String> reference = new HashMap<>();
		List<Integer> sortedReference = new ArrayList<>();
		
		try {
			// insert a lot of key value pairs
			for (int i=0; i<mapSize; i++) {
				int key = (int)(Math.random() * (rangeMax - rangeMin) + rangeMin);
				String value = Integer.toString(key);
				
				treeMap.insert(key, value);
				reference.put(key, value);
				if (!sortedReference.contains(key)) sortedReference.add(key);
			}
			sortedReference.sort((x, y) -> Integer.compare(x, y));
			
			// randomly delete a lot of key value pairs
			for (int i=0; i<numDelete && sortedReference.size() > 1; i++) {
				int key = (int)(Math.random() * (rangeMax - rangeMin) + rangeMin);
				
				treeMap.delete(key);
				reference.remove(key);
				sortedReference.remove(new Integer(key));
				
				int minSoFar = sortedReference.get(0);
				int maxSoFar = sortedReference.get(sortedReference.size() - 1) + 1;
				
				this.compareTreeWithReference(treeMap, reference, minSoFar, maxSoFar);
			}
			
			if (printTree) System.out.println(treeMap);
			
			// delete the rest of the tree except one
			for (int i=sortedReference.get(0); sortedReference.size() > 1; i++) {
				int key = i;
				
				treeMap.delete(key);
				reference.remove(key);
				sortedReference.remove(new Integer(key));
				
				int minSoFar = sortedReference.get(0);
				int maxSoFar = sortedReference.get(sortedReference.size() - 1) + 1;
				
				this.compareTreeWithReference(treeMap, reference, minSoFar, maxSoFar);
			}
			
			// delete last one
			treeMap.delete(sortedReference.get(0));
			reference.remove(sortedReference.get(0));
			this.compareTreeWithReference(treeMap, reference, 0, 0);
			
		} catch (AssertionError ae) {
			System.err.println("Error with tree: ");
			System.err.println(treeMap);
			System.err.println(ae.getMessage());
			throw ae;
		}
	}
	
	public void runRandomInsertTestCase(int rangeMin, int rangeMax, int mapSize, int maxChildren, boolean printTree) {
		BTreeSortedMap<Integer, String> treeMap = new BTreeSortedMap<>(maxChildren);
		Map<Integer, String> reference = new HashMap<>();
		
		try {
			int minSoFar = Integer.MAX_VALUE;
			int maxSoFar = Integer.MIN_VALUE;
			
			for (int i=0; i<mapSize; i++) {
				int key = (int)(Math.random() * (rangeMax - rangeMin) + rangeMin);
				String value = Integer.toString(key);
				
				if (minSoFar > key) minSoFar = key;
				if (maxSoFar <= key) maxSoFar = key + 1;
				
				treeMap.insert(key, value);
				reference.put(key, value);
				
				this.compareTreeWithReference(treeMap, reference, minSoFar, maxSoFar);
			}
		} catch (AssertionError ae) {
			System.err.println("Error with tree: ");
			System.err.println(treeMap);
			System.err.println(ae.getMessage());
			throw ae;
		}
		
		if (printTree) System.out.println(treeMap);
	}
	
	private <K extends Comparable<K>, V> void compareTreeWithReference(
			BTreeSortedMap<K, V> treeMap, Map<K, V> reference, 
			K minSoFar, K maxSoFar) {
		treeMap.verify(minSoFar, maxSoFar);
		
		// make sure every element in reference is in treemap
		for (Map.Entry<K, V> entry : reference.entrySet()) {
			assertEquals("did not find key value pair " + entry.getKey() + ", " + entry.getValue(),
					entry.getValue(), treeMap.get(entry.getKey()));
		}
		
		// make sure reference and treemap have same number of pairs
		assertEquals(reference.size(), treeMap.size());
	}

}
