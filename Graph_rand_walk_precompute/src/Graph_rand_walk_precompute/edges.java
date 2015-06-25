package Graph_rand_walk_precompute;

public class edges {
	edges(int d, double w, int x, int y)
	{
		v = d ;
		wght = w;
		this.x =x;
		this.y = y;
		
	}
	double wght;
	double bowt;
	int v;
	int x ,y;
	
	int dest()
	{
		return v;
	}
	
	double wght()
	{
		return wght;
	}
	double bowt()
	{
		return bowt;
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
