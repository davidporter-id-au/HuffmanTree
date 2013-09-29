import java.util.*;
import java.io.*;
/**
 * Huffman Tree
 * 
 * An implementation of the Huffman tree algorithm, a frequency based compression algorithm. 
 * Uses a priority queue to map the frequency of bytes and then bases a binary tree 
 * mapping around this. Takes an input file and writes the output to a file. 
 * 
 * Use toString() to see byte mappings. 
 * 
 * 
 * Note, that this implementation is not intended to work on a single byte. 
 * 
 * 
 * @author David Porter
 * @version 1
 */
public class HuffmanTree
{
    private HuffNode root; //the root of the Huffman Tree.
    private PriorityQueue pq; //the priority Queue used in construction; 
    private File inputFile; //The file being read in
    private File outputFile; //The file being written to
    private Map huffmanMap; //the huffman map of the file that has going to be transcoded. 
    private Vector mappingList; //The mappings in an alternate form for ease of use. 
    
    private final boolean USEBYTEVIEWER = false; //The option to use the graphic byte viewer to display the contents of the file if required
    
    /**
     * Constructor: files
     * Uses input and output files to construct the huffman tree. 
     * @param input The input file to be compressed
     * @param output The output file to be written to in Huffman encoding
     * @throws IOException
     * 
     */
    public HuffmanTree (File input, File output) throws IOException
    {
        inputFile = input;// Set input files
        outputFile = output;
        
        try
        {
            InputStream in = new FileInputStream(inputFile);
            
            init(in); //run the initialising constructor
        }
        catch (IOException e)
        {
            throw e;
        }
        
        fileOutput(); //write the contents of the file out
    }
    
    /**
     * Constructor: String
     * Uses the supplied strings to access the relevant files and calls the file constructor. 
     * Therefore, all critical functions should be supplied there. 
     * @param input The string representation of the input file, including path
     * @param output The string representation of the output file, including path
     * @throws IOException
     */
    public HuffmanTree (String input, String output) throws IOException
    {
        try //to create a inputstream from the string. 
        {
            inputFile = new File(input);
            InputStream in = new FileInputStream(inputFile);
            init(in); //run the initialising constructor
            
            outputFile = new File(output);
        }
        catch (IOException e)
        {
            throw e;
        }
        
        huffmanMap = huffmanCodingMap(); //Create the mapping of byte-codes to the huffman encoding
        
        fileOutput(); //write the contents of the file out
    }
    
    /**
     * printMappings
     * Prints the current mappings to screen. 
     * @param v The input vector which will be printed out. Intended to be the Huffman encoding and 
     * associated byte value. 
     */
    private String printMappings(Vector v)
    {
        String output = "";
        
        output = "Huffman Tree mappings: \nInteger\tbyte\thuffman code";
        
        for(int i = 0; i<v.size(); i++)
        {   
            String [] val = (String [])v.get(i);
            
            output = output + ("\n" + val[0] + "\t" + (char)Integer.parseInt(val[0]) + "\t" + val[1]);
        }   
        
        return output;
    }
    
    /**
     * toString
     * Prints the mappings to a string.
     */
    public String toString()
    {
        return printMappings(mappingList);
    }
    
    /**
     * init
     * The actual constructing method that is called by either of the constructors. 
     * Performs the operations to create the HuffmanTree. 
     * @param stream The file input stream with which to encode
     */
    private void init (InputStream stream) throws IOException
    {
        //get a frequency distribution and place in into a queue. 
        try
        {
            pq = createQueue(getFreqDistList(freqDist(stream))); //create a priority queue
        }
        catch(IOException e)
        {
            throw e;
        }
        
        HuffNode last = null; //grab the last node generated to as to catch the root 
        
        try{ //while there is data in the queue, keep on creating nodes in the huffman tree
            while(!pq.isEmpty())
            {
                HuffNode left = (HuffNode)pq.front();
                pq.dequeue();
                if(!pq.isEmpty()) //if there is another node left in the queue, branch:
                    {
                        HuffNode right = (HuffNode)pq.front();
                        pq.dequeue();      
                        
                        last = branch(left, right);
                        
                        pq.enqueue(last.getFreq(),last); //put the new branch back in
                    }
                else //The number is odd, therefore the last item will be the root. 
                {
                    last = left;
                }
            }
        }
        catch (EmptyQueueException e)
        {
            System.out.println("Empty Queue Exception - Strange error") ;
        }
        
        root = last;//set the root. 
        
    }
    
    /**
     * fileOutput - local
     * The fileOutput being performed on the local variables. Essentially just directs
     * the writeout methods to the specified local variables. 
     */
    public void fileOutput()
    {
        fileOutput(inputFile, outputFile);
    }
    
