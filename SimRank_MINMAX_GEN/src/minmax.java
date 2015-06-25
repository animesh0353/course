

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;



public class minmax
{
	
	private static void updateMap(Map<Integer, ArrayList<Integer>> map, int K, int V) {
		ArrayList<Integer> value = map.get(K);
		if(value == null)
		{
			value = new ArrayList<Integer>();
			map.put(K, value);
		}
		value.add(V);
	}
	
    
    public static void main(String[] args) throws FileNotFoundException
    {
    	int userCount  = 0;
    	int movieCount = 0;
    
    	
        Properties properties = new Properties();
        

        Map<Integer, ArrayList<Integer>> usersGraph  = new HashMap<Integer, ArrayList<Integer>>();
        Map<Integer, ArrayList<Integer>> moviesGraph = new HashMap<Integer, ArrayList<Integer>>();
        
        try {
       	
        	properties.load(new FileInputStream("/home/animesh/Desktop/datamining/HW3/ques1/ml-100k/u.info"));
        	
        	userCount  = Integer.parseInt(properties.getProperty("users"));
        	movieCount = Integer.parseInt(properties.getProperty("items"));
        	

            File file = new File("/home/animesh/Desktop/datamining/HW3/ques1/ml-100k/u.data");
            BufferedReader in = new BufferedReader(new FileReader(file));
            
            String string;
			while ((string = in.readLine())!=null) {
				
				String[] str = string.split("\t");
				
				int user    = Integer.parseInt(str[0]);
				
				int movie    = Integer.parseInt(str[1]);

				updateMap(usersGraph, user, movie);
				updateMap(moviesGraph, movie, user);
			
			}
			
			
	    
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    

        SimRankFun simRankFun = new SimRankFun(userCount, movieCount);
        simRankFun.computeSimRank(usersGraph, moviesGraph);
       
       
    }

    
  
}

