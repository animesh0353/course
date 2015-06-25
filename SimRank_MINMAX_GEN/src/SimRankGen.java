
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;



public class SimRankGen {

	double[][] simRankUser;
//	double[][] simRankMovie;
	int userSize;
//	int movieSize;
	
	public SimRankGen(int userSize) {
		// TODO Auto-generated constructor stub
	
		simRankUser = new double[userSize+1][userSize+1];
//		simRankMovie = new double[moviesSize+1][moviesSize+1];
		this.userSize = userSize + 1;
//		this.movieSize = moviesSize + 1;
	}
	
	 /**
     * Compute the SimRank for the vertices of the given graph.
     *
     * @param <V> The vertex type
     * @param g The graph
     * @return The SimRank, as a map from pairs of vertices to
     * their similarity
     */
   
	
	void computeSimRank(Map<Integer, ArrayList<Integer>>  voterGraph)
	    {
	        final int kMax = 10;
	        final float C = 0.8f;

//	        double[][] currentRUser = new double[userSize][userSize];
	        computeInitialSimRank(voterGraph, simRankUser);
//	        double[][] currentRMovie = new double[movieSize][movieSize];
//	        computeInitialSimRank(movieGraph, simRankMovie);

	        double[][] nextRUser = new double[userSize][userSize];
//	        double[][] nextRMovie = new double[movieSize][movieSize];
	        for (int k=0; k<kMax; k++)
	        {
//	        	for (Integer ds : userGraph.vertexMap.keySet()) {
//					System.out.println(ds);
//				}
//	        	for (Integer ds : movieGraph.vertexMap.keySet()) {
//					System.out.println(ds);
//				}
	            for (Integer a : voterGraph.keySet())
	            {
	                for (Integer b : voterGraph.keySet())
	                {
	                	if(a.equals(b))
	                	{
	                		
	                		nextRUser[a][b] = 1.0;;
	                	}
	                	else {

	                		
	                			
	                			
	                			int sia = voterGraph.get(a).size();
	                			int sib = voterGraph.get(b).size();
	                			if (sia == 0 || sib == 0)
	                			{
	                				nextRUser[a][b] = 0.0;
	                			}
	                			else
	                			{
	                				
	                				float sum = computeSum(a, b, simRankUser, voterGraph);
	                				
	                				nextRUser[a][b] =  (C/(sia*sib)*sum);
	                			}
	                		
	                	}
	                }
//	                System.out.println("Iteraration "+k+ "finished");
	            }
	            
	  

	            System.out.println("Iteraration "+k+ "finished");
	            //System.out.println("After iteration "+k);
	            //print(g, nextR);

//	            for (int i = 0; i < userSize; i++) {
//					for (int j = 0; j < userSize; j++) {
//						simRankUser[i][j] = nextRUser[i][j];
//						simRankMovie[i][j] = nextRMovie[i][j];
//					}
//				}
	            

	    		{
	    			double[][] T = simRankUser;
	    			simRankUser = nextRUser;
	    			nextRUser = T;
	    		}
	    		
	    		
	           
	        }
	        
	        print(simRankUser,userSize,"/home/animesh/Desktop/datamining/HW3/ques1/voterSimRank");
//	        System.out.println("\n \n \n");
	        
//	        print(simRankMovie,movieSize,"/home/animesh/Desktop/datamining/HW3/ques1/movieSimRank");
	       
	    }
	
//		void swapMatrix(double[][] simRankMovie, double[][] nextRMovie)
//		{
//			double[][] T = simRankMovie;
//			simRankMovie = nextRMovie;
//			nextRMovie = T;
//		}

	    /**
	     * Compute the sum of all SimRank values of all incoming
	     * neighbors of the given vertices
	     *
	     * @param <V> The vertex type
	     * @param g The graph
	     * @param a The first vertex
	     * @param b The second vertex
	     * @param simRank The current SimRank
	     * @return The sum of the SimRank values of the
	     * incoming neighbors of the given vertices
	     */
	    private  float computeSum(Integer a, Integer b, double[][] simRankTemp, Map<Integer, ArrayList<Integer>>  graph)
	    {
	    	
	        ArrayList<Integer> arrayListA = graph.get(a);
	        ArrayList<Integer> arrayListB = graph.get(b);
	        
	        float sum = 0;
	        for (Integer iia : arrayListA)
	        {
	        	for (Integer ijb : arrayListB)
	        	{
	        			sum += simRankTemp[iia][ijb];
	        	}
	        	
	        }
	        return sum;
	    }

	    /**
	     * Compute the initial SimRank for the vertices of the given graph.
	     * This initial SimRank for two vertices (a,b) is 0.0f when
	     * a != b, and 1.0f when a == b
	     *
	     * @param <V> The vertex type
	     * @param g The graph
	     * @return The SimRank, as a map from pairs of vertices to
	     * their similarity
	     */
	    void computeInitialSimRank(Map<Integer, ArrayList<Integer>>  graph, double[][] simrankTemp)
	    {
	        
	        for (Integer a : graph.keySet())
	        {
	            for (Integer b : graph.keySet())
	            {
	            	
	            		
	            		if (a.equals(b))
	            		{
	            			simrankTemp[a][b] = 1.0;
	            		}
	            		else
	            		{
	            			simrankTemp[a][b] = 0.0;
	            		}
	            	
	            }
	        }
	        
	    }

	    /**
	     * Print a table with the SimRank values
	     *
	     * @param <V> The vertex type
	     * @param g The graph
	     * @param simRank The SimRank
	     */
//	   void print(Graph graph)
//	    {
//	        final int columnWidth = 8;
//	        final String format = "%4.3f";
//
//	        List<Integer> vertices = new ArrayList<Integer>(graph.vertexMap.keySet());
//	        System.out.printf("%"+columnWidth+"s", "");
//	        for (int j=0; j<vertices.size(); j++)
//	        {
//	            String s = String.valueOf(vertices.get(j));
//	            System.out.printf("%"+columnWidth+"s", s);
//	        }
//	        System.out.println();
//
//	        for (int i=0; i<vertices.size(); i++)
//	        {
//	            String s = String.valueOf(vertices.get(i));
//	            System.out.printf("%"+columnWidth+"s", s);
//	            for (int j=0; j<vertices.size(); j++)
//	            {
//	                Integer a = vertices.get(i);
//	                Integer b = vertices.get(j);
//	                Pair<Integer> ab = new Pair<Integer>(a,b);
//	                Float value = simRank.get(ab);
//	                String vs = String.format(Locale.ENGLISH, format, value);
//	                System.out.printf("%"+columnWidth+"s", vs);
//	            }
//	            System.out.println();
//	        }
//	    }
	    
	    void print(double[][] M, int Msize, String file1)
	    {
	    	try {
	           	
	            File file = new File(file1);
	            BufferedWriter in = new BufferedWriter(new FileWriter(file));
	            
	            for (int i = 0; i < Msize; i++) {
					for (int j = 0; j < Msize; j++) {
						if(M[i][j] > 0.0)
						{
							String string = "( "+i+" , "+j+" ) "+M[i][j];
							in.write(string+"\n");
						}
					}
					
				}
	            
				
	            in.close();
		    
			} catch (NumberFormatException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    
	    }

	
}
