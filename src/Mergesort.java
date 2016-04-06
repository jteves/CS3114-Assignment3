// On my honor:
//
// - I have not used source code obtained from another student,
// or any other unauthorized source, either modified or
// unmodified.
//
// - All source code and documentation used in my program is
// either my original work, or was derived by me from the
// source code published in the textbook for this course.
//
// - I have not discussed coding details about this project with
// anyone other than my partner (in the case of a joint
// submission), instructor, ACM/UPE tutors or the TAs assigned
// to this course. I understand that I may discuss the concepts
// of this program with other students, and that another student
// may help me debug my program so long as neither of us writes
// anything during the discussion or modifies any computer file
// during the discussion. I have violated neither the spirit nor
// letter of this restriction.

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * The class containing the main method, 
 * the entry point of the application.
 * 
 * also the contains the external sort object
 * 
 * Compiler: javac 1.8.0_20
 * OS: Windows 10 
 * Date Completed: 04/06/2016
 * 
 * @author Jacob Teves- jteves, Andrew Williams- dwill225
 * @version 3/21/2016
 * 
 */
public class Mergesort {

    /**
     * length of file to be sorted
     */
    private int len;
    /**
     *  cache hits
     */
    private int numCache;
    /**
     * number of file reads
     */
    private int numRead;
    /**
     * number of file writes
     */
    private int numWrite;
    /**
     * the bufferpool
     */
    private BufferPool bp;
    
    
    /**
     * Main method for the program
     * 
     * takes an unsorted file name and sorts the file
     * using a mergesort
     * 
     * uses a buffer pool to communicate between the file 
     * and the external mergesort
     * 
     * @param args should be 3 arguments
     * 1. the input file
     * 2. the number of buffers to be used
     * 3. the output stats file
     */
    public static void main(String[] args) {
        String unsortedFile = args[0]; // file name

        int numBuf = Integer.parseInt(args[1]); // number of
        //buffers
        int fLen = (int) (new File(unsortedFile)).length(); 
        // length of unsorted file
        
        // creates the buffer pool
        BufferPool pool = new BufferPool(unsortedFile, numBuf);
        //creats this sort object
        Mergesort sort = new Mergesort(fLen, pool);
        // adds the mergesort to the pool
        pool.addMerge(sort);

        long start = System.currentTimeMillis(); // start time
        sort.sort(); // sorts the file
        sort.getPool().flush(); // flushes the buffers
        
        // gets total sort time
        long sortTime = System.currentTimeMillis() - start;
        try {
            pool.getFile().close(); //closes the file
        } 
        catch (IOException e1) {
            e1.printStackTrace();
        }
        
        try {
            File sFile = new File(args[2]); //stat file
            
            //if file doesn't exists, then create it
            if (!sFile.exists()) {
                sFile.createNewFile();
            }
            //creates writer to write to file
            FileWriter fw = new FileWriter(sFile.getPath(), true);
            //creates buffered writer to write to file
            BufferedWriter bw = new BufferedWriter(fw);
            //wirtes all of the info to the file
            bw.write("Unsorted File: " + unsortedFile);
            bw.newLine();
            bw.write("Cache Hits: " + sort.numCache);
            bw.newLine();
            bw.write("Disk Reads: " + sort.numRead);
            bw.newLine();
            bw.write("Disk Writes: " + sort.numWrite);
            bw.newLine();
            bw.write("Time: " + sortTime);
            bw.newLine();
            bw.newLine();
            
            //closes the file
            bw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * constructor  creates a new mergesort
     * @param x the length of the data to be sorted
     * @param pool is the buffer pool that handles
     * all of the reads and writes from the file
     */
    public Mergesort(int x, BufferPool pool) {
        len = x; //sets length of file
        bp = pool; // sets buffer pool
        
        //initializes stats
        numRead = 0;
        numWrite = 0;
        numCache = 0;
    }
    
    /**
     * sorts the file pointed to by the bufferpool 
     * by using a merge sort
     * 
     * begins with a step size of 8 and increases it every 
     * pass through the file until the file has been sorteed
     */
    public void sort() {
        int beg; // beginning of data
        int temp = 0; // temp step value
        int step = 8; //size of 2 elements
        byte[] arr; //array to be sorted 
        //on one run through the loop
        byte[] ans; // sorted array for one 
        //run through the loop
        int left; // left value being evaluated
        int right; // right value being evaluated
        while (step <= 2 * len) {
            beg = 0; // starts at begin of file
            while (beg < len) {
                temp = step; //saves step value
                if (beg + step > len) {
                    step = len - beg; // protects from
                    //out of bounds array operations
                }
                //gets elements to be sorted this trip through
                // the file
                arr = bp.sendToMerge(beg, beg + step);
                ans = new byte[step]; // will become the sorted
                // array for this trip through the array
                left = 0; // left starts at 0
                right = temp / 2; // right is half way through
                // the array
                
                //START: this section is to protect from bytes
                //       being misinterpreted as negative
                //       integers
                int x = 0x0000ffff;
                int r0 = 0;
                int r1 = 0;
                int l0 = 0;
                int l1 = 0;
                for (int i = 0; i < step / 4; i++) {
                    if (right >= step) {
                        r0 = 0x0fffffff;
                        r1 = 0x0fffffff;
                    }
                    else {
                        r0 = arr[right] & x;
                        r1 = arr[right + 1] & x;
                    }
                    if (left >= temp / 2) {
                        l0 = 0x0fffffff;
                        l1 = 0x0fffffff;
                    }
                    else {
                        l0 = arr[left] & x;
                        l1 = arr[left + 1] & x;
                    }
                //END: end of byte corrections
                    
                    if (right >= step || l0 < r0) {
                        //writes bytes if the left value
                        // is bigger
                        ans[(4 * i)] = arr[left];
                        ans[(4 * i) + 1] = arr[left + 1];
                        ans[(4 * i) + 2] = arr[left + 2];
                        ans[(4 * i) + 3] = arr[left + 3];
                        left += 4;
                    }
                    else if (left >= temp / 2 || l0 > r0) {
                        //writes bytes if the right value is 
                        // bigger
                        ans[(4 * i)] = arr[right];
                        ans[(4 * i) + 1] = arr[right + 1];
                        ans[(4 * i) + 2] = arr[right + 2];
                        ans[(4 * i) + 3] = arr[right + 3];
                        right += 4;
                    }
                    // runs if l0 and r0 are equal
                    else if (l1 > r1) { 
                        //writes bytes if left is bigger
                        ans[(4 * i)] = arr[right];
                        ans[(4 * i) + 1] = arr[right + 1];
                        ans[(4 * i) + 2] = arr[right + 2];
                        ans[(4 * i) + 3] = arr[right + 3];
                        right += 4;
                    }
                    else {
                        //writes bytes if right is bigger
                        ans[(4 * i)] = arr[left];
                        ans[(4 * i) + 1] = arr[left + 1];
                        ans[(4 * i) + 2] = arr[left + 2];
                        ans[(4 * i) + 3] = arr[left + 3];
                        left += 4;
                    }
                }
                bp.recieveFromMerge(beg, ans); // sends the 
                //bytes back to the buffer
                beg += temp;  // moves to next section of
                // the file
            }
            step = temp * 2; //doubles step team for nest 
            // time through the loop
        }
    }
    
    /**
     * For testing purposes
     * @return the buffer pool
     */
    public BufferPool getPool() {
        return bp;
    }
    
    /**
     * increments numCache
     */
    public void upCache() {
        numCache++;
    }
    
    /**
     * increments numRead
     */
    public void upRead() {
        numRead++;
    }
    
    /**
     * increments numWrite
     */
    public void upWrite() {
        numWrite++;
    }
    
    /**
     * gets numCache
     * @return the number of caches
     */
    public int numCache() {
        return numCache;
    }
    
    /**
     * gets numRead
     * @return the number of reads from the disk
     */
    public int numRead() {
        return numRead;
    }
    
    /**
     * gets numWrite
     * @return the number of writes to the disk
     */
    public int numWrite() {
        return numWrite;
    }
}
