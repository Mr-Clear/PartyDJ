package _test;

import java.io.IOException;
import java.util.prefs.BackingStoreException;

public class Tests
{

	/**
	 * @param args
	 * @throws IOException 
	 * @throws BackingStoreException 
	 */
	public static void main(String[] args) throws IOException
	{
		System.out.println(new java.io.File("C:\\Test").hashCode());
		System.out.println(new java.io.File("C:/a/../Test").hashCode());
		System.out.println("C:\\Test".hashCode());
		System.out.println("C:\\TEST".hashCode());
	}
	
	static double f(int i)
	{
		double min = -80;
		double max = 6.0206;
		double fa = 172.17390699942;
		return Math.log((i + 1) * fa) / Math.log(101 * fa) * (max - min) + min;
	}

}
