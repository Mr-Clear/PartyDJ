package _test;

public class Tests
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		for(int i = 0; i <= 100; i += 10)
		{
			System.out.println(i + ": " + f(i));
		}
	}
	
	static double f(int i)
	{
		double min = -80;
		double max = 6.0206;
		double fa = 172.17390699942;
		return Math.log((i + 1) * fa) / Math.log(101 * fa) * (max - min) + min;
	}

}
