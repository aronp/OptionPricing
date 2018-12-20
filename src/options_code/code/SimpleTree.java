package options_code.code;

public class SimpleTree 
{
	double [] data = null;
	int size;
	double time;
	double volatility;
	double forward;
	double dt;
	double rate;
	double df;
	TreeVolHelper treeHelper;
	
	public SimpleTree(int numSteps, double ttime, double tvolatility, double tforward, double trate, TreeVolHelper ttreeHelper)
	{
		treeHelper = ttreeHelper;
		data = new double[numSteps*(numSteps+1)/2];
		size = numSteps;
		time = ttime;
		volatility = tvolatility;
		forward = tforward;
		rate = trate;
		dt = time/(numSteps-1);
		df = Math.pow(Math.pow(1.0/(1.0+rate), time), 1.0/(numSteps-1.0));
		ttreeHelper.setup(numSteps, ttime, tvolatility, tforward, trate);
	}

	public void setVal(int x, int y, double val) throws Exception
	{
	int start = (x*(x+1))/2;
	
	if (y > x+1)
	{
		throw new Exception ("Point not in tree");
	}
	data[start+y] = val;
	}

	public double getVal(int x, int y) throws Exception
	{
	int start = (x*(x+1))/2;
	
	if (y > x+1 || x > size-1)
	{
		throw new Exception ("Point not in tree");
	}
	return data[start+y];
	}
	
	public double valuePayout(double strike,TreePayoutHelper payoutHelper) throws Exception
	{
		// fill in base
		for (int i = 0;i < size;i++)
		{
			double fwd = treeHelper.getForward(size -1, i);
			double payout = payoutHelper.finalPayout(fwd, strike);
			setVal(size-1, i, payout);
		}

		double p = treeHelper.getMovUpProbability();
		double q = 1.0-p;
		
		// now work back
		for (int j = size - 2;j >=0;j--)
		{
			for (int i = 0;i < j+1;i++)
			{
				double downval = getVal(j+1,i);
				double upval = getVal(j+1,i+1);
				double expectedValue = df * (downval *q + upval * p);
				double fwd = treeHelper.getForward(j, i);
				
				setVal(j,i,payoutHelper.midPayout(fwd, strike, expectedValue)); 
			}
		}
		return getVal(0,0);
	}

	
	public double valueCall(double strike) throws Exception
	{
		// fill in base
		for (int i = 0;i < size;i++)
		{
			double fwd = treeHelper.getForward(size -1, i);
			double payout = Math.max(fwd-strike, 0);
			setVal(size-1, i, payout);
		}

		double p = treeHelper.getMovUpProbability();
		double q = 1.0-p;
		
		// now work back
		for (int j = size - 2;j >=0;j--)
		{
			for (int i = 0;i < j+1;i++)
			{
				double downval = getVal(j+1,i);
				double upval = getVal(j+1,i+1);
				setVal(j,i, df * (downval *q + upval * p)); 
			}
		}
		return getVal(0,0);
	}
	
	public double valueAmericanCall(double strike) throws Exception
	{
		// fill in base
		
		for (int i = 0;i < size;i++)
		{
			double fwd = treeHelper.getForward(size -1, i);
			double payout = Math.max(fwd-strike, 0);
			setVal(size-1, i, payout);
		}
		double p = treeHelper.getMovUpProbability();
		double q = 1.0-p;
		// now work back
		for (int j = size - 2;j >=0;j--)
		{
			for (int i = 0;i < j+1;i++)
			{
				double downval = getVal(j+1,i);
				double upval = getVal(j+1,i+1);
				double fwd = treeHelper.getForward(j, i);
				double now = Math.max(fwd-strike, 0);
				setVal(j,i, Math.max(now,  df * (downval *q + upval * p))); 
			}
		}
		return getVal(0,0);
	}
	
	class CallPayout implements TreePayoutHelper
	{

		public double finalPayout(double forward, double strike) 
		{
			return Math.max(forward-strike, 0);
		}

		public double midPayout(double forward, double strike, double expectedValue) 
		{
			return expectedValue;
		}
	}
	
	class PutPayout implements TreePayoutHelper
	{
		public double finalPayout(double forward, double strike) 
		{
			return Math.max(strike-forward, 0);
		}

		public double midPayout(double forward, double strike, double expectedValue) 
		{
			return expectedValue;
		}
	}

	public TreePayoutHelper CallPayout()
	{
		return new CallPayout();
	}

	public TreePayoutHelper PutPayout()
	{
		return new PutPayout();
	}
	
	public TreePayoutHelper AmericanCallPayout()
	{
		return new AmericanCallPayout();
	}

	public TreePayoutHelper AmericanPutPayout()
	{
		return new AmericanPutPayout();
	}

	class AmericanCallPayout implements TreePayoutHelper
	{
	public double finalPayout(double forward, double strike) 
	{
		return Math.max(forward-strike, 0);
	}

	public double midPayout(double forward, double strike, double expectedValue) 
	{
		return Math.max(expectedValue, Math.max(forward-strike, 0));
	}
	}

	class AmericanPutPayout implements TreePayoutHelper
	{
	public double finalPayout(double forward, double strike) 
	{
		return Math.max(strike- forward, 0);
	}

	public double midPayout(double forward, double strike, double expectedValue) 
	{
		return Math.max(expectedValue, Math.max(strike - forward, 0));
	}
	}

	
}
