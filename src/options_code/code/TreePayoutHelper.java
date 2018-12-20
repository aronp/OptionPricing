package options_code.code;

public interface TreePayoutHelper 
{
	double finalPayout(double forward, double strike);
	double midPayout(double forward, double strike, double expectedValue);
}
