package data.derby;

import java.sql.*;
import common.SettingException;

public class UpdateDB
{
	public static boolean update(DerbyDB data, String oldVersion, String newVersion) 
	{
		try
		{
			if(oldVersion.equals("unknown") && newVersion.equals("0.1"))
					return unknownto0_1(data);
			else
				return false;
		}
		catch (SettingException e)
		{
			System.err.println("Update Error:");
			e.printStackTrace();
			return false;
		}
		catch (SQLException e)
		{
			System.err.println("Update Error:");
			e.printStackTrace();
			return false;
		}
	}
	
	private static boolean unknownto0_1(DerbyDB data) throws SettingException, SQLException
	{
		data.writeSetting("DBVersion", "0.1");
		return true;
	}
}
