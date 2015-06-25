import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.*;


//import java.lang.Double.*;


public class Protien {
	
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		long startTime = System.currentTimeMillis();
		
		linalg l = new linalg();
		int N = 152; // number of residues
//		int T = 298;  // Temperature
//		double R = 0.008314;  // R constant
//		double nsp=N*(N+1)/2;   // for now ignoring the completely unfolded state.
		
        
//		int count =0;
		BufferedReader br = null;

		double arr[][] = new double[N+1][N+1];
		
		int i=1, j=1;

		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader("/home/animesh/Dropbox/project/data_ssa_rn.csv"));

			while ((sCurrentLine = br.readLine()) != null) 
			{
					//System.out.println();
					//System.out.println(sCurrentLine+"\n");
					String[] str = sCurrentLine.split(",");
								
					try{
											
						for (i=1;i<=152;i++)
						{	arr[j][i] = Double.parseDouble(str[i-1]);
							if (Double.isNaN(arr[j][i]))
								arr[j][i] = 0.0;
								
						}
						j++;
						
					} 
					catch(NumberFormatException e){
						e.printStackTrace();
					}
				
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	/*	int k=0;
		for (k=0; k<10; k++)
		{
			
		
			for (i=0;i<N;i++)
			{
				System.out.print(arr[k][i]+ " ") ;
			
			}
			System.out.println();
		}
		
	*/	
	//	System.out.println();
//		int[] diag = l.diag(N);
//		System.out.println(arr.length);
//		System.out.println(diag.length);
//		for (i =1; i<diag.length ; i++)
//				System.out.print(diag[i]+ "  ");
		
		l.check(arr );
		
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Time in millisecond "+ totalTime);
		
		
	}

}
