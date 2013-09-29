import java.util.*;

/**
 * PriorityQueueHeap.
 * 
 * A vector-based heap implementation of the priority queue. 
 * 
 * Intuition:
 * 
 * example:
 * 
 * [ ] [2] [7] [4] [8]... [n]
 * 
 * Every new element is added into the queue at the appropriate location. 
 * The heap starts from location 1 ([2]) to [n]. location [1]
 * represents the root of the heap and therefore the element with 
 * the highest priority. 
 * 
 * Insertion of a new element occurs at the end of the vector. Once inserted,
 * the last element is compared to its parent nodes to 'up-bubble'. 
 * 
 * Implementation Rationale:
 * The heap-simulation techniqueue allows for excellent performance in
 * insertion and retrieval, irrespective of the size of the data being worked with.
 * As a result of being an abstraction of a ordered, complete binary tree
 * Onlog(n) performance is attained. It was chosen because of its relative 
 * simplicity in implementation and the aforementioned performance benefits. 
 * 
 * This particular implementation uses a Vector as the underlying concrete 
 * data structure, however, an AbstractList was chosen to ensure modularity. 
 * Therefore, this could be easily changed to any appropriate list structure
 * which imposes order. 
 * 
 * @author David Porter
 * @version 1
 * @param <T> The type of the the data being stored in this particular PriorityQueue
 * @param <K> The key value of the data being stored in the PriorityQueue. 
 * Must implement Comparable. 
 */
public class PriorityQueueHeap <T, K extends Comparable<K>> implements PriorityQueue <T, K>
{

    //---------------------------------------------Variables----------------
    /*
     * queue:
     * The primary set of data being abstracted as a heap. 
     */
    private AbstractList queue; 

    /* int endLoc:
     * The end of the heap in the vector. Must always be null. 
     * Use endLoc - 1 to find the last element in the heap. 
     * Used to keep track of the end of the last element of the heap, 
     * since it may grow and shrink. 
     */
    private int endLoc; 

    //---------------------------------------------Constants------------------

    /*
     * The start location along the queue. Should be at least 1 for the heap
     * abstraction to work correctly.
     */
    private static final int START_LOC = 1; 

    //-----------------------------------------Constructors--------------------
    /**
     * PriorityQueueHeap Constructor
     * The basic object constructor. 
     */
    public PriorityQueueHeap ()
    {
        endLoc = START_LOC; //Assign the last location
        queue = new Vector(); //setup the AbstractList

        //Create as many initial spaces as required 
        //(usually just 1) in the AbstractList. 
        for (int i = 0; i < START_LOC; i++)
        {
            queue.add(null); //initial space at location 0 will be never used. 
        }
    }

    //-----------------------------------------Interface methods--------------
    /**
     * enqueue
     * Takes an object, wraps it in a node and stores it in the heap.
     * Given the ordinal nature of the queue, it will be stored according
     * to its priority. 
     * @param priority The Comparable object being used to compare priority.
     * @param element The data being stored along with the key value. 
     */
    public void enqueue(K priority, T element)
    {
        //find the last location and add the element immediately after that. 
        int addLoc = endLoc; 

        addToendLoc(); //increment length

        //Create Node:
        Node n = new Node(priority, element);

        queue.add(addLoc, n); //add node to the last location. 

        //bubble up at location to restore priority
        bubbleUp(addLoc);
    }
      
    
    /**
     * dequeue
     * removes the item from the queue with the highest priority. 
     * @throws EmptyQueueException Throws exception if the queue is empty. 
     */
    public void dequeue() throws EmptyQueueException
    {
        if (isEmpty()) //If the priority queue is empty, do nothing
            throw new EmptyQueueException ();

        //Remove item with highest priority
        queue.set(START_LOC, queue.get(endLoc - 1));

        //...Now that it has been moved to the front of the queue, 
        //remove it from the end
        queue.remove(endLoc - 1);

        //...and decrement the length of the queue.
        endLoc = endLoc - 1;

        if (! isEmpty())
            bubbleDown(); //bubble-down heap
    }

    /**
     * isEmpty
     * Returns true if the object is empty.
     * @return Returns true of the object is empty. 
     */
    public boolean isEmpty()
    {
        return START_LOC == endLoc;
    }

    /**
     * front
     * returns the object at the front of the priority queue.
     * @return The object with the highest priority
     * @throws EmptyQueueException Throws exception if the queue is empty.  
     */
    public T front()
    {
        if (! isEmpty())
        {
            Node <T, K> data = (Node)queue.get(START_LOC);
            
            return data.getData();
        }
        
        else throw new EmptyStackException();
    }

