package tree;

import java.util.List;

public class Helpers {
	
	/**
	 * Given a list of keys in an intermediate node, get the index of the child
	 * that should be searched for the given search term.
	 * @param list list of elements
	 * @param searchTerm value to look for
	 * @return if searchTerm < list[1]: 0 </br>
	 * else if searchTerm >= list[list.size() - 1]: list.size() - 1 </br>
	 * else: the last index less than the search term
	 */
	public static <T extends Comparable<T>> int chooseChildFromKeys(List<T> list, T searchTerm) {
		for (int i=0; i<list.size(); i++) {
			if (searchTerm.compareTo(list.get(i)) < 0) 
				return i == 0 ? 0 : i - 1;
		}
		return list.size() - 1;
	}
	
	/**
	 * Get the first index in a list at which the value is greater than or 
	 * equal to the given comparable. Returns list.size() if search term is 
	 * greater than all elements in the list.
	 * @param list list of elements
	 * @param searchTerm value to look for
	 * @return first index in list such that list[index] >= searchTerm
	 */
	public static <T extends Comparable<T>> int firstIndexGreaterOrEqual(List<T> list, T searchTerm) {
		for (int i=0; i<list.size(); i++)
			if (searchTerm.compareTo(list.get(i)) <= 0) 
				return i;
		return list.size();
	}
	
	/**
	 * Returns whether the element at a certain index in the list is equal to
	 * the given search term. This method is for convenience because
	 * checks like this are common.
	 * @param list list to look in
	 * @param index index within the list of element in question
	 * @param searchTerm key to compare to element in list
	 * @return true if element in list is within range and equals searchTerm; else false
	 */
	public static <T extends Comparable<T>> boolean elementAtIndexEqualsKey(List<T> list, int index, T searchTerm) {
		return index >= 0 && index < list.size() && list.get(index).compareTo(searchTerm) == 0;
	}
}
