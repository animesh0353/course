
public class Tester {

	private static double getAngleInRadians(Location startSegment, Location endSegment, Location startGPS, Location endGPS)
		{
		//(x1, y1) and (x2, y2) are the start and end co-ordinates of the segment
		//(a1, b1) and (a2, b2) are the start and end co-ordinates of the GPS
		double x2MinusX1 = endSegment.longitude - startSegment.longitude;
		double y2MinusY1 = endSegment.latitude - startSegment.latitude;
		double a2MinusA1 = endGPS.longitude - startGPS.longitude;
		double b2MinusB1 = endGPS.latitude - startGPS.latitude;
		
		double dotProduct = x2MinusX1 * a2MinusA1 + y2MinusY1 * b2MinusB1;
		double rootSumOfSquares_Segment = Math.sqrt(x2MinusX1 * x2MinusX1 + y2MinusY1 * y2MinusY1);
		double rootSumOfSquares_GPS = Math.sqrt(a2MinusA1 * a2MinusA1 + b2MinusB1 * b2MinusB1);
		
		double angle = Math.acos(dotProduct / (rootSumOfSquares_Segment * rootSumOfSquares_GPS));

		return angle;
		}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Location start1 = new Location(0, 3, 2);
		Location end1 = new Location(0, 5, 5);
		Location start2 = new Location(0, -1, 6);
		Location end2 = new Location(0, 4, 2);
		System.out.println("angle = " + getAngleInRadians(start1, end1, start2, end2));
	}

}
