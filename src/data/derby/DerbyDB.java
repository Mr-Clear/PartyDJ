package data.derby;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import common.*;
import data.*;

public class DerbyDB implements IData
{
	Connection conn = null;
	HashMap<Integer, Track> masterList;
	final HashMap<String, Integer> listIndices = new HashMap<String, Integer>();
	final HashMap<String, String> settings = new HashMap<String, String>();
	final HashSet<SettingListener> settingListener = new HashSet<SettingListener>();
	final HashSet<MasterListListener> masterListListener = new HashSet<MasterListListener>();
	
	public final String version = "0.2a";
	
	public DerbyDB(String dbName) throws OpenDbException
	{
		String driver = "org.apache.derby.jdbc.EmbeddedDriver";
		String connectionURL = "jdbc:derby:" + dbName;
		
		try
		{
		    Class.forName(driver); 
		}
		catch(java.lang.ClassNotFoundException e)
		{
			throw new OpenDbException("Datenbanksystem Derby nicht gefunden.", e);
		}
		
		try
		{
			conn = DriverManager.getConnection(connectionURL);
			conn.setAutoCommit(false);
			String dbVersion = null;
			try
			{
				dbVersion = readSetting("DBVersion", "unknown");
			}
			catch (SettingException e)
			{
				throw new OpenDbException(e);
			}
			
			//System.out.println(dbVersion + " " + version + " " + version.equals(dbVersion));
			
			if(!version.equals(dbVersion))
			{
				if(!UpdateDB.update(this, dbVersion, version))
					throw new OpenDbException("Update der Datenbank fehlgeschlagen.\nAlte Verion: " + dbVersion + "\nNeue Version: " + version);
			}
		}
		catch (SQLException e)
		{
			conn = createDb(dbName);
			try
			{
				writeSetting("DBID", String.format("%8H", new java.util.Random().nextLong()).replace(' ', '0'));
				writeSetting("DBVersion", version);
			}
			catch (SettingException e1)
			{
				throw new OpenDbException(e1);
			}
		}
	}

	Connection createDb(String Name) throws OpenDbException
	{
		String driver = "org.apache.derby.jdbc.EmbeddedDriver";
		String connectionURL = "jdbc:derby:" + Name + ";create=true";
		
		try
		{
		    Class.forName(driver); 
		}
		catch(java.lang.ClassNotFoundException e)
		{
			throw new OpenDbException(e);
		}
		
		Connection conn = null;
		try
		{
			conn = DriverManager.getConnection(connectionURL);
			conn.setAutoCommit(false);
			
			Statement s = conn.createStatement();
			s.executeUpdate("CREATE TABLE SETTINGS (NAME VARCHAR(32) NOT NULL, VALUE VARCHAR(256), PRIMARY KEY (NAME))");
			
			s.executeUpdate("CREATE TABLE FILES (" +
					"INDEX INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY, " +
					"PATH VARCHAR(1024) NOT NULL, " +
					"SEARCHNAME VARCHAR(256) NOT NULL, " +
					"NAME VARCHAR(256) NOT NULL," +
					"DURATION DOUBLE DEFAULT 0," +
					"SIZE BIGINT DEFAULT 0, " +
					"PROBLEM SMALLINT DEFAULT 0, " +
					"INFO LONG VARCHAR, " +
					"PRIMARY KEY (INDEX), " +
					"UNIQUE (PATH))");
			s.executeUpdate("CREATE UNIQUE INDEX PATH ON FILES (PATH)");
			s.executeUpdate("CREATE INDEX SEARCHNAME ON FILES (SEARCHNAME)");
			
			s.executeUpdate("CREATE TABLE LISTS " +
					"(INDEX INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY, " +
					"NAME VARCHAR(32) NOT NULL, " +
					"DESCRYPTION VARCHAR(128), " +
					"PRIMARY KEY (INDEX), " +
					"UNIQUE (NAME))");
			
			s.executeUpdate("CREATE TABLE LISTS_CONTENT (LIST INTEGER NOT NULL, INDEX INTEGER NOT NULL, POSITION INTEGER NOT NULL)");
			s.executeUpdate("CREATE INDEX LIST ON LISTS_CONTENT (LIST)");
			s.executeUpdate("CREATE INDEX POSITION ON LISTS_CONTENT (POSITION)");
			
			s.close();
			conn.commit();
			
			return conn;
		}
		catch (SQLException e)
		{
			if(conn != null)
			{
				try
				{
					conn.rollback();
				}
				catch (SQLException e1)
				{
					throw new OpenDbException("Rollback fehlgeschlagen!", e);
				}
			}
			throw new OpenDbException(e.getNextException().getMessage(), e);
		}
	}
	
