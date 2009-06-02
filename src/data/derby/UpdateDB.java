package data.derby;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import basics.Controller;
import data.SettingException;

/**
 *
 * Bringt eine veraltete Datenbank auf den neusten Stand.
 * 
 * @author Eraser
 */
public class UpdateDB
{
	private static String version;
	public static boolean update(DerbyDB data, String oldVersion, String newVersion) 
	{
		version = oldVersion;
		try
		{
			if(!newVersion.equals(data.version))
				return false;
			if((version.equals("unknown") || version.equals("0.1")))
				to0_2a(data);
			if(version.equals("0.2"))
				v0_2to0_2a(data);
			if(version.equals("0.2a"))
				return v0_2ato0_2b(data);
			if(version.equals("0.2b"))
				return v0_2bto0_3(data);
			if(version.equals("0.3"))
				return v0_3to0_4(data);
			return false;
		}
		catch (Exception e)
		{
			Controller.getInstance().logError(Controller.IMPORTANT_ERROR, null, e, "Fehler bei Update von Version " + oldVersion + " auf Version " + newVersion);
			return false;
		}
	}
	
	private static void setVersion(DerbyDB data, String newVersion)
	{
		version = newVersion;
		data.writeSetting("DBVersion", newVersion);
	}
	
	private static boolean to0_2a(DerbyDB data) throws SettingException, SQLException
	{
		Statement s = data.conn.createStatement();
		s.executeUpdate("CREATE INDEX SEARCHNAME ON FILES (SEARCHNAME)");
		s.executeUpdate("DROP INDEX POSITION");
		s.executeUpdate("CREATE TABLE LISTS_CONTENT (LIST INTEGER NOT NULL, INDEX INTEGER NOT NULL, POSITION INTEGER NOT NULL)");
		s.executeUpdate("CREATE INDEX LIST ON LISTS_CONTENT (LIST)");
		s.executeUpdate("CREATE INDEX POSITION ON LISTS_CONTENT (POSITION)");
		
		List<Integer> lists = new ArrayList<Integer>();
		
		ResultSet rs = s.executeQuery("SELECT INDEX FROM LISTS");
		while(rs.next())
			lists.add(rs.getInt(1));
		rs.close();
		
		for(int list : lists)
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
				data.executeUpdate("INSERT INTO LISTS_CONTENT VALUES(?, ?, ?)", list, elementsTrack.get(i), elementsPosition.get(i));
			}
			
			s.executeUpdate("DROP TABLE LIST_" + list);
		}
		
		data.conn.commit();
		
		data.writeSetting("DBID", String.format("%8H", new java.util.Random().nextLong()).replace(' ', '0'));
		setVersion(data, "0.2a");
		return true;
	}
	
	private static boolean v0_2to0_2a(DerbyDB data) throws SettingException, SQLException
	{
		Statement s = data.conn.createStatement();

		s.executeUpdate("DROP INDEX POSITION");
		s.executeUpdate("CREATE INDEX POSITION ON LISTS_CONTENT (POSITION)");
		
		data.conn.commit();
		setVersion(data, "0.2a");
		return true;
	}
	
	private static boolean v0_2ato0_2b(DerbyDB data) throws SettingException, SQLException
	{
		Statement s = data.conn.createStatement();

		s.executeUpdate("ALTER TABLE LISTS DROP COLUMN DESCRYPTION");
		s.executeUpdate("ALTER TABLE LISTS ADD DESCRIPTION LONG VARCHAR");
		s.executeUpdate("CREATE INDEX LIST_NAMES ON LISTS (NAME)");
		data.conn.commit();
		
		setVersion(data, "0.2b");
		return true;
	}
	
	private static boolean v0_2bto0_3(DerbyDB data) throws SettingException, SQLException
	{
		Statement s = data.conn.createStatement();
		s.executeUpdate("ALTER TABLE LISTS ADD PRIORITY SMALLINT DEFAULT 0");
		data.conn.commit();
		
		setVersion(data, "0.3");
		return true;
	}
	
	private static boolean v0_3to0_4(DerbyDB data) throws SettingException, SQLException
	{
		final List<String[]> settings = new ArrayList<String[]>();
		ResultSet rs = data.queryRS("SELECT * FROM SETTINGS");
		while(rs.next())
			settings.add(new String[]{rs.getString(1), rs.getString(2)});
		rs.close();
		data.conn.commit();
		
		data.executeUpdate("DROP TABLE SETTINGS");
		
		data.executeUpdate("CREATE TABLE SETTINGS (NAME VARCHAR(64) NOT NULL, VALUE LONG VARCHAR, PRIMARY KEY (NAME))");
		data.executeUpdate("CREATE INDEX SETTING ON SETTINGS (NAME)");
		
		for(String[] setting : settings)
			data.executeUpdate("INSERT INTO SETTINGS VALUES(?, ?)", setting[0], setting[1]);
		
		data.conn.commit();
		setVersion(data, "0.4");
		
		return true;
	}
}
