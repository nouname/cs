public class W {
	Integer[] A, B, C;
	 
	public static int lowerBound(Integer[] array, int length, int value) {
		int low = 0;
		int high = length;
		while (low < high) {
			final int mid = (low + high) / 2;
			if (value <= array[mid]) {
				high = mid;
			} else {
				low = mid + 1;
			}
		}
		return low;
	}
	
	public W(Integer[] A, Integer[] B, Integer[] C) {
		this.A = A;
		this.B = B;
		this.C = C;
	}
}
