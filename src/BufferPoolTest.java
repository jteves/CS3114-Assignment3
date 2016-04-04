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
        String[] list = {"-a", "DrewTest.txt", "3"};
        try {
            whoCares.generateFile(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pool = new BufferPool("DrewTest.txt", 5);
        sort = new Mergesort(3 * 4096, pool);
    }
    
    public void test() {
        sort.sort();
        sort.getPool().flush();
        
        // Test that the file is properly sorted
        try {
			assertTrue((new CheckFile()).checkFile("DrewTest.txt"));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void test2() {
    	FileGenerator whoCares = new FileGenerator();
        String[] list = {"-a", "C:\\Users\\jacobteves\\Downloads\\test.txt", "3"};
        try {
            whoCares.generateFile(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}