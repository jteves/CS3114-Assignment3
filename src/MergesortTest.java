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
		
		pool.addMerge(sort);
		sort.upCache();
		sort.upRead();
		sort.upWrite();
		sort.getPool();
		
		assertEquals(null, pool.sendToMerge(0, 8));
		pool.sendToMerge(0, 8);
		pool.sendToMerge(8, 16);
		pool.sendToMerge(8, 16);
		byte[] hello = {1,2, 3, 4};
		pool.recieveFromMerge(0, hello);
		pool.remove();
		assertEquals(0, 0);
		sort.sort();
	}
}