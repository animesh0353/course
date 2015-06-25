
/**
 * This class contains information about a location in the map.
 * The data members are a unique id, a latitude, and a longitude
 * @author stanley
 *
 */
public class Location
	{
	public long id;
	public double latitude;
	public double longitude;

	/**
	 * Creates a new instance given an id, latitude, and longitude.
	 * @param id A unique identifier of the location
	 * @param latitude The latitude of the location
	 * @param longitude The longitude of the location
	 */
	Location(long id, double latitude, double longitude)
		{
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		}
	}