	PreparedStatement prepareStatement(String SQL, String... parameters) throws SQLException
	{
		PreparedStatement ps = conn.prepareStatement(SQL);
		for(int i = 0; i < parameters.length; i++)
			ps.setString(i + 1, parameters[i]);
		return ps;
	}
	
	int executeUpdate(String SQL, String... parameters) throws SQLException
	{
		synchronized(conn)
		{
			int ret = prepareStatement(SQL, parameters).executeUpdate();
			return ret;
		}
	}
	
	int executeUpdate(String SQL, long parameter) throws SQLException
	{
		synchronized(conn)
		{
			PreparedStatement ps = conn.prepareStatement(SQL);
			ps.setLong(1, parameter);
			int ret = ps.executeUpdate();
			return ret;
		}
	}
	
	ResultSet queryRS(String SQL, String... parameters) throws SQLException
	{
		return prepareStatement(SQL, parameters).executeQuery();
	}

	
	int queryInt(String SQL, String... parameters) throws SQLException
	{
		synchronized(conn)
		{
			int ret = queryInt(queryRS(SQL, parameters));
			return ret;
		}
	}

	int queryInt(ResultSet rs) throws SQLException
	{
		if(rs.next())
			return rs.getInt(1);
		else
			throw new SQLException("Kein Eintag gefunden:");
	}
	
	String queryString(String SQL, String... parameters) throws SQLException
	{
		synchronized(conn)
		{
			String ret = queryString(queryRS(SQL, parameters));
			return ret;
		}
	}
	
	String queryString(ResultSet rs) throws SQLException
	{
		if(rs.next())
			return rs.getString(1);
		else
			return null;
	}
	
	public HashMap<Integer, Track> getMasterList() throws ListException
	{
		if(masterList == null)
		{
			masterList = new HashMap<Integer, Track>();
			synchronized(conn)
			{
				try
				{
					ResultSet rs = queryRS("SELECT * FROM FILES");
					while(rs.next())
					{
						masterList.put(rs.getInt("index"), new Track(	rs.getInt("INDEX"),
																rs.getString("PATH"),
																rs.getString("NAME"),
																rs.getDouble("DURATION"),
																rs.getLong("SIZE"),
																shortToProblem(rs.getShort("PROBLEM")),
																rs.getString("INFO")));
					}					
					rs.close();
					conn.commit();
				}
				catch (SQLException e)
				{
					throw new ListException(e);
				}
			}
		}
		return masterList;
	}
	
