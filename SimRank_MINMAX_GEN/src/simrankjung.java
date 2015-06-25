

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

public class simrankjung
{
    public static void main(String[] args) throws FileNotFoundException
    {
        Graph<Integer, String> g =
            new UndirectedSparseGraph<Integer, String>();
        int moviesStart = 4000;
        int edgeLabel = 1;
        Set<Integer> userSet = new HashSet<Integer>();
        Set<Integer> movieSet = new HashSet<Integer>();
        
        
        File file = new File("/home/animesh/Desktop/datamining/HW3/ques1/ml-100k/u.data");
        Scanner in = new Scanner(file);
        
        while (in.hasNext()) {
			String string =  in.nextLine();
			String[] str = string.split("\t");
			int userId = Integer.parseInt(str[0]);
			int moviesId = Integer.parseInt(str[1]);
			
			 g.addVertex((Integer)userId);
			 userSet.add((Integer)userId);
			 
		     g.addVertex((Integer)(moviesStart+moviesId));
		     movieSet.add(moviesStart+moviesId);
		     int val = (++edgeLabel);
		     g.addEdge(String.valueOf(val), userId, moviesId);
			
			
		}

       
        
//        g.addEdge("Edge-B", 2, 3);
//        g.addEdge("Edge-C", 4, 3);

        Map<Pair<Integer>, Float> simRank = computeSimRank(g, userSet, movieSet);
//        print(g, simRank);
        Iterator<Map.Entry<Pair<Integer>, Float>> entries = simRank.entrySet().iterator();
        while (entries.hasNext()) {
          Map.Entry<Pair<Integer>, Float>  thisEntry =  entries.next();
          Pair<Integer> key = thisEntry.getKey();
          Float value = thisEntry.getValue();
          if(value != null && (float) value > 0.0 )
          {
        	  System.out.println("("+key.getFirst()+","+key.getSecond()+") "+value);
          }
          
          
        }
//        Iterator<Pair<Integer>, Float> itr = simRank.entrySet().iterator();
    }


    /**
     * Compute the SimRank for the vertices of the given graph.
     *
     * @param <V> The vertex type
     * @param g The graph
     * @return The SimRank, as a map from pairs of vertices to
     * their similarity
     */
    private static <V> Map<Pair<V>, Float> computeSimRank(Graph<V,?> g, Set<V> userSet, Set<V> movieSet)
    {
        final int kMax = 5;
        final float C = 0.8f;

        Map<Pair<V>, Float> currentR = computeInitialSimRank(g);
        Map<Pair<V>, Float> nextR = new LinkedHashMap<Pair<V>, Float>();
        for (int k=0; k<kMax; k++)
        {
        	Iterator<V> it = userSet.iterator();
        	while (it.hasNext()) {
				V a =  it.next();
				Iterator<V> it2 = userSet.iterator();
				while (it2.hasNext()) {
					V b = (V) it2.next();
				
            
                	if(a == b)
                	{
                		Pair<V> ab = new Pair<V>(a,b);
                		nextR.put(ab, 1.0f);
                	}
                	else {
						

                		float sum = computeSum(g, a, b, currentR);
                		Pair<V> ab = new Pair<V>(a,b);
                		int sia = g.inDegree(a);
                		int sib = g.inDegree(b);
               			nextR.put(ab, C / (sia * sib) * sum);
                	}
                }
            }

        	it = movieSet.iterator();
        	while (it.hasNext()) {
				V a =  it.next();
				Iterator<V> it2 = movieSet.iterator();
				while (it2.hasNext()) {
					V b =  it2.next();
				
            
                	if(a.equals(b) )
                	{
                		Pair<V> ab = new Pair<V>(a,b);
                		nextR.put(ab, 1.0f);
                	}
                	else {
						

                		float sum = computeSum(g, a, b, currentR);
                		Pair<V> ab = new Pair<V>(a,b);
                		int sia = g.inDegree(a);
                		int sib = g.inDegree(b);
                		nextR.put(ab, C / (sia * sib) * sum);
                	}
                }
            }
        	
        	
            System.out.println("After iteration "+k);
            //print(g, nextR);

            Map<Pair<V>, Float> temp = nextR;
            nextR = currentR;
            currentR = temp;
        }
        return currentR;
    }

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
    private static <V> float computeSum(
        Graph<V,?> g, V a, V b, Map<Pair<V>, Float> simRank)
    {
        Collection<V> ia = g.getNeighbors(a);
        Collection<V> ib = g.getNeighbors(b);
        float sum = 0;
        for (V iia : ia)
        {
            for (V ijb : ib)
            {
                Pair<V> key = new Pair<V>(iia, ijb);
                Float r = simRank.get(key);
                sum += r;
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
    private static <V> Map<Pair<V>, Float> computeInitialSimRank(Graph<V,?> g)
    {
        Map<Pair<V>, Float> R0 = new LinkedHashMap<Pair<V>, Float>();
        for (V a : g.getVertices())
        {
            for (V b : g.getVertices())
            {
                Pair<V> ab = new Pair<V>(a,b);
                if (a.equals(b))
                {
                    R0.put(ab, 1.0f);
                }
                else
                {
                    R0.put(ab, 0.0f);
                }
            }
        }
        return R0;
    }

    /**
     * Print a table with the SimRank values
     *
     * @param <V> The vertex type
     * @param g The graph
     * @param simRank The SimRank
     */
    private static <V> void print(Graph<V,?> g, Map<Pair<V>, Float> simRank)
    {
        final int columnWidth = 8;
        final String format = "%4.3f";

        List<V> vertices = new ArrayList<V>(g.getVertices());
        System.out.printf("%"+columnWidth+"s", "");
        for (int j=0; j<vertices.size(); j++)
        {
            String s = String.valueOf(vertices.get(j));
            System.out.printf("%"+columnWidth+"s", s);
        }
        System.out.println();

        for (int i=0; i<vertices.size(); i++)
        {
            String s = String.valueOf(vertices.get(i));
            System.out.printf("%"+columnWidth+"s", s);
            for (int j=0; j<vertices.size(); j++)
            {
                V a = vertices.get(i);
                V b = vertices.get(j);
                Pair<V> ab = new Pair<V>(a,b);
                Float value = simRank.get(ab);
                String vs = String.format(Locale.ENGLISH, format, value);
                System.out.printf("%"+columnWidth+"s", vs);
            }
            System.out.println();
        }
    }

}

