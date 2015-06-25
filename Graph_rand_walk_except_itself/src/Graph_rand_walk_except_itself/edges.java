package Graph_rand_walk_except_itself;

public class edges {
	edges(int d, double w, int x, int y)
	{
		v = d ;
		wght = w;
		this.x =x;
		this.y = y;
	}
	double wght;
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
	int co_x()
	{
		return x;
	}
	int co_y()
	{
		return y;
	}
}
