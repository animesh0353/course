package DSA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.lang.Math;

public class linalg1 
{
	/*

	    * Initializes the map to with size equal to number of vertices in a graph

	    * Maps each vertex to a given List Object 

	    */

	    
	
	
	
	
	public int[] diag(int N)
	{
		int i = 1, j = 1;
		int[] temp = new int[150 + 1];
		temp[1] = N + (N-1);
		for (i=2; i<=150; i++)
		{
			temp[i] = temp[i-1] + (N-i);
			
		}
		return temp;
		
	}
	
	public void countseq(int spmat[][], int N)
	{
		int i, j, count = 1;
		for (i = 1; i <= N; i++ )
			for (j=1; j<= N-i+1; j++)
			{
				spmat[i][j] = count;
				count++;
			}
		
		
	}
	
	public boolean in(int[] a, int m)
	{
		for (int i = 1; i<a.length; i++)
		{
			if (a[i] == m)
				return true;
		}
		return false;
	}
	
	
	public void check(double fe[][] )
	{
		
		
		int N=152; // number of residues
		int T = 298;  // Temperature
		double R = 0.008314;  // R constant
		double nsp=N*(N+1)/2;   // for now ignoring the completely unfolded state.
		int[][] spmat = new int[N+1][N+1];
		int[] diage = diag(N);
		
		countseq(spmat, N);
		
		int nmol=1;
		int nsteps=1000000;
		double[][] ind = new double[nsteps+1][2];
		double[][] ind2 = new double[nsteps+1][nmol];
		double[][] ind3 = new double[nsteps+1][nmol];
		double[][] ind4 = new double[nsteps+1][nmol];
		
		Random rand = new Random();
		
	//	System.out.println("\n"+nsteps);
		
		int i,j,m,n, nposs ;
		for (i=0; i<nmol; i++)
		{
			m=1;n=80; // starting from the bottom of the funnel (row,col)
			
			for (j=1; j<=nsteps; j++)
			{
				if (m==N & n==1 )     // the most folded..bottom most
				{
				    nposs=2;   // number of possible moves
			        int[] mnew = {0,m-1, m-1};
			        int[] nnew = {0, n, n+1};
			        
			        int x = rand.nextInt(nposs);
			        double bolwt = Math.exp( -(fe[mnew[x+1]][nnew[x+1]] - fe[m][n]) / (R*T) );
			        if (bolwt > 1)
			        {
			        	m=mnew[x+1];
			        	n=nnew[x+1];
			        	ind[j][0] = m;	ind[j][1] = n;
			        	ind2[j][i] = spmat[m][n];	     
			        	ind3[j][i] = fe[m][n];
			        	ind4[j][i] = m;
			        }
			        else
			        {
			        	 if ( bolwt > rand.nextDouble() )
			        	 {
			        		 m = mnew[x+1];
		                     n = nnew[x+1];
		                     ind[j][0] = m;		ind[j][1] = n;
		                     ind2[j][i] = spmat[m][n];	     
					         ind3[j][i] = fe[m][n];
					         ind4[j][i] = m;
			        	 }
		                else
		                {
		                	ind[j][0] = m;		ind[j][1] = n;
		                    ind2[j][i] = spmat[m][n];	     
					        ind3[j][i] = fe[m][n];
					        ind4[j][i] = m;

		                	
		                }
			        	
			        }
			        continue;
			        
				}
			
				if ( m > 1 && m < N && n==1)    // the first column other than the first and last cell
				{
					nposs = 3;    // number of possible moves
					int[] mnew = {0, m-1, m-1, m+1};
					int[] nnew = {0, n, n+1, n};
					int x = rand.nextInt(nposs);
					double  bolwt = Math.exp(-(fe[mnew[x+1]][nnew[x+1]] - fe[m][n] ) / (R * T));
					if ( bolwt > 1 )
					{
						m = mnew[x+1];
						n = nnew[x+1];
						ind[j][0] = m;		ind[j][1] = n;
						ind2[j][i] = spmat[m][n];	     
						ind3[j][i] = fe[m][n];
						ind4[j][i] = m;
					}
					else
					{
						if ( bolwt > rand.nextDouble())
						{
							m = mnew[x+1];
							n = nnew[x+1];
							ind[j][0] = m;		ind[j][1] = n;
							ind2[j][i] = spmat[m][n];	     
							ind3[j][i] = fe[m][n];
							ind4[j][i] = m;
			    		
						}
						else
						{
							ind[j][0] = m;		ind[j][1] = n;
							ind2[j][i] = spmat[m][n];	     
							ind3[j][i] = fe[m][n];
							ind4[j][i] = m;
						}
					}
					continue;
				}
				
				if ( m==1 && n==1 )    // the first cell
				{

					nposs = 1;    // number of possible moves
					int[] mnew = {0, m+1 };
					int[] nnew = {0, n };
					int x = rand.nextInt(nposs);
					double  bolwt = Math.exp(-(fe[mnew[x+1]][nnew[x+1]] - fe[m][n] ) / (R * T));
		            if ( bolwt > 1)
		            {
		            	m = mnew[x+1];
						n = nnew[x+1];
						ind[j][0] = m;		ind[j][1] = n;
						ind2[j][i] = spmat[m][n];	     
						ind3[j][i] = fe[m][n];
						ind4[j][i] = m;
		            }
		            else
		            {
		            	if ( bolwt > rand.nextDouble())
						{
							m = mnew[x+1];
							n = nnew[x+1];
							ind[j][0] = m;		ind[j][1] = n;
							ind2[j][i] = spmat[m][n];	     
							ind3[j][i] = fe[m][n];
							ind4[j][i] = m;
			    		
						}
						else
						{
							ind[j][0] = m;		ind[j][1] = n;
							ind2[j][i] = spmat[m][n];	     
							ind3[j][i] = fe[m][n];
							ind4[j][i] = m;
						}
		            	
		            }
		            continue;
				}

				if ( m==1 && n==N )    // the last cell in the first row
				{
					nposs = 1;    // number of possible moves
					int[] mnew = {0, m+1 };
					int[] nnew = {0, n-1 };
					int x = rand.nextInt(nposs);
					double  bolwt = Math.exp(-(fe[mnew[x+1]][nnew[x+1]] - fe[m][n] ) / (R * T));
		            if ( bolwt > 1)
		            {
		            	m = mnew[x+1];
						n = nnew[x+1];
						ind[j][0] = m;		ind[j][1] = n;
						ind2[j][i] = spmat[m][n];	     
						ind3[j][i] = fe[m][n];
						ind4[j][i] = m;
		            }
		            else
		            {
		            	if ( bolwt > rand.nextDouble())
						{
							m = mnew[x+1];
							n = nnew[x+1];
							ind[j][0] = m;		ind[j][1] = n;
							ind2[j][i] = spmat[m][n];	     
							ind3[j][i] = fe[m][n];
							ind4[j][i] = m;
			    		
						}
						else
						{
							ind[j][0] = m;		ind[j][1] = n;
							ind2[j][i] = spmat[m][n];	     
							ind3[j][i] = fe[m][n];
							ind4[j][i] = m;
						}
		            	
		            }
		            continue;
					
				}

				if ( m==1 && n>1 && n<N )    // the first row except the first and last cell
				{
					nposs = 2;    // number of possible moves
					int[] mnew = {0, m+1, m+1 };
					int[] nnew = {0, n-1, n };
					int x = rand.nextInt(nposs);
					double  bolwt = Math.exp(-(fe[mnew[x+1]][nnew[x+1]] - fe[m][n] ) / (R * T));
		            if ( bolwt > 1)
		            {
		            	m = mnew[x+1];
						n = nnew[x+1];
						ind[j][0] = m;		ind[j][1] = n;
						ind2[j][i] = spmat[m][n];	     
						ind3[j][i] = fe[m][n];
						ind4[j][i] = m;
		            }
		            else
		            {
		            	if ( bolwt > rand.nextDouble())
						{
							m = mnew[x+1];
							n = nnew[x+1];
							ind[j][0] = m;		ind[j][1] = n;
							ind2[j][i] = spmat[m][n];	     
							ind3[j][i] = fe[m][n];
							ind4[j][i] = m;
			    		
						}
						else
						{
							ind[j][0] = m;		ind[j][1] = n;
							ind2[j][i] = spmat[m][n];	     
							ind3[j][i] = fe[m][n];
							ind4[j][i] = m;
						}
		            	
		            }
		            continue;
					
					
					
				}

				if ( m != 1 && m != N && in(diage, spmat[m][n]) )     // for the diag except the extreme elements
				{
					nposs = 3;    // number of possible moves
					int[] mnew = {0, m+1, m-1, m-1 };
					int[] nnew = {0, n-1, n, n+1 };
					int x = rand.nextInt(nposs);
					double  bolwt = Math.exp(-(fe[mnew[x+1]][nnew[x+1]] - fe[m][n] ) / (R * T));
		            if ( bolwt > 1)
		            {
		            	m = mnew[x+1];
						n = nnew[x+1];
						ind[j][0] = m;		ind[j][1] = n;
						ind2[j][i] = spmat[m][n];	     
						ind3[j][i] = fe[m][n];
						ind4[j][i] = m;
		            }
		            else
		            {
		            	if ( bolwt > rand.nextDouble())
						{
							m = mnew[x+1];
							n = nnew[x+1];
							ind[j][0] = m;		ind[j][1] = n;
							ind2[j][i] = spmat[m][n];	     
							ind3[j][i] = fe[m][n];
							ind4[j][i] = m;
			    		
						}
						else
						{
							ind[j][0] = m;		ind[j][1] = n;
							ind2[j][i] = spmat[m][n];	     
							ind3[j][i] = fe[m][n];
							ind4[j][i] = m;
						}
		            	
		            }
		            continue;
				}
	
				if ( m != 1 && n !=1 && !in(diage, spmat[m][n] ) )
				{
					nposs = 4;    // number of possible moves
					int[] mnew = {0, m+1, m+1, m-1, m-1 };
					int[] nnew = {0, n-1, n, n, n+1 };
					int x = rand.nextInt(nposs);
					double  bolwt = Math.exp(-(fe[mnew[x+1]][nnew[x+1]] - fe[m][n] ) / (R * T));
		            if ( bolwt > 1)
		            {
		            	m = mnew[x+1];
						n = nnew[x+1];
						ind[j][0] = m;		ind[j][1] = n;
						ind2[j][i] = spmat[m][n];	     
						ind3[j][i] = fe[m][n];
						ind4[j][i] = m;
		            }
		            else
		            {
		            	if ( bolwt > rand.nextDouble())
						{
							m = mnew[x+1];
							n = nnew[x+1];
							ind[j][0] = m;		ind[j][1] = n;
							ind2[j][i] = spmat[m][n];	     
							ind3[j][i] = fe[m][n];
							ind4[j][i] = m;
			    		
						}
						else
						{
							ind[j][0] = m;		ind[j][1] = n;
							ind2[j][i] = spmat[m][n];	     
							ind3[j][i] = fe[m][n];
							ind4[j][i] = m;
						}
		            	
		            }
		            continue;
					
				}
			            
			           
			
			
			
			
	//====================================================================================		
			    	
			
			}
			
		}
		System.out.println("Ind00,Ind01,Ind2,Ind3,Ind4");
		for (i=1;i<=nsteps;i++)
			System.out.println(ind[i][0] +","+ ind[i][1]+","+ind2[i][0]+","+ind3[i][0]+","+ind4[i][0]);
/*		System.out.println();
		for (i=1;i<=nsteps;i++)
			System.out.print(ind2[i][0]+"   ");
		System.out.println();
		for(i=1;i<=nsteps;i++)
			System.out.print(ind3[i][0]+"  ");
		System.out.println();
		for(i=1;i<=nsteps;i++)
			System.out.print(ind4[i][0]+"  ");
		*/
		
		
	}
	
	
	
}
