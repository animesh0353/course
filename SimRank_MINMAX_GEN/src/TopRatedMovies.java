import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class TopRatedMovies {

	private static void updateMap(Map<Integer, ArrayList<Integer>> map, int K, int V) {
		ArrayList<Integer> value = map.get(K);
		if(value == null)
		{
			value = new ArrayList<Integer>();
			map.put(K, value);
		}
		value.add(V);
	}

	static void topRatedMovies(int[] arr, int[] rating, int c) 
	{
		int key = arr[c-1];
		int val = rating[c-1];
		boolean bool = false;
		int i;
		for(i=c-2; i>=0; i-- )
		{
			if(rating[i] < val)
			{
				rating[i+1] = rating[i];
				arr[i+1] = arr[i];
			}
			else {
				rating[i+1] = val;
				arr[i+1] = key;
				bool = true;
				break;
			}
		}
		if(!bool)
		{
			rating[i+1] = val;
			arr[i+1] = key;
		}
	}


	static void topSimilarMovies(int[] arr, double[] similar, int c) 
	{
		int key = arr[c-1];
		double val = similar[c-1];
		boolean bool = false;
		int i;
		for(i=c-2; i>=0; i-- )
		{
			if(similar[i] < val)
			{
				similar[i+1] = similar[i];
				arr[i+1] = arr[i];
			}
			else {
				similar[i+1] = val;
				arr[i+1] = key;
				bool = true;
				break;
			}
		}
		if(!bool)
		{
			similar[i+1] = val;
			arr[i+1] = key;
		}
	}



	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Map<Integer, ArrayList<Integer>> moviesGraph = new HashMap<Integer, ArrayList<Integer>>();

		try {

//			File file = new File("/home/animesh/Desktop/datamining/HW3/ques1/ml-100k/u.data");
			File file = new File("/home/animesh/Desktop/datamining/HW3/ques1/Wiki-Vote.txt");
			BufferedReader in = new BufferedReader(new FileReader(file));

			String string;
			while ((string = in.readLine())!=null) {

				if(string.charAt(0) == '#') continue;
				String[] str = string.split("\t");

				int user    = Integer.parseInt(str[0]);

				int movie    = Integer.parseInt(str[1]);

				updateMap(moviesGraph, movie, user);

			}



		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int[] topHundredMovies = new int[100];
		int[] topHundredMoviessize = new int[100];
		int c = 0;
		Iterator<Map.Entry<Integer, ArrayList<Integer>>> itr = moviesGraph.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<Integer, ArrayList<Integer>> entry = (Map.Entry<Integer, ArrayList<Integer>>) itr.next();
			if(c<100)
			{
				topHundredMovies[c] = entry.getKey();
				topHundredMoviessize[c] = entry.getValue().size();
				++c;
				topRatedMovies(topHundredMovies, topHundredMoviessize, c);
			}
			else {
				int val = entry.getValue().size();
				int key = entry.getKey();
				if(val > topHundredMoviessize[c-1])
				{
					topHundredMovies[c-1] = key;
					topHundredMoviessize[c-1] = val;
					topRatedMovies(topHundredMovies, topHundredMoviessize, c);
				}
			}
		}
		for (int i = 0; i < topHundredMoviessize.length; i++) {
			System.out.println(topHundredMovies[i]+" "+topHundredMoviessize[i]);
		} 

		int[] k = {5,10,25,50,100};
		double[][] simRankMovie = new double[9000][9000];
		try {

			File file = new File("/home/animesh/Desktop/datamining/HW3/ques1/voterSimRank");
			BufferedReader in = new BufferedReader(new FileReader(file));

			String string;
			while ((string = in.readLine())!=null) {

				String[] str = string.split(" ");

				int movie1   = Integer.parseInt(str[1]);

				int movie2    = Integer.parseInt(str[3]);
				simRankMovie[movie1][movie2] = Double.parseDouble(str[5]);



			}

			in.close();

		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		double[][] similar = new double[100][100];
		int[][] movies = new int[100][100];
		c = 0;
		for(int j=0; j<100; j++)
		{
			for(int i=0; i<9000; i++) {

				if(topHundredMovies[j] != i)
				{
					if(c<100)
					{

						movies[j][c] = i;
						similar[j][c] = simRankMovie[topHundredMovies[j]][i];
						++c;
						topSimilarMovies(movies[j], similar[j], c);
					}
					else {
						double val = simRankMovie[topHundredMovies[j]][i];
						int key = i;
						if(val > similar[j][c-1])
						{
							movies[j][c-1] = key;
							similar[j][c-1] = val;
							topSimilarMovies(movies[j], similar[j], c);
						}
					}
				}
			}

		}
		for (int i = 0; i < 100; i++) {
			for (int j = 0; j < 100; j++) {
				System.out.print(movies[i][j]+" ");
			}
			
			System.out.println();
		}
	}

}
