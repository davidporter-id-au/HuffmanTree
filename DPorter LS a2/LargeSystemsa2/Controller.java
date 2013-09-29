import java.io.*;
import java.util.*;
/**
 * Controller
 * The operational part of the Huffman Tree, performs the controlling
 * functions. 
 * 
 * @author David Porter
 * @version 1
 */
public class Controller {

   /**
    * main
    * An example of a use for the HuffmanTree Class
    */
    public static void main (String [] args)
    {
        HuffmanTree huffmanTree = null; //Create a new huffman tree
        
        try
        {
            huffmanTree = new HuffmanTree(args[0], args[1]); //the main huffman tree.
        }
        catch (IOException e)
        {
            System.out.println("File not found");
        }
        
        
        if(huffmanTree != null)
        {
            Map map = huffmanTree.getMap(); //return the mapping of the characters. 
            
            //Display the mappings on screen:
            
            System.out.println(huffmanTree); //Display mappings
            
            
            System.out.println(huffmanTree.printByteFreq());
        }
    }
}