	public ArrayList<Track> readList(String listName, String searchString, common.SortOrder order) throws ListException
	{
		if(searchString != null)
			searchString = makeSearchString(searchString);
		
		try
		{
			synchronized(conn)
			{
				PreparedStatement ps;
				if(listName == null)	// Inhalt aus MasterList
				{
	
					String statement;
					if(searchString == null)
						statement = "SELECT INDEX FROM FILES";
					else
						statement = "SELECT INDEX FROM FILES WHERE SEARCHNAME LIKE ?";
										
					switch(order)
					{
					case NONE:
						break;
					case MASTERLISTINDEX:
						statement += " ORDER BY INDEX";
						break;
					case NAME:
					case DEFAULT:
						statement += " ORDER BY SEARCHNAME";
						break;
					case PATH:
						statement += " ORDER BY PATH";
						break;
					case DURATION:
						statement += " ORDER BY DURATION";
						break;
					case SIZE:
						statement += " ORDER BY SIZE";
						break;
					case POSITION:
						throw new ListException("SortOrder.POSITION wird von der Hauptliste nicht unterstützt.");
					}
					
					ps = conn.prepareStatement(statement);
					
					if(searchString != null)
					{
						searchString = makeSearchString(searchString.replace("*", "%"));
						if(searchString.charAt(0) == '^')
							searchString = searchString.substring(1);
						else
							searchString = "%" + searchString;
						
						if(searchString.charAt(searchString.length() - 1) == '$')
							searchString = searchString.substring(0, searchString.length() - 1);
						else
							searchString = searchString + "%";
						
						ps.setString(1, searchString);
					}
				}
				else					// Inhalt aus ClientList
				{
					int listIndex = getListIndex(listName);
					// Testen ob Liste existiert
					if(listIndex == -1)
					{
						// Wenn nicht, Liste erstellen
						addList(listName);
						return new ArrayList<Track>();
					}
					else
					{
	
						String statement;
						if(searchString == null)
							statement = "SELECT INDEX FROM LISTS_CONTENT WHERE LIST = ?";
						else
							statement = "SELECT INDEX FROM FILES, LISTS_CONTENT WHERE LIST = ? AND FILES.INDEX = LISTS_CONTENT.INDEX AND FILES.SEARCHNAME LIKE ?";
											
						switch(order)
						{
						case NONE:
							break;
						case DEFAULT:
						case POSITION:
							statement += " ORDER BY POSITION";
							break;
						case MASTERLISTINDEX:
							statement += " ORDER BY FILES.INDEX";
							break;
						case NAME:
							statement += " ORDER BY SEARCHNAME";
							break;
						case PATH:
							statement += " ORDER BY PATH";
							break;
						case DURATION:
							statement += " ORDER BY DURATION";
							break;
						case SIZE:
							statement += " ORDER BY SIZE";
							break;
						}
						
						ps = conn.prepareStatement(statement);
						
						ps.setInt(1, listIndex);
						
						if(searchString != null)
							ps.setString(2, searchString);
					}
				}
	
				ArrayList<Track> list = new ArrayList<Track>();
				
				synchronized(conn)
				{
					ResultSet rs = ps.executeQuery();
	
					while(rs.next())
						list.add(masterList.get(rs.getInt(1)));
					
					rs.close();
					conn.commit();
				}
				
				return list;
			}
		}
		catch (SQLException e)
		{
			throw new ListException(e);
		}
	}
	
	public int addTrack(Track track) throws ListException
	{
		try
		{
			int index;
			try
			{
				index = checkIndex(track.path);
			}
			catch(ListException e)
			{
				index = -1;
			}
				
			if(index == -1)
			{
				if(track.name == null)
					track.name = track.path.substring(track.path.lastIndexOf("\\") + 1, track.path.lastIndexOf("."));
				
				synchronized(conn)
				{
					PreparedStatement ps = conn.prepareStatement("INSERT INTO FILES (PATH, SEARCHNAME, NAME, DURATION, SIZE, PROBLEM, INFO) VALUES(?, ?, ?, ?, ?, ?, ?)");
					ps.setString(1, track.path);
					ps.setString(2, makeSearchString(track.name));
					ps.setString(3, track.name);
					ps.setDouble(4, track.duration);
					ps.setLong(5, track.size);
					ps.setShort(6, problemToShort(track.problem));
					ps.setString(7, track.info);
					ps.executeUpdate();
					conn.commit();
				}
				
				index = checkIndex(track.path);
				conn.commit();
				track.index = index;
				
				if(masterList != null)
				{
					masterList.put(track.index, track);
				}
					
				synchronized(masterListListener)
				{
					for(MasterListListener listener : masterListListener)
						listener.trackAdded(track);
				}					
			}
			else
				conn.commit(); //commit nach checkIndex()
			
			track.index = index;
			return index;
		}
		catch (SQLException e)
		{
			throw new ListException(e);
		}
	}
	
