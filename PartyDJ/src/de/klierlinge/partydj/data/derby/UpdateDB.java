package de.klierlinge.partydj.data.derby;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.data.SettingException;

/**
 *
 * Bringt eine veraltete Datenbank auf den neusten Stand.
 * 
 * @author Eraser
 */
public final class UpdateDB
{
	private UpdateDB(){}
	
	private static String version;
	public static boolean update(final DerbyDB data, final String oldVersion, final String newVersion) 
	{
		version = oldVersion;
		try
		{
			if(!newVersion.equals(data.version))
				return false;
			if(("unknown".equals(version) || "0.1".equals(version)))
				to0_2a(data);
			if("0.2".equals(version))
				v0_2to0_2a(data);
			if("0.2a".equals(version))
				return v0_2ato0_2b(data);
			if("0.2b".equals(version))
				return v0_2bto0_3(data);
			if("0.3".equals(version))
				return v0_3to0_4(data);
			return false;
		}
		catch (final Exception e)
		{
			Controller.getInstance().logError(Controller.IMPORTANT_ERROR, null, e, "Fehler bei Update von Version " + oldVersion + " auf Version " + newVersion);
			return false;
		}
	}
	
	private static void setVersion(final DerbyDB data, final String newVersion)
	{
		version = newVersion;
		data.writeSetting("DBVersion", newVersion);
	}
	
	private static boolean to0_2a(final DerbyDB data) throws SettingException, SQLException
	{
		try(Statement s = data.conn.createStatement())
		{
			s.executeUpdate("CREATE INDEX SEARCHNAME ON FILES (SEARCHNAME)");
			s.executeUpdate("DROP INDEX POSITION");
			s.executeUpdate("CREATE TABLE LISTS_CONTENT (LIST INTEGER NOT NULL, INDEX INTEGER NOT NULL, POSITION INTEGER NOT NULL)");
			s.executeUpdate("CREATE INDEX LIST ON LISTS_CONTENT (LIST)");
			s.executeUpdate("CREATE INDEX POSITION ON LISTS_CONTENT (POSITION)");
		
			final List<Integer> lists = new ArrayList<>();
			
			try (ResultSet rs = s.executeQuery("SELECT INDEX FROM LISTS"))
			{
				while (rs.next())
					lists.add(rs.getInt(1));
			}
			
			for(final int list : lists)
			{
				final List<Integer> elementsTrack = new ArrayList<>();
				final List<Integer> elementsPosition = new ArrayList<>();
				try (ResultSet rs = s.executeQuery("SELECT INDEX, POSITION FROM LIST_" + list))
				{
					while (rs.next())
					{
						elementsTrack.add(rs.getInt(1));
						elementsPosition.add(rs.getInt(2));
					}
				}
				
				for(int i = 0; i < elementsPosition.size(); i++)
				{
					data.executeUpdate("INSERT INTO LISTS_CONTENT VALUES(?, ?, ?)", list, elementsTrack.get(i), elementsPosition.get(i));
				}
				
				s.executeUpdate("DROP TABLE LIST_" + list);
			}
		}
		
		data.conn.commit();
		
		data.writeSetting("DBID", String.format("%8H", new java.util.Random().nextLong()).replace(' ', '0'));
		setVersion(data, "0.2a");
		return true;
	}
	
	private static boolean v0_2to0_2a(final DerbyDB data) throws SettingException, SQLException
	{
		try (Statement s = data.conn.createStatement())
		{
			s.executeUpdate("DROP INDEX POSITION");
			s.executeUpdate("CREATE INDEX POSITION ON LISTS_CONTENT (POSITION)");
			
			data.conn.commit();
		}

		setVersion(data, "0.2a");
		return true;
	}
	
	private static boolean v0_2ato0_2b(final DerbyDB data) throws SettingException, SQLException
	{
		try(Statement s = data.conn.createStatement())
		{
			s.executeUpdate("ALTER TABLE LISTS DROP COLUMN DESCRYPTION");
			s.executeUpdate("ALTER TABLE LISTS ADD DESCRIPTION LONG VARCHAR");
			s.executeUpdate("CREATE INDEX LIST_NAMES ON LISTS (NAME)");
			data.conn.commit();
		}
		
		setVersion(data, "0.2b");
		return true;
	}
	
	private static boolean v0_2bto0_3(final DerbyDB data) throws SettingException, SQLException
	{
		try(Statement s = data.conn.createStatement())
		{
			s.executeUpdate("ALTER TABLE LISTS ADD PRIORITY SMALLINT DEFAULT 0");
		}
		data.conn.commit();
		
		setVersion(data, "0.3");
		return true;
	}
	
	private static boolean v0_3to0_4(final DerbyDB data) throws SettingException, SQLException
	{
		final List<String[]> settings = new ArrayList<>();
		try(ResultSet rs = data.queryRS("SELECT * FROM SETTINGS"))
		{
			while(rs.next())
				settings.add(new String[]{rs.getString(1), rs.getString(2)});
		}
		data.conn.commit();
		
		data.executeUpdate("DROP TABLE SETTINGS");
		
		data.executeUpdate("CREATE TABLE SETTINGS (NAME VARCHAR(64) NOT NULL, VALUE LONG VARCHAR, PRIMARY KEY (NAME))");
		data.executeUpdate("CREATE INDEX SETTING ON SETTINGS (NAME)");
		
		for(final String[] setting : settings)
			data.executeUpdate("INSERT INTO SETTINGS VALUES(?, ?)", setting[0], setting[1]);
		
		data.conn.commit();
		setVersion(data, "0.4");
		
		return true;
	}
}