    /**
     * length
     * Returns the number of elements in the priority queue.
     * @return the length of the queue
     */
    public int length()
    {
        return endLoc -1; //subtract 1 because of the offset by position 1
    }

    //------------------------------------------Private methods --------------
    /**
     * parent
     * Given the integer location of a node, return its parent's 
     * location as an integer. If given the root already, returns 0. 
     * 
     * @param loc - the integer location of the node whose 
     * parent is to be returned. 
     * @return the parent of the node as given by an integer
     */
    private int parent(int loc)
    {
        return (int)(loc/2);    
    }

    /**
     * isRoot
     * Returns True if integer location is root;
     * @param loc - the integer locaiton of the node being tested for being root. 
     * @return Returns true if the given node is the root of the heap.
     */
    private boolean isRoot(int loc)
    {
        return loc == START_LOC; //given the implentation as a binary search tree, 
        //a location at 1 must always be the root. 
    }

    /**
     * lChild
     * Given the integer location of a node, returns its left child. 
     * If child not present, returns the same value. 
     * @return The left child location in the heap. 
     */
    private int lChild(int loc)
    {
        if (loc*2 > endLoc)//no nodes to the left, return same node. 
            return loc;
        else return loc*2; //return left child
    }

    /**
     * rChild
     * Given the integer location of a node, returns its right child. 
     * @return The right child location in the heap.
     */
    private int rChild(int loc)
    {
        if (loc*2+1 > endLoc)
            return loc; //no further nodes to the right, return same node. 
        else return loc*2+1; //return right child
    }

    /**
     * addToendLoc
     * Increments the endLoc variable to indicate the addition of 
     * another element being added to the heap. 
     */
    private void addToendLoc()
    {
        endLoc = endLoc +1;
    }

    /**
     * bubbleUp
     * Compares the current element to its parent and swaps until 
     * all parent:child nodes are in height order. Higher priority 
     * elements rise to the top, thus the name. 
     * @param loc - The element upon which to perform the bubble-up comparison. 
     */
    private void bubbleUp(int loc)
    {
        //for clarity the locations of the elements being worked with are created here:

        int child = loc; //the node being worked with
        int parent = parent(loc); //its parent

        //For clarity, the nodes being worked with:
        Node childNode = (Node)queue.get(child); //the child node
        Node parentNode = (Node)queue.get(parent); //the parent node

        //While not at the root and the parent is of lesser priority 
        //than the current element, swap
        while (parent > 0 && 
            parentNode.getPriority().compareTo(childNode.getPriority()) > 0)
        {
            Object temp = parentNode; //get the parent element and store temporarily

            queue.set(parent, childNode); //place the element in it's parent's place. 

            queue.set(child, temp); //place the former parent into the place of the child. 

            //and now do the same for the above nodes:
            child = parent;
            parent = parent(parent);

            //and reassign nodes
            childNode = (Node)queue.get(child);
            parentNode = (Node)queue.get(parent);

        }

    }
    /**
     * bubbleDown
     * Used in node removal in a priority queue: The last element of the queue 
     * is copied over the element with the highest priority (thereby removing it)
     * and then compared with its child nodes. Following the comparison, it is 
     * swapped with the node with the highest priority if it is of a lower priority
     */
    private void bubbleDown()
    {

        //Integers - used for clarity
        int parent = START_LOC;//The parent node, also the node being bubbled down initially.
        int rChild = rChild(parent); //The right child node of the node being bubbled down...
        int lChild = lChild(parent); //...and the left child node. 
        
        //Parent Node. Corresponding Nodes, declared here for clarity
        Node parentNode = (Node)queue.get(parent);
        Node rChildNode = null;//rChild Node.
        Node lChildNode = null;//lChild Node.

        if (rChild < endLoc)//if the left and right nodes exist, create them now. 
        {
            rChildNode = (Node)queue.get(rChild);
        }

        if (lChild < endLoc) //if the left node exists, create it as a variable here now. 
        {
            lChildNode = (Node)queue.get(lChild);
        }

        //If there is bubbling required engage while loop:
        boolean left = false; //used to indicate if there is operations required to the left.
        boolean right = false; //used to indicated if there is operations required on the right. 

        if(rChildNode != null)//avoid empty branches to prevent null pointer exceptions. 
            //is there bubbling down required on the right?
            if(parentNode.getPriority().compareTo(rChildNode.getPriority()) > 0) 
                right = true;//if so, set the boolean flag to true to perform operations.

        if(lChildNode != null)
            //is there bubbling down required on the left?
            if (parentNode.getPriority().compareTo(lChildNode.getPriority()) > 0) 
                left = true;

        //if there is bubbling required, engage the loop:
        while (left || right)
        {
            
            //if both nodes need to be bubbled, resolve the conflict by 
            //determining which is of greater priority
            if (left && right) 
            {
                //If the node to the right is of greater priority, swap to the right:
                if (rChildNode.getPriority().compareTo(lChildNode.getPriority()) < 0)  
                {
                    swapDown(parentNode, parent, rChildNode, rChild);
                    parent = rChild(parent);//and move iterator down to the right child
                    rChild = rChild(parent);//move the children down too
                    lChild = lChild(parent);
                }
                
                else //it must be that the lChildNode has greater priority, swap that
                {
                    swapDown(parentNode, parent, lChildNode, lChild);
                    parent = lChild(parent); //move iterator down to the left child
                    lChild = lChild(parent);
                    rChild = rChild(parent);                    
                }
            }
            
            else if(left) //only operations required to the left
            {
                swapDown(parentNode, parent, lChildNode, lChild);
                parent = lChild(parent); //move iterator down to the left child
                lChild = lChild(parent);
                rChild = rChild(parent);
            }
            
            else if (right)//only operations requried on the right
            {
                swapDown(parentNode, parent, rChildNode, rChild);
                parent = rChild(parent);//and move iterator down to the right child
                rChild = rChild(parent);//move the children down too
                lChild = lChild(parent);
            }
            
            //and reset everything to prevent unexpected behaviour:
            lChildNode = null;
            rChildNode = null;
            
            parentNode = (Node)queue.get(parent);
            
            if (rChild < endLoc)
                rChildNode = (Node)queue.get(rChild);//rChild Node.
                
            if (lChild < endLoc)
                lChildNode = (Node)queue.get(lChild);//lChild Node.
            

            left = false; //reset the swapping
            right = false; 
            
            //Test if it is necessary to bubble to the left and right:
            if (rChildNode !=null)//avoid empty branches to prevent null pointer exceptions. 
                //is there bubbling down required on the right?
                if(parentNode.getPriority().compareTo(rChildNode.getPriority()) > 0) 
                    right = true;//if so, set flag to true. 

            if (lChildNode !=null)//is there bubbling down required on the left?
                if (parentNode.getPriority().compareTo(lChildNode.getPriority()) >0) 
                    left = true;

        }
    }

