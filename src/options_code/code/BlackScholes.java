package options_code.code;

public class BlackScholes 
{
//	static final org.apache.commons.math3.distribution.NormalDistribution normalDistribution = new org.apache.commons.math3.distribution.NormalDistribution();

	static double E = Math.E;
	static double Pi = Math.PI;
	
	// calculate price from forward.
	public static double blackScholesGeneralizedCallValue(
			double forward,
			double volatility,
			double optionMaturity,
			double optionStrike,
			double payoffUnit)
	{
		if(optionMaturity < 0) 
		{
			return 0;
		}
		else if(forward < 0) 
		{
			// We use max(X,0) = X + max(-X,0)
			return (forward - optionStrike) * payoffUnit + blackScholesGeneralizedCallValue(-forward, volatility, optionMaturity, -optionStrike, payoffUnit);
		}
		else if((forward == 0) || (optionStrike <= 0.0) || (volatility <= 0.0) || (optionMaturity <= 0.0))
		{
			// Limit case (where dPlus = +/- infty)
			return Math.max(forward - optionStrike,0) * payoffUnit;
		}
		else
		{
			// Calculate analytic value
			double valueAnalytic =blackScholesForwardCall(forward,volatility*volatility*optionMaturity,optionStrike) * payoffUnit;

			return valueAnalytic;
		}
	}

	public static double NormalGeneralizedCallValue(
			double forward,
			double volatility,
			double optionMaturity,
			double optionStrike,
			double payoffUnit)
	{
		if(optionMaturity < 0) 
		{
			return 0;
		}
		else if(forward < 0) 
		{
			// We use max(X,0) = X + max(-X,0)
			return (forward - optionStrike) * payoffUnit + NormalGeneralizedCallValue(-forward, volatility, optionMaturity, -optionStrike, payoffUnit);
		}
		else if((forward == 0) || (optionStrike <= 0.0) || (volatility <= 0.0) || (optionMaturity <= 0.0))
		{
			// Limit case (where dPlus = +/- infty)
			return Math.max(forward - optionStrike,0) * payoffUnit;
		}
		else
		{
			// Calculate analytic value
			double valueAnalytic =NormalForwardCall(forward,volatility*volatility*optionMaturity,optionStrike) * payoffUnit;

			return valueAnalytic;
		}
	}

	
	public static double blackScholesGeneralizedPutValue(
			double forward,
			double volatility,
			double optionMaturity,
			double optionStrike,
			double payoffUnit)
	{
		double callValue = blackScholesGeneralizedCallValue(
				forward,
				volatility,
				optionMaturity,
				optionStrike,
				1);
		double fwdputVal = callValue+optionStrike - forward;
		
		return fwdputVal*payoffUnit;
	}

	public static double NormalGeneralizedPutValue(
			double forward,
			double volatility,
			double optionMaturity,
			double optionStrike,
			double payoffUnit)
	{
		double callValue = NormalGeneralizedCallValue(
				forward,
				volatility,
				optionMaturity,
				optionStrike,
				1);
		double fwdputVal = callValue+optionStrike - forward;
		
		return fwdputVal*payoffUnit;
	}

	public static double blackScholesForwardCall(
			double fwd,
			double variance,
			double strike	)
	{
		double rho = Math.sqrt(variance);
		double mu = (-variance + 2*GeneralFunctions.Log(fwd))/2.0;
		double callPrice = (Math.pow(Math.E,mu + variance/2.0)*
			      (1 + Erf((mu + variance - Math.log(strike))/(Math.sqrt(2.0)*rho))) + 
			      strike*(-2 + Erfc((mu - Math.log(strike))/(Math.sqrt(2)*rho))))/2.0;
		
		return callPrice;
	}

	public static double NormalForwardCall(double fwd,double variance, double strike)
	{
		double rho = Math.sqrt(variance);
		double mu = fwd;

		double callVal = rho/(Math.pow(E, Power(mu - strike,2)/(2.0*Power(rho,2)))*Sqrt(2.0*Pi)) + 
		   ((mu - strike)*(1.0 + Erf((mu - strike)/(Sqrt(2.0)*rho))))/2.0;
		
		return callVal;
	}

	
	static double Power(double x,double y)
	{
		return Math.pow(x, y);
	}

	static double Sqrt(double x)
	{
		return Math.sqrt(x);
	}

	public static double ATMCall(double fwd, double variance)
	
	{
		double rho = Math.sqrt(variance);
		double mu = (-variance + 2*Math.log(fwd))/2.0;

		double callVal = Math.pow(Math.E,mu + Math.pow(rho,2)/2.)*Erf(rho/(2.*Math.sqrt(2.0)));
		
		return callVal;
	}

	public static double NormalATMCall(double fwd, double variance)
	
	{
		double rho = Math.sqrt(variance);
		double mu = (-variance + 2*Math.log(fwd))/2.0;

		double callVal = rho/Sqrt(2.0*Pi);
		
		return callVal;
	}

	
	public static double ATMvolAdjustCall(double fwd, double variance)
	{
		double rho = Math.sqrt(variance);
		double mu = (-variance + 2*Math.log(fwd))/2.0;
		
	double val = (Math.pow(Math.E,mu)*(-1 + Math.pow(Math.E,Math.pow(rho,2)/2.)*(1 + Erf(rho/Math.sqrt(2)))))/2.0;
	return val;
	}

	public static double Erf(double x)
	{
		return org.apache.commons.math3.special.Erf.erf(x);
	}

	public static double Erfc(double x)
	{
		return org.apache.commons.math3.special.Erf.erfc(x);
	}

}
