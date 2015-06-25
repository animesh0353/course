import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;



public class datacleaning {

static public class Pair
{
	int key;
	int val;
	int err;
	private Pair(int key, int val, int err){
		this.key = key;
		this.val = val;
		this.err = err;
	}
	static public Pair of(int key, int val, int err) { return new Pair(key, val, err); }
	public String toString(){ return (key+","+val+","+"\n"); }
	int keyRet()
	{
		return key;
	}
	int valRet()
	{
		return val;
	}
	int errRet()
	{
		return err;
	}
}


public static void printFun(Pair[] arrSpaceSavingPair) 
{
//	File file = new File("/home/animesh/Desktop/datamining/HW4/Xcsv.dat");
	File file = new File("/home/animesh/Desktop/datamining/HW4/Ycsv.dat");
	try {
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		for (int i = 0; i < arrSpaceSavingPair.length; i++) {
			StringBuffer str = new StringBuffer();
			str.append(arrSpaceSavingPair[i].keyRet()+","+arrSpaceSavingPair[i].valRet()+","+arrSpaceSavingPair[i].errRet()+"\n");
			out.write(str.toString());
		}
		
		out.close();
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
}





public static void main(String[] args) throws IOException {
	// TODO Auto-generated method stub
	
	
//	File file2 = new File("/home/animesh/Desktop/datamining/HW4/aniX");
	File file2 = new File("/home/animesh/Desktop/datamining/HW4/aniY");
	
	Pair[] arrSpaceSavingPair = new Pair[10000];


	try {
		BufferedReader in2 = new BufferedReader(new FileReader(file2));
		String str;
		int i = 0;
		in2.readLine();  // removing headers
		in2.readLine();  // removing headers
		i = 0;
		while((str = in2.readLine())!= null)
		{
			String s1 = str.substring(0,7);
			String s2 = str.substring(7,13);
			String s3 = str.substring(13);
//			String[] strings = str.split(" ");
//			int key = 
//			for (int i = 0; i < strings.length; i++) {
//				
//			}
			
//			originalCountMap.put(Integer.parseInt(s1.trim()), Integer.parseInt(s2.trim()));
			arrSpaceSavingPair[i] = Pair.of(Integer.parseInt(s1.trim()), Integer.parseInt(s2.trim()),Integer.parseInt(s3.trim()));
			++i;
		}
		
		
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	printFun(arrSpaceSavingPair);
	
}

}
