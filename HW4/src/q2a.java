import java.awt.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;


public class q2a {
	
	 
	
	
	static public class Pair
	{
		int key;
		int val;
		private Pair(int key, int val) {
			// TODO Auto-generated constructor stub
			this.key = key;
			this.val = val;
		}
		static public Pair of(int key, int val) { return new Pair(key, val); }
		public String toString(){ return (key+","+val+"\n"); }
	}

	static public Comparator<Pair> asc = new Comparator<Pair>(){
        @Override
        public int compare(Pair b1, Pair b2){
            return (b1.val - b2.val);
        }
    };
    static public Comparator<Pair> desc = new Comparator<Pair>(){
        @Override
        public int compare(Pair b1, Pair b2){
            return (b2.val - b1.val);
        }
    };

	
	
	
	
	static public void main(String[] args) throws IOException
	{
		
		
		Map<Integer, Integer> keyFrequency = new HashMap<Integer, Integer>();
		File file = new File("/home/animesh/Desktop/datamining/HW4/Xnew.dat");
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str;
			while((str = in.readLine())!=null)
			{
//				String string = str.trim();
				int key = Integer.parseInt(str);
				if(keyFrequency.containsKey(key))
				{
					int val = keyFrequency.get(key);
					keyFrequency.put(key, val+1);
				}
				else {
					keyFrequency.put(key, 1);
				}
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int size = keyFrequency.size();
		Pair[] arr = new Pair[size];

		int i = 0;
		for (Map.Entry<Integer, Integer> entry : keyFrequency.entrySet()) {
			arr[i] = Pair.of(entry.getKey(), entry.getValue());
			i++;
		}
		
		File file1 = new File("/home/animesh/Desktop/datamining/HW4/XOriginal.dat");
		BufferedWriter out = new BufferedWriter(new FileWriter(file1));
		
		int count = 1;
		Arrays.sort(arr,desc);
		for (Pair pair : arr) {
			out.write(pair.toString());
			if(count == 10000)
				break;
			++count;
		}
 		out.close();
		System.out.println("hello");
	}
	
//	static public void main(String[] args) throws IOException
//	{
//		
//		
//		File file = new File("/home/animesh/Desktop/datamining/HW4/Y.dat");
//		File file2 = new File("/home/animesh/Desktop/datamining/HW4/Ynew.dat");
//		try {
//			BufferedReader in = new BufferedReader(new FileReader(file));
//			BufferedWriter out = new BufferedWriter(new FileWriter(file2));
//			String str;
//			int max = 0;
//			while((str = in.readLine())!=null)
//			{
//				String string = str.trim();
//				out.write(string+"\n");
//				if(max < Integer.parseInt(string))
//					max = Integer.parseInt(string);
//				
//			}
//			in.close();
//			out.close();
//			System.out.println(max);
//			
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
}