	public void updateTrack(Track track) throws ListException
	{
		synchronized(conn)
		{
			try
			{
				queryInt("SELECT INDEX FROM FILES WHERE INDEX = ?", Integer.toString(track.index));
			}
			catch (SQLException e)
			{
				throw new ListException("Track existiert nicht in Datenbank.", e);
			}
			
			PreparedStatement ps;
			try
			{
				ps = conn.prepareStatement("UPDATE FILES SET PATH = ?, SEARCHNAME = ?, NAME = ?, DURATION = ?, SIZE = ?, PROBLEM = ?, INFO = ? WHERE INDEX = ?");
				ps.setString(1, track.path);
				ps.setString(2, makeSearchString(track.name));
				ps.setString(3, track.name);
				ps.setDouble(4, track.duration);
				ps.setLong(5, track.size);
				ps.setShort(6, problemToShort(track.problem));
				ps.setString(7, track.info);
				ps.setInt(8, track.index);
				ps.executeUpdate();
				conn.commit();
			}
			catch (SQLException e)
			{
				throw new ListException(e);
			}
		}
		
		if(masterList != null)
		{
			masterList.put(track.index, track);
		}
		
		synchronized(masterListListener)
		{
			for(MasterListListener listener : masterListListener)
				listener.trackChanged(track);
		}
	}
	
	public void updateTrack(Track track, Track.TrackElement element) throws ListException
	{
		synchronized(conn)
		{
			try
			{
				queryInt("SELECT INDEX FROM FILES WHERE INDEX = ?", Integer.toString(track.index));
			}
			catch (SQLException e)
			{
				throw new ListException("Track existiert nicht in Datenbank.", e);
			}
			
			PreparedStatement ps;
			try
			{
				int lastSet = 2;
				switch(element)
				{
				case PATH:
					ps = conn.prepareStatement("UPDATE FILES SET PATH = ? WHERE INDEX = ?");
					ps.setString(1, track.path);
					break;
				case NAME:
					ps = conn.prepareStatement("UPDATE FILES SET SEARCHNAME = ?, NAME = ? WHERE INDEX = ?");
					ps.setString(1, makeSearchString(track.name));
					ps.setString(2, track.name);
					lastSet = 3;
					break;
				case DURATION:
					ps = conn.prepareStatement("UPDATE FILES SET DURATION = ? WHERE INDEX = ?");
					ps.setDouble(1, track.duration);
					break;
				case SIZE:
					ps = conn.prepareStatement("UPDATE FILES SET SIZE = ? WHERE INDEX = ?");
					ps.setLong(1, track.size);
					break;
				case PROBLEM:
					ps = conn.prepareStatement("UPDATE FILES SET PROBLEM = ? WHERE INDEX = ?");
					ps.setShort(1, problemToShort(track.problem));
					break;
				case INFO:
					ps = conn.prepareStatement("UPDATE FILES SET INFO = ? WHERE INDEX = ?");
					ps.setInt(1, track.index);
					break;
					default:
						throw new IllegalArgumentException("Unbekanntes Element");
				}
				
				ps.setInt(lastSet, track.index);
				ps.executeUpdate();
				conn.commit();
			}
			catch (SQLException e)
			{
				throw new ListException(e);
			}
		}
		
		if(masterList != null)
		{
			masterList.put(track.index, track);
		}
		
		synchronized(masterListListener)
		{
			for(MasterListListener listener : masterListListener)
				listener.trackChanged(track);
		}
	}
	
	public void deleteTrack(Track track) throws ListException
	{
		if(track == null)
			throw new NullPointerException("Kein Track übergeben.");
		synchronized(conn)
		{
			try
			{

				executeUpdate("DELETE FROM LISTS_CONTENT WHERE INDEX = ?", Integer.toString(track.index));
				executeUpdate("DELETE FROM FILES WHERE INDEX = ?", Integer.toString(track.index));
				conn.commit();
			}
			catch (SQLException e)
			{
				try
				{
					conn.rollback();
				}
				catch (SQLException e1)
				{
					throw new ListException("Rollback fehlgeschlagen!", e1);
				}
				throw new ListException(e);
			}
		}

		
		if(masterList != null)
		{
			masterList.remove(track.index);
		}
		
		synchronized(masterListListener)
		{
			for(MasterListListener listener : masterListListener)
				listener.trackDeleted(track);
		}
	}
	
	int checkIndex(String trackPath) throws ListException
	{
		try
		{
			return queryInt("SELECT INDEX FROM FILES WHERE PATH = ?", trackPath);
		}
		catch (SQLException e)
		{
			throw new ListException(e);
		}
	}
	
	public void addMasterListListener(MasterListListener listener)
	{
		masterListListener.add(listener);
	}
	public void removeMasterListListener(MasterListListener listener)
	{
		masterListListener.remove(listener);
	}
	
