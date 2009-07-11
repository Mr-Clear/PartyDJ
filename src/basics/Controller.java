package basics;

import gui.KeyStrokeManager;
import gui.SplashWindow;
import gui.settings.SettingNode;
import java.awt.Frame;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.Stack;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import players.IPlayer;
import players.PlayerException;
import common.*;
import lists.EditableListModel;
import lists.ListException;
import lists.ListProvider;
import data.*;
import data.derby.DerbyDB;

/**
 * Hauptklasse vom PartyDJ.
 * <p>Enthält die main-Funktion.
 * <p>Bietet Zugriff auf alle anderen wichtigen Elemente des PartyDJ.
 * 
 * 
 * @author Eraser
 * @author Sam
 */
public class Controller
{
	/** Version des PartyDJ */
	public final String version = "3.0.1";
	
	/** Statischer Verweis auf diese Instanz. */
	protected static Controller instance;
	/** Verbindung zur Datenbank. */
	protected IData data;
	/** Dieser Thread überwacht ob der PartyDJ geschlossen wird. */
	protected Thread closeListenThread;

	/** Wunschliste aus derimmer der oberste Track gespielt und dabei gelöscht wird. */ 
	protected EditableListModel playList;
	/** Liste der zuletzt gespielten lieder. Hat maximal 100 Einträge */
	protected EditableListModel lastPlayedList;
	/** Zugriff auf Listen */
	protected ListProvider listProvider;
	/** Verwendeter Player */
	protected IPlayer player;
	/** Liste aller registrierten CloseListener */ 
	protected final Set<CloseListener> closeListener = new HashSet<CloseListener>();
	/** Liste aller registrierten Fenster.
	 *  Wenn die Liste leer wird, schließt sich der PartyDJ. */
	protected final Set<Frame> windows = new HashSet<Frame>();
	/** RootNode der Einstellungen. Alle Einstellungen haben einen Knoten in diesem Baum. */
	protected final SettingNode settingTree;
	/** Timer der im regelmäßigen Abstand die Dauer der Tracks aus trackUpdateStack einliest. */
	protected Timer trackUpdateTimer; 
	/** Stapel mit Liedern deren Dauer eingelesen werden soll. */
	final protected Stack<DbTrack> trackUpdateStack = new Stack<DbTrack>();
	/** Wird am Ende des Konstruktors auf true gesetzt. */
	protected boolean loadFinished = false;
	protected final Scripter scripter;
	
	
	protected java.io.PrintWriter logStream;
	
	
	protected Controller(String[] args)
	{	
		if(instance == null)
			instance = this;
		else
		{
			logError(IMPORTANT_ERROR, this, null, "Es darf nur ein Controller erstellt werden!");
			throw new RuntimeException("Es darf nur ein Controller erstellt werden!");
		}
		
		SplashWindow splash = new SplashWindow(); 
	
		
		closeListenThread = new Thread(){
					@Override public void run()
					{
						closePartyDJ();
					}};
		Runtime.getRuntime().addShutdownHook(closeListenThread);
		
		String dbPath = Functions.getFolder() + System.getProperty("file.separator") + "DataBase";
		List<String> windowsToLoad = new ArrayList<String>();
		int whichPlayer = -1;	// 0=JMF, 1=JL
		
		splash.setInfo("Lade Look And Feel");
		{
			//UI
			try
			{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch (ClassNotFoundException e)
			{
				logError(UNIMPORTANT_ERROR, this, e, "Fehler bei Laden von Look And Feel");
			}
			catch (InstantiationException e)
			{
				logError(UNIMPORTANT_ERROR, this, e, "Fehler bei Laden von Look And Feel");
			}
			catch (IllegalAccessException e)
			{
				logError(UNIMPORTANT_ERROR, this, e, "Fehler bei Laden von Look And Feel");
			}
			catch (UnsupportedLookAndFeelException e)
			{
				logError(UNIMPORTANT_ERROR, this, e, "Fehler bei Laden von Look And Feel");
			}
		}
		
		int lastParam = 0;
		for(String arg : args)
		{
			String argl = arg.toLowerCase();
			if(arg.charAt(0) == '-' || arg.charAt(0) == '+')
			{
				if(argl.equals("-dbpath"))
					lastParam = 1;
				else if(argl.equals("-player"))
					lastParam = 2;
				else if(argl.equals("+window"))
					lastParam = 3;
				else
					lastParam = 0;
			}
			else
			{
				switch(lastParam)
				{
				case 1:	//-dbpath
					dbPath = arg;
					break;
				case 2:	//-player
					if(argl.equals("jmf"))
						whichPlayer = 0;
					else if(argl.equals("jl"))
						whichPlayer = 1;
					break;
				case 3:	//+window
					windowsToLoad.add(arg);
					break;
				}
				lastParam = 0;
			}
		}
		
		splash.setOpacity(0.82f);
		splash.setInfo("Verbinde zur Datenbank");
		{
			try
			{
				data = new DerbyDB(dbPath);
			}
			catch (OpenDbException e)
			{
				logError(IMPORTANT_ERROR, this, e, "Keine Verbindung zur Datenbank möglich");
				System.exit(1);
			}
		}
			
		splash.setOpacity(0.88f);
		splash.setInfo("Lade Player");
		{
			PlayerListener playerListener = new PlayerListener();	// implements PlayerContact, PlayStateListener
			splash.setInfo("Lade Player0");
			switch(whichPlayer)
			{
			case 0:			
				player = new players.jmf.JMFPlayer(playerListener);
				break;
			case 1:
			default:
				player = new players.jl.JLPlayer(playerListener);
				break;
			}
			splash.setInfo("Lade Player 1");
			try
			{
				player.setVolume(Integer.parseInt(data.readSetting("Volume", "100")));
			}
			catch (NumberFormatException e1)
				{player.setVolume(100);}
			catch (SettingException e1)
				{player.setVolume(100);}
			
			splash.setInfo("Lade Player2");
			player.addPlayStateListener(playerListener);
			splash.setInfo("Lade Player3");
		}
		
		splash.setOpacity(0.9f);
		splash.setInfo("Lade Listen");
		{
			try
			{
				listProvider = new ListProvider();
				playList = listProvider.getDbList("Wunschliste");				
				lastPlayedList = listProvider.getDbList("LastPlayed");
			}
			catch (ListException e)
			{
				logError(IMPORTANT_ERROR, this, e, "Listen konnten nicht geladen werden!");
				System.exit(1);
			}
		}

		scripter = new Scripter();
		
		splash.setOpacity(0.95f);
		splash.setInfo("Lade Fenster");
		{
			settingTree = new SettingNode("Einstellungen", gui.settings.About.class);
			addSettingNode(new SettingNode("Tracks", gui.settings.TrackManager.class), settingTree);
			addSettingNode(new SettingNode("Listen", gui.settings.MainList.class), settingTree);
			addSettingNode(new SettingNode("Shuffle", gui.settings.Shuffle.class), settingTree);
			addSettingNode(new SettingNode("HotKeys", gui.settings.HotKeys.class), settingTree);
			addSettingNode(new SettingNode("Verschiedenes", gui.settings.Misc.class), settingTree);
			addSettingNode(new SettingNode("Zeug", gui.settings.Stuff.class), settingTree);
			
			if(windowsToLoad.size() == 0)
				windowsToLoad.add("gui.ClassicWindow");
			
			for(String window : windowsToLoad)
			{
				loadWindow(window);
			}
		}
		
		splash.setOpacity(1f);
		if(listProvider.getMasterList().getSize() == 0)
		{
			registerWindow(new gui.SetupWindow());
		}
		else
		{
			splash.setInfo("Starte");
			String firstTrackPath = null;
			try
			{
				firstTrackPath = data.readSetting("Playing");
			}
			catch (SettingException e){logError(UNIMPORTANT_ERROR, this, e, "data.readSetting(\"Playing\")");}
			if(firstTrackPath != null)
			{
				DbTrack firstTrack = null;
				firstTrack = listProvider.getMasterList().getTrackByPath(firstTrackPath);
				if(firstTrack != null)
				{
					splash.setInfo("Starte " + firstTrack.getName());
					try
					{
						double pos = 0;
						double lastPosition = 0;
						boolean autoPlay = false;
						player.load(firstTrack);
						try
						{
							autoPlay = Boolean.parseBoolean(data.readSetting("AUTO_PLAY", "true"));
							pos = Double.parseDouble(data.readSetting("POSITION", "-1"));
							lastPosition = Double.parseDouble(data.readSetting("LastPosition", "0"));
						}
						catch (NumberFormatException e){}catch (SettingException e){}
						
						player.setPosition(pos = pos >= 0 ? pos : lastPosition, false);
						if(autoPlay)
							player.fadeIn();
					}
					catch (PlayerException e){logError(UNIMPORTANT_ERROR, this, e, "player.load(firstTrack);");}
				}
			}
		}
		
		splash.setInfo("Lade Plugins");
		{
			try
			{
				new network.ClassicUdpReceiver().start();
				new network.WinLircReceiver().initialise();
			}
			catch(Throwable t)
			{
				logError(IMPORTANT_ERROR, this, t, "Plugin laden fehlgeschlagen.");
			}
		}
		
		splash.setInfo("PartyDJ bereit :)");
		try
		{
			if(System.getenv().get("OS").equalsIgnoreCase("windows_NT"))
				KeyStrokeManager.getInstance().initHotKeys();
		}
		catch(Throwable t)
		{
			logError(REGULAR_ERROR, this, t, "Fehler beim Laden von Intellitype!");
		}
		
		getData().writeSetting("LastLoadTime", Long.toString(splash.getElapsedTime()));
		splash.close();
		
		loadFinished = true;
		
		/*player.addPlayStateListener(new players.PlayStateAdapter(){
			@Override
			public void currentTrackChanged(Track playedLast, Track playingCurrent, Reason reason)
			{

				System.out.println(playingCurrent);
			}
		});*/
	}
	

	/**
	 * @return Die Instanz des Controllers.
	 */
	public static Controller getInstance()
	{
		if(instance == null)
			throw new Error("Controller nicht geladen!");
		return instance;
	}

	public IData getData()
	{
		return data;
	}
	
	public IPlayer getPlayer()
	{
		return player;
	}
	
	public Scripter getScripter()
	{
		return scripter;
	}
	
	public ListProvider getListProvider()
	{
		return listProvider;
	}
	
	public void setPlayList(EditableListModel list)
	{
		playList = list;
	}
	
	public EditableListModel getPlayList()
	{
		return playList;
	}
	
	public String getLastPlayedName()
	{
		return "LastPlayed";
	}
	
	/** Gibt zurück ob der PartyDJ vollständig geladen ist. 
	 * @return True, wenn der PartyDJ vollständig geladen ist.*/
	public boolean isLoadFinished()
	{
		return loadFinished;
	}
	
	/**Schiebt einen Track auf den Duration-Update-Stapel.
	 * Damit wird die länge des Tracks eingelesen, sobald er an der Reihe ist.
	 * @param track Track dessen Dauer eingelesen werden soll.
	 */
	public void pushTrackToUpdate(DbTrack track)
	{
		trackUpdateStack.push(track);
		
		if(trackUpdateTimer == null)
		{
			trackUpdateTimer = new Timer();
			trackUpdateTimer.schedule(new TrackUpdateTask(trackUpdateStack), 0, 1000); 
		}
	}
	
	/**Lädt das Fenster mit dem angegebenen Namen und registriert es.
	 * Wenn ein Fehler auftritt, wird eine Menldung ausgegeben. 
	 * 
	 * @param className Name des Fensters.
	 * @return true, wenn erfolgreich.
	 */
	public boolean loadWindow(String className)
	{
		if(className.toLowerCase().equals("classic"))
			className = "gui.ClassicWindow";
		else if(className.toLowerCase().equals("settings"))
			className = "gui.settings.SettingWindow";
		else if(className.toLowerCase().equals("test"))
			className = "gui.TestWindow";
		else if(className.toLowerCase().equals("derbydebug"))
			className = "data.derby.DebugWindow";
		try
		{
			Class<?> c = Class.forName(className);
			if(JFrame.class.isAssignableFrom(c))
				registerWindow((JFrame)c.getConstructor().newInstance());
			else
				throw new ClassCastException(c.toString() + " ist kein Fenster");
			return true;
		}
		catch (Exception e)
		{
			logError(IMPORTANT_ERROR, this, e, "Fenster kann nicht geladen werden: " + className);
			return false;
		}
	}
	
	/**Registriert ein Fenster.
	 * Erstellt einen WindowsListener für das Fenster der bei windowColsing das Fenster schließt.
	 * Wenn das letzte registrierte Fenster geschlossen wird, beendet sich der PartyDJ.
	 * @param window Fenster das registriert werden soll.
	 */
	public void registerWindow(JFrame window)
	{
		windows.add(window);
		window.addWindowListener(new ClientWindowListener(window));
	}
	
	/**Schließt das angegebene Fenster.
	 * Wenn es das letzte registrierte Fenster ist, wird der PartyDJ beendet.
	 * 
	 * @param window
	 */
	public void unregisterWindow(Frame window)
	{
		windows.remove(window);
		window.dispose();
		if(windows.isEmpty())
			closePartyDJ();
	}
	
	/** CloseListener.closing wird aufgerufen bevor der PDJ geschlossen wird. 
	 * @param listener Der CloseListener.*/
	public void addCloseListener(CloseListener listener)
	{
		closeListener.add(listener);
	}
	public void removeCloseListener(CloseListener listener)
	{
		closeListener.remove(listener);
	}
	
	/**Fügt eine SettingNode im SettingWindow ein.
	 * 
	 * @param node Node die eingefügt wird.
	 * @param parent Vorhandene Node an die die neue Node als Child angehängt wird.
	 */
	public void addSettingNode(SettingNode node, SettingNode parent)
	{
		parent.add(node);
	}
	@SuppressWarnings("unused")
	@Deprecated
	public void addSettingNode(SettingNode node, String path)
	{
		//TODO
		throw new UnsupportedOperationException();
	}
	@SuppressWarnings("unused")
	@Deprecated
	public void removeSettingNode(SettingNode node)
	{
		//TODO
		throw new UnsupportedOperationException();
	}
	public SettingNode getSetingTree()
	{
		return settingTree;
	}
	
	/**Information, die normalerweise nicht beachtet wird.
	 * <p><code>message</code> wird in die Log-Datei geschrieben.
	 * <p><b>Used By:</b><ul>
	 * <li><code>Controller.reportError</code></li> */
	public static final int UNIMPORTANT_INFO = 1;
	/**Information, die interessant für Debugging o.ä. sein könnte.
	 * <p><code>message</code> und <code>sender</code> werden in Log-Datei und <code>System.err</code> geschrieben.
	 * <p><b>Used By:</b><ul>
	 * <li><code>Controller.reportError</code></li> */
	public static final int INERESTING_INFO = 2;
	/**Fehler, ohne weitere Bedeutung.
	 * <p><code>message</code>, <code>sender</code> und <code>exception</code> 
	 * werden in Log-Datei und <code>System.err</code> geschrieben.
	 * <p><b>Used By:</b><ul>
	 * <li><code>Controller.reportError</code></li> */
	public static final int UNIMPORTANT_ERROR = 3;
	/**Fehler, der gelegendlich auftreten darf.
	 * <p><code>message</code>, <code>sender</code> und <code>exception</code> mit Stack-Trace
	 * werden in Log-Datei und <code>System.err</code> geschrieben.
	 * <p><b>Used By:</b><ul>
	 * <li><code>Controller.reportError</code></li> */
	public static final int REGULAR_ERROR = 4;
	/**Fehler, der nicht auftreten sollte, aber den Programmablauf nicht stört.
	 * <p><code>message</code>, <code>sender</code> und <code>exception</code> mit Stack-Trace
	 * werden in Log-Datei und <code>System.err</code> geschrieben.
	 * <br>Fehlermeldung mit <code>message</code> und <code>exception</code> wird ausgegeben.
	 * <p><b>Used By:</b><ul>
	 * <li><code>Controller.reportError</code></li> */
	public static final int NORMAL_ERROR = 5;
	/**Schwerwiegender Fehler, der den Programmablauf beeinträchtigt.
	 * <p><code>message</code>, <code>sender</code> und <code>exception</code> mit Stack-Trace
	 * werden in Log-Datei und <code>System.err</code> geschrieben.
	 * <br>Fehlermeldung mit <code>message</code> und <code>exception</code> incl. Stack-Trace wird ausgegeben.
	 * <p><b>Used By:</b><ul>
	 * <li><code>Controller.reportError</code></li> */
	public static final int IMPORTANT_ERROR = 6;
	/**Schwerwiegender Fehler, die Stabilität der Daten gefährden kann.
	 * <p><code>message</code>, <code>sender</code> und <code>exception</code> mit Stack-Trace
	 * werden in Log-Datei und <code>System.err</code> geschrieben.
	 * <br>Fehlermeldung mit <code>message</code> und <code>exception</code> incl. Stack-Trace wird ausgegeben.
	 * <p>PartyDJ wird getötet.
	 * <br>Nicht gespeicherte Einsellungen gehen verloren!
	 * <p><b>Used By:</b><ul>
	 * <li><code>Controller.reportError</code></li> */
	public static final int FATAL_ERROR = 7;
	/**Verarbeitet eine Fehlermeldung.
	 * <p>Abhängig von der Priorität werden unterschiedliche Aktionen ausgeführt.
	 * 
	 * @param priority Priorität der Meldung.
	 * @param sender Objekt das die Meldung schickt. Üblicherweise this.
	 * @param exception Wenn vorhanden, eine Exception die mit der Meldung verbunden ist.
	 * @param message Zusätzliche Nachricht zur Meldung.
	 */
	public void logError(int priority, Object sender, Throwable exception, String message)
	{
		final int minimumLogMessage = UNIMPORTANT_INFO;
		final int minimumLogSender = INERESTING_INFO;
		final int minimumLogException = UNIMPORTANT_ERROR;
		final int minimumLogStackTrace = REGULAR_ERROR;
		final int minimumPrintMessage = INERESTING_INFO;
		final int minimumPrintSender = INERESTING_INFO;
		final int minimumPrintException = UNIMPORTANT_ERROR;
		final int minimumPrintStackTrace = REGULAR_ERROR;
		//TODO minimumShowInfo
		final int minimumShowMessage = NORMAL_ERROR;
		final int minimumShowException = IMPORTANT_ERROR;
		
		final java.text.SimpleDateFormat dateFormater = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 

		if(logStream == null)
		{
			try
			{
				String logFileName = Functions.getFolder("log.txt");
				logStream = new java.io.PrintWriter(new FileWriter(logFileName));
				logStream.println("--------------");
				logStream.println("New Session...");
				System.out.println("Fehler werden gespeichert in " + logFileName + ".");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}	
		}
		
		String senderClassString = "";
		if(sender != null)
		{
			Class<?> c = sender.getClass();
			while(c != null)
			{
				senderClassString += c.getCanonicalName();
				c = c.getSuperclass();
				if(c == Object.class)
					break;
				senderClassString += " <- ";
			}
		}
		
		if(logStream != null)
		{
			if(priority >= minimumLogMessage)
			{
				logStream.println();
				logStream.println(dateFormater.format(new java.util.Date()));
				if(!loadFinished)
					logStream.println("PartyDJ noch nicht vollständig geladen.");
				if(message != null)
					logStream.println(message);
			}
			if(priority >= minimumLogSender && sender != null)
			{
				logStream.println(senderClassString);
				logStream.println(sender);
			}
			if(priority >= minimumLogException && exception != null)
				logStream.println(exception);
			if(priority >= minimumLogStackTrace && exception != null)
				exception.printStackTrace(logStream);
			logStream.flush();
		}
		
		{
			if(priority >= minimumPrintMessage)
			{
				System.err.println();
				System.err.println(dateFormater.format(new java.util.Date()));
				if(!loadFinished)
					System.err.println("PartyDJ noch nicht vollständig geladen.");
				if(message != null)
					System.err.println(message);
			}
			if(priority >= minimumPrintSender && sender != null)
			{
				System.err.println(senderClassString);
				System.err.println(sender);
			}
			if(priority >= minimumPrintException && exception != null)
				System.err.println(exception);
			if(priority >= minimumPrintStackTrace && exception != null)
				exception.printStackTrace();
			System.err.flush();
		}
		
		if(priority >= minimumShowMessage && priority < minimumShowException)
		{
			String toShow = "";
			if(message != null)
				toShow += message + "\n";
			if(exception != null)
				toShow += exception;
			JOptionPane.showMessageDialog(null, toShow, "PartyDJ Fehler", JOptionPane.ERROR_MESSAGE);
		}
		else if(priority >= minimumShowException)
		{
			String toShow = "";
			if(message != null)
				toShow += message + "\n";
			if(!loadFinished)
				toShow += "PartyDJ noch nicht vollständig geladen.\n";
			if(sender != null)
				toShow += senderClassString + "\n" + sender + "\n";
			if(exception != null)
				toShow += exception;
			JOptionPane.showMessageDialog(null, toShow, "PartyDJ Fehler", JOptionPane.ERROR_MESSAGE);
		}
		
		if(priority >= FATAL_ERROR)
		{
			JOptionPane.showMessageDialog(null, "PartyDJ wird jetzt beendet.\nEinstellungen werden nicht gespeichert.", "Schwerwiegender PartyDJ Fehler", JOptionPane.ERROR_MESSAGE);
			// CloseListener werden nicht informiert.
			Runtime.getRuntime().removeShutdownHook(closeListenThread);
			System.exit(FATAL_ERROR);
		}
	}
	//TODO überladen
	
	/** Beendet den PartyDJ komplett. */
	public void closePartyDJ()
	{
		try
		{
			Runtime.getRuntime().removeShutdownHook(closeListenThread);
		}
		catch(java.lang.IllegalStateException e){}
		try
		{
			data.writeSetting("LastPosition", Double.toString(player.getPosition()));
			TrayIcon[] icons = SystemTray.getSystemTray().getTrayIcons();
			for(TrayIcon icon : icons)
				SystemTray.getSystemTray().remove(icon);
		}
		catch (SettingException e){}
		synchronized(closeListener)
		{
			for(CloseListener listener : closeListener)
				listener.closing();
		}
		System.exit(0);
	}

	/** Wird Fenstern die sich registrieren übergeben.
	 *  Damit werden sie automatisch beim Controller abgemeldet.*/
	class ClientWindowListener extends WindowAdapter
	{
		Frame window;
		ClientWindowListener(Frame window)
		{
			this.window = window;
		}
		
		@Override public void windowClosing(WindowEvent arg0)
		{
			unregisterWindow(window);
		}
	}
	
	/** Main-Funktion.
	 *  Startet den PartyDJ.
	 *  @param args Befehlszeilenargumente des PartyDJ.
	 */
	public static void main(String[] args)
	{
		try
		{
			new basics.Controller(args);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Fehler beim Erstellen des PartyDJ-Controllers:\n" + e.getMessage(), "PartyDJ", JOptionPane.ERROR_MESSAGE);
			getInstance().closePartyDJ();
		}		
	}
}

