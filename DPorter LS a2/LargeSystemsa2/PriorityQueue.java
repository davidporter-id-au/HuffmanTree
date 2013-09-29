
/**
 * PriorityQueue
 * An interface of the priority queue abstract data type. The priority queue 
 * takes set of objects in order and allows for their prioritised 
 * removal with 'dequeue and retrieval with 'front'. 
 * @author David
 * @version 1
 * @param <T>
 * @param <K>
 */
public interface PriorityQueue <T, K extends Comparable<K>>
{
    
    /**
     * isEmpty
     * Method Returns True if queue is empty. 
     * @return Returns true if queue is empty. 
     */
    public boolean isEmpty();
    
    /**
     * enqueue
     * Takes a Comparable key and an Object and places them and places 
     * them in the appropriate location in the priority queue. Comparable 
     * is given as a key to give ordinal value to the priority queue.
     * 
     * @param priority - the assigned priority of the object being put 
     * into the queue.
     * @param element - the entity being added to the queue. 
     */
    public void enqueue(K priority, T element);
    
    /**
     * dequeue
     * Removes the element with the highest priority from the queue. 
     * Nothing is returned. 
     * @throws EmptyQueueException if an attempt is made to dequeue when
     * the priority queue is already empty
     */
    public void dequeue() throws EmptyQueueException;
    
    /**
     * front
     * Returns an object corresponding to the front of the queue. 
     * That is, the object with the highest priority. 
     * @return The object stored with the highest priority
     * @throws EmptyQueueException if an attempt is made to 
     * get the front object if the priority queue is already 
     * empty. 
     */
    public T front() throws EmptyQueueException;
}
