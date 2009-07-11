package data.derby;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import data.IData;
import data.ListListener;
import data.OpenDbException;
import data.SettingException;
import data.SettingListener;
import basics.CloseListener;
import lists.ListException;
import lists.data.DbTrack;

/**
 * Speichert Einstellungen und Listen in einer Derby-Datenbank.
 * 
 * @author Eraser
 * 
 * @see IData
 */
public class DerbyDB implements IData, CloseListener
{
	Connection conn = null;
	HashMap<Integer, DbTrack> masterList;
	final Map<String, Integer> listIndices = new HashMap<String, Integer>();
	final Map<String, String> settings = new HashMap<String, String>();
	final Set<SettingListener> settingListener = new HashSet<SettingListener>();
	final Set<ListListener> listListener = new HashSet<ListListener>();
	
	public final String version = "0.4";
	
	/** Verbindet zur Datenbank.
	 * 
	 * @param dbName Verzeichnispfad der Datenbank.
	 * @throws OpenDbException Tritt auf, wenn keine Verbindung möglich ist.
	 */
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
	
	/** Erstellt eine neue Datenbank.
	 *	Wird vom Konstruktor aufgerufen, wenn Datenbank noch nicht existiert.
	 */
	protected Connection createDb(String Name) throws OpenDbException
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
		
		Connection newConn = null;
		try
		{
			newConn = DriverManager.getConnection(connectionURL);
			newConn.setAutoCommit(false);
			
			Statement s = newConn.createStatement();
			s.executeUpdate("CREATE TABLE SETTINGS (NAME VARCHAR(64) NOT NULL, VALUE LONG VARCHAR, PRIMARY KEY (NAME))");
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
					"PRIORITY SMALLINT DEFAULT 0, " +
					"PRIMARY KEY (INDEX), " +
					"UNIQUE (NAME))");
			s.executeUpdate("CREATE INDEX LIST_NAMES ON LISTS (NAME)");
			
			s.executeUpdate("CREATE TABLE LISTS_CONTENT (LIST INTEGER NOT NULL, INDEX INTEGER NOT NULL, POSITION INTEGER NOT NULL, PRIMARY KEY (LIST, POSITION))");
			
			s.close();
			newConn.commit();
			
