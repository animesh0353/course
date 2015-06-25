import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class DataPreprocesser
	{
	public static String LOCATION_FILE = "/home/animesh/Desktop/datamining/project/dataset/Nodes.txt";
	public static String HIGHWAY_SEGMENTS_FILE = "/home/animesh/Desktop/datamining/project/dataset/HighwaySegments.txt";
	public static String HIGHWAY_SEGMENTS_FILE_OUT = "/home/animesh/Desktop/datamining/project/dataset/CleanedHighwaySegments.txt";
	public static String HIGHWAY_SEGMENTS_LAT_LON_FILE_OUT = "/home/animesh/Desktop/datamining/project/dataset/HighwaySegmentsLatLon.txt";

	public static Map<Long, Location> idMap = new HashMap<Long, Location>();
	public static Set<Long> setPoint = new HashSet<Long>();
	
	public static Map<Long, Location> readLocationFile()
		{
		File file = new File(LOCATION_FILE);
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			
			String string;
			while ((string = in.readLine()) != null)
				{
				//split the input line on comma
				String[] subStrings = string.split(",");
				//remove preceding "(" from subStrings[0]
				subStrings[0] = subStrings[0].substring(1);
				//remove preceding "LLA(" from subStrings[1]
				subStrings[1] = subStrings[1].substring(4);
				
				long key = Long.parseLong(subStrings[0]);
				Location location = new Location(key, Double.parseDouble(subStrings[1]), Double.parseDouble(subStrings[2]));
				idMap.put(key, location);
				}
			in.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return idMap;
		}
	
	public static void readHighwaySegmentsFileAndWriteFile()
		{
		File file = new File(HIGHWAY_SEGMENTS_FILE);
		File file_out = new File(HIGHWAY_SEGMENTS_FILE_OUT);
		File file2_out = new File(HIGHWAY_SEGMENTS_LAT_LON_FILE_OUT);
		
		try
			{
			BufferedReader in = new BufferedReader(new FileReader(file));
			BufferedWriter out = new BufferedWriter(new FileWriter(file_out));
			BufferedWriter out2 = new BufferedWriter(new FileWriter(file2_out));

			String string;
			while ((string = in.readLine()) != null)
				{
				//split the input line on "(" to remove "Segment"
				string = string.split("\\(")[1];
				//string no longer contains "Segment"
				//split the string on "["
				String[] subStrings = string.split("\\[");
				//subStrings[0] now contains the road segment's start and end location ids
				String[] subSubStrings = subStrings[0].split(",");
				//subSubStrings[0] now contains the road segment's start id
				long startId = Long.parseLong(subSubStrings[0]);
				if(!setPoint.contains(startId))
					setPoint.add(startId);
				//subSubStrings[1] contains the road segment's end id
				long endId = Long.parseLong(subSubStrings[1]);
				if(!setPoint.contains(endId))
					setPoint.add(endId);
				//subStrings[1] contains the other locations in the segment, length, and additional info
				subSubStrings = subStrings[1].split("\\]");
				//subSubStrings[1] now contains the length of the segment and additional info
				String[] subSubSubStrings = subSubStrings[1].split(",");
				//subSubSubStrings[1] new contains the length of the segment
				double length = Double.parseDouble(subSubSubStrings[1]);
				
				//subSubString[0] contains the other locations in the segment
				subSubSubStrings = subSubStrings[0].split(",");
				ArrayList<Long> internalLocations = new ArrayList<Long>();
				for(String str : subSubSubStrings)
					internalLocations.add(Long.parseLong(str));
				
				StringBuffer stringBuffer = new StringBuffer();
				stringBuffer.append(Long.toString(startId));
				stringBuffer.append(";");
				stringBuffer.append(Long.toString(endId));
				stringBuffer.append(";");
				stringBuffer.append(Double.toString(length));
				stringBuffer.append(";");
				stringBuffer.append(internalLocations.get(0));
				for(int i = 1 ; i < internalLocations.size() ; ++i)
					{
					stringBuffer.append(",");
					stringBuffer.append(internalLocations.get(i));
					}
				stringBuffer.append("\n");
				
//				System.out.print(stringBuffer.toString());
				
				out.write(stringBuffer.toString());
				}
			for(Long id : setPoint)
			{
				StringBuffer strBuffer = new StringBuffer();
				Location loc = idMap.get(id);
				strBuffer.append(Long.toString(loc.id));
				strBuffer.append(",");
				strBuffer.append(Double.toString(loc.longitude));
				strBuffer.append(",");
				strBuffer.append(Double.toString(loc.latitude));
				strBuffer.append("\n");
				out2.write(strBuffer.toString());
			}
			in.close();
			out.close();
			out2.close();
			} 
		catch (Exception e)
			{
			// TODO: handle exception
			}
		}
	
//	public static void main(String[] args)
//		{
//		readLocationFile();
//		readHighwaySegmentsFileAndWriteFile();
//		
//		
//		
//		
//		
//		
//		
//		
//	}

}