    /**
     * fileOutput
     * Takes the output file and transcodes the input file with the help of the huffman coding.
     * @param input the file being read in to be transcoded to String
     * @parm output the file being written to
     */
    private void fileOutput(File input, File output)
    {
        String huffmanCoding = null;//declare a string to use for conversion and eventually therefore output.
        FileOutputStream fileOut = null;
        
        try
        {
            huffmanCoding = transcode(input, huffmanMap); //Get the output stream as a string of bytes
            fileOut = new FileOutputStream(output);
            
            if(huffmanCoding != null)
            {
                byte [] fileBytes = stringToBytes(huffmanCoding);
                
                fileOut.write(fileBytes);
                fileOut.close();
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }   
        
        if(USEBYTEVIEWER)
        {
            GraphicByteViewer g = new GraphicByteViewer(output); //Optional: 
        }
    }
    
    /**
     * stringToBytes
     * Transforms the string to a byte array suitable for writing to a file. The algorithm itself is relatively simple,
     * but relies upon Strings - particularly in the transcode method. It has not been sufficiently optimised to
     * achieve much in the way of performance. 
     * 
     * @param in The string input to be transformed
     */
    private byte [] stringToBytes(String in)
    {
        Vector output = new Vector(); //initialise the output variable
        
        int whole = in.length() - (in.length() % 8); //use the Mod by 8 to cut of the items on the string which are not going to fit directly into a byte.
        int leftOver = in.length()%8; //The remaining binary values which do not fit into a complete byte.
        
        int arrayLength;//create an integer representing the length of the array that is going to be exported
        if(leftOver == 0)
            arrayLength = in.length() / 8;
        else arrayLength = (in.length() /8) + 1;//make room for the patial byte at the end. 
        
        byte [] byteArray = new byte [arrayLength]; //initialise the output array;
        int counter = 0; //create a counter to keep track of where up to in the output array
        
        //Start by putting in the whole bits which will fit
        for(int i = 0; i < whole; i = i + 8) //Iterate over each block of bytes, jumping up by 8 each time and loading them in the inner loop
        {
            StringBuffer singleByte = new StringBuffer();
            
            for(int j = i; j % 8 != 0 || j == i; j++ ) //get the next 8 elements from the string
            {
                singleByte.append(in.charAt(j)); //append each character as they are received
            }
            
            byteArray[counter] = constructByte(singleByte.toString()); //and add it to the output vector;
            counter = counter + 1;
        }
        
       
        //Construct leftover byte if boundaries don't fit 8 bits exactly:
        
        if(leftOver != 0)
        {
            byte partialByte = 0; //initialise empty byte
            
            String byteString = "";
            
            for(int i = in.length() - leftOver; i < in.length(); i++)
            {
                byteString = byteString + in.charAt(i);
            }
            
            partialByte = constructByte(byteString);
        
            byteArray[byteArray.length -1] = partialByte;//add that last leftover byte
        
        }
        
        
        return byteArray;
        
    }
    
    /**
     * constructByte
     * Takes a string of 8 characters long and constructs a byte out of it. 
     * @param in The string of 8 bits. Can be less. 
     */
    private byte constructByte(String in)
    {
        int offset = (in.length() % 8); //determine the amount leftover with which to deal with separately by padding. 
        
        byte partialByte = 0; //initialise the output byte
        Byte pattern = Byte.parseByte("00000001",2); //Create a pattern of 1, for using OR with;
        
        for(int i = 0; i < in.length(); i++) //for the remaining characters in the string...
        {
            if(in.charAt(i) == ('1')) //This part of the bitpattern is a 1, add accordingly. 
            {
                partialByte = (byte)(partialByte << 1); //shift bits to the right
                
                partialByte = (byte)(partialByte | pattern.byteValue()); //do an OR to put a 1 at the end of the bit pattern
            }
            else //the part of the bitpattern is simply a zero, therefore just increment
            {
                partialByte = (byte)(partialByte <<1);
            }
        }
        
        for(int i=0; i< 8 - offset && offset != 0; i++) //pad with zeroes if necessary
        {
            partialByte = (byte)(partialByte << 1); 
        }
        
        return partialByte;
    }
    
    /**
     * outputString
     * The transcode method being performed on the object variables
     */
    private void outputString() throws IOException
    {
        try
        {
            transcode(inputFile, huffmanMap);
        }
        catch(IOException e)
        {
            throw e;
        }
    }
    
    /**
     * transcode
     * Takes an input stream and produces a string arranged as a set of bytes with the equivalent huffman Mapping. 
     * Requires that a huffman mapping already exists. 
     * 
     * Because of it's reliace upon Strings, it is very vulnerable to problems with string concatenation.
     * To circumvent this problem, a StringBuffer allows for far greater performance. 
     */
    private String transcode(File in, Map map) throws IOException, NullPointerException
    {
        InputStream inputStream;
        
        try
        {
            inputStream = new FileInputStream(in);
        }
        catch(IOException e)
        {
            throw new IOException("IOException: A problem has occured in the transcoding from the file to Huffman Coding");
        }
        
        int currByte = inputStream.read(); //the current byte with which to read and translate from with the map. 
        StringBuffer outputStream = new StringBuffer();//The output stream to append to
        String huffValues; //temporary array in which to store each block as they are retrieved from the map. should be 1x2 array;
        
        while(currByte != -1)
        {
            
            try
            {
                huffValues = (String)map.get(currByte +""); //Get the key from byte from the map
            }
            catch (NullPointerException e)
            {
                throw new NullPointerException("Key not in map, serious error");
            }
            
            outputStream.append(huffValues);//Add the key value to the output vector. 
            
            currByte = inputStream.read();
        }
        

        return outputStream.toString();
    }
    
    /**
     * branch
     * Takes two nodes and connects them via a branch that has the frequency of the sum of the two
     * Nodes below. Note byte value is zero. 
     */
    private HuffNode branch(HuffNode l, HuffNode r)
    {
        return new HuffNode(0, l.getFreq() + r.getFreq(), l, r); 
    }
    
    /**
     * getFreqDistList
     * Returns the mapping of values and their frequencies as a List. 
     */
    private List getFreqDistList(Map m)
    {
        return new Vector(m.values());
    }
    
    
    /**
     * printByteFreq
     * A simple method to illustrate the byte frequency in the read file. Used for debugging.
     */
    public String printByteFreq()
    {
        
        Vector v = null;
        
        try
        {
            v = new Vector(getFreqDistList(freqDist(new FileInputStream(inputFile)))); //create the appropriate vector
        }
        catch(IOException e)
        {}
        StringBuffer s = new StringBuffer(); //output 
        
        s.append("\nByte Frequency: \n\n byte \t char \t freq");
        
        for(int i = 0; i < v.size(); i++)
        {
            int [] values = (int [])v.get(i);
            s.append("\n" + values[0] + "\t" + (char)values[0] + "\t" + values[1]);
        }
        
        return s.toString();
    }
    
    /**
     * createQueue
     * Takes a list of characters and their frequency distribution as HuffNodes
     * and places them in the priority queue according to there distribution
     * frequency. Intended to have it such that characters with the higher
     * distribution frequency will have higher priority in the queue. 
     * 
     * Note that it is assumed that this will enqueue HuffNodes, it cannot 
     * use another data type. It is assumed that this method will decode the 
     * 2 unit array produced elsewhere. 
     * 
     * Returns a priority queue.
     * 
     * @param l The Orderedlist of elements
     */
    private PriorityQueue createQueue(List l)
    {
        PriorityQueue priorityQueue= new PriorityQueueHeap();
        
        for(int i=0; i<l.size(); i++)
        {
            int [] block = (int []) l.get(i);
            
            int byteValue = block[0]; //expicitly declared for clarity; 
            int priority = block[1];
            
            HuffNode h = new HuffNode(byteValue, priority);
            
            priorityQueue.enqueue(priority, h);
        }
        
        return priorityQueue;
    }
    
    /**
     * freqDist
     * returns a frequency distribution for the bytes for the file. 
     * each element of the Map is a size 2 array, first with the byte
     * and then followed by the frequency. 
     * 
     * thus:
     * 
     * Map:
     *  | [a][3]
     *  | [b][1]
     *  | [c][4]
     *  
     *  indicates 'a' is found 3 times, b found once etc. 
     *  
     *  To do this, it uses a hashmap, increasing the counter of each byte as found. 
     */
    public  Map freqDist(InputStream readFile) throws IOException
    {
        Map map = new HashMap(); //initialise the frequency map
        int freqCounter = 0; //initialise a frequency counter to catch a particular bug
        
        try{
            int currByte = readFile.read(); //initial byte
    
            while(currByte != -1) //while not at the end, keep reading
            {
                if(map.containsKey(currByte)) //if current byte already exists...
                {
                    int [] elem = (int[])map.get(currByte); //get the block and...
                    elem[1] = elem[1] + 1; //increment the frequency by one. 
                }
    
                else //The byte is not in the table, add it now. 
                {
                    int [] elem = new int [] {currByte, 1} ;//create a block with freq 1. 
    
                    map.put(currByte, elem); //put in into the mapping
                }
    
                currByte = readFile.read(); //get next byte for reading. 
    
            }
        }
        catch(IOException e)
        {
            throw e;
        }

        if(freqCounter == 1) //throw single byte exception
            throw new IOException("HuffmanCoding is a frequency based compression algorithm. It does not work on a single byte."); 
        
        return map;
    }
    
    /**
     * huffmanCodingMap
     * Uses the codes generated to create a map of byes. Uses the genCodes() method
     * to create an array and then stores returns the following as a map with the 
     * byte value as the key and the mapping object the huffman code. 
     * 
     * Note that that for every entry in genCodes();
     * [0] - byte value
     * [1] - Huffman code
     */
    private Map huffmanCodingMap()
    {
        Vector v = genCodes(); //create the variables to work with
        Map map = new HashMap();
        
        for(int i=0; i<v.size(); i++) //iterate over the output vector,
        {
            String [] codes = (String [])v.get(i); //and the the individual array blocks
            map.put(codes[0], codes[1]); //and place them into the mapping, using the huffman coding as a key value. 
        }
        
        //Debugging feature: pass the vector to the output on screen for testing. 
        //printMappings(v);
        mappingList = v; //Store the mapping for debugging later. 
        
        return map;
    }
    
    /**
     * genCodes
     * Starts the code genearation method
     */
    public Vector genCodes()
    {
        return genCodes(root, "");
    }
    
    /**
     * genCodes
     * Generates the huffmoan codes by tranversing the tree. 
     * Returns codes as an array int the following format:
     * Stored as an array within a vector.
     * 
     * Vector:
     * | [ByteValue] [Code] 
     * | [ByteValue] [Code] 
     */
    private Vector genCodes(HuffNode h, String path)
    {
        if (h != null)
        {
            Vector v = new Vector();
            
            if(h.getL() != null && h.getR() != null) //if not a leaf node, continue transversal
            { //And concantenate the arrays given by the left and right transversals:
                v.addAll(genCodes(h.getL(),  path +"" + 0 )); //get left elements recursively
                //need to add to direction string. 
                
                v.addAll(genCodes(h.getR(), path + "" + 1)); //get right elements recursively.               
            }
            else //leaf node
            {
                Vector out = new Vector(); //create a new vector to output
                out.add(new String []  {h.getByte() + "", path} ); //add the 
                return out;   
            }
            
            
            return v;
        }
        
        else return null; //Nothing contained in the element. 
    }
    
    /**
     * getMap
     * Provides a map of the data
     */
    public Map getMap()
    {
        return huffmanMap;
    }
    
    
    /**
     * HuffNode
     * A special form of node, specific to Huffman Trees. 
     * Contains a left and right branches as per a typical Binary tree, 
     * however, also contains the byte value and the frequency of the 
     * byte. 
     */
    private class HuffNode {

        private HuffNode left;      //Left node
        private HuffNode right;     //Right node

        private int freq;           //The frequency of the byte value
        private int byteVal;        //the byte value being stored. 

        /**
         * Full Constructor
         * Builts a HuffNode. 
         * @param b The byte value stored in the node.
         * @param f The frequency of the byte value. 
         * @param l The left Huffman Node.
         * @param r The right huffman node. 
         */
        public HuffNode(int b, int f, HuffNode l, HuffNode r)
        {
            freq = f;
            byteVal = b;
            left = l;
            right = r;   
        }
        
        /**
         * Constructor
         * Builts a HuffNode. 
         * @param b The byte value stored in the node.
         * @param f The frequency of the byte value. 
         */
        public HuffNode (int b, int f)
        {
            freq = f;
            byteVal = b;
            left = null;
            right = null;
        }

        /**
         * setL
         * Sets the left node. 
         */
        public void setL(HuffNode l)
        {
            left = l;
        }

        /**
         * setR
         * Sets the right node. 
         */
        public void setR(HuffNode r)
        {
            right = r;
        }

        /**
         * setByte
         * Sets the byte value of the node
         * @param b The byte value of the node. 
         */
        public void setByte(int b)
        {
            byteVal = b;
        }

        /**
         * setFreq
         * Sets the frequency of the byte value. 
         * @param f The Frequency of the byte value. 
         */
        public void setFreq(int f)
        {
            freq = f;
        }

        /**
         * getL
         * Returns the left value of the node
         */
        public HuffNode getL()
        {
            return left;
        }

        /**
         * getR
         * Returns the right value of the node. 
         */
        public HuffNode getR()
        {
            return right;
        }

        /**
         * getFreq
         * Returns the frequency of the node.
         */
        public int getFreq()
        {
            return freq;
        }

        /**
         * getByte
         * Returns the byte value of the node.
         */
        public int getByte()
        {
            return byteVal;
        }
    }
    
}
