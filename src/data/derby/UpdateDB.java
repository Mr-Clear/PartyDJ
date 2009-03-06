package data.derby;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import common.SettingException;

public class UpdateDB
{
	public static boolean update(DerbyDB data, String oldVersion, String newVersion) 
	{
		try
		{
			if((oldVersion.equals("unknown") || oldVersion.equals("0.1")) && newVersion.equals("0.2"))
				return to0_2(data);
			else
				return false;
		}
		catch (Exception e)
		{
			System.err.println("---------------------------");
			System.err.println("Fehler bei Update von Version " + oldVersion + " auf Version " + newVersion + ":");
			e.printStackTrace();
			System.err.println("---------------------------");
			return false;
		}
	}
	
	private static boolean to0_2(DerbyDB data) throws SettingException, SQLException
	{
		Statement s = data.conn.createStatement();
		s.executeUpdate("CREATE INDEX SEARCHNAME ON FILES (SEARCHNAME)");
		s.executeUpdate("DROP INDEX POSITION");
		s.executeUpdate("CREATE TABLE LISTS_CONTENT (LIST INTEGER NOT NULL, INDEX INTEGER NOT NULL, POSITION INTEGER NOT NULL)");
		s.executeUpdate("CREATE INDEX LIST ON LISTS_CONTENT (LIST)");
		s.executeUpdate("CREATE UNIQUE INDEX POSITION ON LISTS_CONTENT (POSITION)");
		
		List<Integer> lists = new ArrayList<Integer>();
		
		ResultSet rs = s.executeQuery("SELECT INDEX FROM LISTS");
		while(rs.next())
			lists.add(rs.getInt(1));
		rs.close();
		
		for(int list : lists)
		{
			try
			{
				List<Integer> elementsTrack = new ArrayList<Integer>();
				List<Integer> elementsPosition = new ArrayList<Integer>();
				rs = s.executeQuery("SELECT INDEX, POSITION FROM LIST_" + list);
				while(rs.next())
				{
					elementsTrack.add(rs.getInt(1));
					elementsPosition.add(rs.getInt(2));
				}
				rs.close();
				
				for(int i = 0; i < elementsPosition.size(); i++)
				{
					PreparedStatement ps = data.conn.prepareStatement("INSERT INTO LISTS_CONTENT VALUES(?, ?, ?)");
					ps.setInt(1, list);
					ps.setInt(2, elementsTrack.get(i));
					ps.setInt(3, elementsPosition.get(i));
					ps.executeUpdate();
					ps.close();
				}
				
				s.executeUpdate("DROP TABLE LIST_" + list);
			}
			catch (SQLException e)
			{
				//Wegen einem Fehler in der alten Version kann sein, dass die Liste nicht exisiert. 
			}
		}
		
		data.conn.commit();
		
		data.writeSetting("DBID", String.format("%8H", new java.util.Random().nextLong()).replace(' ', '0'));
		data.writeSetting("DBVersion", "0.2");
		return true;
	}
}
