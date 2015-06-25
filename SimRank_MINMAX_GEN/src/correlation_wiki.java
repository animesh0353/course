import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.PrimitiveIterator.OfDouble;


public class correlation_wiki {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int size_mv = 2625;
		
//		Map<Integer, ArrayList<Integer>> moviesGraph = new HashMap<Integer, ArrayList<Integer>>();
		
		int[] movie = new int[100];
		int[] movie2 = new int[100];
		int[][] arr = new int[100][100];
		int[][] arr2 = new int[100][100];

		try {

//			File file = new File("/home/animesh/Desktop/datamining/HW3/ques1/top100movies");
			File file = new File("/home/animesh/Desktop/datamining/HW3/ques1/RWR/top100WikiRWR");
			BufferedReader in = new BufferedReader(new FileReader(file));

			
			String string;
			int kl =0;
			boolean bool = false;
			
			while ((string = in.readLine())!=null) {

				if(!bool)
				{
					movie[0] = Integer.parseInt(string.split(" ")[0]);
					for (int i = 1; i < 100; i++) {
						string = in.readLine();
						String[] str = string.split(" ");
						movie[i] = Integer.parseInt(str[0]);
						//					updateMap(moviesGraph, movie, user);
					}
					bool = true;
				}
				else {
					
					String[] str = string.split(" ");
					for (int i = 0; i < 100; i++) {
						arr[kl][i] = Integer.parseInt(str[i]);
					}
					++kl;

				}
			}
			in.close();
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
//			File file = new File("/home/animesh/Desktop/datamining/HW3/ques1/RWR/top100RWR");
			File file = new File("/home/animesh/Desktop/datamining/HW3/ques1/RWR/rwr_wiki_100");

			BufferedReader in = new BufferedReader(new FileReader(file));
			int kl = 0;
			String string;
			while ((string = in.readLine())!=null){

				if(kl<100)
				{

				//					String[] str = string.split(" ");
				//					movie2[kl] = Integer.parseInt(str[0]);
				movie2[kl] = Integer.parseInt(string);

//				for (int i = 1 ; i < 101; i++) {
//					arr2[kl][i-1] = Integer.parseInt(str[i]);
//				}
//				++kl;

				string = in.readLine();
				String[] str2 = string.split(",");
				
				for (int i = 0 ; i < 100; i++) {
					str2[i] = str2[i].trim();
					String[] str = str2[i].split(" "); 
					
					arr2[kl][i] = Integer.parseInt(str[0]);
				}
				++kl;
				}

			}
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int[] k = {5, 10, 25,50,100};

		for (int i2 = 0; i2 < k.length; i2++) {

			double[] cor = new double[100];
			int[] sizeSet = new int[100];
			for (int i = 0; i < 100; i++) {
				Set set = new HashSet<Integer>();
				double sum = 0.0;
				boolean[] mark = new boolean[k[i2]];
				for (int j = 0; j < k[i2]; j++) {
					set.add(arr[i][j]);
					set.add(arr2[i][j]);
					boolean bool = false;
					int j2;
					for ( j2 = 0; j2 < k[i2]; j2++) {

						if(arr[i][j] == arr2[i][j2])
						{
							sum += Math.pow(Math.abs(j-j2), 2);
							bool =true;
							mark[j2] = true;
						}

					}
					if(!bool)
					{
						sum += Math.pow(Math.abs(k[i2] - j), 2);
					}
				}
				for (int j = 0; j < k[i2]; j++) {
					if(!mark[j])
						sum += Math.pow((k[i2]-j),2);
				}
				cor[i] = 1.0 - ((6 * sum)/ (set.size()*(set.size()*set.size() -1)));
				sizeSet[i] = set.size();

			}



			double sum = 0.0;
			for (int i = 0; i < cor.length; i++) {
				sum += cor[i];
			}
			sum /= 100;

			System.out.println(sum);
		}
		
	}

}

