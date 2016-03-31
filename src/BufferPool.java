import java.io.IOException;
import java.io.RandomAccessFile;
//FIX READ METHOD

/**
 * The list that is used to get the names at each leafnode of the tree
 * @author Drew Williams, Jacob Teves
 * @version 2/25/2016
 */
public class BufferPool  {

    /**
     * The head node of the String List
     */
    private Node head;
    
    /**
     * The current node of the list
     */
    private Node ite;
    
    /**
     * The size of the list
     */
    private int size;
    private Node last;
    private int max;
    
    /**
     * A generic list constructor
     * @param x is the max list size
     */
    public BufferPool(int x) {
        head = new Node(-1, null);
        last = null;
        size = 0;
        max = x;
        iteToHead();
    }
    
    /**
     * Inserts a string into the list
     * 
     * @param s The string data inserted into the list
     */
    public void insert(int loc, byte[] x) {
        Node node = new Node(loc, x);
        node.next = head.next;
        head.next = node;
        if (size == 0) {
            last = node;
        }
        size++;
    }
    
    
    
    /**
     * Removes the specified string in the list if it exists
     * @param s The specified string value
     * @return true if the string was removed from the list
     */
    public byte[] remove() {
        byte[] ans;
        iteToHead();
        while(ite.next != last) {
            iteNext();
        }
        last = ite;
        ans = ite.next.data;
        ite.next = null;
        size--;
        return ans;
    }
    
    /**
     * Sets the current node to the head
     */
    public void iteToHead() {
        ite = head;
    }
    
    /**
     * Sets the current node to the next node
     */
    public void iteNext() {
        ite = ite.next;
    }
    
    
    public boolean isFull() {
        return size >= max;
    }
    
    /**
     * 
     * @param x the position in the file that we are
     * lookin for
     * @return if the value is contained
     */
    public boolean isContained(int x) {
        iteToHead();
        boolean ans = false;
        while(ite != last) {
            iteNext();
            if (ite.pos == x) {
                ans = true;
                break;
            }
        }
        return ans;
    }
    
    public void read(RandomAccessFile file, int x) {
        byte[] temp = new byte[4096];
        try {
            file.read(temp, x * 4096, 4096);
        } catch (IOException e) {
            e.printStackTrace();
        }
        insert(x, temp);
    }
    
    public void write(RandomAccessFile file, int x) {
        byte[] temp;
        isContained(x);
        temp = ite.data;
        try {
            file.write(temp, x * 4096, 4096);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //FIX THIS NOT FINISHED
    public Byte[] sendToMerge(int beg, int end) {
        int dif = end - beg;
        Byte[] temp = new Byte[dif];
        int bytesRead = 0;
        while (bytesRead != temp.length) {
            if (isContained((beg + bytesRead) / 4096)) {
                int i = (bytesRead + beg) % 4096;
                while (i < 4096 && i < dif) {
                    
                }
            }
        }
        return null;
    }
    
    /**
     * The Nodes that contain the string data and next node
     * @author Drew Williams, Jacob Teves
     * @version 2/25/2016
     */
    private class Node {
        
        private Node next;
        private byte[] data;
        private int pos;
        
        /**
         * @param s the string data the node contains
         */
        private Node(int loc, byte[] bytes) {
            data = bytes;
            pos = loc;
        }
    }
}


