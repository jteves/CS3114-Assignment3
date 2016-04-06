import java.io.IOException;

import student.TestCase;

/**
 * @author CS3114 staff
 * @version 1
 */

public class MergesortTest 
    extends TestCase {
    
    /**
     * This method sets up the tests that follow.
     */
    public void setUp() {
        // no op
    }
    
    /**
     * Tests the initial states of the program
     */
    public void testInit() {
        BufferPool pool = new BufferPool("DrewTest.txt", 10);
        pool.getFile();
        pool.flush();
        Mergesort sort = new Mergesort(1000 * 4096, pool);
        
        pool.addMerge(sort);
        sort.upCache();
        sort.upRead();
        sort.upWrite();
        sort.getPool();

        assertEquals(8, pool.sendToMerge(0, 8).length);
        assertEquals(16, pool.sendToMerge(0, 16).length);
        assertEquals(24, pool.sendToMerge(0, 24).length);
        assertEquals(32, pool.sendToMerge(0, 32).length);
        assertEquals(8, pool.sendToMerge(8, 16).length);
        assertEquals(8, pool.sendToMerge(16, 24).length);
        assertEquals(8, pool.sendToMerge(24, 32).length);
        assertEquals(4096, pool.sendToMerge(0, 4096).length);
        
        byte[] hello = {1, 2, 3, 4};
        pool.recieveFromMerge(0, hello);
        pool.remove();
        assertEquals(0, 0);
        sort.sort();

        assertEquals(8195049, sort.numCache());
        assertEquals(26986, sort.numRead());
        assertEquals(26975, sort.numWrite());
    }
    
    /**
     * Test the main method of the Mergesort class
     */
    public void testMain() {
        String[] list = {"-b", "test.txt", "10"};        
        try {
            (new FileGenerator()).generateFile(list);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }

        String[] args = {"test.txt", "3", "stats.txt"};
        String[] args2 = {"DrewTest.txt", "3", "stats2.txt"};
        Mergesort.main(args);
        Mergesort.main(args2);
        
        try {
            assertTrue((new CheckFile()).checkFile("test.txt"));
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}