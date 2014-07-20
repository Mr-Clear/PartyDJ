package basics;

import common.Functions;
import common.Track;

import data.IData;

import lists.EditableListModel;
import lists.data.ListProvider;

import gui.settings.SettingNode;

import players.IPlayer;

import java.awt.Frame;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.Timer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Bietet Zugriff auf alle anderen wichtigen Elemente des PartyDJ.
 *
 * @author Eraser
 * @author Sam
 */
public abstract class Controller
{
	/** Version des PartyDJ */
	private final static String version = "3.0";

	/** Statischer Verweis auf diese Instanz. */
	protected static Controller instance;
	/** Dieser Thread überwacht ob der PartyDJ geschlossen wird. */
	protected Thread closeListenThread;

	/** Wunschliste aus der immer der oberste Track gespielt und dabei gelöscht wird. */
	protected EditableListModel playList;
	/** Liste der zuletzt gespielten lieder. Hat maximal 100 Einträge. */
	protected EditableListModel lastPlayedList;
	/** Zugriff auf Listen. */
	protected ListProvider listProvider;
	/** Liste aller registrierten CloseListener. */
	protected final Set<CloseListener> closeListener = new HashSet<>();
	/** Liste aller registrierten ErrorListener. */
	protected final Set<ErrorListener> errorListener = new HashSet<>();
	/** Liste aller registrierten Fenster.
	 *  Wenn die Liste leer wird, schließt sich der PartyDJ. */
	protected final Set<Frame> windows = new HashSet<>();
	/** RootNode der Einstellungen. Alle Einstellungen haben einen Knoten in diesem Baum. */
	protected SettingNode settingTree;
	/** Timer der im regelmäßigen Abstand die Dauer der Tracks aus trackUpdateStack einliest. */
	protected Timer trackUpdateTimer;
	/** Stapel mit Liedern deren Dauer eingelesen werden soll. */
	protected final Stack<Track> trackUpdateStack = new Stack<>();
	/** Wird am Ende des Konstruktors auf true gesetzt. */
	protected boolean loadFinished = false;
	/** Ermöglicht es einfache Befehle auszuführen. */
	protected final Scripter scripter;

	/** Stream der Fehlermeldungen in eine Datei schreibt. */
	protected java.io.PrintWriter logStream;
	/** Executor service für das gesamte Programm. */
	protected final Executor executor = Executors.newCachedThreadPool();
	
	/**
	 * @param args Befehlszeilenargumente.
	 */
	protected Controller(final String[] args)
	{
		/* Prüfen ob bereits eine Instanz läuft. */
		if(instance == null)
			instance = this;
		else
		{
			logError(IMPORTANT_ERROR, this, null, "Es darf nur ein Controller erstellt werden!");
			throw new RuntimeException("Es darf nur ein Controller erstellt werden!");
		}

		scripter = new Scripter();
		loadSettings();
	}

