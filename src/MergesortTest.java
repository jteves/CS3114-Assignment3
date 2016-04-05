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
	
	public void testInit() {
		BufferPool pool = new BufferPool("DrewTest.txt", 10);
		pool.getFile();
		pool.flush();
		Mergesort sort = new Mergesort(1000 * 4096, pool);
		Mergesort sort2 = new Mergesort();
		pool.addMerge(sort);
		sort.upCache();
		sort.upRead();
		sort.upWrite();
		sort.getPool();
		pool.read(null, 10);
		pool.write(null, 10);
		pool.sendToMerge(0, 50);
		byte[] hello = {1,2, 3, 4};
		pool.recieveFromMerge(0, hello);
		pool.remove();
	}
}