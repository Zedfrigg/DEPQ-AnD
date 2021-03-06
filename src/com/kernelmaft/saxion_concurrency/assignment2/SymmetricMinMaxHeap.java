package com.kernelmaft.saxion_concurrency.assignment2;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class SymmetricMinMaxHeap<E> implements DoubleEndedPrioQueue<E>
{
	private final ArrayList<PrioritisedElement<E>> array;
	
	public SymmetricMinMaxHeap()
	{
		array = new ArrayList<>();
		// Add empty element at index 0 for easier index arithmetic
		array.add(null);
		// Add empty root element
		array.add(null);
	}
	
	/**
	 * Whether the SMMH currently has no elements in it.
	 * @return A boolean indicating the aforementioned.
	 */
	@Override public boolean isEmpty()
	{
		return size() == 0;
	}
	
	/**
	 * Gives the number of elements in the SMMH.
	 * @return The number of elements.
	 */
	@Override public int size()
	{
		assert array.size() >= 2;
		return array.size() - 2;
	}
	
	/**
	 * Get a reference to the element with the smallest priority. Does not remove the element from the SMMH.
	 * @return The element reference.
	 * @throws java.util.NoSuchElementException When there is no smallest element because the SMMH is empty.
	 */
	@Override public E getMin()
	{
		if (isEmpty())
			throw new NoSuchElementException("Cannot get the lowest priority element because the queue is empty");
		return array.get(2).element;
	}
	
	/**
	 * Get a reference to the element with the highest priority. In the case there is only one element in the SMMH it
	 * will return the same element as {@link #getMin}. Does not remove the element from the SMMH.
	 * @return The element reference.
	 * @throws java.util.NoSuchElementException When there is no biggest element because the SMMH is empty.
	 */
	@Override public E getMax()
	{
		if (isEmpty())
			throw new NoSuchElementException("Cannot get the highest priority element because the queue is empty");
		return size() == 1 ? array.get(2).element : array.get(3).element;
	}
	
	/**
	 * Add a new element with a certain priority to the SMMH.
	 * @param newElement The new element to add.
	 * @param priority   The priority of the element.
	 */
	@Override public void put(E newElement, int priority)
	{
		assert isValidSMMH();
		
		array.add(new PrioritisedElement<>(newElement, priority));
		// If this is the first element the heap is already valid
		if (size() > 1) {
			final int index = array.size() - 1;
			final int leftSibling = index - 1;
			// Check if we need to swap this element with its left sibling
			if (!isLeftChild(index) && biggerThanRightSibling(leftSibling)) {
				swap(index, leftSibling);
				bubbleUp(leftSibling);
			}
			else {
				bubbleUp(index);
			}
		}
		
		assert isValidSMMH();
	}
	
	/**
	 * Restore correct SMMH order by working our way up the tree after an insertion. Checks for P2 and P3 violations
	 * and performs the appropriate swaps where necessary.
	 * @param index The index of the element to start at.
	 */
	private void bubbleUp(int index)
	{
		final int leftChildGrandparent = (index / 4) * 2;
		final int rightChildGrandparent = leftChildGrandparent + 1;
		// If the element has no grandparent it doesn't need to be checked, we're at the top of the tree
		if (leftChildGrandparent != 0) {
			if (array.get(index).priority < array.get(leftChildGrandparent).priority) {
				// P2 is violated
				
				swap(index, leftChildGrandparent);
				bubbleUp(leftChildGrandparent);
			}
			else if (array.get(index).priority > array.get(rightChildGrandparent).priority) {
				// P3 is violated
				
				swap(index, rightChildGrandparent);
				bubbleUp(rightChildGrandparent);
			}
		}
	}
	
	/**
	 * Swaps two elements in the heap.
	 * @param indexFirst The index of the first element.
	 * @param indexSecond The index of the second element.
	 */
	private void swap(int indexFirst, int indexSecond)
	{
		final PrioritisedElement<E> temporary = array.get(indexFirst);
		array.set(indexFirst, array.get(indexSecond));
		array.set(indexSecond, temporary);
	}
	
	/**
	 * Checks whether P1 is violated for an element with a certain index and its left sibling.
	 * @param index The index of the element to check.
	 * @return True when the P1 is satisfied, false when it doesn't.
	 */
	private boolean biggerThanRightSibling(int index)
	{
		assert index > 0;
		assert index + 1 < array.size();
		return array.get(index).priority > array.get(index + 1).priority;
	}
	
	/**
	 * Remove and return the element with the lowest priority from the SMMH.
	 * @return A reference to the removed element.
	 * @throws java.util.NoSuchElementException When there is no element to remove because the SMMH is empty.
	 */
	@Override public E removeMin()
	{
		assert isValidSMMH();
		
		if (isEmpty())
			throw new NoSuchElementException("Cannot remove the lowest priority element because the queue is empty");
		
		final PrioritisedElement<E> removedElement = array.get(2);
		final PrioritisedElement<E> lastElement = array.remove(array.size() - 1);
		// If it exists, overwrite the smallest element with the one removed from the end
		if (size() >= 1)
			array.set(2, lastElement);
		// If there's only one element left skip the bubble down
		if (size() >= 2)
			bubbleDown(2);
		
		assert isValidSMMH();
		return removedElement.element;
	}
	
	/**
	 * Remove and return the element with the highest priority from the SMMH. The element could also be the element
	 * with the smallest priority it it's the only one.
	 * @return A reference to the removed element.
	 * @throws java.util.NoSuchElementException When there is no element to remove because the SMMH is empty.
	 */
	@Override public E removeMax()
	{
		assert isValidSMMH();
		
		if (isEmpty())
			throw new NoSuchElementException("Cannot remove the highest priority element because the queue is empty");
		
		int elementToRemove = 3;
		if (size() == 1)
			// If there is only one element the smallest element is also the biggest one
			elementToRemove = 2;
		final PrioritisedElement<E> removedElement = array.get(elementToRemove);
		final PrioritisedElement<E> lastElement = array.remove(array.size() - 1);
		// If there is only one element left we don't need to do anything more
		if (size() >= 2) {
			array.set(elementToRemove, lastElement);
			bubbleDown(elementToRemove);
		}
		
		assert isValidSMMH();
		return removedElement.element;
	}
	
	/**
	 * Restore SMMH order by working our way down the tree after removal of an element.
	 * @param index The index of the element to start at.
	 */
	private void bubbleDown(int index)
	{
		assert index > 0;
		assert index < array.size();
		
		final int elementPrio = array.get(index).priority;
		final int leftChild = index * 2;
		if (isLeftChild(index)) {
			final int rightSibling = index + 1;
			final boolean hasRightSibling = rightSibling < array.size();
			if (hasRightSibling && biggerThanRightSibling(index)) {
				// Element is bigger than right sibling, violation of P1
				
				swap(index, rightSibling);
				bubbleDown(index);
				bubbleDown(rightSibling);
			}
			else if (leftChild < array.size()) {
				// Element has a left child
				
				final int leftChildPrio = array.get(leftChild).priority;
				final boolean leftChildSmallerThanElem = leftChildPrio < elementPrio;
				final int rightNephew = leftChild + 2;
				final boolean rightNephewExists = rightNephew < array.size();
				
				if (leftChildSmallerThanElem) {
					// Element is bigger than left child, violation of P2
					
					if (rightNephewExists && array.get(rightNephew).priority < leftChildPrio) {
						// Right nephew is even smaller than left child
						swap(index, rightNephew);
						bubbleDown(rightNephew);
					}
					else {
						// Left child is the smallest
						swap(index, leftChild);
						bubbleDown(leftChild);
					}
				}
				else if (rightNephewExists && array.get(rightNephew).priority < elementPrio) {
					// Right nephew is the smallest
					swap(index, rightNephew);
					bubbleDown(rightNephew);
				}
			}
		}
		else {
			final int leftSibling = index - 1;
			final int leftNephew = leftChild - 1;
			if (biggerThanRightSibling(leftSibling)) {
				// Left sibling is bigger than element, violation of P1
				
				swap(index, leftSibling);
				bubbleDown(index);
				bubbleDown(leftSibling);
			}
			else if (leftNephew < array.size()) {
				// Element has a left nephew
				
				final int leftNephewPrio = array.get(leftNephew).priority;
				final boolean leftNephewBiggerThanElem = leftNephewPrio > elementPrio;
				final int rightChild = leftChild + 1;
				final boolean rightChildExists = rightChild < array.size();
				
				if (leftNephewBiggerThanElem) {
					// Element is smaller than right nephew, violation of P3
					
					if (rightChildExists && array.get(rightChild).priority > leftNephewPrio) {
						// Right child is even bigger than left nephew
						swap(index, rightChild);
						bubbleDown(rightChild);
					}
					else {
						// Left nephew is the smallest
						swap(index, leftNephew);
						bubbleDown(leftNephew);
					}
				}
				else if (rightChildExists && array.get(rightChild).priority > elementPrio) {
					// Right child is the biggest
					swap(index, rightChild);
					bubbleDown(rightChild);
				}
			}
		}
	}
	
	/**
	 * Simple check to see if an element is a left child.
	 * @param index The index of the element to check.
	 * @return A boolean indicating whether the element is a left child.
	 */
	private static boolean isLeftChild(int index)
	{
		assert index > 0;
		return index % 2 == 0;
	}
	
	/**
	 * Internal integrity check, only for debugging and assertions.
	 * @return Whether the heap is currently a valid SMMH.
	 */
	private boolean isValidSMMH()
	{
		for (int i = 2; i < array.size(); i++) {
			// P1 check
			if (i % 2 == 0 && i + 1 < array.size()) {
				if (array.get(i).priority > array.get(i + 1).priority) {
					System.out.println("P1 violated at index " + i);
					return false;
				}
			}
			if (i / 4 > 0) {
				// P2 check
				if (array.get(i / 4 * 2).priority > array.get(i).priority) {
					System.out.println("P2 violated at index " + i);
					return false;
				}
				// P3 check
				if (array.get(i / 4 * 2 + 1).priority < array.get(i).priority) {
					System.out.println("P3 violated at index " + i);
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Gives the DOT-language tree representation of the internal heap. Can be used for visualisation and debugging.
	 * @param includeIndices  Whether the index of each element in the heap array should be included in the node.
	 * @param includeElements Whether the string representation of the content of each element should be included in
	 *                        the node.
	 * @return                A string containing the representation.
	 */
	public String toDotTree(boolean includeIndices, boolean includeElements)
	{
		final StringBuilder dotOutput = new StringBuilder();
		dotOutput.append("digraph { ");
		
		dotOutput.append("1 [label=\"1, root\"]; ");
		for (int i = 2; i < array.size(); i++) {
			final int elemPrio = array.get(i).priority;
			dotOutput.append(i).append(" [label=\"");
			if (includeIndices)
				// Include the indices of the nodes in the heap array
				dotOutput.append(i).append(", ");
			dotOutput.append(elemPrio);
			if (includeElements)
				// Include the toString representation of the elements in the nodes
				dotOutput.append(", ").append(array.get(i).element);
			dotOutput.append("\"]; ");
			
			final int parentIndex = i / 2;
			dotOutput.append(parentIndex).append("->").append(i).append("; ");
		}
		
		dotOutput.append("}");
		return dotOutput.toString();
	}
}
