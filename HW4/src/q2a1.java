import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;




public class q2a1 {
	
	static public class Pair
	{
		int key;
		int val;
		private Pair(int key, int val){
			this.key = key;
			this.val = val;
		}
		static public Pair of(int key, int val) { return new Pair(key, val); }
		public String toString(){ return (key+","+val+"\n"); }
		int keyRet()
		{
			return key;
		}
		int valRet()
		{
			return val;
		}
	}
	
	static public void accuracy(Pair[] arrOriginalPair, Pair[] arrSpaceSavingPair)
	{
		System.out.println("Accuracy for different value of k ");
		Set<Integer> originalSet = new HashSet<Integer>();
		Set<Integer> spaceSavingSet = new HashSet<Integer>();
		
		int[] kTop = {100,500, 1000, 5000};
		for( int k : kTop)
		{
			for (int i = 0; i < k; i++) {
				originalSet.add(arrOriginalPair[i].keyRet());
				spaceSavingSet.add(arrSpaceSavingPair[i].keyRet());
			}
			int count = 0;
			for(Integer keyInteger : spaceSavingSet)
			{
				if(originalSet.contains(keyInteger))
					count++;
			}
			double result = (double)count / (double) k;
			System.out.println(k+","+result);
			
			originalSet.clear();
			spaceSavingSet.clear();
		}
		
		for (int i = 0; i < arrSpaceSavingPair.length; i++) {
			Pair pair = arrSpaceSavingPair[i];
			
		}
		
	}
	
	static public void error(Pair[] arrOriginalPair, Pair[] arrSpaceSavingPair)
	{
		
		System.out.println("Error rate for different value of k ");
		int[] kTop = {100,500, 1000, 5000};
		for( int k : kTop)
		{
			double sum = 0.0;
			for (int i = 0; i < k; i++) {
				sum += (arrSpaceSavingPair[i].valRet() - arrOriginalPair[i].valRet());
			}
			double avg = sum / (double) k;
			
			System.out.println(k+","+avg);
		}
	}
	
	public static void printFun(Pair[] arrOriginalPair, Pair[] arrSpaceSavingPair) 
	{
		File file = new File("/home/animesh/Desktop/datamining/HW4/Xcsv.dat");
//		File file = new File("/home/animesh/Desktop/datamining/HW4/Ycsv.dat");
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
//			BufferedWriter out2 = new BufferedWriter(new FileWriter(file2));
			for (int i = 0; i < arrSpaceSavingPair.length; i++) {
				StringBuffer str = new StringBuffer();
				str.append(arrOriginalPair[i].keyRet()+","+arrOriginalPair[i].valRet()+","+arrSpaceSavingPair[i].keyRet()+","+arrSpaceSavingPair[i].valRet()+"\n");
				out.write(str.toString());
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}



	

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
//		Map<Integer, Integer> originalCountMap = new HashMap<Integer, Integer>();
//		Map<Integer, Integer> spaceSavingCountMap = new HashMap<Integer, Integer>();
		
		File file = new File("/home/animesh/Desktop/datamining/HW4/XOriginal.dat");
		File file2 = new File("/home/animesh/Desktop/datamining/HW4/aniX");
//		File file = new File("/home/animesh/Desktop/datamining/HW4/YOriginal.dat");
//		File file2 = new File("/home/animesh/Desktop/datamining/HW4/aniY");
		
		Pair[] arrOriginalPair = new Pair[10000];
		Pair[] arrSpaceSavingPair = new Pair[10000];


		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			BufferedReader in2 = new BufferedReader(new FileReader(file2));
			String str;
			int i = 0;
			while((str = in.readLine())!= null)
			{
				String[] strings = str.split(",");
//				originalCountMap.put(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]));
				arrOriginalPair[i] = Pair.of(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]));
				++i;
			}
			in2.readLine();  // removing headers
			in2.readLine();  // removing headers
			i = 0;
			while((str = in2.readLine())!= null)
			{
				String s1 = str.substring(0,7);
				String s2 = str.substring(7,13);
//				String[] strings = str.split(" ");
//				int key = 
//				for (int i = 0; i < strings.length; i++) {
//					
//				}
				
//				originalCountMap.put(Integer.parseInt(s1.trim()), Integer.parseInt(s2.trim()));
				arrSpaceSavingPair[i] = Pair.of(Integer.parseInt(s1.trim()), Integer.parseInt(s2.trim()));
				++i;
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		accuracy(arrOriginalPair, arrSpaceSavingPair);
		error(arrOriginalPair, arrSpaceSavingPair);
		
		printFun(arrOriginalPair,arrSpaceSavingPair);
		
	}

}