	public void addList(String listName) throws ListException
	{
		synchronized(conn)
		{
			try
			{
				executeUpdate("INSERT INTO LISTS (NAME) VALUES (?)", listName);
				conn.commit();
			}
			catch (SQLException e)
			{
				if(e instanceof SQLIntegrityConstraintViolationException)
					throw new ListException("Liste " + listName + " existiert bereits.", e);
				throw new ListException(e);
			}
		}
	}
	
	int getListIndex(String listName)
	{
		synchronized(listIndices)
		{
			//Wenn möglich aus temporärer Liste lesen
			if(listIndices.containsKey(listName))
			{
				return listIndices.get(listName);
			}
			//Ansonsten aus Datenbank lesen
			else
			{
				try
				{
					int listIndex = queryInt("SELECT INDEX FROM LISTS WHERE NAME = ?", listName);
					listIndices.put(listName, listIndex);
					return listIndex;
				}
				catch (SQLException e)
				{
					return -1;
				}
			}
		}
	}
	
	public void removeList(String listName) throws ListException
	{
		try
		{
			synchronized(conn)
			{
				int listIndex = getListIndex(listName);
				PreparedStatement ps = conn.prepareStatement("DELETE FROM LISTS WHERE NAME = ?");
				ps.setString(1, listName);
				ps.executeUpdate();
				
				ps = conn.prepareStatement("DELETE FROM LISTS_CONTENT WHERE LIST = ?");
				ps.setInt(1, listIndex);
				ps.executeUpdate();
				
				conn.commit();
			}
		}
		catch (SQLException e)
		{
			try
			{
				conn.rollback();
			}
			catch (SQLException e1)
			{
				throw new ListException("Rollback fehlgeschlagen!", e1);
			}
			throw new ListException(e);
		}
		
		// Aus temporärer Liste löschen
		if(listIndices.containsKey(listName))
			listIndices.remove(listName);
	}
	
	public void writeSetting(String name, String value) throws SettingException
	{
		synchronized(settings)
		{
			if(!value.equals(readSetting(name)))	// Prüfen ob Einstellung tatsächlich verändert wurde
			{
				// In Datenbank speichern
				try
				{
					if(readSetting(name) == null)
						executeUpdate("INSERT INTO SETTINGS VALUES(?, ?)", name, value);
					else
						executeUpdate("UPDATE SETTINGS SET VALUE = ? WHERE NAME = ?", value, name);
					conn.commit();
				}
				catch (SQLException e)
				{
					throw new SettingException(e);
				}
				
				// In temporäre Liste schreiben
				settings.put(name, value);
			}
		}
		
		synchronized(settingListener)
		{
			for(SettingListener listener : settingListener)
				listener.settingChanged(name, value);
		}
	}
	
	public String readSetting(String name) throws SettingException
	{
		return readSetting(name, null);
	}
	public String readSetting(String name, String defaultValue) throws SettingException
	{
		synchronized(settings)
		{
			//Wenn möglich aus temporärer Liste lesen
			if(settings.containsKey(name))
			{
				return settings.get(name);
			}
			//Ansonsten aus Datenbank lesen
			else
			{
				try
				{
					String value = queryString("SELECT VALUE FROM SETTINGS WHERE NAME = ?", name);
					if(value != null)
						settings.put(name, value);
					else						
						value = defaultValue;
					conn.commit();
					return value;
				}
				catch (SQLException e)
				{
					throw new SettingException(e);
				}
			}
		}
	}
	
	public void addSettingListener(SettingListener listener)
	{
		settingListener.add(listener);
	}

	public void removeSettingListener(SettingListener listener)
	{
		settingListener.remove(listener);
	}

	public void insertTrack(String listName, Track track) throws ListException
	{
		synchronized(conn)
		{
			try
			{
				int listIndex = getListIndex(listName);
				int position = queryInt("SELECT COUNT(LIST) FROM LISTS_CONTENT WHERE LIST = ?", Integer.toString(listIndex));
				PreparedStatement ps = conn.prepareStatement("INSERT INTO LISTS_CONTENT (LIST, INDEX, POSITION) VALUES(?, ?, ?)");
				ps.setInt(1, listIndex);
				ps.setInt(2, track.index);
				ps.setInt(3, position);
				ps.executeUpdate();
				conn.commit();
				
			}
			catch (SQLException e)
			{
				try
				{
					conn.rollback();
				}
				catch (SQLException e1)
				{
					throw new ListException("Rollback fehlgeschlagen!", e1);
				}
				throw new ListException(e);
			}
		}
	}
	
