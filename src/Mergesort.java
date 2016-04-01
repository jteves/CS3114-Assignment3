/**
 * { your description of the project here }
 */

/**
 * The class containing the main method, the entry point of the application.
 * 
 * @author Jacob Teves- jteves, Andrew Williams- dwill225
 * @version 3/21/2016
 * 
 */
public class Mergesort {

    private int len;
    BufferPool bp;
    
    
	/**
	 * The entry point of the application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Hello, World");
	}
	
	/**
	 * constructor 
	 * @param x the length of the data to be sorted
	 */
	public Mergesort(int x, BufferPool pool) {
	    len = x;
	    bp = pool;
	}
	
	/**
     * constructor 
     */
    public Mergesort() {
        len = 0;
    }
    
    public void sort() {
        int beg;
        int step = 8;
        byte[] arr;
        byte[] ans;
        int left;
        int right;
        while (step <= len) {
            beg = 0;
            while (beg < len) {
                arr = bp.sendToMerge(beg, beg + step);
                ans = new byte[step];
                left = 0;
                right = step / 2;
                for (int i = 0; i < step / 4; i++) {
                    if (right == step || arr[left] > arr[right]) {
                        ans[(4 * i)] = arr[left];
                        ans[(4 * i) + 1] = arr[left + 1];
                        ans[(4 * i) + 2] = arr[left + 2];
                        ans[(4 * i) + 3] = arr[left + 3];
                        left += 4;
                    }
                    else if (left == step / 2 || arr[left] < arr[right]) {
                        ans[(4 * i)] = arr[right];
                        ans[(4 * i) + 1] = arr[right + 1];
                        ans[(4 * i) + 2] = arr[right + 2];
                        ans[(4 * i) + 3] = arr[right + 3];
                        right += 4;
                    }
                    else if (arr[right + 1] > arr[left + 1]) {
                        ans[(4 * i)] = arr[right];
                        ans[(4 * i) + 1] = arr[right + 1];
                        ans[(4 * i) + 2] = arr[right + 2];
                        ans[(4 * i) + 3] = arr[right + 3];
                        right +=4;
                    }
                    else {
                        ans[(4 * i)] = arr[left];
                        ans[(4 * i) + 1] = arr[left + 1];
                        ans[(4 * i) + 2] = arr[left + 2];
                        ans[(4 * i) + 3] = arr[left + 3];
                        left += 4;
                    }
                }
                int test = ans[1];
                for (int j = 1; j < ans.length; j += 4) {
                    if (test < ans[j]) {
                        System.out.println("dang");
                    }
                    test = ans[j];
                    
                }
                bp.recieveFromMerge(beg, ans);
                beg += step;
            }
            
            step*=2;
        }
    }
}
