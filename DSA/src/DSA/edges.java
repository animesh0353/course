package DSA;

public class edges {
	edges(long d, double w,double bowlt, int x, int y)
	{
		v = d ;
		wght = w;
		this.bowlt = bowlt;
		this.x =x;
		this.y = y;
	}
	double wght, bowlt = 0.0;
	long v;
	int x ,y;
	
	long dest()
	{
		return v;
	}
	
	double wght()
	{
		return wght;
	}
	
	double bowlt()
	{
		return bowlt;
	}
	int co_x()
	{
		return x;
	}
	int co_y()
	{
		return y;
	}
}