	public void insertTrackAt(String listName, Track track, int trackPosition) throws ListException
	{
		synchronized(conn)
		{
			try
			{
				int listIndex = getListIndex(listName);
				int size = queryInt("SELECT COUNT(LIST) FROM LISTS_CONTENT WHERE LIST = ?", Integer.toString(listIndex));
				
				// trackPosition korrigieren.
				if(trackPosition < 0)
					trackPosition = 0;
				if(trackPosition > size)
					trackPosition = size;
				
				for(int i = size; i >= trackPosition; i--)
				{
					PreparedStatement ps = conn.prepareStatement("UPDATE LISTS_CONTENT SET POSITION = ? WHERE POSITION = ? AND LIST = ?");
					ps.setInt(1, i + 1);
					ps.setInt(2, i);
					ps.setInt(3, listIndex);
					ps.executeUpdate();
				}
				
				PreparedStatement ps = conn.prepareStatement("INSERT INTO LISTS_CONTENT (LIST, INDEX, POSITION) VALUES(?, ?, ?)");
				ps.setInt(1, listIndex);
				ps.setInt(2, track.index);
				ps.setInt(3, trackPosition);
				ps.executeUpdate();
				
				conn.commit();
			}
			
			catch (SQLException e)
			{
				try
				{
					conn.rollback();
				}
				catch (SQLException e1)
				{
					throw new ListException("Rollback fehlgeschlagen!", e1);
				}
				throw new ListException(e);
			}
		}
	}

	public void removeTrack(String listName, int trackPosition) throws ListException
	{
		synchronized(conn)
		{
			try
			{
				int listIndex = getListIndex(listName);
				
				int size = queryInt("SELECT COUNT(LIST) FROM LISTS_CONTENT WHERE LIST = ?", Integer.toString(listIndex));
				
				
				// Wenn trackPosition ausserhalb der Liste, nichts löschen.
				if(trackPosition < 0 || trackPosition >= size)
					return;

				PreparedStatement ps = conn.prepareStatement("DELETE FROM LISTS_CONTENT WHERE POSITION = ? AND LIST = ?");
				ps.setInt(1, trackPosition);
				ps.setInt(2, listIndex);
				
				for(int i = trackPosition; i < size; i++)
				{
					ps = conn.prepareStatement("UPDATE LISTS_CONTENT SET POSITION = ? WHERE POSITION = ? AND LIST = ?");
					ps.setInt(1, i);
					ps.setInt(2, i + 1);
					ps.setInt(3, listIndex);
					ps.executeUpdate();
				}
				
				conn.commit();
			}

			catch (SQLException e)
			{
				try
				{
					conn.rollback();
				}
				catch (SQLException e1)
				{
					throw new ListException("Rollback fehlgeschlagen!", e1);
				}
				throw new ListException(e);
			}
		}
	}
	
	String makeSearchString(String original)
	{
		return original.toLowerCase(Locale.ENGLISH).replace('ä', 'a').replace('ö', 'o').replace('ü', 'u').replace('ß', 's');
	}
	
	short problemToShort(Track.Problem problem)
	{
		switch(problem)
		{
		case NONE:
			return 0;
		case FILE_NOT_FOUND:
			return 1;
		case CANT_PLAY:
			return 2;
		case OTHER:
		default:
			return -1;
		}
	}
	
	Track.Problem shortToProblem (short number)
	{
		switch(number)
		{
		case 0:
			return Track.Problem.NONE;
		case 1:
			return Track.Problem.FILE_NOT_FOUND;
		case 2:
			return Track.Problem.CANT_PLAY;
		default:
			return Track.Problem.OTHER;
		}
	}

	public void close() throws ListException
	{
		try
		{
			synchronized(conn)
			{
				conn.close();
			}
		}
		catch (SQLException e)
		{
			throw new ListException(e);
		}
	}
}
