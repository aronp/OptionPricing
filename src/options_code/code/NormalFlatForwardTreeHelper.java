package options_code.code;

public class NormalFlatForwardTreeHelper implements TreeVolHelper
{
	double forward;
	double up;
	double down;
	double p;
	double q;
	
	public NormalFlatForwardTreeHelper()
	{
	}

	public void setup(int numSteps, double ttime, double tvolatility, double tforward, double trate) 
	{
		forward = tforward;
		double dt = ttime/(numSteps-1);
		up = tvolatility * Math.sqrt(dt);
		down = -up;
		p = 0.5;
		q = 1.0-p;
	}

	public double getForward(int x, int y) throws Exception
	{
		if (y > x+1)
		{
			throw new Exception ("Point not in tree");
		}
		double updown = up*y+(down*( x-y));
		return forward + updown;
	}

	public double getMovUpProbability() 
	{
		return p;
	}
}
