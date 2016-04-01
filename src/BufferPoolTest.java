import java.io.IOException;

import student.TestCase;

public class BufferPoolTest extends TestCase {
    
    BufferPool pool;
    /** 
     * set up
     * @throws IOException 
     */
    public void setUp() throws IOException {
        FileGenerator whoCares = new FileGenerator();
        String[] list = {"-a", "DrewTest.txt", "5"};
        try {
            whoCares.generateFile(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pool = new BufferPool("DrewTest.txt", 3);
    }
    
    public void test() {
        pool.read(pool.raf, 4);
        pool.read(pool.raf, 0);
        pool.read(pool.raf, 4);
        pool.write(pool.raf, 1);
        pool.remove();
    }

}
