package options_code.code;

public class LNFlatForwardTreeHelper implements TreeVolHelper
{
	double forward;
	double up;
	double down;
	double p;
	double q;

	public LNFlatForwardTreeHelper()
	{
	}
	
	public void setup(int numSteps, double ttime, double tvolatility, double tforward, double trate) 
	{
		forward = tforward;
		double dt = ttime/(numSteps-1);
		up = Math.exp(tvolatility * Math.sqrt(dt));
		down = 1.0/up;
		p = (1-down)/(up-down);
		q = 1-p;
	}

	
	public double getForward(int x, int y) throws Exception 
	{
		if (y > x+1)
		{
			throw new Exception ("Point not in tree");
		}
		double updown = Math.pow(up, y)*Math.pow(down, x-y);
		return forward * updown;
	}

	public double getMovUpProbability() 
	{
		return p;
	}
}