			return newConn;
		}
		catch (SQLException e)
		{
			if(newConn != null)
			{
				try
				{
					newConn.rollback();
				}
				catch (SQLException e1)
				{
					throw new OpenDbException("Rollback fehlgeschlagen!", e);
				}
			}
			throw new OpenDbException(e.getNextException().getMessage(), e);
		}
	}
	
	PreparedStatement prepareStatement(String SQL, Object... parameters) throws SQLException
	{
		PreparedStatement ps = conn.prepareStatement(SQL);
		for(int i = 0; i < parameters.length; i++)
		{
			Class<?> c = parameters[i].getClass();
			if(c == Boolean.class)
				ps.setBoolean(i + 1, (Boolean)parameters[i]);
			else if(c == Byte.class)
				ps.setByte(i + 1, (Byte)parameters[i]);
			else if(c == Double.class)
				ps.setDouble(i + 1, (Double)parameters[i]);
			else if(c == Float.class)
				ps.setFloat(i + 1, (Float)parameters[i]);
			else if(c == Integer.class)
				ps.setInt(i + 1, (Integer)parameters[i]);
			else if(c==Long.class)
				ps.setLong(i + 1, (Long)parameters[i]);
			else if(c == String.class)
				ps.setString(i + 1, (String)parameters[i]);
			else if(c == Short.class)
				ps.setShort(i + 1, (Short)parameters[i]);
			else
				ps.setObject(i + 1, parameters[i]);
		}
		return ps;
	}
	
	int executeUpdate(String SQL, Object... parameters) throws SQLException
	{
		synchronized(conn)
		{
			int ret = prepareStatement(SQL, parameters).executeUpdate();
			return ret;
		}
	}
	
	ResultSet queryRS(String SQL, Object... parameters) throws SQLException
	{
		return prepareStatement(SQL, parameters).executeQuery();
	}

	
	int queryInt(String SQL, Object... parameters) throws SQLException
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
		throw new SQLException("Kein Eintag gefunden:");
	}
	
	String queryString(String SQL, Object... parameters) throws SQLException
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
		return null;
	}
	
	@Override
	public HashMap<Integer, DbTrack> readMasterList() throws ListException
	{
		if(masterList == null)
		{
			masterList = new HashMap<Integer, DbTrack>();
			synchronized(conn)
			{
				try
				{
					ResultSet rs = queryRS("SELECT * FROM FILES");
					while(rs.next())
					{
						masterList.put(rs.getInt("index"), new DbTrack(	rs.getInt("INDEX"),
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
	
	@Override
	public List<DbTrack> readList(String listName, String searchString, data.SortOrder order) throws ListException
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
					case PROBLEM:
						statement += " ORDER BY PROBLEM";
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
						return new ArrayList<DbTrack>();
					}
					
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
					case PROBLEM:
						statement += " ORDER BY FILES.PROBLEM";
						break;
					}
					
					ps = conn.prepareStatement(statement);
					
					ps.setInt(1, listIndex);
					
					if(searchString != null)
						ps.setString(2, searchString);
				}
	
				ArrayList<DbTrack> list = new ArrayList<DbTrack>();
				
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
	
	@Override
	public boolean addTrack(DbTrack track) throws ListException
	{
		String path = track.getPath();
		
		try
		{
			int index;
			try
			{
				index = checkIndex(path);
			}
			catch(SQLException e)
			{
				index = -1;
			}
				
			if(index == -1)
			{
				if(track.getName() == null)
					track.setName(path.substring(path.lastIndexOf("\\") + 1, path.lastIndexOf(".")));
				
				synchronized(conn)
				{
					PreparedStatement ps = conn.prepareStatement("INSERT INTO FILES (PATH, SEARCHNAME, NAME, DURATION, SIZE, PROBLEM, INFO) VALUES(?, ?, ?, ?, ?, ?, ?)");
					ps.setString(1, path);
					ps.setString(2, makeSearchString(track.getName()));
					ps.setString(3, track.getName());
					ps.setDouble(4, track.getDuration());
					ps.setLong(5, track.getSize());
					ps.setShort(6, problemToShort(track.getProblem()));
					ps.setString(7, track.getInfo());
					ps.executeUpdate();
					conn.commit();
				}
				
				index = checkIndex(path);
				conn.commit();
				track.setIndex(index);
				
				if(masterList != null)
				{
					masterList.put(track.getIndex(), track);
				}
					
				synchronized(listListener)
				{
					for(ListListener listener : listListener)
						listener.trackAdded(track);
				}
				
				return true;
			}
			
			track.setIndex(index);
			conn.commit(); //commit nach checkIndex()
			return false;
		}
		catch (SQLException e)
		{
			throw new ListException(e);
		}
	}
	
	@Override
	public void updateTrack(DbTrack track) throws ListException
	{
		synchronized(conn)
		{
			if(track.getIndex() == -1)
			{
				try
				{
					track.setIndex(checkIndex(track.getPath()));
				}
				catch (SQLException e)
				{
					throw new ListException(e);
				}
			}
			
			try
			{
				queryInt("SELECT INDEX FROM FILES WHERE INDEX = ?", track.getIndex());
			}
			catch (SQLException e)
			{
				throw new ListException("Track existiert nicht in Datenbank.", e);
			}
			
			PreparedStatement ps;
			try
			{
				ps = conn.prepareStatement("UPDATE FILES SET PATH = ?, SEARCHNAME = ?, NAME = ?, DURATION = ?, SIZE = ?, PROBLEM = ?, INFO = ? WHERE INDEX = ?");
				ps.setString(1, track.getPath());
				ps.setString(2, makeSearchString(track.getName()));
				ps.setString(3, track.getName());
				ps.setDouble(4, track.getDuration());
				ps.setLong(5, track.getSize());
				ps.setShort(6, problemToShort(track.getProblem()));
				ps.setString(7, track.getInfo());
				ps.setInt(8, track.getIndex());
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
			masterList.put(track.getIndex(), track);
		}
		
		synchronized(listListener)
		{
			for(ListListener listener : listListener)
				listener.trackChanged(track);
		}
	}
	
	@Override
	public void updateTrack(DbTrack track, DbTrack.TrackElement element) throws ListException
	{
		synchronized(conn)
		{
			try
			{
				queryInt("SELECT INDEX FROM FILES WHERE INDEX = ?", track.getIndex());
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
					ps.setString(1, track.getPath());
					break;
				case NAME:
					ps = conn.prepareStatement("UPDATE FILES SET SEARCHNAME = ?, NAME = ? WHERE INDEX = ?");
					ps.setString(1, makeSearchString(track.getName()));
					ps.setString(2, track.getName());
					lastSet = 3;
					break;
				case DURATION:
					ps = conn.prepareStatement("UPDATE FILES SET DURATION = ? WHERE INDEX = ?");
					ps.setDouble(1, track.getDuration());
					break;
				case SIZE:
					ps = conn.prepareStatement("UPDATE FILES SET SIZE = ? WHERE INDEX = ?");
					ps.setLong(1, track.getSize());
					break;
				case PROBLEM:
					ps = conn.prepareStatement("UPDATE FILES SET PROBLEM = ? WHERE INDEX = ?");
					ps.setShort(1, problemToShort(track.getProblem()));
					break;
				case INFO:
					ps = conn.prepareStatement("UPDATE FILES SET INFO = ? WHERE INDEX = ?");
					ps.setInt(1, track.getIndex());
					break;
					default:
						throw new IllegalArgumentException("Unbekanntes Element");
				}
				
				ps.setInt(lastSet, track.getIndex());
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
			masterList.put(track.getIndex(), track);
		}
		
		synchronized(listListener)
		{
			for(ListListener listener : listListener)
				listener.trackChanged(track);
		}
	}
	
	@Override
	public void deleteTrack(DbTrack track) throws ListException
	{
		if(track == null)
			throw new NullPointerException("Kein Track übergeben.");
		synchronized(conn)
		{
			try
			{

				executeUpdate("DELETE FROM LISTS_CONTENT WHERE INDEX = ?", track.getIndex());
				executeUpdate("DELETE FROM FILES WHERE INDEX = ?", track.getIndex());
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
			masterList.remove(track.getIndex());
		}
		
		synchronized(listListener)
		{
			for(ListListener listener : listListener)
				listener.trackDeleted(track);
		}
	}
	
	int checkIndex(String trackPath) throws SQLException
	{
		return queryInt("SELECT INDEX FROM FILES WHERE PATH = ?", trackPath);
	}
	
	@Override
	public void addListListener(ListListener listener)
	{
		listListener.add(listener);
	}
	@Override
	public void removeListListener(ListListener listener)
	{
		listListener.remove(listener);
	}
	
	@Override
	public void addList(String listName) throws ListException
	{
		addList(listName, null);
	}
	
	@Override
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
	
	@Override
	public void removeList(String listName) throws ListException
	{
		try
		{
			synchronized(conn)
			{
				int listIndex = getListIndex(listName);
				executeUpdate("DELETE FROM LISTS WHERE NAME = ?", listName);				
				executeUpdate("DELETE FROM LISTS_CONTENT WHERE LIST = ?", listIndex);				
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
	
	@Override
	public int getListPriority(String listName) throws ListException
	{
		try
		{
			synchronized(conn)
			{
				return queryInt("SELECT PRIORITY FROM LISTS WHERE NAME = ?", listName);
			}
		}
		catch (SQLException e)
		{
			throw new ListException(e);
		}
	}
	
	@Override
	public void setListPriority(String listName, int priority) throws ListException
	{
		try
		{
			synchronized(conn)
			{
				executeUpdate("UPDATE LISTS SET PRIORITY = ? WHERE NAME = ?", priority, listName);
			}
		}
		catch (SQLException e)
		{
			throw new ListException(e);
		}
		synchronized(listListener)
		{
			for(ListListener listener : listListener)
				listener.listPriorityChanged(listName, priority);
		}
	}
	
	@Override
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
	
	@Override
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
		synchronized(listListener)
		{
			for(ListListener listener : listListener)
				listener.listCommentChanged(listName, description);
		}
	}
	
	@Override
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
	
	@Override
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
	
	@Override
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
	
	@Override
	public String readSetting(String name) throws SettingException
	{
		return readSetting(name, null);
	}
	@Override
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
	
	@Override
	public void addSettingListener(SettingListener listener)
	{
		settingListener.add(listener);
	}

	@Override
	public void removeSettingListener(SettingListener listener)
	{
		settingListener.remove(listener);
	}

	@Override
	public void insertTrack(String listName, DbTrack track) throws ListException
	{
		if(track.getIndex() == -1)
			throw new ListException("Track nicht in Hauptliste: " + track);
		int size;
		synchronized(conn)
		{
			try
			{
				int listIndex = getListIndex(listName);
				size = queryInt("SELECT COUNT(LIST) FROM LISTS_CONTENT WHERE LIST = ?", listIndex);
				executeUpdate("INSERT INTO LISTS_CONTENT (LIST, INDEX, POSITION) VALUES(?, ?, ?)", listIndex, track.getIndex(), size);
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
	
	@Override
	public void insertTrackAt(String listName, DbTrack track, int trackPosition) throws ListException
	{		
		if(track.getIndex() == -1)
			throw new ListException("Track nicht in Hauptliste");
		synchronized(conn)
		{
			try
			{
				int listIndex = getListIndex(listName);
				int size = queryInt("SELECT COUNT(LIST) FROM LISTS_CONTENT WHERE LIST = ?", listIndex);
				
				// trackPosition korrigieren.
				if(trackPosition < 0)
					trackPosition = 0;
				if(trackPosition > size)
					trackPosition = size;
				
				//System.out.println(trackPosition);
				//System.out.println(size);
				
				for(int i = size; i > trackPosition; i--)
				{
					//System.out.println("UPDATE LISTS_CONTENT SET POSITION = " + i + " WHERE POSITION = " + (i - 1) + " AND LIST = "+ listIndex);
					executeUpdate("UPDATE LISTS_CONTENT SET POSITION = ? WHERE POSITION = ? AND LIST = ?", i, i - 1, listIndex);
				}

				//System.out.println("INSERT INTO LISTS_CONTENT (LIST, INDEX, POSITION) VALUES(" + listIndex + ", " + track.index + ", " + trackPosition + ")");
				executeUpdate("INSERT INTO LISTS_CONTENT (LIST, INDEX, POSITION) VALUES(?, ?, ?)", listIndex, track.getIndex(), trackPosition);
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

	@Override
	public void removeTrack(String listName, int trackPosition) throws ListException
	{
		synchronized(conn)
		{
			try
			{
				int listIndex = getListIndex(listName);
				
				int size = queryInt("SELECT COUNT(LIST) FROM LISTS_CONTENT WHERE LIST = ?", listIndex);
				
				
				// Wenn trackPosition ausserhalb der Liste, nichts löschen.
				if(trackPosition < 0 || trackPosition >= size)
					return;

				executeUpdate("DELETE FROM LISTS_CONTENT WHERE POSITION = ? AND LIST = ?", trackPosition, listIndex);
				
				for(int i = trackPosition; i < size; i++)
				{
					executeUpdate("UPDATE LISTS_CONTENT SET POSITION = ? WHERE POSITION = ? AND LIST = ?", i, i + 1, listIndex);
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
	
	@Override
	public void swapTrack(String listName, int trackA, int trackB) throws ListException
	{
		synchronized(conn)
		{
			try
			{
				int listIndex = getListIndex(listName);
				executeUpdate("UPDATE LISTS_CONTENT SET POSITION = -1 WHERE LIST = ? AND POSITION = ?", listIndex, trackA);
				executeUpdate("UPDATE LISTS_CONTENT SET POSITION = ? WHERE LIST = ? AND POSITION = ?", trackA, listIndex, trackB);
				executeUpdate("UPDATE LISTS_CONTENT SET POSITION = ? WHERE LIST = ? AND POSITION = -1", trackB, listIndex);
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
	
	short problemToShort(DbTrack.Problem problem)
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
	
	DbTrack.Problem shortToProblem (short number)
	{
		switch(number)
		{
		case 0:
			return DbTrack.Problem.NONE;
		case 1:
			return DbTrack.Problem.FILE_NOT_FOUND;
		case 2:
			return DbTrack.Problem.CANT_PLAY;
		default:
			return DbTrack.Problem.OTHER;
		}
	}
	
	@Override
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

	@Override
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
