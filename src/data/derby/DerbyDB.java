package data.derby;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import basics.CloseListener;
import lists.ListException;

import common.*;
import data.*;

public class DerbyDB implements IData, CloseListener
{
	Connection conn = null;
	HashMap<Integer, Track> masterList;
	final Map<String, Integer> listIndices = new HashMap<String, Integer>();
	final Map<String, String> settings = new HashMap<String, String>();
	final Set<SettingListener> settingListener = new HashSet<SettingListener>();
	final Set<ListListener> listListener = new HashSet<ListListener>();
	
	public final String version = "0.2b";
	
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
			s.executeUpdate("CREATE TABLE SETTINGS (NAME VARCHAR(32) NOT NULL, VALUE LONG VARCHAR, PRIMARY KEY (NAME))");
			s.executeUpdate("CREATE INDEX SETTING ON SETTINGS (NAME)");
			
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
					"DESCRIPTION LONG VARCHAR, " +
					"PRIMARY KEY (INDEX), " +
					"UNIQUE (NAME))");
			s.executeUpdate("CREATE INDEX LIST_NAMES ON LISTS (NAME)");
			
			s.executeUpdate("CREATE TABLE LISTS_CONTENT (LIST INTEGER NOT NULL, INDEX INTEGER NOT NULL, POSITION INTEGER NOT NULL, PRIMARY KEY (LIST, POSITION))");
			//s.executeUpdate("CREATE UNIQUE INDEX POSITIONS ON LISTS_CONTENT (LIST, POSITION)");
			
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
	
	public HashMap<Integer, Track> readMasterList() throws ListException
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
	
	public List<Track> readList(String listName, String searchString, data.SortOrder order) throws ListException
	{
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
		}
		
		if(order == null)
			order = data.SortOrder.DEFAULT;
		
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
							statement = "SELECT FILES.INDEX FROM FILES, LISTS_CONTENT WHERE LIST = ? AND FILES.INDEX = LISTS_CONTENT.INDEX";
						else
							statement = "SELECT FILES.INDEX FROM FILES, LISTS_CONTENT WHERE LIST = ? AND FILES.INDEX = LISTS_CONTENT.INDEX AND FILES.SEARCHNAME LIKE ?";
											
						switch(order)
						{
						case NONE:
							break;
						case DEFAULT:
						case POSITION:
							statement += " ORDER BY LISTS_CONTENT.POSITION";
							break;
						case MASTERLISTINDEX:
							statement += " ORDER BY FILES.INDEX";
							break;
						case NAME:
							statement += " ORDER BY FILES.SEARCHNAME";
							break;
						case PATH:
							statement += " ORDER BY FILES.PATH";
							break;
						case DURATION:
							statement += " ORDER BY FILES.DURATION";
							break;
						case SIZE:
							statement += " ORDER BY FILES.SIZE";
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
					
				synchronized(listListener)
				{
					for(ListListener listener : listListener)
						listener.trackAdded(track);
				}
				
				track.index = index;
				return index;
			}
			else				
			{
				conn.commit(); //commit nach checkIndex()
				return -1;
			}
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
		
		synchronized(listListener)
		{
			for(ListListener listener : listListener)
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
		
		synchronized(listListener)
		{
			for(ListListener listener : listListener)
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
		
		synchronized(listListener)
		{
			for(ListListener listener : listListener)
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
	
	public void addListListener(ListListener listener)
	{
		listListener.add(listener);
	}
	public void removeListListener(ListListener listener)
	{
		listListener.remove(listener);
	}
	
	public void addList(String listName) throws ListException
	{
		addList(listName, null);
	}
	
	public void addList(String listName, String description) throws ListException
	{
		synchronized(conn)
		{
			try
			{
				if(description != null)
					executeUpdate("INSERT INTO LISTS (NAME, DESCRIPTION) VALUES (?, ?)", listName, description);
				else
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
		synchronized(listListener)
		{
			for(ListListener listener : listListener)
				listener.listAdded(listName);
		}
	}
	
	int getListIndex(String listName) throws ListException
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
					throw new ListException("Liste existiert nicht: " + listName);
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
		
		synchronized(listListener)
		{
			for(ListListener listener : listListener)
				listener.listRemoved(listName);
		}
		
		// Aus temporärer Liste löschen
		if(listIndices.containsKey(listName))
			listIndices.remove(listName);
	}
	
	public String getListDescription(String listName) throws ListException
	{
		try
		{
			String ret = queryString("SELECT DESCRIPTION FROM LISTS WHERE NAME = ?", listName);
			conn.commit();
			return ret;
		}
		catch (SQLException e)
		{
			throw new ListException(e);
		}
	}
	
	public void setListDescription(String listName, String description) throws ListException
	{
		try
		{
			executeUpdate("UPDATE LISTS SET DESCRIPTION = ? WHERE NAME = ?", description, listName);
			conn.commit();
		}
		catch (SQLException e)
		{
			throw new ListException(e);
		}
	}
	
	public void renameList(String oldName, String newName) throws ListException
	{
		try
		{
			executeUpdate("UPDATE LISTS SET NAME = ? WHERE NAME = ?", newName, oldName);
			conn.commit();
			for(ListListener listener: listListener)
				listener.listRenamed(oldName, newName);
		}
		catch (SQLException e)
		{
			throw new ListException(e);
		}
		synchronized(listListener)
		{
			for(ListListener listener : listListener)
				listener.listRenamed(oldName, newName);
		}
	}
	
	public List<String> getLists() throws ListException
	{
		List<String> lists = new ArrayList<String>();
		try
		{
			ResultSet rs = queryRS("SELECT NAME FROM LISTS ORDER BY NAME");
			while(rs.next())
				lists.add(rs.getString(1));
		}
		catch (SQLException e)
		{
			throw new ListException(e);
		}
		return lists;
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
				
				synchronized(settingListener)
				{
					for(SettingListener listener : settingListener)
						listener.settingChanged(name, value);
				}
			}
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
		int size;
		synchronized(conn)
		{
			try
			{
				int listIndex = getListIndex(listName);
				size = queryInt("SELECT COUNT(LIST) FROM LISTS_CONTENT WHERE LIST = ?", Integer.toString(listIndex));
				PreparedStatement ps = conn.prepareStatement("INSERT INTO LISTS_CONTENT (LIST, INDEX, POSITION) VALUES(?, ?, ?)");
				ps.setInt(1, listIndex);
				ps.setInt(2, track.index);
				ps.setInt(3, size);
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
		synchronized(listListener)
		{
			for(ListListener listener : listListener)
				listener.trackInserted(listName, size, track);
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
					ps.setInt(1, i);
					ps.setInt(2, i - 1);
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
		
		synchronized(listListener)
		{
			for(ListListener listener : listListener)
				listener.trackInserted(listName, trackPosition, track);
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
				ps.executeUpdate();
				
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
		synchronized(listListener)
		{
			for(ListListener listener : listListener)
				listener.trackRemoved(listName, trackPosition);
		}
	}
	
	public void swapTrack(String listName, int trackA, int trackB) throws ListException
	{
		synchronized(conn)
		{
			try
			{
				int listIndex = getListIndex(listName);
				executeUpdate("UPDATE LISTS_CONTENT SET POSITION = -1 WHERE LIST = ? AND POSITION = ?", Integer.toString(listIndex), Integer.toString(trackA));
				executeUpdate("UPDATE LISTS_CONTENT SET POSITION = ? WHERE LIST = ? AND POSITION = ?", Integer.toString(trackA), Integer.toString(listIndex), Integer.toString(trackB));
				executeUpdate("UPDATE LISTS_CONTENT SET POSITION = ? WHERE LIST = ? AND POSITION = -1", Integer.toString(trackB), Integer.toString(listIndex));
				conn.commit();
			}
			catch(SQLException e)
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
		synchronized(listListener)
		{
			for(ListListener listener : listListener)
				listener.tracksSwaped(listName, trackA, trackB);
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

	public void closing() //Von CloseListener
	{
		try
		{
			close();
		}
		catch (ListException e)
		{
			System.err.println("Fehler beim Schließen der Datenbank.");
			e.printStackTrace();
		}
	}
}
