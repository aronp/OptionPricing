package options_code.code;

public interface TreeVolHelper 
{
	void setup(int numSteps, double ttime, double tvolatility, double tforward, double trate);
	double getForward(int x, int y) throws Exception;
	double getMovUpProbability();
}
