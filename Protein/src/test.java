import Jama.Matrix;



public class test {
	
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int[] arr = {5 ,5 ,5, 3 ,3, 1 ,2 ,8,9};
		String str = new String("Animesh Kumar Singh");
		for (int i=0; i<arr.length; i++)
			System.out.print(arr[i]+" ");
		System.out.print("\n");
		System.out.println(arr.length);
		
		System.out.println(str.contains("nim"));
		
		double[][] vals = {{1.,2.,3},{4.,5.,6.},{7.,8.,10.}};
	      Matrix A = new Matrix(vals);
	      Matrix b = Matrix.random(3,1);
	      Matrix x = A.solve(b);
	      Matrix r = A.times(x).minus(b);
	      double rnorm = r.normInf();
		System.out.print(A);
	}

}
