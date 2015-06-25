package test;




//============================================================



import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.ListIterator;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;

 

public class GraphAdjacencyList 

{

   /* Makes use of Map collection to store the adjacency list for each vertex.*/

    private static  Map<Integer, ArrayList<edges>> Adjacency_List;	

   /*

    * Initializes the map to with size equal to number of vertices in a graph

    * Maps each vertex to a given List Object 

    */

    public GraphAdjacencyList(int number_of_vertices)

    {

        Adjacency_List = new HashMap<Integer, ArrayList<edges>>();	

        for (int i = 1 ; i <= number_of_vertices ; i++)

        { 

            Adjacency_List.put(i, new ArrayList<edges>());

        }

    }

 

 

    /* Adds nodes in the Adjacency list for the corresponding vertex */

    public void setEdge(int source , int destination , double wght, int x ,int y)

    {

       if (source > Adjacency_List.size() || destination > Adjacency_List.size())

       {

           System.out.println("the vertex entered in not present ");

           return;

       }

       ArrayList<edges> slist = Adjacency_List.get(source);

       slist.add(new edges(destination, wght,x ,y));

      // ArrayList<Integer> dlist = Adjacency_List.get(destination);

       //dlist.add(new edges(source, wght));

   }

 

   /* Returns the List containing the vertex joining the source vertex */		

    public ArrayList<edges> getEdge(int source)

    {

        if (source > Adjacency_List.size())

        {

            System.out.println("the vertex entered is not present");

            return null;

        }

        return Adjacency_List.get(source);

    }
    
    public void rand_walk(GraphAdjacencyList  adjacencyList, int start1)
    {
    	int N=152; // number of residues
		int T = 298;  // Temperature
		double R = 0.008314;  // R constant
		int index = 1;
		//double nsp=N*(N+1)/2;   // for now ignoring the completely unfolded state.
		//int[][] spmat = new int[N+1][N+1];
		//int[] diage = diag(N);
		
		//countseq(spmat, N);
		
		int nmol=1;
		int nsteps=100000;
		double[][] ind = new double[nsteps+1][2];
		double[] ind2 = new double[nsteps+1];
		//double[][] ind3 = new double[nsteps+1][nmol];
		//double[][] ind4 = new double[nsteps+1][nmol];
		double curr_wght = 0.0;
		int curr_x=1, curr_y = 80;
		ArrayList<edges> edgeList = adjacencyList.getEdge(start1);
		ListIterator<edges> itr = edgeList.listIterator();
		while(itr.hasNext())
		{
			edges temp = (edges)itr.next();
			if (start1 == temp.dest())
			{
				curr_wght = temp.wght();
				curr_x = temp.co_x();
				curr_y = temp.co_y();
				break;
			}
		}
		
		Random rand = new Random();
		
	//	System.out.println("\n"+nsteps);
		
		int i,j,m,n, nposs, x,l ;
		edges e1;
		ind[0][0]=curr_x;
		ind[0][1]=curr_y;
		ind2[0] = curr_wght;
		for (j=1; j<nsteps; j++)
		{
			edgeList = adjacencyList.getEdge(start1);
			nposs = edgeList.size();
			x = rand.nextInt(nposs-1);
			//edgelist.indexof()
			e1 = edgeList.get(x);
			if (start1 == e1.dest())
			{
				e1 = edgeList.get(nposs - 1);
			}
			
			double bolwt = Math.exp( -(e1.wght - ind2[j-1]) / (R*T) );
			//System.out.print(bolwt+"  ");
            //if (edgeList.size() == 0)
			if (bolwt > 1)
			{
	        	ind[j][0] = e1.co_x() ;	ind[j][1] = e1.co_y();
	        	start1 = e1.dest();
	        	ind2[j] = e1.wght();
	        }
	        else
	        {
	        	 if ( bolwt > rand.nextDouble() )
	        	 {
	        		 start1 = e1.dest();
	        		 ind[j][0] = e1.co_x() ;	ind[j][1] = e1.co_y();
	        		 ind2[j] = e1.wght();
	        	 }
                else
                {
                	ind[j][0] = ind[j-1][0];		ind[j][1] = ind[j-1][1];
                    ind2[j] = ind2[j-1];	     
			        
                	
                }
	        	
	        }
	       
	        
		}
   	
		System.out.println("Index,Ind00,Ind01");
		for (i=0; i<nsteps; i++)
			System.out.println(index++ + ","+ind[i][0]+","+ind[i][1]);
		
    }
    

 

    /*

     * Main Function reads the number of vertices and edges in a graph.

     * then creates a Adjacency List for the graph and prints it  

     */

     public static void main(String...arg)

