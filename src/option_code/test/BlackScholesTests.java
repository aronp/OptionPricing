package option_code.test;

import static org.junit.Assert.*;

import org.junit.Test;

import options_code.code.BlackScholes;
import options_code.code.LNFlatForwardTreeHelper;
import options_code.code.NormalFlatForwardTreeHelper;
import options_code.code.SimpleTree;
import options_code.code.TreePayoutHelper;
import options_code.code.TreeVolHelper;

public class BlackScholesTests 
{
	public static void main(String[] args) 
	{
		// run tests
		// BSTests();
		try {
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static TreeVolHelper treeHelpers[] = { new LNFlatForwardTreeHelper(),new NormalFlatForwardTreeHelper()}; 

	@Test
	public void BSTests()
	{
		double maturity = 1;
		double vol = 0.1;
		double tolerance = 1e-10;

		for (maturity =0.01; maturity<5;maturity +=0.1)
		{
			for (vol = 0.01;vol < 1; vol+= 0.1)
			{
				double fwd = 1;

				double callPrice =BlackScholes.blackScholesForwardCall(
						fwd,
						vol*vol*maturity,
						fwd); 

				double putPrice =BlackScholes.blackScholesGeneralizedPutValue(
						fwd,
						vol,maturity,
						fwd,1); 


				// Check ATM call = 
				double err1 = callPrice - BlackScholes.ATMCall(fwd, vol*vol*maturity);
				double err2 = putPrice-callPrice;


				assertTrue(Math.abs(err1)< tolerance);
				assertTrue(Math.abs(err2)< tolerance);
			}
		}
	}

	@Test
	//  Test that our tree pricer is close to BS.
	// Test that American is >= European.
	public void LogTreeTests1() throws Exception
	{
		double maturity = 1;
		double vol = 0.1;
		double tolerance = 0.01;
		int numSteps = 200;
		double rate  = 0.05;
		double minPrice = 0.001;
		TreeVolHelper treehelper = new LNFlatForwardTreeHelper();


		for (maturity =0.01; maturity<5;maturity +=0.1)
			{
				for (vol = 0.1;vol < 0.5; vol+= 0.1)
				{
					for (double scale = 0.1; scale < 1.1; scale += 0.1)
					{

						double fwd = 1;
						double strike = fwd*scale;

						SimpleTree slt = new SimpleTree(numSteps, maturity, vol, fwd, rate, treehelper);

						double callPrice =BlackScholes.blackScholesForwardCall(
								fwd,
								vol*vol*maturity,
								strike)*Math.pow((1.0/(1.0+rate)),maturity); 
						double treePrice = slt.valueCall(strike);

						double americanTreePrice = slt.valuePayout(strike, slt.CallPayout());

						if (callPrice > minPrice)
						{
							assertTrue(Math.abs(callPrice/treePrice-1)< tolerance);
						}

						assertTrue(treePrice <= americanTreePrice);
					}
				}
			}
		}

	@Test
	// test that Big ITM options have immediate excercise
	public void LogTreeTests2() throws Exception
	{
		double maturity = 1;
		double vol = 0.05;
		double tolerance = 0.005;
		int numSteps = 100;
		double rate  = 0.05;
		double minPrice = 0.001;
		double minFwd = 0.01;

		for (TreeVolHelper treeHelper : treeHelpers)
		{
			for (maturity =0.01; maturity<5;maturity +=0.1)
			{
				for (vol = 0.1;vol < 0.5; vol+= 0.1)
				{

					double fwd = 1;
					double strike = minFwd;


					SimpleTree slt = new SimpleTree(numSteps, maturity, vol, fwd, rate,treeHelper);

					double callPrice =BlackScholes.blackScholesForwardCall(
							fwd,
							vol*vol*maturity,
							strike)*Math.pow((1.0/(1.0+rate)),maturity); 
					
					double treePrice = slt.valuePayout(strike, slt.CallPayout());

					double americanTreePrice = slt.valuePayout(strike, slt.AmericanCallPayout());

					double intrinsic = (fwd - strike);

					assertTrue(Math.abs( americanTreePrice/intrinsic - 1) < tolerance);
				}
			}
		}
	}

	@Test

	public void EurodollarTests() throws Exception
	{
		
		double underlying = 97.36;
		double rate = 0.0264;
		double maturity = 1.7333333333333334;
		double strikes[] = 
			{
				98.5,
				98.5,
				99.5,
				99.5,
				96,
				96,
				97.25, 
				97.25
		} ;
		boolean callPut[] = {true, false, false, true, true, false, false, true};
		
		double bpVol[] = {
				79.08,
				// 69.73,
				79.08,
				// 57.11,
				88.2,
				88.2,
				//45.7,
				66.27,
				66.27,
				67,
				67
		};
		 
		int numSteps = 50;
		
		double payoffUnit = Math.pow(1.0/(1.0+rate), maturity);
		
		for (int i = 0;i < strikes.length ;i++)
		{
			double vol = bpVol[i]/100;
			
			SimpleTree slt = new SimpleTree(numSteps, maturity, vol, underlying, rate, new NormalFlatForwardTreeHelper());

			double analyticValue = 0;
			double strike = strikes[i];
			
			TreePayoutHelper payoutHelper;
			TreePayoutHelper vanillapayoutHelper;
			if (callPut[i])
			{
				payoutHelper = slt.AmericanCallPayout();
				vanillapayoutHelper = slt.CallPayout();
				
				analyticValue = BlackScholes.NormalGeneralizedCallValue(
						underlying,
						vol,
						maturity,
						strike,
						payoffUnit);
			}
			else
			{
				payoutHelper = slt.AmericanPutPayout();
				vanillapayoutHelper = slt.PutPayout();
				analyticValue = BlackScholes.NormalGeneralizedPutValue(
						underlying,
						vol,
						maturity,
						strike,
						payoffUnit);

			}
			double value = slt.valuePayout(strikes[i], payoutHelper);
			double vanillavalue = slt.valuePayout(strikes[i], vanillapayoutHelper);
			
			System.out.println("Price is " + value + " analaytic " + analyticValue + " Best estimate " + (analyticValue + value-vanillavalue) + " American value " +(value-vanillavalue) );
		}
	}
}
