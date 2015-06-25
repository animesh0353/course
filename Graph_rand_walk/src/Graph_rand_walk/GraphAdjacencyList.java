package Graph_rand_walk;




//============================================================



import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;


 

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
		int index =1;
		   // for now ignoring the completely unfolded state.
		
		int nsteps=10000;
		double[][] ind = new double[nsteps+1][2];
		double[] ind2 = new double[nsteps+1];
		
		double curr_wght = 0.0;
		int curr_x=1, curr_y = 80;
		ArrayList<edges> edgeList = adjacencyList.getEdge(start1);
		
		
		
		
		
		Random rand = new Random();
		
	
		
		int i,j, nposs = 0;
		double x;
		edges e1;
		ind[0][0]=curr_x;
		ind[0][1]=curr_y;
		ind2[0] = curr_wght;
		for (j=1; j<nsteps; j++)
		{
			edgeList = adjacencyList.getEdge(start1);
			
			
			
			double sum = 0.0, sum2 = 0.0;
			SortedMap<Double,Integer> tr = new TreeMap<Double,Integer>();
			ArrayList<Double> ar1 = new ArrayList<Double>();
			
			ListIterator<edges> itr = edgeList.listIterator();
			
			while(itr.hasNext())
			{
				edges temp = (edges)itr.next();
				sum += temp.wght();
				ar1.add(temp.wght());
				
				
			}
			
			ListIterator<Double> iter = ar1.listIterator(); 
			
			while(iter.hasNext())
			{
				
				sum2 += (sum - iter.next());
				
				
			}
			int size = ar1.size();
		
			
			
			double sum3 =0.0;
			while(iter.hasPrevious())
			{
				sum3  += (sum - iter.previous());
				
				tr.put((Double)(sum3/sum2), --size );
			
				
			}
			
			size = tr.size();
			
		
			Set<Entry<Double, Integer>> set = tr.entrySet();
			Iterator<Entry<Double, Integer>> itr1 = set.iterator(); 
			
			x = rand.nextDouble();
			while(itr1.hasNext())
			{
				Entry<Double, Integer> map = itr1.next();
				if (x < map.getKey())
				{
					nposs = map.getValue();
					break;
					
				}
			}
					
			
			
			
		
			e1 = edgeList.get(nposs);
						
			
			ind[j][0] = e1.co_x() ;	ind[j][1] = e1.co_y();
	        start1 = e1.dest();
	        ind2[j] = e1.wght();
	        
	       
	        
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
 		
 	
 		adjacencyList.rand_walk(adjacencyList, spmat[1][80]);
 		
 		long endTime   = System.currentTimeMillis();
 		long totalTime = endTime - startTime;
 		//System.out.println("Time in millisecond "+ totalTime);
 		
         
         
         //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

         Scanner scan = new Scanner(System.in);


          scan.close();
          
   

     }

}