import java.io.IOException;

import student.TestCase;

public class BufferPoolTest extends TestCase {
    
    BufferPool pool;
    Mergesort sort;
    /** 
     * set up
     * @throws IOException 
     */
    public void setUp() throws IOException {
        FileGenerator whoCares = new FileGenerator();
        String[] list = {"-a", "DrewTest.txt", "1000"};
        try {
            whoCares.generateFile(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pool = new BufferPool("DrewTest.txt", 5);
        sort = new Mergesort(1000 * 4096, pool);
    }
    
    public void test() {
        sort.sort();
        sort.bp.flush();
    }

}