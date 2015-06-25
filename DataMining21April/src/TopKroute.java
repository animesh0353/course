import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

import org.w3c.dom.NodeList;


public class TopKroute {
	
	static SegmentProcessTree recursionTree(SegmentProcess startNode , int M, Map<Long, ArrayList<SegmentProcess>> nodeIdMap)
	{
		int depth = 1;
		nodeTree nodeTemp = new nodeTree(startNode);
		nodeTree rootTree = nodeTemp;
		Queue<nodeTree> queue = new LinkedList<nodeTree>();
		queue.add(nodeTemp);
		while(!queue.isEmpty())
		{
			nodeTemp = queue.remove();
			depth = nodeTemp.depth;
			ArrayList<Node> nodeList = nodeIdMap.get(nodeTemp.nodeCurr.nodeEnd);
			if(nodeList != null)
			{
				for(Node node2: nodeList)
				{
					nodeTree nodeTemp2 = new nodeTree(node2);
					nodeTemp2.depth = depth + 1;
					nodeTemp.nodeChild.add(nodeTemp2);
					if(nodeTemp2.depth < M)
						queue.add(nodeTemp2);
				}
			}
			//nodeTemp.
			depth++;
		}
		
		
		return rootTree;
	}
	
	static void printFun(nodeTree rootNode)
	{
		Queue<nodeTree> queue = new LinkedList<nodeTree>();
		queue.add(rootNode);
		while(!queue.isEmpty())
		{
			nodeTree nodeTemp = queue.remove();
			nodeTemp.nodeCurr.nodePrint();
			for(nodeTree nodeTemp2: nodeTemp.nodeChild)
			{
				queue.add(nodeTemp2);
			}
		}
	}
	
	
	static void rMNP(nodeTree rootNode, double[] profit, ArrayList<ArrayList<Node>> nodeList, int k, int M, int breadth)
	{
		int depth = M-k+1;
		if(depth == M)
		{
			double profitTemp = 0.0;
			ArrayList<Node> routeTempNode = new ArrayList<Node>();
			profitTemp = rootNode.nodeCurr.fare;
			routeTempNode.add(rootNode.nodeCurr); 
			profit[0] = profitTemp;
			nodeList.addAll(routeTempNode);
		}
		else {
			double[] profitTemp = new double[10];   // Finding profit for only TOP-10 Route
			ArrayList<Node> routeTempNode = new ArrayList<Node>();
			ArrayList<nodeTree> nodeTree3 = rootNode.nodeChild;
			Iterator<nodeTree> itr = nodeTree3.iterator();
			int temp = -1;
			while (itr.hasNext()) {
				temp++;
				
				nodeTree nodeTemp2 = itr.next();
				nodeList.clear();
				rMNP(nodeTemp2, profit, nodeList, k-1, M, -1);
				if(profit[temp]>profitTemp)
				{
					profitTemp[temp] = profit[temp];
					routeTempNode.clear();
					routeTempNode.addAll(nodeList);
				}
			}
			profit[0] = rootNode.nodeCurr.fare + (1 - rootNode.nodeCurr.pickUpProb)*profitTemp;
			nodeList.clear();
			nodeList.addAll(routeTempNode);
			nodeList.add(rootNode.nodeCurr);
			
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Map<Long, ArrayList<Node>> nodeIdMap = new HashMap<Long, ArrayList<Node>>();
		Node node;
		
		File file = new File("/home/animesh/Desktop/datamining/dataset_to_animesh/buffer_op.txt");
		try {
			Scanner in = new Scanner(file);
			while (in.hasNextLine()) {
				String string = in.nextLine();
				String[] str = string.split(" ");
				node = new Node(Long.parseLong(str[0]), Long.parseLong(str[1]), Double.parseDouble(str[2]), Double.parseDouble(str[3]), Double.parseDouble(str[4]), Double.parseDouble(str[5]), Double.parseDouble(str[6]), Double.parseDouble(str[7]), Double.parseDouble(str[8]));
				ArrayList<Node> nodeList;
				long nodeId = Long.parseLong(str[0]);   // Mapping based on starting nodeID
				if(nodeIdMap.containsKey(nodeId))
				{
					nodeList = nodeIdMap.get(nodeId);     // If present then add it in arrayList
					nodeList.add(node);
				}
				else {
					nodeList = new ArrayList<Node>();
					nodeList.add(node);
					nodeIdMap.put(nodeId, nodeList);	// If not present then put new nodeId in HashMap
				}
				
			}
			
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int count = 0;
		for(ArrayList<Node> node2List : nodeIdMap.values())
		{
			for(Node nodeTemp : node2List)
			{
//				nodeTemp.nodePrint();
				count++;
			}
			
		}
		System.out.println(count);
		int M = 5; // Default
		long start = 1167787090l;
		long end = 65337071l;
		ArrayList<Node> temp = nodeIdMap.get(start);
		Node startNode = null;
		for(Node nodeTemp: temp)
		{
			if(nodeTemp.nodeEnd == end)
			{
				startNode = nodeTemp;
				break;
			}
		}
		nodeTree nodeRoot = recursionTree(startNode, M, nodeIdMap);
//		printFun(nodeRoot);
		
		double[] profit = new double[10];    // Finding only TOP-10 Route
		ArrayList<ArrayList<Node>> nodeList = new ArrayList<ArrayList<Node>>();
		int k = 5;
		rMNP(nodeRoot, profit, nodeList, k, M, -1);
		System.out.println("========================================================================");
		System.out.println(profit[0]);
		Iterator<ArrayList<Node>> itrNodeList = nodeList.iterator();
		while (itrNodeList.hasNext()) {
			ArrayList<Node> arrayList = (ArrayList<Node>) itrNodeList.next();
			Iterator<Node> itr = arrayList.iterator();
			while (itr.hasNext()) {
				Node node2 = (Node) itr.next();
				node2.nodePrint2();
			}
			System.out.println("====================================================================");
		}
		
	}

}