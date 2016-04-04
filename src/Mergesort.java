import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

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
    private int numCache;
    private int numRead;
    private int numWrite;
    
    BufferPool bp;
    
    
	/**
	 * The entry point of the application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String unsortedFile = args[0];

		int numBuf = Integer.parseInt(args[1]);
		int fLen = (int) (new File(unsortedFile)).length();
		
        BufferPool pool = new BufferPool(unsortedFile, numBuf);
        Mergesort sort = new Mergesort(fLen, pool);

		long start = System.currentTimeMillis();
        sort.sort();
        sort.getPool().flush();
		long sortTime = System.currentTimeMillis() - start;
        
		try {
    		File sFile = new File(args[2]);
    		
    		//if file doesn't exists, then create it
    		if(!sFile.exists()){
    			sFile.createNewFile();
    		}
    		FileWriter fw = new FileWriter(sFile.getName(),true);
	        BufferedWriter bw = new BufferedWriter(fw);
	        bw.write("Unsorted File: " + unsortedFile + "\n");
	        bw.write("Cache Hits: " + sort.numCache + "\n");
	        bw.write("Disk Reads: " + sort.numRead + "\n");
	        bw.write("Disk Writes: " + sort.numWrite + "\n");
	        bw.write("Time: " + sortTime + "\n");
	        
	        bw.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
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
        int temp = 0;
        int step = 8;
        byte[] arr;
        byte[] ans;
        int left;
        int right;
        while (step <= 2 * len) {
            beg = 0;            
            while (beg < len) {
            	temp = step;
            	if (beg + step > len) {
            		step = len - beg;
            	}
            	
                arr = bp.sendToMerge(beg, beg + step);
                ans = new byte[step];
                left = 0;
                right = temp / 2;
                for (int i = 0; i < step / 4; i++) {

                    if (right >= step || arr[left] < arr[right]) {
                        ans[(4 * i)] = arr[left];
                        ans[(4 * i) + 1] = arr[left + 1];
                        ans[(4 * i) + 2] = arr[left + 2];
                        ans[(4 * i) + 3] = arr[left + 3];
                        left += 4;
                    }
                    else if (left >= temp / 2 || arr[left] > arr[right]) {
                        ans[(4 * i)] = arr[right];
                        ans[(4 * i) + 1] = arr[right + 1];
                        ans[(4 * i) + 2] = arr[right + 2];
                        ans[(4 * i) + 3] = arr[right + 3];
                        right += 4;
                    }
                    else if (arr[left + 1] >= arr[right + 1]) {
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
                bp.recieveFromMerge(beg, ans);
                beg += temp;
            }
            step = temp * 2;
        }
    }
    
    /**
     * For testing purposes
     * @return
     */
    public BufferPool getPool() {
    	return bp;
    }
}
