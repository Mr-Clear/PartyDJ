package de.klierlinge.partydj.basics;

import java.awt.Frame;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import org.apache.logging.log4j.core.LogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.klierlinge.partydj.common.Track;
import de.klierlinge.partydj.data.IData;
import de.klierlinge.partydj.gui.ErrorLogWindow;
import de.klierlinge.partydj.gui.settings.SettingNode;
import de.klierlinge.partydj.lists.EditableListModel;
import de.klierlinge.partydj.lists.data.ListProvider;
import de.klierlinge.partydj.logging.LoggedMessage;
import de.klierlinge.partydj.players.IPlayer;

/**
 * Bietet Zugriff auf alle anderen wichtigen Elemente des PartyDJ.
 *
 * @author Eraser
 * @author Sam
 */
public abstract class Controller
{
	private static final Logger log = LoggerFactory.getLogger(Controller.class);
	
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
	
	/** Fenster zum Anzeigen von Fehlern. */
	protected final ErrorLogWindow errorLogWindow = new ErrorLogWindow(this);

	/**
	 * @param args Befehlszeilenargumente.
	 */
	protected Controller(final String[] args)
	{
		/* Prüfen ob bereits eine Instanz läuft. */
		if(instance != null)
		{
			log.error("Es darf nur ein Controller erstellt werden!");
			throw new RuntimeException("Es darf nur ein Controller erstellt werden!");
		}

        instance = this;
		scripter = new Scripter();
		loadSettings();
	}

	private void loadSettings()
	{
		settingTree = new SettingNode("Einstellungen", de.klierlinge.partydj.gui.settings.About.class);
		addSettingNode(new SettingNode("Tracks", de.klierlinge.partydj.gui.settings.TrackManager.class), settingTree);
		addSettingNode(new SettingNode("Listen", de.klierlinge.partydj.gui.settings.Lists.class), settingTree);
		addSettingNode(new SettingNode("Shuffle", de.klierlinge.partydj.gui.settings.Shuffle.class), settingTree);
		addSettingNode(new SettingNode("HotKeys", de.klierlinge.partydj.gui.settings.HotKeys.class), settingTree);
		addSettingNode(new SettingNode("Verschiedenes", de.klierlinge.partydj.gui.settings.Misc.class), settingTree);
		addSettingNode(new SettingNode("Zeug", de.klierlinge.partydj.gui.settings.Stuff.class), settingTree);
	}

	public static boolean isLoaded()
    {
        return instance != null;
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
	public boolean loadWindow(final String windowClassName)
	{
		class WindowLoader implements Runnable
		{
			private boolean result;
			private String className;

			public WindowLoader(final String className)
			{
				this.className = className;
			}

			@Override
			public void run()
			{
				if("classic".toLowerCase().equals(className))
					className = de.klierlinge.partydj.gui.ClassicWindow.class.getName();
				else if("settings".toLowerCase().equals(className))
					className = de.klierlinge.partydj.gui.settings.SettingWindow.class.getName();
				else if("test".toLowerCase().equals(className))
					className = de.klierlinge.partydj.gui.TestWindow.class.getName();
				else if("derbydebug".toLowerCase().equals(className))
					className = de.klierlinge.partydj.data.derby.DebugWindow.class.getName();
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
					log.error("Fenster kann nicht geladen werden: " + className, e);
					result = false;
				}
			}

			public boolean getResult()
			{
				return result;
			}

		}

		final WindowLoader windowLoader = new WindowLoader(windowClassName);
		if(SwingUtilities.isEventDispatchThread())
			windowLoader.run();
		else
			try
			{
				SwingUtilities.invokeAndWait(windowLoader);
			}
			catch (final Exception e)
			{
				log.error("Fenster kann nicht geladen werden: " + windowClassName, e);
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
	@SuppressWarnings("unused")
	public static void addSettingNode(final SettingNode node, final String path)
	{
		//TODO SettingNodes komfortabler machen.
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@SuppressWarnings("unused")
	public static void removeSettingNode(final SettingNode node)
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

	public void logEvent(final LogEvent event)
	{
	    synchronized(errorListener)
		{
			for(final ErrorListener listener : errorListener)
			{
				try
				{
					listener.errorOccurred(event);
				}
				catch (final Exception e)
				{
				    log.warn("Failed to notify error listener.", e);
				}
			}
		}
	}
	
	public void fatalExit()
	{
		log.error("Fatal exit!");
		JOptionPane.showMessageDialog(null, "PartyDJ wird jetzt beendet.\nEinstellungen werden nicht gespeichert.", "Schwerwiegender PartyDJ Fehler", JOptionPane.ERROR_MESSAGE);
		Runtime.getRuntime().removeShutdownHook(closeListenThread); /* CloseListener werden nicht informiert. */
		System.exit(1);
	}
	
	/** Öffnet das Fenster zum Anzeigen von Fehlern. */
	public void showErrorWindow()
	{
		errorLogWindow.setVisible(true);
	}

	/** Beendet den PartyDJ komplett. */
	public void closePartyDJ()
	{
		try
		{
			Runtime.getRuntime().removeShutdownHook(closeListenThread);
		}
		catch(final java.lang.IllegalStateException e)
		{
		    log.warn("Failed to remove shutdown hook", e);
		}

		for(final TrayIcon icon : SystemTray.getSystemTray().getTrayIcons())
		{
			try
			{
				SystemTray.getSystemTray().remove(icon);
			}
			catch (final Exception e)
			{
				log.warn("Fehler bei Schließen von Tray Icon", e);
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
				catch (final Exception e)
				{
					log.warn("Fehler bei Schließen von Plugin: " + closeListener, e);
				}
			}
		}
		log.info("End");
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

