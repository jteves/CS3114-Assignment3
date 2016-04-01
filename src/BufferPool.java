import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


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
    RandomAccessFile raf;
    
    /**
     * A generic list constructor
     * @param x is the max list size 
     * @throws IOException 
     */
    public BufferPool(String name, int x) throws IOException {
        head = new Node(-1, null);
        last = null;
        size = 0;
        max = x;
        iteToHead();
        
        raf = new RandomAccessFile(name, "rw");
        
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
    
    public void iteNodeToHead() {
        Node temp = head;
        while (temp.next != ite) {
            temp = temp.next;
        }
        temp.next = ite.next;
        ite.next = head.next;
        head.next = ite;
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
        while(ite.next != null) {
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
            file.seek(x * 4096);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            file.readFully(temp);
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
            file.seek(4096 * x);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            file.write(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void flush() {
        byte[] temp;
        iteToHead();
        iteNext();
        while (ite != null) {
            temp = ite.data;
            try {
                raf.seek(4096 * ite.pos);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                raf.write(temp);
            } catch (IOException e) {
                e.printStackTrace();
            }
            iteNext();
        }
    }
    
    
    public byte[] sendToMerge(int beg, int end) {
        int dif = end - beg;
        byte[] temp = new byte[dif];
        int bytesRead = 0;
        while (bytesRead < temp.length) {
            if (isContained((beg + bytesRead) / 4096)) {
                int i = (beg + bytesRead) % 4096;
                byte[] cur = ite.data;
                while (bytesRead < temp.length && i < 4096) {
                    temp[bytesRead] = cur[i];
                    temp[bytesRead + 1] = cur[i + 1];
                    temp[bytesRead + 2] = cur[i + 2];
                    temp[bytesRead + 3] = cur[i + 3];
                    bytesRead += 4;
                    i+=4;
                }
                iteNodeToHead();
            }
            else {
                if (isFull()){
                    write(raf, last.pos);
                    remove();
                }
                read(raf, (beg + bytesRead) / 4096);
                int i = (beg + bytesRead) % 4096;
                byte[] cur = head.next.data;
                while (bytesRead != temp.length && i < 4096) {
                    temp[bytesRead] = cur[i];
                    temp[bytesRead + 1] = cur[i + 1];
                    temp[bytesRead + 2] = cur[i + 2];
                    temp[bytesRead + 3] = cur[i + 3];
                    bytesRead += 4;
                    i+=4;
                }
            }
        }
        return temp;
    }
    
    public void recieveFromMerge(int beg, byte[] arr) {
        int dif = arr.length;
        int bytesRead = 0;
        while (bytesRead < dif) {
            if (isContained((beg + bytesRead) / 4096)) {
                int i = (beg + bytesRead) % 4096;
                byte[] cur = ite.data;
                while (bytesRead < dif && i < 4096) {
                    cur[i] = arr[bytesRead];
                    cur[i + 1] = arr[bytesRead + 1];
                    cur[i + 2] = arr[bytesRead + 2];
                    cur[i + 3] = arr[bytesRead + 3];
                    bytesRead += 4;
                    i+=4;
                }
                iteNodeToHead();
                head.next.data = cur;
            }
            else {
                if (isFull()){
                    write(raf, last.pos);
                    remove();
                }
                read(raf, (beg + bytesRead) / 4096);
                int i = (beg + bytesRead) % 4096;
                byte[] cur = head.next.data;
                while (bytesRead < dif && i < 4096) {
                    cur[i] = arr[bytesRead];
                    cur[i + 1] = arr[bytesRead + 1];
                    cur[i + 2] = arr[bytesRead + 2];
                    cur[i + 3] = arr[bytesRead + 3];
                    bytesRead += 4;
                    i+=4;
                }
                head.next.data = cur;
            }
        }
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


