import java.util.Arrays;

public class Sorting {

	public static void main(String[] args) {
		
		int n = 5;
		double[] array = new double[n];
		
		for (int i = 0; i < array.length; i++)
			array[i] = Math.random()*20;
		
		double[] array_1 = new double[n];
		System.arraycopy(array, 0, array_1, 0, n);
		
		/*
		// Test the insertion sorting method
		System.out.println(Arrays.toString(array) +"\n");
		long start = System.currentTimeMillis();
		InsertionSort(array);
		long end = System.currentTimeMillis();
		System.out.println(Arrays.toString(array));
		System.out.println("\nProcessing time: " + (end - start) + " ms \n"); */
		
		
		// Test the merge sorting method
		System.out.println(Arrays.toString(array_1));
		long start_1 = System.currentTimeMillis();
		mergeSort(array_1);
		long end_1 = System.currentTimeMillis();
		System.out.println(Arrays.toString(array_1));
		System.out.println("\nProcessing time: " + (end_1 - start_1) + " ms\n");

	}
	

	public static void InsertionSort (double[] array) {
		InsertSort(array, array.length, 0);
	}

	private static void InsertSort(double[] array, int high, int low) {
		for (int i = low; i < high; i++) { // Tranverse over the whole array
			for (int j = i; j > low; j--) { // Traverse backwards in the sub array
				if (array[j] < array[j-1]) { // Swap until the value is at the right position assuming the sub array is sorted
					double holder = array[j];
					array[j] = array[j-1];
					array[j-1] = holder;
				}

			}
		}

	}


	public static void mergeSort(double[] array) {
		double[] holder = new double[array.length];
		mergeSort(array, holder, 0, array.length - 1);
	}

	private static void mergeSort(double[] array, double[] holder, int low, int high) {
		// Base case : low == high (equivalent to the length-1 subarray)
		if (low < high) {
			// Split the array into 2 sub array
			int mid = (high + low)/2;
			// int mid = (high+1)/2;

			//Sort the first half
			mergeSort(array, holder, low, mid);

			// Sort the second half
			mergeSort(array, holder, mid + 1, high);

			//Merge the sub array
			merge(array, holder, high, mid, low);
		}

	}

	
	private static void merge(double[] array, double[] holder, int high, int mid, int low) {
		/* 
		The mid variable is needed to mark the endpoint of sub array
		*/

		// copy to the temporary array for comparison
        for (int index = low; index <= high; index++) {
            holder[index] = array[index]; 
        }

		// merge (copy) back to the original array
		// i == pointer of first array
		// j == pointer of second array
		int i = low, j = mid+1;
        for (int index = low; index <= high; index++) {
			/* Adding the last element
			   Restriction: After comparison, there is at least 1 element left in one of the sub arrays.
			   The number of positions yet left to filled == the number of elements left in sub array */

			// if the elemens are in the first half array
			if (i > mid) {
				array[index] = holder[j++];
				continue;
			}              
				
			// if the elements are in the second half array
			if (j > high) {
				array[index] = holder[i++];
				continue;
			}               
				

			//Comparing the element from the "sub array"
			if (array[j] < holder[i])
				array[index] = holder[j++];
			else
				array[index] = holder[i++];
		}
				
				
		
				
	}
		

}
