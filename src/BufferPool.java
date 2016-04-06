import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * Linked list of byte arrays
 * 
 * The buffer pool is used to cache  reads from the file
 * 
 * handles all of the reads and writes to between the file
 * and mergesort
 * 
 * @author Drew Williams, Jacob Teves
 * @version 2/25/2016
 */
public class BufferPool  {

    /**
     * The head node of the list
     * value is always null
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
    /**
     * the last node of the list
     */
    private Node last;
    /**
     * This is the max size that the linked
     * list is allowed to grow to
     */
    private int max;
    /**
     * the file that we will read from
     */
    private RandomAccessFile raf;
    /**
     * The mergesort that will be used to sort the file
     */
    private Mergesort merge;
    
    /**
     * constructor
     * @param x is the max list size 
     * @param name the name of the file
     * 
     * opens the file and sets the max size of the 
     * list of buffers 
     */
    public BufferPool(String name, int x) {
        head = new Node(-1, null); // head node
        last = null; //there are no values yet so last
        // is null
        size = 0; //no nodes yet
        max = x; // sets max
        iteToHead(); //moves the iterator to the head node
        
        try {
            raf = new RandomAccessFile(name, "rw");
            // opens the file
        } 
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * This method should be called after
     * creating a mergesort
     * 
     * The method is needed to send cache hit,
     * read, and write statistics back to the 
     * mergesort
     * 
     * @param sort is the mergesort
     */
    public void addMerge(Mergesort sort) {
        merge = sort; 
    }
    
    /**
     * Inserts a node into the list containing
     * a byte array
     * 
     * @param loc is the location of the data in the
     * file
     * @param x is the byte array to be stored
     */
    public void insert(int loc, byte[] x) {
    	//creates a new node
        Node node = new Node(loc, x); 

        // inserts at the beginning
        node.next = head.next; 
        head.next = node;
        if (size == 0) {
            last = node; //sets the value for last
        }
        size++; // increase the size of the list
    }
    
    
    
    /**
     * Removes the last buffer from the list
     * 
     * @return the byte array that was removed
     */
    public byte[] remove() {
        byte[] ans;
        iteToHead();
        // Traverse to the second to last node
        while (ite.next != last) {
            iteNext();
        }
        last = ite; //sets last value to new
        // last node
        ans = ite.next.data; // returns this
        ite.next = null; //last node has no next node
        size--; // decrements size
        return ans;
    }
    
    /**
     * Sets the current node to the head
     */
    public void iteToHead() {
        ite = head; //sets the iterator to the head
    }
    
    /**
     * moves the node pointed to by the iterator
     * to the front of the list
     */
    public void iteNodeToHead() {
    	//used to traverse through the list
        Node temp = head;
        while (temp.next != ite) {
            temp = temp.next; // traverses
        }
        temp.next = ite.next; // setting values
        ite.next = head.next; // to move the node
        head.next = ite;      // to the front
    }
    
    /**
     * Sets the current node to the next node
     */
    public void iteNext() {
    	// moves the iterator to the next node
        ite = ite.next;
    }
    
    /**
     * 
     * @return if the list is full
     */
    public boolean isFull() {
        return size >= max;
    }
    
    /**
     * 
     * @param x is the block of bytes being 
     * looked for
     * @return if the block of bytes
     *  is contained in the list
     */
    public boolean isContained(int x) {
        iteToHead();
        boolean ans = false; //value to be 
        //returned
        while (ite.next != null) {
            iteNext();
            if (ite.pos == x) {
                ans = true; //block is found
                break;
            }
        }
        return ans;
    }
    
    /**
     * reads a block of data from the file
     * 
     * 
     * @param file the file to be read
     * @param x the block to be read from the file
     */
    public void read(RandomAccessFile file, int x) {
    	//data being read will go here
        byte[] temp = new byte[4096]; 
        try {
        	//moves file pointer to desired location
            file.seek(x * 4096); 
        } 
        catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
        	//reads from file to byte array
            file.readFully(temp); 
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        insert(x, temp); //inserts the data to the list
        merge.upRead(); // increments the read counter
    }
    
    /**
     * Writes bytes to the file from the list
     * 
     * @param file the file to be written to
     * @param x the block to write to in the file
     */
    public void write(RandomAccessFile file, int x) {
        byte[] temp; //will hold data to be written
        isContained(x); //moves ite to proper node
        temp = ite.data; //grabs data
        try {
            file.seek(4096 * x); // moves file pointer
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        try {
            file.write(temp); // writes bytes to file
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        merge.upWrite(); // increments byte counter
    }
    
    /**
     * writes all of the bytes in the buffers back
     * to the file
     * 
     * this method is meant to be called after the 
     * sort is over
     */
    public void flush() {
        byte[] temp;
        iteToHead();
        iteNext();
        while (ite != null) {
            temp = ite.data;
            try {
                raf.seek(4096 * ite.pos); // moves 
                //file pointer
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
            try {
                raf.write(temp); // writes bytes
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
            iteNext();
            merge.upWrite(); // increments write 
            //count
        }
    }
    
    /**
     * returns a section of bytes from the file
     * 
     * This method will be called by the merge sort
     * it handles all reads and writes that need to 
     * be done and returns an array of bytes of desired
     * length
     * 
     * @param beg the beginning location of bytes
     * inclusive
     * @param end the end location of the bytes
     * exclusive
     * @return the section of bytes between beg and 
     * end
     */
    public byte[] sendToMerge(int beg, int end) {
        int dif = end - beg; // length of byte array
        byte[] temp = new byte[dif]; // array to be returned
        int bytesRead = 0; //keeps track of bytes read
        while (bytesRead < temp.length) {
            if (isContained((beg + bytesRead) / 4096)) {
                int i = (beg + bytesRead) % 4096; //gives the
                //proper byte location in the buffer
                byte[] cur = ite.data; // bytes in the buffer
                while (bytesRead < temp.length && i < 4096) {
                    //reads the bytes
                    temp[bytesRead] = cur[i];
                    temp[bytesRead + 1] = cur[i + 1];
                    temp[bytesRead + 2] = cur[i + 2];
                    temp[bytesRead + 3] = cur[i + 3];
                    bytesRead += 4;
                    i += 4;
                    merge.upCache(); // increments cache
                }
                iteNodeToHead(); // resents ite to head
            }
            else {
                if (isFull()) {
                    //removes a node if the list is 
                    //full because a new one will be
                    // read into the list
                    write(raf, last.pos);
                    remove();
                }
                read(raf, (beg + bytesRead) / 4096); //finds
                //the proper block of the file
                int i = (beg + bytesRead) % 4096; // begins 
                // writing from location i in the byte array
                byte[] cur = head.next.data; //array to be 
                //read from
                while (bytesRead != temp.length && i < 4096) {
                    //writes bytes to the answer array
                    temp[bytesRead] = cur[i];
                    temp[bytesRead + 1] = cur[i + 1];
                    temp[bytesRead + 2] = cur[i + 2];
                    temp[bytesRead + 3] = cur[i + 3];
                    bytesRead += 4;
                    i += 4;
                }
            }
        }
        return temp;
    }
    
    /**
     * writes the values in the byte array back into
     * the buffers and to the file as needed
     * 
     * This method will be called by the merge sort
     * it handles all reads and writes that need to 
     * be done and receives all values from the merge
     * sort properly
     * 
     * @param beg the beginning location of bytes
     * inclusive
     * @param arr the array of bytes to be received
     * 
     */
    public void recieveFromMerge(int beg, byte[] arr) {
        int dif = arr.length; // length of the array
        int bytesRead = 0; // keeps track of bytes written
        while (bytesRead < dif) {
            if (isContained((beg + bytesRead) / 4096)) {
                int i = (beg + bytesRead) % 4096; //finds the 
                // proper position in the buffer
                byte[] cur = ite.data; // byte array to be written to
                while (bytesRead < dif && i < 4096) {
                    // writes to buffer
                    cur[i] = arr[bytesRead];
                    cur[i + 1] = arr[bytesRead + 1];
                    cur[i + 2] = arr[bytesRead + 2];
                    cur[i + 3] = arr[bytesRead + 3];
                    bytesRead += 4;
                    i += 4;
                }
                iteNodeToHead();
                head.next.data = cur; // new value for data
            }
            else {
                if (isFull()) {
                    //writes to file and removes 
                    //node because a new byte array must
                    // be read
                    write(raf, last.pos);
                    remove();
                }
                read(raf, (beg + bytesRead) / 4096); // reads bytes
                // gets beginning of write location in the array
                int i = (beg + bytesRead) % 4096; 
                byte[] cur = head.next.data; //gets byte array
                while (bytesRead < dif && i < 4096) {
                    // writes to array
                    cur[i] = arr[bytesRead];
                    cur[i + 1] = arr[bytesRead + 1];
                    cur[i + 2] = arr[bytesRead + 2];
                    cur[i + 3] = arr[bytesRead + 3];
                    bytesRead += 4;
                    i += 4;
                }
                //sets data
                head.next.data = cur;
            }
        }
    }

    /**
     * Getter for the random access file of the pool
     * @return the file
     */
     public RandomAccessFile getFile() {
         return raf;
     }
     
    /**
     * The Nodes that contain the buffer data and next node
     * @author Drew Williams, Jacob Teves
     * @version 2/25/2016
     */
    private class Node {
        
        private Node next;
        private byte[] data;
        private int pos;
        
        /**
         * Node that stores a byte array and loc index
         * @param loc the location of the node
         * @param bytes the bytes data of the node
         */
        private Node(int loc, byte[] bytes) {
            data = bytes;
            pos = loc;
        }
    }
}


