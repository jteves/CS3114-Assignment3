import java.io.IOException;

import student.TestCase;

public class BufferPoolTest extends TestCase {
    
    BufferPool pool;
    Mergesort sort;
    /** 
     * set up
     */
    public void setUp() {
        FileGenerator whoCares = new FileGenerator();
        String[] list = {"-a", "DrewTest.txt", "10"};
        try {
            whoCares.generateFile(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pool = new BufferPool("DrewTest.txt", 6);
        sort = new Mergesort(10 * 4096, pool);
        pool.addMerge(sort);
    }
    
    public void test() {
        sort.sort();
        sort.getPool().flush();
        
        System.out.println(sort.numRead);
        System.out.println(sort.numWrite);
        System.out.println(sort.numCache);
        
        // Test that the file is properly sorted
        try {
			assertTrue((new CheckFile()).checkFile("DrewTest.txt"));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
//    public void test2() {
//    	FileGenerator whoCares = new FileGenerator();
//        String[] list = {"-a", "C:\\Users\\jacobteves\\Downloads\\test.txt", "3"};
//        try {
//            whoCares.generateFile(list);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}