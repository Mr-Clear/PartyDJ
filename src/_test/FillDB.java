package _test;
import data.OpenDbException;
import data.derby.DerbyDB;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import lists.ListException;
import lists.data.DbTrack;



public class FillDB
{
	/**
	 * @param args Braucht keiner. 
	 * @throws IOException 
	 */
	public static void mmain(String[] args) throws IOException
	{
		
		DerbyDB data = null;
		try
		{
			data = new DerbyDB("DerbyDB/PDJ-Data");
		}
		catch (OpenDbException e)
		{
			e.printStackTrace();
			System.exit(1);
			return;
		}
				
		BufferedReader r = new BufferedReader(new FileReader("\\\\Merkur\\Musik\\Playlists\\Quelle.m3u"));
		String line;
		int i = 0;
		System.out.println("Start.");
		while ((line = r.readLine()) != null)
		{
			String name = line.substring(line.lastIndexOf("\\") + 1, line.lastIndexOf("."));
			try
			{
				boolean fast = false;
				if(!fast)
				{
					File file = new File(line);
					long size;
					DbTrack.Problem problem;
					if(file.exists())
					{
						size = file.length();
						problem = DbTrack.Problem.NONE;
					}
					else
					{
						size = 0;
						problem = DbTrack.Problem.FILE_NOT_FOUND;
					}
					data.addTrack(new DbTrack(0, line, name, 0, size, problem, null));
				}
				else
					data.addTrack(new DbTrack(0, line, name, 0, 0, DbTrack.Problem.NONE, null));
			}
			catch (ListException e)
			{
				e.printStackTrace();
				if(true)
					break;
				System.out.println("Fehler:");
				System.out.println(i + ": " + line);
				System.out.println(i + ": " + name);
			}
			if (i++ % 100 == 0)
				System.out.println(i + ": " + name);
		}
		System.out.println("Done.");
		
	}

}
