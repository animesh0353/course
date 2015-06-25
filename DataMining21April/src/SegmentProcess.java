
public class SegmentProcess {

	/**
	 * timeSlot is the number of slot in a day 
	 */
	public static int timeSlot = 3;
	/**
	 * Is the mileage of the taxi in metres per litres.
	 */
	public static double MILEAGE = 5000;
	
	/**
	 * Is the Company fare to be paid for taking a Taxi to drive
	 */
	public static double COMPANY_FEE = 0.0012;
	/**
	 * Is the cost of gas in $ per litre.
	 */
	public static double GAS = 0.97;
	
	/**
	 * Is the fare charged per metres.
	 * TODO  check here fare
	 */
	public static double FARE_PER_METER = 0.00285;
	
	/**
	 * Contains information about the starting location of the segment.
	 */
	public Location startLocation;
	
	/**
	 * Contains information about the ending location of the segment.
	 */
	public Location endLocation;
	
	/**
	 * Is the length of the segment in metres.
	 */
	public double segmentLength;
	
	/**
	 * Is the Potential earning of the taxi in dollar.
	 */
	public double potentialEarning ;
	/**
	 * Is the potential cost incurred while crossing this segment by the taxi in dollar.
	 */
	public double potentialCost;
	
	/**
	 * Is the frequency of a customer being found whenever a taxi passed through 
	 * this segment in a particular duration of time of the day.
	 */
	public double freqSuccess;
	
	/**
	 * Is the frequency of a customer NOT being found whenever a taxi passed through 
	 * this segment in a particular duration of time of the day.
	 */
	public double freqFailure;

	/**
	 * Is the length of the distance in metres traveled from this segment.
	 */
	public double distanceTravelled;
	/**
	 * Is the time (in seconds) required to cross the segment from start location to end location.
	 */
	public double timeToCrossSegment;

	

	/**
	 * Creates a new instance given the start location, end location, segment length, frequency of success, 
	 * frequency of failure, and the time required to cross the segment.
	 * @param startLocation The start location
	 * @param endLocation The end location
	 * @param segmentLength The length of the road segment in metres
	 * @param freqSuccess The frequencies of success for different intervals of time of the day
	 * @param freqFailure The frequencies of failure for different intervals of time of the day
	 * @param timeToCrossSegment The time required to cross the road segment in seconds
	 */
	
	 public SegmentProcess(Location startLocation, Location endLocation, double segmentLength, double distanceTravelled, double freqSuccess, double freqFailure, double timeToCrossSegment)
	 {
		 this.startLocation = startLocation;
		 this.endLocation = endLocation;
		 this.segmentLength = segmentLength;
		 this.timeToCrossSegment = timeToCrossSegment;
		 this.potentialCost = this.calculatePotentialCost();
		 this.distanceTravelled = distanceTravelled;
		 this.freqSuccess = freqSuccess;
		 this.freqFailure = freqFailure;
		 potentialEarning = calculatePotentialEarning();

	 }	
	/**
	 * TODO:
	 * @param timeRange
	 * @return
	 */
	public double findPickupProbability()
		{
		return this.freqSuccess / (this.freqSuccess + this.freqFailure);
		}
	
	public double calculatePotentialCost()
		{
		return this.segmentLength * GAS / MILEAGE + this.timeToCrossSegment * COMPANY_FEE;
		}
	public double calculatePotentialEarning()
		{
		return distanceTravelled * FARE_PER_METER;
		}
	
	void segmentProcessPrint()
		{
		System.out.println(startLocation.id+" "+endLocation.id+" "+startLocation.longitude+" "+startLocation.latitude+" "+endLocation.longitude+" "+endLocation.latitude+" "+segmentLength+" "+distanceTravelled+" "+potentialEarning+" "+potentialCost+" "+timeToCrossSegment);
		}
	
	}
