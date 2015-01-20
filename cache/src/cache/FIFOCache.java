package cache;

import java.util.NoSuchElementException;
import java.util.Vector;

public class FIFOCache {
	private Vector<Object> objectVector = new Vector<Object>();
	private int maxSize = 0; // the maximum size this queue has had since it was
								// created.

	/**
	 * Add item to queue
	 * 
	 * @param arg0
	 *            The item to add to the queue
	 */
	public void add(Object arg0) {
		objectVector.add(arg0);
		if (objectVector.size() > maxSize) {
			maxSize = objectVector.size();
		}

	}

	/**
	 * Returns the first object in the FIFO-Queue
	 * 
	 * @throws NoSuchElementException
	 *             The queue must not be empty (if it is, a
	 *             java.util.NoSuchElementException is thrown).
	 * @return Object returns the first Object in the queue
	 */
	public Object first() throws NoSuchElementException {
		if (objectVector.isEmpty()) {
			throw new NoSuchElementException("The FIFO-Queue is empty!");
		} else {
			return objectVector.firstElement();
		}

	}

	/**
	 * Check if the FIFO-queue is empty
	 * 
	 * @return boolean returns true if the size of this Queue is 0, otherwise it
	 *         returns false.
	 */
	public boolean isEmpty() {
		if (objectVector.isEmpty()) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * The maximum size this queue has had since it was created.
	 * 
	 * @return maxSize The maximum size this queue has had since it was created.
	 */
	public int maxSize() {
		return maxSize;
	}

	/**
	 * Removes the first item in the FIFO-Queue
	 * 
	 * @throws NoSuchElementException
	 *             The queue must not be empty (if it is, a
	 *             java.util.NoSuchElementException is thrown).
	 */
	public void removeFirst() throws NoSuchElementException {
		if (objectVector.isEmpty()) {
			throw new NoSuchElementException("The FIFO-Queue is empty!");
		} else {
			objectVector.removeElementAt(0);
		}
	}

	/**
	 * Returns the number of Objects in the FIFO-Queue
	 * 
	 * @return int Size of the queue
	 */
	public int size() {
		return objectVector.size();
	}

	/**
	 * Returns a concatenated string of the queue's items
	 * 
	 * @return String The queue's items
	 */
	public String toString() {
		String s = "Queue: ";
		for (Object o : objectVector) {
			s += "(" + String.valueOf(o) + ") ";
		}
		return s;

	}

	/**
	 * Checks if this and another queue is equal
	 * 
	 * @param f
	 *            The other queue to compare with
	 * @return boolean True if equal
	 */
	public boolean equals(Object f) {
		// pre: f must be of the same type as this class. Otherwise, a
		// ClassCastException is thrown.
		// post: returns true if and only if all the following holds:
		// * this and f has the same size
		// * For every position in this queue:
		// o If the position contains a null reference, the corresponding
		// position in f contains a null reference.
		// o If the position contains a reference to the object o1, the
		// corresponding position in f contains the object o2 such that
		// o1.equals(o2) == true

		if (this.getClass() != f.getClass()) {
			throw new ClassCastException("That wasn't a FIFO-queue!");
		}

		if (this.size() == ((FIFOCache) f).size()) {
			for (int i = 0; i < this.size(); i++) {
				if ((this.objectVector.elementAt(i) != null)
						&& (((FIFOCache) f).objectVector.elementAt(i) != null)) {
					if (!(this.objectVector.elementAt(i)
							.equals(((FIFOCache) f).objectVector.elementAt(i)))) {
						return false;
					}
				} else if (this.objectVector.elementAt(i) != ((FIFOCache) f).objectVector
						.elementAt(i)) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
}
