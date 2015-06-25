package Graph_rand_walk;

public class edges {
	edges(int d, double w, int x, int y)
	{
		v = d ;
		wght = w;
		this.x =x;
		this.y = y;
	}
	double wght;  //weight or energy if the edge is taken
	int v;        // destination vertex
	int x ,y;   // coordinates which will help to plot the graph in matlab
	
	int dest()   // function to return  destination nodes
	{
		return v;
	}
	
	double wght()   // function to return weight
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
