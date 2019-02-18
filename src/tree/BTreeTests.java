package tree;

import static org.junit.Assert.*;

import java.util.HashMap;
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
		assertEquals("4 \n"
				+ "\tLeaf: 1:1 2:2 3:3 \n"
				+ "\tLeaf: 4:4 5:5 6:6 ",
				map.toString());
		map.verify(1, 7);
		
		// saturate one node
		map.insert(7, 7);
		map.insert(8, 8);
		assertEquals("4 \n"
				+ "\tLeaf: 1:1 2:2 3:3 \n"
				+ "\tLeaf: 4:4 5:5 6:6 7:7 8:8 ",
				map.toString());
		map.verify(1, 9);
		
		// add a child node
		map.insert(9, 9);
		assertEquals("4 7 \n"
				+ "\tLeaf: 1:1 2:2 3:3 \n"
				+ "\tLeaf: 4:4 5:5 6:6 \n"
				+ "\tLeaf: 7:7 8:8 9:9 ",
				map.toString());
		map.verify(1, 10);
		
		// saturate root node
		for (int i=10; i<=17; i++) {
			map.insert(i, i);
		}
		assertEquals("4 7 10 13 \n"
				+ "\tLeaf: 1:1 2:2 3:3 \n"
				+ "\tLeaf: 4:4 5:5 6:6 \n"
				+ "\tLeaf: 7:7 8:8 9:9 \n"
				+ "\tLeaf: 10:10 11:11 12:12 \n"
				+ "\tLeaf: 13:13 14:14 15:15 16:16 17:17 ",
				map.toString());
		map.verify(1, 18);
		
		// split intermediate node
		map.insert(18, 18);
		assertEquals("10 \n"
				+ "\t4 7 \n"
				+ "\t\tLeaf: 1:1 2:2 3:3 \n"
				+ "\t\tLeaf: 4:4 5:5 6:6 \n"
				+ "\t\tLeaf: 7:7 8:8 9:9 \n"
				+ "\t13 16 \n"
				+ "\t\tLeaf: 10:10 11:11 12:12 \n"
				+ "\t\tLeaf: 13:13 14:14 15:15 \n"
				+ "\t\tLeaf: 16:16 17:17 18:18 ",
				map.toString());
		map.verify(1, 19);
	}
	
	@Test
	public void randomInsertTestCases() {
		this.runRandomInsertTestCase(-1000, 1000, 1000, 4, true);
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
				
				treeMap.verify(minSoFar, maxSoFar);
				
				// make sure every element in reference is in treemap
				for (Map.Entry<Integer, String> entry : reference.entrySet()) {
					assertEquals("did not find key value pair " + entry.getKey() + ", " + entry.getValue(),
							entry.getValue(), treeMap.get(entry.getKey()));
				}
				
				// make sure reference and treemap have same number of nodes
				assertEquals(reference.size(), treeMap.size());
			}
		} catch (AssertionError ae) {
			System.err.println("Error with tree: ");
			System.err.println(treeMap);
			System.err.println(ae.getMessage());
			throw ae;
		}
		
		if (printTree) System.out.println(treeMap);
	}

}
