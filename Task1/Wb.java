public class Wb extends W {
	
	public Wb(Integer[] A, Integer[] B, Integer[] C) {
		super(A, B, C);
	}
	
	public static Integer[] compute(Integer[] A, Integer[] B, Integer[] C)  {
		for (int i = 0; i < B.length; i++) {
			int x = B[i];
			var a = lowerBound(A, A.length, x);
			var b = lowerBound(B, B.length, x);
			C[a + b] = x;
		}
		return C;
	}
}