     {

         int source , destination;
         double wght;

         int number_of_edges,number_of_vertices;

         int count = 1;
         
         edges e1;
         
         //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
         
 		long startTime = System.currentTimeMillis();
		
 		linalg1 l = new linalg1();
 		int N = 152; // number of residues
 		number_of_vertices = (N*(N + 1))/2;
 		
 		GraphAdjacencyList adjacencyList = new GraphAdjacencyList(number_of_vertices);
 		
 		int[][] spmat = new int[N+1][N+1];
 		l.countseq(spmat, N);
 		int level = 0, next = 1;

 		BufferedReader br = null;

 		double arr[][] = new double[N+1][N+1];
 		
 		int i=1, j=1;

 		try
 		{

 				String sCurrentLine;

 				br = new BufferedReader(new FileReader("/home/animesh/Dropbox/project/data_ssa_rn.csv"));

 				while ((sCurrentLine = br.readLine()) != null) 
 				{
 					
 					String[] str = sCurrentLine.split(",");
 								
 					try
 					{
 						level++;
 											
 						for (i=1;i<=152;i++)
 						{	//arr[j][i] = Double.parseDouble(str[i-1]);
 							wght = Double.parseDouble(str[i-1]);
 							
 							if (!(Double.isNaN(wght)))
 							{
 								int nodes = spmat[level][i];
 								adjacencyList.setEdge(nodes, nodes, wght,level,i);
 								if(level > 1)
 								{
 									adjacencyList.setEdge( spmat[level-1][i], nodes, wght,level,i);
 									adjacencyList.setEdge(spmat[level-1][i+1], nodes,  wght,level,i);
 									
 								}
 								if(level < 152)
 								{
 									if (spmat[level+1][i]>(double)0.0)
 									{
 										adjacencyList.setEdge( spmat[level+1][i], nodes, wght,level,i);
 									}
 									
 									if (i>1)
 									{
 										adjacencyList.setEdge(spmat[level+1][i-1], nodes,  wght,level,i);
 									}
 								}
 								
 								
 							}
 								//arr[j][i] = 0.0;
 								
 						}
 						j++;
 						
 					} 
 					catch(NumberFormatException e)
 					{
 						
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
 		
 		//System.out.println(arr.length);
 		/*for (i=1 ; i<5 ; i++)
 		{
 			for (j=1; j<N ; j++)
 				System.out.print(arr[i][j]+"  ");
 			System.out.println();
 		}*/
 	
 		
 	//	l.check(arr );
 		
 	//	l.countseq(spmat, N);
 		adjacencyList.rand_walk(adjacencyList, spmat[1][80]);
 		
 		long endTime   = System.currentTimeMillis();
 		long totalTime = endTime - startTime;
 	//	System.out.println("Time in millisecond "+ totalTime);
 		
         
         
         //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

         Scanner scan = new Scanner(System.in);

         try

         {

             /* Read the number of vertices and edges in graph */
//
        //     System.out.println("Enter the number of vertices and edges in graph");

             //number_of_vertices = scan.nextInt();

             //number_of_edges = scan.nextInt();

             //GraphAdjacencyList adjacencyList = new GraphAdjacencyList(number_of_vertices);

 

             /* Reads the edges present in the graph */

          //   System.out.println("Enter the edges in graph Format : <source index> <destination index>");

           /*  while (count <= number_of_edges)

             {

                 source = scan.nextInt();

                 destination = scan.nextInt();
                 
                 wght = scan.nextDouble();

                 adjacencyList.setEdge(source, destination, wght);

                 count++;

             }
          */
 

             /* Prints the adjacency List representing the graph.*/

    /*         System.out.println ("the given Adjacency List for the graph \n");

             for ( i = 1000 ; i <= 1005  ; i++)

             {

                 System.out.print(i+"->");

                 ArrayList<edges> edgeList = adjacencyList.getEdge(i);
                 if (edgeList.size() == 0)
                 {
                	 System.out.println();
                	 continue;
                 }
                		 
                            

                 for ( j = 1 ; ; j++ )

                 {

                     if (j != edgeList.size())

                     {
                    	 e1 = edgeList.get(j - 1 );
                         System.out.print(e1.v+"("+e1.wght+")"+"->");

                     }else

                     {
                    	 e1 = edgeList.get(j - 1 );
                         System.out.print(e1.v+"("+e1.wght+")");

                         //System.out.print(edgeList.get(j - 1 ));

                         break;

                     }						 

                 }

                 System.out.println();					

              }
           */

          } 

          catch(InputMismatchException inputMismatch)

          {

              System.out.println("Error in Input Format. \nFormat : <source index> <destination index>");

          }

          scan.close();
          
       //   System.out.println("Value of spmat[152][1]  "+ spmat[1][80]);

     }

}