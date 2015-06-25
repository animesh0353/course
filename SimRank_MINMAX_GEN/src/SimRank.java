

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;



public class SimRank
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
//    	int voterCount  = 0;
//    	int movieCount = 0;
    
    	int voterMaxId = 0;
    	
//        Properties properties = new Properties();
        

        Map<Integer, ArrayList<Integer>> voterGraph  = new HashMap<Integer, ArrayList<Integer>>();
//        Map<Integer, ArrayList<Integer>> moviesGraph = new HashMap<Integer, ArrayList<Integer>>();
        
        try {
       	
//        	properties.load(new FileInputStream("/home/animesh/Desktop/datamining/HW3/ques1/ml-100k/u.info"));
        	
//        	userCount  = Integer.parseInt(properties.getProperty("users"));
//        	movieCount = Integer.parseInt(properties.getProperty("items"));
        	

            File file = new File("/home/animesh/Desktop/datamining/HW3/ques1/Wiki-Vote.txt");
            BufferedReader in = new BufferedReader(new FileReader(file));
            
            String string;
			while ((string = in.readLine())!=null) {
				if(string.charAt(0) == '#') continue;
				String[] str = string.split("\t");
				
				int voter    = Integer.parseInt(str[0]);
				
				int votedFor    = Integer.parseInt(str[1]);

				if(voterMaxId < votedFor)	voterMaxId = votedFor;
				
				updateMap(voterGraph, votedFor, voter);
//				updateMap(moviesGraph, movie, user);
			
			}
			
			
	    
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    

        SimRankGen simRankGen = new SimRankGen(voterMaxId);
        simRankGen.computeSimRank(voterGraph);
       
       
    }
//    public static void main(String[] args) throws FileNotFoundException
//    {
//    	
//    	try {
//           	
////        	properties.load(new FileInputStream("/home/animesh/Desktop/datamining/HW3/ques1/ml-100k/u.info"));
//        	
////        	userCount  = Integer.parseInt(properties.getProperty("users"));
////        	movieCount = Integer.parseInt(properties.getProperty("items"));
//        	
//
//            File file = new File("/home/animesh/Desktop/datamining/HW3/ques1/voterSimRank");
//            BufferedReader in = new BufferedReader(new FileReader(file));
//            
//            File file2 = new File("/home/animesh/Desktop/datamining/HW3/ques1/voterSimRank1");
//            BufferedWriter in2 = new BufferedWriter(new FileWriter(file));
//            
//            String string;
//			while ((string = in.readLine())!=null) {
//				
//				String[] str = string.split(" ");
//				
//				if(Double.parseDouble(str[5]) == 0.0)
//					continue;
//				else {
//					in2.write(string);
//				}
////				updateMap(moviesGraph, movie, user);
//			
//			}
//			in.close();
//			in2.close();
//	    
//		} catch (NumberFormatException | IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    }
//  
}

