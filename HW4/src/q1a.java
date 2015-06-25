
public class q1a {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

//		(1 – e-km/n)k
		double[] result = new double[21];
		double k  = 0;
		double m = 1000000000.0;
		double n = 8000000000.0;
		for (int i = 1; i <= 20; i++) {
			k = i;
			result[i] = Math.pow((1.0 - Math.exp(-(k/8.0))), k);
		}
		
		for (int i = 1; i <= 20; i++) {
			System.out.println(result[i]);
		}
	}

}