    /**
     * swapDown
     * A method which swaps the nodes around, taking Parent and placing it 
     * where Child was in the queue. 
     * @param parentNode The Parent node being swapped with the child node.
     * @param parent The location of the parent node as an integer
     * @param childNode The child node being swapped
     * @param child the location of the child node as an integer
     */
    public void swapDown(Node parentNode, int parent, Node childNode, int child)
    {
        Node temp = childNode; //create a temporary variable
        queue.set(child, parentNode); //...and...
        queue.set(parent, temp); //...swap
    }

   
/**
 * Node
 * The items within the priority queue, consisting of the linked object 
 * as well as the assigned priority.
 * 
 * Priority will be determined by the 'priority' variable, given as an integer.
 * The lower the integer, the greater the priority and the earlier in the queue 
 * it will sit. 
 * 
 * If no priority given, it will default to a default value.    
 * @author David Porter
 * @version 1
 * @param <T> The type of data being stored in the node. 
 * @param <K> The Comparable type data being stored as a key value. Must implement Comparable.  
 * While this requirement is going to be taken care of in the PriorityQueueHeap, 
 * it makes sense from a structural position to require this condition in case Node is reused.
 */
public class Node <T, K extends Comparable <K>>
{
    /* linkedData
     * The data being pointed to within the Queue. 
     */
    private T linkedData; 
    
    /* priority
     * The priority of the node given as an integer. 
     */
    private K priority; 
    
    /**
     * Constructor: priority value given
     * @param dataIn - The data object being assigned. 
     * @param givenPriority - the priority key being assigned. 
     */
    public Node (K givenPriority, T dataIn) 
    {
        setData(dataIn);
        setPriority(givenPriority);
    }
    
    /**
     * getData
     * Returns the data object pointer stored in the node. 
     * @return The data originally placed within the node.
     */
    public T getData()
    {
        return linkedData;
    }
    
    /**
     * getPriority
     * Returns the priority of the node as a Comparable. 
     * @return The comparable assigned priority
     */
    public K getPriority()
    {
        return priority;
    }
    
    /**
     * setData
     * Sets the data stored in the node. 
     * @param dataIn - an object pointer to be stored in the node
     */
    public void setData(T dataIn)
    {
        linkedData = dataIn;
    }
    
    /**
     * setPriority
     * Sets the priority of the node.
     * @param priorityIn - the corresponding integer value for priority.
     */
    public void setPriority(K priorityIn)
    {
        priority = priorityIn;
    }
    
}

   
}


