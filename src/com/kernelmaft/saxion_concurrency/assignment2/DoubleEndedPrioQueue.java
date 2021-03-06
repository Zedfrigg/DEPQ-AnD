package com.kernelmaft.saxion_concurrency.assignment2;

/**
 * A priority queue where elements with both the highest and lowest priority are available.
 */
public interface DoubleEndedPrioQueue<E>
{
	/**
	 * Whether the priority queue currently has no elements in it.
	 * @return A boolean indicating whether the priority queue is empty.
	 */
	public boolean isEmpty();
	
	/**
	 * Get the number of elements currently in the priority queue.
	 * @return The number of elements.
	 */
	public int size();
	
	/**
	 * Get the element in the priority queue with the lowest priority. The element will not be removed from the queue.
	 * @return The aforementioned element.
	 * @throws java.util.NoSuchElementException If the queue is empty.
	 */
	public E getMin();
	
	/**
	 * Get the element in the priority queue with the highest priority. The element will not be removed from the queue.
	 * @return The aforementioned element.
	 * @throws java.util.NoSuchElementException If the queue is empty.
	 */
	public E getMax();
	
	/**
	 * Add a new element with a specified priority to the priority queue.
	 * @param newElement The new element to add.
	 * @param priority   The priority of the element.
	 */
	public void put(E newElement, int priority);
	
	/**
	 * Get and remove the element in the priority queue with the lowest priority.
	 * @return The element that was removed.
	 * @throws java.util.NoSuchElementException If the queue is empty.
	 */
	public E removeMin();
	
	/**
	 * Get and remove the element in the priority queue with the highest priority.
	 * @return The element that was removed.
	 * @throws java.util.NoSuchElementException If the queue is empty.
	 */
	public E removeMax();
}
