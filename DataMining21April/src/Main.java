

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;


public class Main {
	/**
	 * timeSlot is the number of slot in a day 
	 */
	public static int noOfTimeSlot = 3;
	
	// startId and endId for segment
//	public static long start = 330665612l;
//    public static long end = 65325804l;
	
	// Path 2
	static long  start = 65339290l;
	static long end = 2819241004l;
	
	// Path 3
//	static long  start = 65360287l;
//	static long end = 2089587799l;
	
	// path 4
//	static long  start = 65339305l;
//	static long end = 259138847l;
	
	// Path 5
//	static long  start = 65352339l;
//	static long end = 65352344l;
	
	// Path 6
//	static long start = 65325369l;
//	static long end = 65325371l;
	
	
	// Path 6
//	static long start = 258758038l;
//	static long end = 65327138l;
	 
	// Path 7
	//static long start = 258758534L;
	//static long end = 65343044l;


	// Path 8  65325061			65334473
//	static long  start = 65325061l;
//	static long end = 65334473l;
	
	
			
	public static int M = 5;
	public static int k = 10;
	public static Map<Long, ArrayList<SegmentProcess>> segmentProcessIdMap = new HashMap<Long, ArrayList<SegmentProcess>>();
	public static String INPUT_SEGMENT_FILE = "CompleteSegmentData.txt";
	
	
	
