import java.io.*;
import java.awt.*;
import javax.swing.*;

/**
 * ByteViewer
 *
 * This program displays the byte data in a file as bit patterns, one byte per line.
 *
 * @author PAS
 * @version 5/10/11
 */

public class GraphicByteViewer extends JFrame {
    private JTextPane charView, byteView;
    private static final int VIEW_WIDTH = 400;
    private static final int VIEW_HEIGHT = 500;
    private static final Dimension SIZE = new Dimension(VIEW_WIDTH*2+40,VIEW_HEIGHT+20);
    
    /**
     * Construct a new viewer frame for the given file
     * 
     * @param f file to view
     */
    public GraphicByteViewer(File f) {
        super("Byte Viewer");
        
        this.setSize(SIZE);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
        
        charView = new JTextPane();
        byteView = new JTextPane();
        
        charView.setPreferredSize(new Dimension(VIEW_WIDTH, VIEW_HEIGHT));
        byteView.setPreferredSize(new Dimension(VIEW_WIDTH, VIEW_HEIGHT));
        
        charView.setText(showFormat(new String(readBytes(f))));
        byteView.setText(bytesToString(readBytes(f)));
        
        add(new JScrollPane(charView), BorderLayout.WEST);
        add(new JScrollPane(byteView), BorderLayout.EAST);
        validate();
    }
    
    /**
     * Read the contents of a file as a byte array.
     * Note: reads the whole file in one go.  This code
     * is for demonstration purposes only. This won't 
     * work for large files.
     * 
     * @param f the file to read
     * @return contents of f as array of bytes
     */
    private byte [] readBytes(File f) {
        try {
        FileInputStream fi = new FileInputStream(f);
        byte [] aa = new byte[(int)(f.length())];
        if (fi.read(aa) == aa.length)
            return aa;
        } catch (IOException e) { e.printStackTrace(); }
        return null;
    }
    
    /**
     * Convert a byte array into a string representing each byte in the array on a line
     *
     * @param a input byte array to display
     * @return String representation of a
     */
    private static String bytesToString(byte [] a) {
        String s = "";
        for (byte b : a) {
            for(int i = 0; i < 8; i++) {
                if ((b&128) == 0)
                    s = s + "0";
                else
                    s = s + "1";
                b <<= 1;
            }
            s += "\n";
        }

        return s;
    }

    /**
     * Creates a new string showing whitespace formatting characters visibly
     * 
     * @param s string to format
     * @return formatted string
     */
    private static String showFormat(String s) {
        // replace spaces with hollow box (U+25A1)
        // replace tabs with filled box (U+25A0)
        return s.replace(' ','\u25A1').replace('\t','\u25A0');
    }
    
    // The main program uses a file chooser to prompt for a file
    // to display, then creates the viewer frame for that file.
    public static void main(String [] args) {
        JFileChooser chooser = new JFileChooser();
        int ret = chooser.showOpenDialog(null);
        
        if (ret == JFileChooser.APPROVE_OPTION)
            new GraphicByteViewer(chooser.getSelectedFile());
    }
   
}
