
/**
 * EmptyQueueException
 * Exception intended to be thrown when the user attemps to dequeue a 
 * priority queue when there are no further elements in the queue. 
 * 
 * @author David Porter
 * @version 1
 */
public class EmptyQueueException extends Exception 
{
    
    /**
     * Constructor
     * Creates the exception. No parameters required. 
     */
    public EmptyQueueException()  { }
    
}