	public static void populateSegmentProcessIdMap()
	{
		File file = new File(INPUT_SEGMENT_FILE);
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String string;
			while((string = in.readLine()) != null)
			{
				String[] str = string.split(";");
				String[] subString = str[0].split(",");
				Location startLocation = new Location(Long.parseLong(subString[0]), Double.parseDouble(subString[1]), Double.parseDouble(subString[2]));
				subString = str[1].split(",");
				Location endLocation = new Location(Long.parseLong(subString[0]), Double.parseDouble(subString[1]), Double.parseDouble(subString[2]));
				double segmentLength = Double.parseDouble(str[2]);
				subString = str[3].split(",");
				String[] subString2 = str[4].split(",");
				String[] subString3 = str[5].split(",");
				double distanceTravelled = 0.0;
				double freqSuccess = 0.0;
				double freqFailure = 0.0;
				for (int i = 0; i < subString.length; i++) {

					distanceTravelled += Double.parseDouble(subString[i]);
					freqSuccess += Double.parseDouble(subString2[i]);
					freqFailure += Double.parseDouble(subString3[i]);

				}
					
				distanceTravelled /= 24;  // to take average distance
				
				
				long timeToCrossSegment = Long.parseLong(str[6]);
				SegmentProcess segmentProcess = new SegmentProcess(startLocation, endLocation, segmentLength, distanceTravelled, freqSuccess, freqFailure, timeToCrossSegment);
				if(segmentProcessIdMap.containsKey(startLocation.id) == true)
					{
						ArrayList<SegmentProcess> segmentProcessesList = segmentProcessIdMap.get(startLocation.id);
						
						segmentProcessesList.add(segmentProcess);
					}
				else
					{
						ArrayList<SegmentProcess> segmentProcessesList = new ArrayList<SegmentProcess>();
						segmentProcessesList.add(segmentProcess);
						segmentProcessIdMap.put(startLocation.id, segmentProcessesList);
					}
			}
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	
	/**
	 * 
	 * @param rootToLeaves  all possible routes from start to end 
	 * @return routeProfitMap object consist of route and Net profit of corresponding routes 
	 */
	public static RouteProfitMap[] findAllNetProfit(ArrayList<ArrayList<SegmentProcess>> rootToLeaves)
	{
		int noOfRoutes = rootToLeaves.size();
		double[] allNetProfit = new double[noOfRoutes];
		RouteProfitMap[] routeProfitMaps = new RouteProfitMap[noOfRoutes];
		double netProfitOnROute = 0.0;

		for(int i = 0 ; i < noOfRoutes ; ++i)
		{
			double netProfitperSegment = 0.0;
			ArrayList<SegmentProcess> route = rootToLeaves.get(i);
			int routeSize = route.size();
			double failureProduct = 1.0;
			double potentialCost = route.get(0).potentialCost;
			double potentialEarning = route.get(0).potentialEarning;
			double pickUpProbabilty = route.get(0).findPickupProbability();
			netProfitperSegment = potentialEarning*pickUpProbabilty - potentialCost*(1 - pickUpProbabilty);
			for(int j = 1 ; j < routeSize ; ++j)
			{
				potentialCost = route.get(j).potentialCost;
				potentialEarning = route.get(j).potentialEarning;
				pickUpProbabilty = route.get(j).findPickupProbability();
				double currentSegmentNetProfit = potentialEarning*pickUpProbabilty - potentialCost*(1 - pickUpProbabilty);
				failureProduct *= (1 - route.get(j - 1).findPickupProbability());
				netProfitperSegment = currentSegmentNetProfit + ( failureProduct * netProfitperSegment);
				
				
			}
			netProfitOnROute = netProfitperSegment;
			routeProfitMaps[i] = new RouteProfitMap(route, netProfitOnROute);
			
		}

		return routeProfitMaps;
	}

	public static void topKRoute(RouteProfitMap[] routeProfitMap, int k) 
	{
		
		
//		PriorityQueue<RouteProfitMap> heap = new PriorityQueue<RouteProfitMap>();
//		for (int i = 0; i < routeProfitMap.length; i++) {
//			heap.add(routeProfitMap[i]);
//		}
//		for (int i = 0; i < routeProfitMap.length; i++) 
//		   System.out.println(heap.poll().netProfit);
		
//		ArrayList<RouteProfitMap> arrayList = new ArrayList<RouteProfitMap>();
//		for (int i = 0; i < routeProfitMap.length; i++) {
//			arrayList.add(routeProfitMap[i]);
//		}

//		Comparator<RouteProfitMap> descendingNetProfit = new Comparator<RouteProfitMap>()
//		{
//			@Override
//			public int compare(RouteProfitMap o1, RouteProfitMap o2)
//			{
//				// TODO Auto-generated method stub
//				if(o1.netProfit < o2.netProfit)
//					return 1;
//				else 
//					return 0;
//			}
//
//			
//		};
//		
//
//		Collections.sort(arrayList,descendingNetProfit);
		Arrays.sort(routeProfitMap);
		
//		RouteProfitMap[] n = new RouteProfitMap[rootToLeaves.length - 1];
//		    System.arraycopy(rootToLeaves, 0, n, 0, k );
//		    System.arraycopy(n, 0, rootToLeaves, 0, k);
//		    return routeProfitMap;
	}
	
	
//	public static ArrayList<ArrayList<SegmentProcess>> main2() {
	public static void main(String[] strings) {
//		int timeSlot =2;
		// Populate the road segment from CompleteSegmentData.txt
		
		populateSegmentProcessIdMap();
		
		System.out.println("Number of distinct start location segmentProcess "+segmentProcessIdMap.size());
		
		ArrayList<SegmentProcess> segmentProcessList = segmentProcessIdMap.get(start);
		SegmentProcess segmentProcess = null;
		for (SegmentProcess segmentProcessTemp : segmentProcessList) {
			if(segmentProcessTemp.endLocation.id == end)
			{
				segmentProcess = segmentProcessTemp;
				break;
			}
		}
		SegmentProcessTree segmentProcessTree = new SegmentProcessTree();
		segmentProcessTree = segmentProcessTree.recursionTree(segmentProcess, M, segmentProcessIdMap);
		segmentProcessTree.printFun(segmentProcessTree);
		
		ArrayList<ArrayList<SegmentProcess>> rootToLeaves = new ArrayList<ArrayList<SegmentProcess>>();
		ArrayList<SegmentProcess> tempList = new ArrayList<SegmentProcess>();
		tempList.add(segmentProcessTree.segmentProcess);
		
//		double minCost = findMinCost(rootToLeaves);
//		double maxCost = findMaxCost(rootToLeaves);
		
		SegmentProcessTree.topK(rootToLeaves,segmentProcessTree, tempList,1);
		
		// To check Repetition of segment we iterate over root to leaves
		
		ArrayList<ArrayList<SegmentProcess>> tempToremove = new ArrayList<ArrayList<SegmentProcess>>();
		for (int i = 0; i < rootToLeaves.size(); i++) {
			ArrayList<SegmentProcess> pathArrayList = rootToLeaves.get(i);
			Set<Long> set = new HashSet<Long>();
			set.add(pathArrayList.get(0).startLocation.id);
			set.add(pathArrayList.get(0).endLocation.id);
			boolean checkRepetion = false;
			for (int j = 1; j < pathArrayList.size(); j++) {
				SegmentProcess temp = pathArrayList.get(j);
				long endId = temp.endLocation.id;
				if (set.contains(endId)) {
					checkRepetion = true;
					break;
				}
				else
					set.add(endId);
			}
			
			if(checkRepetion)
				tempToremove.add(pathArrayList);
		}
		rootToLeaves.removeAll(tempToremove);
		

        //calculate the Net Profit of each route
        
       

        RouteProfitMap[] netProfitMap = findAllNetProfit(rootToLeaves);
        topKRoute(netProfitMap, netProfitMap.length);
        System.out.println("\n Net profit of the route");
        for(RouteProfitMap d : netProfitMap)
                System.out.println(d.netProfit);
               
		System.out.println("Total number of routes : "+netProfitMap.length);
		if(k > netProfitMap.length)
			topKRoute(netProfitMap, k);
		System.out.println("Top K routes : "+netProfitMap.length);
		
		System.out.println("******************************************************************\n");
		
		System.out.println(rootToLeaves.size() + " routes found..");
		
		HashSet<ArrayList<SegmentProcess>> routeSet = new HashSet<ArrayList<SegmentProcess>>();
		routeSet.addAll(rootToLeaves);
		
		rTree.mainRtree(netProfitMap);
		
	}






	

}
