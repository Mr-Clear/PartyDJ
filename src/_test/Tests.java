package _test;

import java.io.File;

public class Tests
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		File f = new File("C:\\ding");
		System.out.println(f.compareTo(new File("C:/Dang")));
	}
	
	static double f(int i)
	{
		double min = -80;
		double max = 6.0206;
		double fa = 172.17390699942;
		return Math.log((i + 1) * fa) / Math.log(101 * fa) * (max - min) + min;
	}

}
