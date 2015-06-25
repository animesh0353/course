package DSA;




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

    private static  Map<Long, ArrayList<edges>> Adjacency_List;	

   /*

    * Initializes the map to with size equal to number of vertices in a graph

    * Maps each vertex to a given List Object 

    */

    public GraphAdjacencyList()  //int number_of_vertices)

    {

        Adjacency_List = new HashMap<Long, ArrayList<edges>>();	

       /* for (int i = 1 ; i <= number_of_vertices ; i++)

        { 

            Adjacency_List.put((long)i, new ArrayList<edges>());

        }
        */

    }

 
    public void setVertices(long nodes)

    {

        Adjacency_List.put(nodes, new ArrayList<edges>());
      //  System.out.println("  "+Adjacency_List.size()+"  ");

        

    }

 

    /* Adds nodes in the Adjacency list for the corresponding vertex */

    public void setEdge(long source , long destination , double wght,double bowlt, int x ,int y)

    {

       //if (source > Adjacency_List.size() || destination > Adjacency_List.size())

       //{

        //   System.out.println("the vertex entered in not present ");

          // return;

       //}

       ArrayList<edges> slist = Adjacency_List.get(source);

       slist.add(new edges(destination, wght,bowlt,x ,y));
//       System.out.println("  "+Adjacency_List.size()+"  ");

      // ArrayList<Integer> dlist = Adjacency_List.get(destination);

       //dlist.add(new edges(source, wght));

   }

 

   /* Returns the List containing the vertex joining the source vertex */		

    public ArrayList<edges> getEdge(long source)

    {

        return Adjacency_List.get(source);

    }
    
    public void rand_walk(GraphAdjacencyList  adjacencyList, int start1)
    {
    	int N=152; // number of residues
		int T = 298;  // Temperature
		double R = 0.008314;  // R constant
		//double nsp=N*(N+1)/2;   // for now ignoring the completely unfolded state.
		//int[][] spmat = new int[N+1][N+1];
		//int[] diage = diag(N);
		
		//countseq(spmat, N);
		
		//int nmol=1;
		int nsteps=100000;
		double[][] ind = new double[nsteps+1][2];
		double[] ind2 = new double[nsteps+1];
		//double[][] ind3 = new double[nsteps+1][nmol];
		//double[][] ind4 = new double[nsteps+1][nmol];
		double curr_wght = 0.0;
		int curr_x=1, curr_y = 80;
		ArrayList<edges> edgeList = adjacencyList.getEdge(start1);
		
		
		
		
		
		Random rand = new Random();
		
	//	System.out.println("\n"+nsteps);
		
		int i,j, nposs = 0;
		double x;
		edges e1;
		ind[0][0]=curr_x;
		ind[0][1]=curr_y;
		ind2[0] = curr_wght;
		for (j=1; j<nsteps; j++)
		{
			edgeList = adjacencyList.getEdge(start1);
			//nposs = edgeList.size();
			
			
			double sum = 0.0, sum2 = 0.0;
			SortedMap<Double,Integer> tr = new TreeMap<Double,Integer>();
			ArrayList<Double> ar1 = new ArrayList<Double>();
			
			ListIterator<edges> itr = edgeList.listIterator();
			
			while(itr.hasNext())
			{
				edges temp = (edges)itr.next();
				sum += temp.wght();
				ar1.add(temp.wght());
				//tr.put((Double)edges.wght, value);
				
			}
			
			ListIterator<Double> iter = ar1.listIterator(); 
			//itr = edgeList.listIterator();
			while(iter.hasNext())
			{
				
				sum2 += (sum - iter.next());
				//tr.put((Double)edges.wght, value);
				
			}
			int size = ar1.size();
		//.out.println("Size of Adjacency list "+size);
			
			
			double sum3 =0.0;
			while(iter.hasPrevious())
			{
				sum3  += (sum - iter.previous());
				//System.out.println("Sum value "+sum3+"  "+sum2);
				tr.put((Double)(sum3/sum2), --size );
				//tr.put((Double)edges.wght, value);
				
			}
			
			size = tr.size();
			
			//ar1.clear();
			//ar1.addAll(tr.keySet());
			Set<Entry<Double, Integer>> set = tr.entrySet();
			Iterator<Entry<Double, Integer>> itr1 = set.iterator(); 
			
			x = rand.nextDouble();
			//System.out.println("Random Value "+x);
			while(itr1.hasNext())
			{
				Entry<Double, Integer> map = itr1.next();
				//System.out.println(map.getKey()+"  "+map.getValue());
				if (x < map.getKey())
				{
					nposs = map.getValue();
					break;
					
				}
			}
					
			
			
			
			//x = rand.nextInt(nposs-1);
			
			//edgelist.indexof()
		//	System.out.println(nposs);
			e1 = edgeList.get(nposs);
						
			
			ind[j][0] = e1.co_x() ;	ind[j][1] = e1.co_y();
	        start1 = (int)e1.dest();
	        ind2[j] = e1.wght();
	        
	       
	        
		}
   	
		System.out.println("Ind00,Ind01");
		for (i=0; i<nsteps; i++)
			System.out.println(ind[i][0]+","+ind[i][1]);
		
    }
    
    public void precompute(GraphAdjacencyList  adjacencyList)
    {
    	Iterator itr = adjacencyList.Adjacency_List.entrySet().iterator();
    	int T = 298;  // Temperature
		double R = 0.008314;  // R constant
    	int count = 0,i,j;
    	double wght = 0.0;
    	double wght_self = 0.0;
    	while (itr.hasNext())
    	{
    		i = 0;
    		//itr = (Iterator) itr.next();
    		Map.Entry pairs = (Map.Entry)itr.next();
    		ArrayList<edges> e = (ArrayList<edges>) pairs.getValue();
    		 Iterator<edges> edge = e.iterator();
    		 while (edge.hasNext())
    		 {
    			 edges e1_self = edge.next();
    			 if((long)e1_self.dest() == (long)pairs.getKey())
    			 {
    				 wght_self = e1_self.wght();
    			 }
    		 }
    		 
           // System.out.println(pairs.getKey() + " = " + edge.next().wght());
            
            String str1 = Long.toBinaryString((long)pairs.getKey());
        	int len1 = str1.length();
//        	char[] a1 = new char[56] ;
        	StringBuilder sb1 = new StringBuilder();
            
        	if (len1<56)
        	{
        		int more = 56 - len1;
        	//	System.out.print(more );
        		for (i=0;i<more;i++)
        		{
        			sb1.append('0');
        		}
        	}
        	
        	String str2 = sb1 + str1;
         //   System.out.print(str2.length() + "|"+str2 +  " + " +sb1.length()+ " + " +  str1.length());
            
            
           Iterator itr1 = adjacencyList.Adjacency_List.entrySet().iterator();
           
		   edges[] e_array = new edges[100];
		   int obj = 0;
            
            while (itr1.hasNext())
            {
            	i = 0;
            	Map.Entry p = (Map.Entry)itr1.next();
            	String str = Long.toBinaryString((long)p.getKey());
            	int len = str.length();
            	StringBuilder sb = new StringBuilder();
            	if (len<56)
            	{
            		int more = 56 - len;
            		for (i=0;i<more;i++)
            		{
            			sb.append('0');
            		}
            	}
            	
            	String temp = sb + str;
            	//System.out.println(temp.length() + "|"+temp +  " + " +sb.length()+ " + " +  str.length());
            	//break;
            	int counter =0;
            	for (j=0;j<56;j++)
            	{
            		if(str2.charAt(j) != temp.charAt(j))
            		{
            			counter++;
            		}
            	}
            	//System.out.println(" | "+count);
            	
            	if (counter < 2)
            	{
            		 ArrayList<edges> slist = Adjacency_List.get(p.getKey());
            		// double bolwt = Math.exp( -(e1.wght - ind2[j-1]) / (R*T) );
            		int x =0, y = 0;
            		Iterator<edges> e1 = slist.iterator();
                    // System.out.println(pairs.getKey() + " = " + edge.next().wght());
            		while (e1.hasNext())
            		{
            			edges e_self = e1.next();
            			if ((long)e_self.dest() == (long)p.getKey())
            			{
            			
            				wght = e_self.wght();
            				double bolwt = Math.exp( -(wght - wght_self) / (R*T) );
            				e_array[obj] =  new edges((long)p.getKey(),   e_self.wght(),bolwt,x ,y);
            				//adjacencyList.setEdge((long)pairs.getKey(),(long)p.getKey(), wght, bolwt, 0, 0);
            				obj++;
            			}
            		}

            	       
            	}
            	
            	
            }
            for(j=0;j<obj;j++)
            {
            	if((long)e_array[j].dest() != (long)pairs.getKey())
            	adjacencyList.setEdge((long)pairs.getKey(),(long)e_array[j].dest(), e_array[j].wght(), e_array[j].bowlt(), 0, 0);
            }
           if (count%100 == 0)
        	   System.out.println(".");
           else
        	   System.out.print(" "+count);
        		   
    		count++;
    	}
    	
    	System.out.println("node,edges(dest,weight,bowlt)");
    	Iterator itr_file = adjacencyList.Adjacency_List.entrySet().iterator();
    	while (itr_file.hasNext())
    	{

			// itr = (Iterator) itr.next();
			Map.Entry pairs_file = (Map.Entry) itr_file.next();
			ArrayList<edges> e_file = (ArrayList<edges>) pairs_file.getValue();
			System.out.print(pairs_file.getKey()); // print node
			Iterator<edges> edge_file = e_file.iterator();
			while (edge_file.hasNext()) {
				edges e1_self_file = edge_file.next();
				System.out.print("," + e1_self_file.dest() + ","
						+ e1_self_file.wght() + "," + e1_self_file.bowlt());
			}
			System.out.println();
    		
    	}
    	
		
    	
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
 		
 		GraphAdjacencyList adjacencyList = new GraphAdjacencyList();
 		
 		int[][] spmat = new int[N+1][N+1];
 		l.countseq(spmat, N);
 		int level = 0, next = 1;

 		BufferedReader br = null;

 		double arr[][] = new double[N+1][N+1];
 		
 		
 		int i=1, j=0 ;
 		int no_ones = 0, start_fi = 0, ones_fi = 0, start_si = 0, ones_si = 0;

 		try
 		{

 				String sCurrentLine;

 				br = new BufferedReader(new FileReader("/home/animesh/Dropbox/project/dsa1.csv"));

 				while ((sCurrentLine = br.readLine()) != null) 
 				{
 					
 					String[] str = sCurrentLine.split(",");
 								
 					try
 					{
 						level++;
 						long nodes = 0;
 						int k = 0;
 						
 						no_ones = Integer.parseInt(str[0]);
 						wght = Double.parseDouble(str[1]);
 						start_fi = Integer.parseInt(str[2]);
 						ones_fi = Integer.parseInt(str[3]);
 						start_si = Integer.parseInt(str[4]);
 						ones_si = Integer.parseInt(str[5]);
 						int count1 = 0;
 						String temp;
 						temp = "0";
 						
 						for (i= 0; i<56; i++)
 						{
 							if (( i >= (start_fi-1) && i < (start_fi + ones_fi - 1)) || (i >= (start_si -1) && (i < ( start_si + ones_si -1 ) )))
 								{
 									nodes = (long) (nodes + (long)Math.pow(2, i));
 									count1 ++;
 									k++;
 								}
 							
 						}
 							
 						/* if (count1 == no_ones)
 							if (j>-1)
 								System.out.print(k+" "+"True");  */
 					//	System.out.print(nodes+"  "+Long.toBinaryString(nodes));
 						adjacencyList.setVertices(nodes);
 								//int nodes = spmat[level][i];
 						
 						adjacencyList.setEdge(nodes, nodes, wght,0.0,0,0);
 								
 								
 								
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
 	//	adjacencyList.rand_walk(adjacencyList, spmat[1][80]);
 		
 		long endTime   = System.currentTimeMillis();
 		long totalTime = endTime - startTime;
 		System.out.println("Time in millisecond "+ totalTime);
 		
        adjacencyList.precompute(adjacencyList) ;
         
         //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

      //   Scanner scan = new Scanner(System.in);

      //   try

     //    {

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

             //System.out.println ("the given Adjacency List for the graph \n");

             for ( i = 1000 ; i <= 100  ; i++)

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
          

   //       } 

   //       catch(InputMismatchException inputMismatch)

   //       {

  //            System.out.println("Error in Input Format. \nFormat : <source index> <destination index>");
//
   //       }

 //         scan.close();
          
       //   System.out.println("Value of spmat[152][1]  "+ spmat[1][80]);

             edges edg = new edges(d, w, bowlt, x, y);
             edg.hashCode();
     }

}