	private void loadSettings()
	{
		settingTree = new SettingNode("Einstellungen", gui.settings.About.class);
		addSettingNode(new SettingNode("Tracks", gui.settings.TrackManager.class), settingTree);
		addSettingNode(new SettingNode("Listen", gui.settings.Lists.class), settingTree);
		addSettingNode(new SettingNode("Shuffle", gui.settings.Shuffle.class), settingTree);
		addSettingNode(new SettingNode("HotKeys", gui.settings.HotKeys.class), settingTree);
		addSettingNode(new SettingNode("Verschiedenes", gui.settings.Misc.class), settingTree);
		addSettingNode(new SettingNode("Zeug", gui.settings.Stuff.class), settingTree);
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

	public abstract IData getData();

	public abstract IPlayer getPlayer();

	public Scripter getScripter()
	{
		return scripter;
	}

	public ListProvider getListProvider()
	{
		return listProvider;
	}

	public void setPlayList(final EditableListModel list)
	{
		playList = list;
	}

	public EditableListModel getPlayList()
	{
		return playList;
	}

	public static String getLastPlayedName()
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
	public void pushTrackToUpdate(final Track track)
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
	 * @param windowClassName Name des Fensters.
	 * @return true, wenn erfolgreich.
	 */
	public boolean loadWindow(String windowClassName)
	{
		class WindowLoader implements Runnable
		{
			private boolean result;
			private String className;

			public WindowLoader(String className)
			{
				this.className = className;
			}

			@Override
			public void run()
			{
				if("classic".toLowerCase().equals(className))
					className = "gui.ClassicWindow";
				else if("settings".toLowerCase().equals(className))
					className = "gui.settings.SettingWindow";
				else if("test".toLowerCase().equals(className))
					className = "gui.TestWindow";
				else if("derbydebug".toLowerCase().equals(className))
					className = "data.derby.DebugWindow";
				try
				{
					final Class<?> c = Class.forName(className);
					if(JFrame.class.isAssignableFrom(c))
						registerWindow((JFrame)c.getConstructor().newInstance());
					else
						throw new ClassCastException(c.toString() + " ist kein Fenster");
					result = true;
				}
				catch (final Exception e)
				{
					logError(IMPORTANT_ERROR, this, e, "Fenster kann nicht geladen werden: " + className);
					result = false;
				}
			}

			public boolean getResult()
			{
				return result;
			}

		}

		WindowLoader windowLoader = new WindowLoader(windowClassName);
		if(SwingUtilities.isEventDispatchThread())
			windowLoader.run();
		else
			try
			{
				SwingUtilities.invokeAndWait(windowLoader);
			}
			catch (Exception e)
			{
				logError(IMPORTANT_ERROR, this, e, "Fenster kann nicht geladen werden: " + windowClassName);
				return false;
			}

		return windowLoader.getResult();

	}

	/**Registriert ein Fenster.
	 * Erstellt einen WindowsListener für das Fenster der bei windowColsing das Fenster schließt.
	 * Wenn das letzte registrierte Fenster geschlossen wird, beendet sich der PartyDJ.
	 * @param window Fenster das registriert werden soll.
	 */
	public void registerWindow(final JFrame window)
	{
		try
		{
			if(SwingUtilities.isEventDispatchThread())
			{
				windows.add(window);
				window.addWindowListener(new ClientWindowListener(window));
			}
			else
				SwingUtilities.invokeAndWait(new Runnable()
				{
					@Override
					public void run()
					{
						windows.add(window);
						window.addWindowListener(new ClientWindowListener(window));
					}
				});
		}
		catch (final InterruptedException e){e.printStackTrace();}
		catch (final InvocationTargetException e){e.printStackTrace();}
	}

	/**Schließt das angegebene Fenster.
	 * Wenn es das letzte registrierte Fenster ist, wird der PartyDJ beendet.
	 *
	 * @param window
	 */
	public void unregisterWindow(final Frame window)
	{
		windows.remove(window);
		window.dispose();
		if(windows.isEmpty())
			closePartyDJ();
	}

	/** CloseListener.closing wird aufgerufen bevor der PDJ geschlossen wird.
	 * @param listener Der CloseListener.*/
	public void addCloseListener(final CloseListener listener)
	{
		closeListener.add(listener);
	}
	public void removeCloseListener(final CloseListener listener)
	{
		closeListener.remove(listener);
	}

	/** ErrorListener.errorOccurred wird aufgerufen wenn ein Fehler über Controller.logError gemeldet wird.
	 * @param listener Der ErrorListener.*/
	public void addErrorListener(final ErrorListener listener)
	{
		errorListener.add(listener);
	}
	public void removeErrorListener(final ErrorListener listener)
	{
		errorListener.remove(listener);
	}

	/**Fügt eine SettingNode im SettingWindow ein.
	 *
	 * @param node Node die eingefügt wird.
	 * @param parent Vorhandene Node an die die neue Node als Child angehängt wird.
	 */
	public static void addSettingNode(final SettingNode node, final SettingNode parent)
	{
		parent.add(node);
	}

	@Deprecated
	public static void addSettingNode(@SuppressWarnings("unused") final SettingNode node, @SuppressWarnings("unused") final String path)
	{
		//TODO SettingNodes komfortabler machen.
		throw new UnsupportedOperationException();
	}

	@Deprecated
	public static void removeSettingNode(@SuppressWarnings("unused") final SettingNode node)
	{
		//TODO SettingNodes komfortabler machen.
		throw new UnsupportedOperationException();
	}
	
	public SettingNode getSetingTree()
	{
		return settingTree;
	}

	public static String getVersion()
	{
		return version;
	}
	
	public Executor getExecutor()
	{
	    return executor;
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
	public void logError(final int priority, final Object sender, final Throwable exception, final String message)
	{
		final int minimumLogMessage = UNIMPORTANT_INFO;
		final int minimumLogSender = INERESTING_INFO;
		final int minimumLogException = UNIMPORTANT_ERROR;
		final int minimumLogStackTrace = REGULAR_ERROR;
		final int minimumPrintMessage = INERESTING_INFO;
		final int minimumPrintSender = INERESTING_INFO;
		final int minimumPrintException = UNIMPORTANT_ERROR;
		final int minimumPrintStackTrace = REGULAR_ERROR;
		final int minimumShowMessage = NORMAL_ERROR;
		final int minimumShowException = IMPORTANT_ERROR;

		final java.text.SimpleDateFormat dateFormater = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		synchronized(errorListener)
		{
			for(final ErrorListener listener : errorListener)
			{
				try
				{
					listener.errorOccurred(priority, sender, exception, message, new java.util.Date());
				}
				catch (Exception e)
				{
					// Kein Fehler beim melden eines Fehlers melden.
				}
			}
		}

		if(logStream == null)
		{
			try
			{
				final String logFileName = Functions.getFolder("log.txt");
				logStream = new java.io.PrintWriter(new FileWriter(logFileName, true), true);
				logStream.println("--------------");
				logStream.println("New Session...");
				System.out.println("Fehler werden gespeichert in " + logFileName + ".");
			}
			catch (final IOException e)
			{
				e.printStackTrace();
			}
		}

		final StringBuilder senderClassStringBuilder = new StringBuilder();
		if(sender != null)
		{
			Class<?> c = sender.getClass();
			while(c != null)
			{
				senderClassStringBuilder.append(c.getCanonicalName());
				c = c.getSuperclass();
				if(c == Object.class)
					break;
				senderClassStringBuilder.append(" <- ");
			}
		}

		final String senderClassString = senderClassStringBuilder.toString();

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
	/**Verarbeitet eine Fehlermeldung.
	 * <p>Abhängig von der Priorität werden unterschiedliche Aktionen ausgeführt.
	 *
	 * @param priority Priorität der Meldung.
	 * @param sender Objekt das die Meldung schickt. Üblicherweise this.
	 * @param exception Wenn vorhanden, eine Exception die mit der Meldung verbunden ist.
	 */
	public void logError(final int priority, final Object sender, final Throwable exception)
	{
		logError(priority, sender, exception, null);
	}
	/**Verarbeitet eine Fehlermeldung.
	 * <p>Abhängig von der Priorität werden unterschiedliche Aktionen ausgeführt.
	 *
	 * @param priority Priorität der Meldung.
	 * @param exception Wenn vorhanden, eine Exception die mit der Meldung verbunden ist.
	 */
	public void logError(final int priority, final Throwable exception)
	{
		logError(priority, null, exception, null);
	}

	/** Beendet den PartyDJ komplett. */
	public void closePartyDJ()
	{
		try
		{
			Runtime.getRuntime().removeShutdownHook(closeListenThread);
		}
		catch(final java.lang.IllegalStateException e) { /* ignore */ }

		for(final TrayIcon icon : SystemTray.getSystemTray().getTrayIcons())
		{
			try
			{
				SystemTray.getSystemTray().remove(icon);
			}
			catch (Exception e)
			{
				logError(Controller.UNIMPORTANT_ERROR, this, e, "Fehler in Plugin");
			}
		}

		synchronized(closeListener)
		{
			for(final CloseListener listener : closeListener)
			{
				try
				{
					listener.closing();
				}
				catch (Exception e)
				{
					logError(Controller.UNIMPORTANT_ERROR, listener, e, "Fehler in Plugin");
				}
			}
		}
		System.exit(0);
	}

	/** Wird Fenstern die sich registrieren übergeben.
	 *  Damit werden sie automatisch beim Controller abgemeldet.*/
	class ClientWindowListener extends WindowAdapter
	{
		Frame window;
		ClientWindowListener(final Frame window)
		{
			this.window = window;
		}

		@Override public void windowClosing(final WindowEvent arg0)
		{
			unregisterWindow(window);
		}
	}
}

