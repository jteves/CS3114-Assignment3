import java.io.IOException;

import student.TestCase;

/**
 * Test for the buffer pool
 * 
 * @author Drew Williams, Jacob Teves
 * @version 4/05/2016
 */
public class BufferPoolTest extends TestCase {
    
    private BufferPool pool;
    private Mergesort sort;
    /** 
     * set up
     */
    public void setUp() {
        FileGenerator fg = new FileGenerator();
        String[] list1 = {"-c", "DrewTest.txt", "10"};
        String[] list2 = {"-b", "DrewTest.txt", "10"};
        String[] list3 = {"-a", "DrewTest.txt", "10"};
        try {
            fg.generateFile(list1);
            fg.generateFile(list2);
            fg.generateFile(list3);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        pool = new BufferPool("DrewTest.txt", 6);
        sort = new Mergesort(10 * 4096, pool);
        pool.addMerge(sort);
    }
    
    /**
     * Tests to make sure the generated file gets sorted
     */
    public void test() {
        try {
            assertFalse((new CheckFile()).checkFile("DrewTest.txt"));
        } 
        catch (Exception e1) {
            e1.printStackTrace();
        }
        sort.sort();
        sort.getPool().flush();
        
        // Test that the file is properly sorted
        try {
            assertTrue((new CheckFile()).checkFile("DrewTest.txt"));
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
//    public void test2() {
//        FileGenerator whoCares = new FileGenerator();
//        String[] list = {"-a", "test.txt", "3"};
//        try {
//            whoCares.generateFile(list);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}