package data.derby;

import basics.CloseListener;
import basics.Controller;
import common.Track;
import data.IData;
import data.ListListener;
import data.OpenDbException;
import data.SettingException;
import data.SettingListener;
import lists.ListException;
import lists.data.DbTrack;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
	/** Index eines Tracks -> Track. */
	final Map<Integer, DbTrack> tracksByIndex = new TreeMap<>();
	/** Pfad eines Tracks -> Track. */
	final Map<String, DerbyDbTrack> tracksByPath = new TreeMap<>();
	/** Name einer Liste -> Index in Datenbank. */
	final Map<String, Integer> listIndices = new HashMap<>();
	/** Name einer Einstellung -> Inhalt. */
	final Map<String, String> settings = new HashMap<>();
	final Set<SettingListener> settingListener = new HashSet<>();
	final Set<ListListener> listListener = new HashSet<>();
	final String dbPath;
	final private Controller controller = Controller.getInstance();

	protected final String version = "0.4";

	/** Verbindet zur Datenbank.
	 *
	 * @param dbPath Verzeichnispfad der Datenbank.
	 * @throws OpenDbException Tritt auf, wenn keine Verbindung möglich ist.
	 */
	public DerbyDB(final String dbPath) throws OpenDbException
	{
		this.dbPath = dbPath;
		final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
		final String connectionURL = "jdbc:derby:" + dbPath;

		try
		{
		    Class.forName(driver);
		}
		catch(final java.lang.ClassNotFoundException e)
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
			catch (final SettingException e)
			{
				throw new OpenDbException(e);
			}

			if(!version.equals(dbVersion))
			{
				if(!UpdateDB.update(this, dbVersion, version))
					throw new OpenDbException("Update der Datenbank fehlgeschlagen.\nAlte Verion: " + dbVersion + "\nNeue Version: " + version);
			}
		}
		catch (final SQLException e)
		{
			conn = createDb(dbPath);
			try
			{
				writeSetting("DBID", String.format("%8H", new java.util.Random().nextLong()).replace(' ', '0'));
				writeSetting("DBVersion", version);
			}
			catch (final SettingException e1)
			{
				throw new OpenDbException(e1);
			}
		}

		try
		{
			readMasterList();
		}
		catch(ListException e)
		{
			throw new OpenDbException(e);
		}
	}

	/** Erstellt eine neue Datenbank.
	 *	Wird vom Konstruktor aufgerufen, wenn Datenbank noch nicht existiert.
	 */
	protected static Connection createDb(final String name) throws OpenDbException
	{
		final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
		final String connectionURL = "jdbc:derby:" + name + ";create=true";

		try
		{
		    Class.forName(driver);
		}
		catch(final java.lang.ClassNotFoundException e)
		{
			throw new OpenDbException(e);
		}

		Connection newConn = null;
		try
		{
			newConn = DriverManager.getConnection(connectionURL);
			newConn.setAutoCommit(false);

			try(Statement s = newConn.createStatement())
			{
				s.executeUpdate("CREATE TABLE SETTINGS (NAME VARCHAR(64) NOT NULL, VALUE LONG VARCHAR, PRIMARY KEY (NAME))");
				s.executeUpdate("CREATE INDEX SETTING ON SETTINGS (NAME)");
	
				s.executeUpdate("CREATE TABLE FILES ("
					+ "INDEX INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY, "
					+ "PATH VARCHAR(1024) NOT NULL, "
					+ "SEARCHNAME VARCHAR(256) NOT NULL, "
					+ "NAME VARCHAR(256) NOT NULL,"
					+ "DURATION DOUBLE DEFAULT 0,"
					+ "SIZE BIGINT DEFAULT 0, "
					+ "PROBLEM SMALLINT DEFAULT 0, "
					+ "INFO LONG VARCHAR, "
					+ "PRIMARY KEY (INDEX), "
					+ "UNIQUE (PATH))");
				s.executeUpdate("CREATE UNIQUE INDEX PATH ON FILES (PATH)");
				s.executeUpdate("CREATE INDEX SEARCHNAME ON FILES (SEARCHNAME)");
	
				s.executeUpdate("CREATE TABLE LISTS "
						+ "(INDEX INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY, "
						+ "NAME VARCHAR(32) NOT NULL, "
						+ "DESCRIPTION LONG VARCHAR, "
						+ "PRIORITY SMALLINT DEFAULT 0, "
						+ "PRIMARY KEY (INDEX), "
						+ "UNIQUE (NAME))");
				s.executeUpdate("CREATE INDEX LIST_NAMES ON LISTS (NAME)");
	
				s.executeUpdate("CREATE TABLE LISTS_CONTENT (LIST INTEGER NOT NULL, INDEX INTEGER NOT NULL, POSITION INTEGER NOT NULL, PRIMARY KEY (LIST, POSITION))");
			}
			newConn.commit();

			return newConn;
		}
		catch (final SQLException e)
		{
			if(newConn != null)
			{
				try
				{
					newConn.rollback();
				}
				catch (final SQLException e1)
				{
					throw new OpenDbException("Rollback fehlgeschlagen!", e);
				}
			}
			throw new OpenDbException(e.getNextException().getMessage(), e);
		}
	}

	protected Map<Integer, DbTrack> readMasterList() throws ListException
	{
		if(tracksByIndex.isEmpty())
		{
			synchronized(conn)
			{
				try(ResultSet rs = queryRS("SELECT * FROM FILES"))
				{
					while(rs.next())
					{
						final DerbyDbTrack newTrack =  new DerbyDbTrack(this,
																rs.getInt("INDEX"),
																rs.getString("PATH"),
																rs.getString("NAME"),
																rs.getDouble("DURATION"),
																rs.getLong("SIZE"),
																shortToProblem(rs.getShort("PROBLEM")),
																rs.getString("INFO"));
						tracksByIndex.put(rs.getInt("index"), newTrack);
						tracksByPath.put(rs.getString("PATH"), newTrack);
					}
					conn.commit();
				}
				catch (final SQLException e)
				{
					throw new ListException(e);
				}
			}
		}

		return tracksByIndex;
	}

	protected PreparedStatement prepareStatement(final String sql, final Object... parameters) throws SQLException
	{
		final PreparedStatement ps = conn.prepareStatement(sql);
		for(int i = 0; i < parameters.length; i++)
		{
			final Class<?> c = parameters[i].getClass();
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
			else if(c == Long.class)
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

	protected int executeUpdate(final String sql, final Object... parameters) throws SQLException
	{
		synchronized(conn)
		{
			final int ret = prepareStatement(sql, parameters).executeUpdate();
			return ret;
		}
	}

	protected ResultSet queryRS(final String sql, final Object... parameters) throws SQLException
	{
		return prepareStatement(sql, parameters).executeQuery();
	}


	protected int queryInt(final String sql, final Object... parameters) throws SQLException
	{
		synchronized(conn)
		{
			final int ret = queryInt(queryRS(sql, parameters));
			return ret;
		}
	}

	protected static int queryInt(final ResultSet rs) throws SQLException
	{
		if(rs.next())
			return rs.getInt(1);
		throw new SQLException("Kein Eintag gefunden:");
	}

	protected String queryString(final String sql, final Object... parameters) throws SQLException
	{
		synchronized(conn)
		{
			final String ret = queryString(queryRS(sql, parameters));
			return ret;
		}
	}

	protected static String queryString(final ResultSet rs) throws SQLException
	{
		if(rs.next())
			return rs.getString(1);
		return null;
	}

	@SuppressWarnings("resource")
	@Override
	public List<DbTrack> readList(final String listName, String searchString, data.SortOrder order) throws ListException
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
					final int listIndex = getListIndex(listName);
					// Testen ob Liste existiert
					if(listIndex == -1)
					{
						// Wenn nicht, Liste erstellen
						addList(listName);
						return new ArrayList<>();
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

				final ArrayList<DbTrack> list = new ArrayList<>();

				final ResultSet rs = ps.executeQuery();
				while(rs.next())
					list.add(tracksByIndex.get(rs.getInt(1)));

				rs.close();
				ps.close();
				conn.commit();

				return list;
			}
		}
		catch (final SQLException e)
		{
			throw new ListException(e);
		}
	}

	@Override
	public DbTrack addTrack(final Track track, final boolean eventsFollowing) throws ListException
	{
		boolean trackIsNew = false;
		DerbyDbTrack ret = null;

		if(track != null)
		{
			final String path = track.getPath();
			ret = tracksByPath.get(track.getPath());

			if(ret == null)
			{
				if(track.getName() == null)
					track.setName(path.substring(path.lastIndexOf("\\") + 1, path.lastIndexOf(".")));

				final int index;
				synchronized(conn)
				{
					try(PreparedStatement ps = conn.prepareStatement("INSERT INTO FILES (PATH, SEARCHNAME, NAME, DURATION, SIZE, PROBLEM, INFO) VALUES(?, ?, ?, ?, ?, ?, ?)");)
					{
						ps.setString(1, path);
						ps.setString(2, makeSearchString(track.getName()));
						ps.setString(3, track.getName());
						ps.setDouble(4, track.getDuration());
						ps.setLong(5, track.getSize());
						ps.setShort(6, problemToShort(track.getProblem()));
						ps.setString(7, track.getInfo());
						ps.executeUpdate();
						ps.close();
						conn.commit();

						index = checkIndex(path);
						conn.commit();
					}
					catch(SQLException e)
					{
						throw new ListException(e);
					}
				}

				ret = new DerbyDbTrack(this, track);
				ret.setIndex(index);

				tracksByIndex.put(ret.getIndex(), ret);
				tracksByPath.put(path, ret);

				trackIsNew = true;
			}
		}

		if(trackIsNew || track == null)
		{
			synchronized(listListener)
			{
				final DbTrack newTrack = ret;
				for(final ListListener listener : listListener)
				{
					//XXX Glaub nicht dass das Starten eines Threads so toll ist. Können ganz schön viele werden.
					final Thread t = new Thread()
					{
						@Override public void run()
						{
							listener.trackAdded(newTrack, eventsFollowing);
						}
					};
					t.setDaemon(true);
					t.setName("Update Lists");
					t.start();
				}
			}
		}

		return ret;
	}

	@Override
	public void updateTrack(final DbTrack track, final DbTrack.TrackElement element, final boolean eventsFollowing) throws ListException
	{
		final common.Track oldTrack = new common.Track(track);

		if(track != null)
		{
			synchronized(conn)
			{
				try
				{
					queryInt("SELECT INDEX FROM FILES WHERE INDEX = ?", track.getIndex());
				}
				catch (final SQLException e)
				{
					throw new ListException("Track existiert nicht in Datenbank.", e);
				}

				@SuppressWarnings("resource")
				PreparedStatement ps = null;
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
				catch (final SQLException e)
				{
					throw new ListException(e);
				}
				finally
				{
					if(ps != null)
						try
						{
							ps.close();
						}
						catch(final SQLException e1)
						{
							Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e1, "Konnte SQL-Statement nicht schließen.");
						}
				}
			}

			/*if(masterList != null)
			{
				masterList.put(track.getIndex(), track);
			}*/
		}

		synchronized(listListener)
		{
			for(final ListListener listener : listListener)
			{
				//XXX Glaub nicht dass das Starten eines Threads so toll ist. Können ganz schön viele werden.
				final Thread t = new Thread()
				{
					@Override public void run()
					{
						listener.trackChanged(track, oldTrack, eventsFollowing);
					}
				};
				t.setDaemon(true);
				t.setName("Update Lists");
				t.start();
			}
		}
	}

	@Override
	public void deleteTrack(final DbTrack track, final boolean eventsFollowing) throws ListException
	{
		if(track != null)
		{
			synchronized(conn)
			{
				try
				{

					executeUpdate("DELETE FROM LISTS_CONTENT WHERE INDEX = ?", track.getIndex());
					executeUpdate("DELETE FROM FILES WHERE INDEX = ?", track.getIndex());
					conn.commit();
				}
				catch (final SQLException e)
				{
					try
					{
						conn.rollback();
					}
					catch (final SQLException e1)
					{
						throw new ListException("Rollback fehlgeschlagen!", e1);
					}
					throw new ListException(e);
				}
			}


			tracksByIndex.remove(track.getIndex());
			tracksByPath.remove(track.getPath());
		}

		synchronized(listListener)
		{
			for(final ListListener listener : listListener)
			{
				//XXX Glaub nicht dass das Starten eines Threads so toll ist. Können ganz schön viele werden.
				final Thread t = new Thread()
				{
					@Override public void run()
					{
						listener.trackDeleted(track, eventsFollowing);
					}
				};
				t.setDaemon(true);
				t.setName("Update Lists");
				t.start();
			}
		}
	}

	@Override
	public DbTrack getTrack(String path, boolean autoCreate) throws ListException
	{
		DbTrack ret = tracksByPath.get(path);
		if(ret != null)
			return ret;
		if(autoCreate)
		{
			return addTrack(new Track(path, false), false);
		}

		return null;
	}

	@Override
	public DbTrack getTrack(int index)
	{
		return tracksByIndex.get(index);
	}

	protected int checkIndex(final String trackPath) throws SQLException
	{
		DbTrack track = tracksByPath.get(trackPath);
		if(track != null)
			return track.getIndex();
		return queryInt("SELECT INDEX FROM FILES WHERE PATH = ?", trackPath);
	}

	@Override
	public void addListListener(final ListListener listener)
	{
		listListener.add(listener);
	}
	@Override
	public void removeListListener(final ListListener listener)
	{
		listListener.remove(listener);
	}

	@Override
	public void addList(final String listName) throws ListException
	{
		addList(listName, null);
	}

	@Override
	public void addList(final String listName, final String description) throws ListException
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
			catch (final SQLException e)
			{
				if(e instanceof SQLIntegrityConstraintViolationException)
					throw new ListException("Liste " + listName + " existiert bereits.", e);
				throw new ListException(e);
			}
		}
		synchronized(listListener)
		{
			for(final ListListener listener : listListener)
			{
				try
				{
					listener.listAdded(listName);
				}
				catch (Exception e)
				{
					controller.logError(Controller.NORMAL_ERROR, listener, e, "Fehler in Plugin");
				}
			}
		}
	}

	protected int getListIndex(final String listName)
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
				final int listIndex = queryInt("SELECT INDEX FROM LISTS WHERE NAME = ?", listName);
				listIndices.put(listName, listIndex);
				return listIndex;
			}
			catch (final SQLException e)
			{
				return -1;
			}
		}
	}

	@Override
	public void removeList(final String listName) throws ListException
	{
		try
		{
			synchronized(conn)
			{
				final int listIndex = getListIndex(listName);
				executeUpdate("DELETE FROM LISTS WHERE NAME = ?", listName);
				executeUpdate("DELETE FROM LISTS_CONTENT WHERE LIST = ?", listIndex);
				conn.commit();
			}
		}
		catch (final SQLException e)
		{
			try
			{
				conn.rollback();
			}
			catch (final SQLException e1)
			{
				throw new ListException("Rollback fehlgeschlagen!", e1);
			}
			throw new ListException(e);
		}

		synchronized(listListener)
		{
			for(final ListListener listener : listListener)
			{
				try
				{
					listener.listRemoved(listName);
				}
				catch (Exception e)
				{
					controller.logError(Controller.NORMAL_ERROR, listener, e, "Fehler in Plugin");
				}
			}
		}

		// Aus temporärer Liste löschen
		if(listIndices.containsKey(listName))
			listIndices.remove(listName);
	}

	@Override
	public int getListPriority(final String listName) throws ListException
	{
		try
		{
			synchronized(conn)
			{
				return queryInt("SELECT PRIORITY FROM LISTS WHERE NAME = ?", listName);
			}
		}
		catch (final SQLException e)
		{
			throw new ListException(e);
		}
	}

	@Override
	public void setListPriority(final String listName, final int priority) throws ListException
	{
		try
		{
			synchronized(conn)
			{
				executeUpdate("UPDATE LISTS SET PRIORITY = ? WHERE NAME = ?", priority, listName);
			}
		}
		catch (final SQLException e)
		{
			throw new ListException(e);
		}
		synchronized(listListener)
		{
			for(final ListListener listener : listListener)
			{
				try
				{
					listener.listPriorityChanged(listName, priority);
				}
				catch (Exception e)
				{
					controller.logError(Controller.NORMAL_ERROR, listener, e, "Fehler in Plugin");
				}
			}
		}
	}

	@Override
	public String getListDescription(final String listName) throws ListException
	{
		try
		{
			final String ret = queryString("SELECT DESCRIPTION FROM LISTS WHERE NAME = ?", listName);
			conn.commit();
			return ret;
		}
		catch (final SQLException e)
		{
			throw new ListException(e);
		}
	}

	@Override
	public void setListDescription(final String listName, final String description) throws ListException
	{
		try
		{
			executeUpdate("UPDATE LISTS SET DESCRIPTION = ? WHERE NAME = ?", description, listName);
			conn.commit();
		}
		catch (final SQLException e)
		{
			throw new ListException(e);
		}
		synchronized(listListener)
		{
			for(final ListListener listener : listListener)
			{
				try
				{
					listener.listCommentChanged(listName, description);
				}
				catch (Exception e)
				{
					controller.logError(Controller.NORMAL_ERROR, listener, e, "Fehler in Plugin");
				}
			}
		}
	}

	@Override
	public void renameList(final String oldName, final String newName) throws ListException
	{
		try
		{
			executeUpdate("UPDATE LISTS SET NAME = ? WHERE NAME = ?", newName, oldName);
			conn.commit();
			for(final ListListener listener : listListener)
			{
				try
				{
					listener.listRenamed(oldName, newName);
				}
				catch (Exception e)
				{
					controller.logError(Controller.NORMAL_ERROR, listener, e, "Fehler in Plugin");
				}
			}
		}
		catch (final SQLException e)
		{
			throw new ListException(e);
		}
		synchronized(listListener)
		{
			for(final ListListener listener : listListener)
			{
				try
				{
					listener.listRenamed(oldName, newName);
				}
				catch (Exception e)
				{
					controller.logError(Controller.NORMAL_ERROR, listener, e, "Fehler in Plugin");
				}
			}
		}
	}

	@Override
	public List<String> getLists() throws ListException
	{
		final List<String> lists = new ArrayList<>();
		try(ResultSet rs = queryRS("SELECT NAME FROM LISTS ORDER BY NAME"))
		{
			while(rs.next())
				lists.add(rs.getString(1));
		}
		catch (final SQLException e)
		{
			throw new ListException(e);
		}
		return lists;
	}

	@Override
	public void writeSetting(final String name, final String value) throws SettingException
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
				catch (final SQLException e)
				{
					throw new SettingException(e);
				}

				// In temporäre Liste schreiben
				settings.put(name, value);

				synchronized(settingListener)
				{
					for(final SettingListener listener : settingListener)
					{
						try
						{
							listener.settingChanged(name, value);
						}
						catch (Exception e)
						{
							controller.logError(Controller.NORMAL_ERROR, listener, e, "Fehler in Plugin");
						}
					}
				}
			}
		}
	}

	@Override
	public String readSetting(final String name) throws SettingException
	{
		return readSetting(name, null);
	}
	@Override
	public String readSetting(final String name, final String defaultValue) throws SettingException
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
			catch (final SQLException e)
			{
				throw new SettingException(e);
			}
		}
	}

	@Override
	public void addSettingListener(final SettingListener listener)
	{
		settingListener.add(listener);
	}

	@Override
	public void removeSettingListener(final SettingListener listener)
	{
		settingListener.remove(listener);
	}

	@Override
	public void insertTrack(final String listName, final DbTrack track, final boolean eventsFollowing) throws ListException
	{
		final int listIndex = getListIndex(listName);
		final int size;
		try
		{
			size = queryInt("SELECT COUNT(LIST) FROM LISTS_CONTENT WHERE LIST = ?", listIndex);
		}
		catch(SQLException e)
		{
			throw new ListException(e);
		}

		if(track != null && track.getIndex() >= 0)
		{
			synchronized(conn)
			{
				try
				{
					executeUpdate("INSERT INTO LISTS_CONTENT (LIST, INDEX, POSITION) VALUES(?, ?, ?)", listIndex, track.getIndex(), size);
					conn.commit();

				}
				catch (final SQLException e)
				{
					try
					{
						conn.rollback();
					}
					catch (final SQLException e1)
					{
						throw new ListException("Rollback fehlgeschlagen!", e1);
					}
					throw new ListException(e);
				}
			}
		}

		synchronized(listListener)
		{
			for(final ListListener listener : listListener)
			{
				//XXX Glaub nicht dass das Starten eines Threads so toll ist. Können ganz schön viele werden.
				final Thread t = new Thread()
				{
					@Override public void run()
					{
						listener.trackInserted(listName, size, track, eventsFollowing);
					}
				};
				t.setDaemon(true);
				t.setName("Update Lists");
				t.start();
			}
		}
	}

	@Override
	public void insertTrackAt(final String listName, final DbTrack track, int trackPosition, final boolean eventsFollowing) throws ListException
	{
		if(track != null && track.getIndex() >= 0)
		{
			synchronized(conn)
			{
				try
				{
					final int listIndex = getListIndex(listName);
					final int size = queryInt("SELECT COUNT(LIST) FROM LISTS_CONTENT WHERE LIST = ?", listIndex);

					// trackPosition korrigieren.
					if(trackPosition < 0)
						trackPosition = 0;
					if(trackPosition > size)
						trackPosition = size;

					executeUpdate("UPDATE LISTS_CONTENT SET POSITION = POSITION + 1 WHERE LIST = ? AND POSITION >= ?", listIndex, trackPosition);
					executeUpdate("INSERT INTO LISTS_CONTENT (LIST, INDEX, POSITION) VALUES(?, ?, ?)", listIndex, track.getIndex(), trackPosition);
					conn.commit();
				}

				catch (final SQLException e)
				{
					try
					{
						conn.rollback();
					}
					catch (final SQLException e1)
					{
						throw new ListException("Rollback fehlgeschlagen!", e1);
					}
					throw new ListException(e);
				}
			}
		}

		final int finalTrackPosition = trackPosition;
		synchronized(listListener)
		{
			for(final ListListener listener : listListener)
			{
				//XXX Glaub nicht dass das Starten eines Threads so toll ist. Können ganz schön viele werden.
				final Thread t = new Thread()
				{
					@Override public void run()
					{
						listener.trackInserted(listName, finalTrackPosition, track, eventsFollowing);
					}
				};
				t.setDaemon(true);
				t.setName("Update Lists");
				t.start();
			}
		}
	}

	@Override
	public void removeTrack(final String listName, final int trackPosition, final boolean eventsFollowing) throws ListException
	{
		if(trackPosition >= 0)
		{
			synchronized(conn)
			{
				try
				{
					final int listIndex = getListIndex(listName);

					final int size = queryInt("SELECT COUNT(LIST) FROM LISTS_CONTENT WHERE LIST = ?", listIndex);


					// Wenn trackPosition ausserhalb der Liste, nichts löschen.
					if(trackPosition < 0 || trackPosition >= size)
						return;

					executeUpdate("DELETE FROM LISTS_CONTENT WHERE LIST = ? AND POSITION = ?", listIndex, trackPosition);
					executeUpdate("UPDATE LISTS_CONTENT SET POSITION = POSITION - 1 WHERE LIST = ? AND POSITION > ? ", listIndex, trackPosition);
					conn.commit();
				}

				catch (final SQLException e)
				{
					try
					{
						conn.rollback();
					}
					catch (final SQLException e1)
					{
						throw new ListException("Rollback fehlgeschlagen!", e1);
					}
					throw new ListException(e);
				}
			}
		}

		synchronized(listListener)
		{
			for(final ListListener listener : listListener)
			{
				//XXX Glaub nicht dass das Starten eines Threads so toll ist. Können ganz schön viele werden.
				final Thread t = new Thread()
				{
					@Override public void run()
					{
						listener.trackRemoved(listName, trackPosition, eventsFollowing);
					}
				};
				t.setDaemon(true);
				t.setName("Update Lists");
				t.start();
			}
		}
	}

	@Override
	public void swapTrack(final String listName, final int trackA, final int trackB, final boolean eventsFollowing) throws ListException
	{
		if(trackA >= 0 && trackB >= 0 && trackA != trackB)
		{
			synchronized(conn)
			{
				try
				{
					final int listIndex = getListIndex(listName);
					executeUpdate("UPDATE LISTS_CONTENT SET POSITION = -1 WHERE LIST = ? AND POSITION = ?", listIndex, trackA);
					executeUpdate("UPDATE LISTS_CONTENT SET POSITION = ? WHERE LIST = ? AND POSITION = ?", trackA, listIndex, trackB);
					executeUpdate("UPDATE LISTS_CONTENT SET POSITION = ? WHERE LIST = ? AND POSITION = -1", trackB, listIndex);
					conn.commit();
				}
				catch(final SQLException e)
				{
					try
					{
						conn.rollback();
					}
					catch (final SQLException e1)
					{
						throw new ListException("Rollback fehlgeschlagen!", e1);
					}
					throw new ListException(e);
				}
			}
		}

		synchronized(listListener)
		{
			for(final ListListener listener : listListener)
			{
				//XXX Glaub nicht dass das Starten eines Threads so toll ist. Können ganz schön viele werden.
				final Thread t = new Thread()
				{
					@Override public void run()
					{
						listener.tracksSwaped(listName, trackA, trackB, eventsFollowing);
					}
				};
				t.setDaemon(true);
				t.setName("Update Lists");
				t.start();
			}
		}
	}

	@Override
	public boolean isInDb(final String trackPath) throws ListException
	{
		try
		{
			return checkIndex(trackPath) != -1;
		}
		catch (final SQLException e)
		{
			return false;
		}
	}

	protected static String makeSearchString(final String original)
	{
		return original.toLowerCase(Locale.ENGLISH).replace('ä', 'a').replace('ö', 'o').replace('ü', 'u').replace('ß', 's');
	}

	protected static short problemToShort(final DbTrack.Problem problem)
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

	protected static DbTrack.Problem shortToProblem(final short number)
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
		catch (final SQLException e)
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
		catch (final ListException e)
		{
			System.err.println("Fehler beim Schließen der Datenbank.");
			e.printStackTrace();
		}
	}

	@Override
	public String getDbPath()
	{
		return dbPath;
	}
}
