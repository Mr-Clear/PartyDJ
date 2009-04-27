package basics;
import gui.SplashWindow;
import gui.settings.SettingNode;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.Stack;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.tree.TreeNode;
import players.IPlayer;
import players.PlayStateListener;
import players.PlayerException;
import common.*;
import common.Track.Problem;
import common.Track.TrackElement;
import lists.DbClientListModel;
import lists.EditableListModel;
import lists.ListException;
import lists.ListProvider;
import lists.TrackListModel;
import data.*;
import data.derby.DerbyDB;

/**
 * Hauptklasse vom PartyDJ.
 * <p>Enthält die main-Funktion.
 * <p>Bietet Zugriff auf alle anderen wichtigen Elemente des PartyDJ.
 * 
 * 
 * @author Eraser, Sam
 */
public class Controller
{
	/** Verion des PartyDJ */
	public final String version = "3.0.0a";
	
	private static Controller instance;
	private IData data;
	private ListProvider listProvider;
	private IPlayer player;
	private Track currentTrack;
	private EditableListModel playList;
	private TrackListModel favourites;
	
	private DbClientListModel lastPlayedList;
	
	private Thread closeListenThread;
	private Runtime runtime = Runtime.getRuntime();
	
	private final Set<CloseListener> closeListener = new HashSet<CloseListener>();
	private final Set<Frame> windows = new HashSet<Frame>();
	private final SettingNode settingTree;
	
	private Stack<Track> trackUpdateStack = new Stack<Track>();
	Timer trackUpdateTimer; 
	
	private boolean loadFinished = false;
	private Track predictedTrack;
	
	private Controller(String[] args)
	{
		SplashWindow splash = new SplashWindow(); 
		
		if(instance == null)
			instance = this;
		else
			throw new RuntimeException("Es darf nur ein Controller erstellt werden!");
		
		closeListenThread = new Thread(){
					public void run()
					{
						closePartyDJ();
					}};
		runtime.addShutdownHook(closeListenThread);
		
		String dbPath = Functions.getFolder() + System.getProperty("file.separator") + "DataBase";
		List<String> windows = new ArrayList<String>();
		int whichPlayer = -1;	// 0=JMF, 1=JL
		
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
					windows.add(arg);
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
				System.err.println("Keine Verbindung zur Datenbank möglich:");
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Keine Verbindung zur Datenbank möglich!\n\n" + dbPath + "\n" + e.getMessage(), "PartyDJ", JOptionPane.ERROR_MESSAGE);
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
				favourites = listProvider.getDbList("Playlist");
			}
			catch (ListException e)
			{
				System.err.println("Listen konnten nicht geladen werden:");
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Listen konnten nicht geladen werden!\n\n" + e.getMessage(), "PartyDJ", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
		}

		splash.setOpacity(0.95f);
		splash.setInfo("Lade Fenster");
		{
			settingTree = new SettingNode("Einstellungen", gui.settings.About.class);
			addSettingNode(new SettingNode("Einstellungen", gui.settings.Settings.class), settingTree);
			addSettingNode(new SettingNode("Hauptliste", gui.settings.MasterList.class), settingTree);
			addSettingNode(new SettingNode("Zeug", gui.settings.Stuff.class), settingTree);
			
			if(windows.size() == 0)
				windows.add("gui.ClassicWindow");
			
			for(String window : windows)
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
			catch (SettingException e1){}
			if(firstTrackPath != null)
			{
				Track firstTrack = null;
				firstTrack = listProvider.getMasterList().getTrackByPath(firstTrackPath);
				if(firstTrack != null)
				{
					splash.setInfo("Starte " + firstTrack.name);
					try
					{
						player.load(firstTrack);
						double pos = 0;
						try
						{
							pos = Double.parseDouble(data.readSetting("LastPosition", "0"));
						}
						catch (NumberFormatException e){}catch (SettingException e){}
						player.setPosition(pos);
						player.fadeIn();
					}
					catch (PlayerException e){}
				}
			}
		}

		splash.setInfo("PartyDJ bereit :)");
		getData().writeSetting("LastLoadTime", Long.toString(splash.getElapsedTime()));
		splash.close();
		loadFinished = true;
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
	
	public ListProvider getListProvider()
	{
		return listProvider;
	}
	


	public Track getCurrentTrack()
	{
		return currentTrack;
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
	
	/** Gibt zurück ob der PartyDJ vollständig geladen ist. */
	public boolean isLoadFinished()
	{
		return loadFinished;
	}
	
	/**Schiebt einen Track auf den Duration-Update-Stapel.
	 * Damit wird die länge des Tracks eingelesen, sobald er an der Reihe ist.
	 */
	public void pushTrackToUpdate(Track track)
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
			JOptionPane.showMessageDialog(null, "Fenster kann nicht geladen werden: " + className + "\n\n" + e.getMessage(), "PartyDJ", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
	/**Registriert ein Fenster.
	 * Erstellt einen WindowsListener für das Fenster der bei windowColsing das Fenster schließt.
	 * Wenn das letzte registrierte Fenster geschlossen wird, beendet sich der PartyDJ.
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
		//System.out.println("unregister " + window.getTitle());
		windows.remove(window);
		window.dispose();
		if(windows.isEmpty())
			closePartyDJ();
	}
	
	/** CloseListener.closing wird aufgerufen bevor der PDJ geschlossen wird. */
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
	 */
	public void addSettingNode(SettingNode node, SettingNode parent)
	{
		parent.add(node);
	}
	public void addSettingNode(SettingNode node, String path)
	{
		//TODO
	}
	public void removeSettingNode(SettingNode node)
	{
		//TODO
	}
	public TreeNode getSetingTree()
	{
		return settingTree;
	}

	public void closePartyDJ()
	{
		try
		{
			runtime.removeShutdownHook(closeListenThread);
		}
		catch(java.lang.IllegalStateException e){}
		try
		{
			data.writeSetting("LastPosition", Double.toString(player.getPosition()));
		}
		catch (SettingException e){}
		synchronized(closeListener)
		{
			for(CloseListener listener : closeListener)
				listener.closing();
		}
		System.exit(0);
	}

	private class PlayerListener implements PlayerContact, PlayStateListener
	{
		public Track predictNextTrack()
		{
			if(predictedTrack == null)
			{
				if(favourites.getSize() > 0 && Math.random() > 0.5)
				{
					predictedTrack = favourites.getElementAt((int)(Math.random() * favourites.getSize()));
				}
				else
				{
					predictedTrack = listProvider.getMasterList().getElementAt((int)(Math.random() * listProvider.getMasterList().getSize()));
				}
			}
			return predictedTrack;
		}
		
		public Track requestNextTrack()
		{
			Track nextTrack = null;
			if(playList != null)
			{
				synchronized(playList)
				{

					if(playList.getSize() > 0)
					{
						nextTrack = playList.getElementAt(0);
						try
						{
							playList.remove(0);
						}
						catch (ListException e)
						{
							e.printStackTrace();
							//TODO
						}
					}
				}
			}
			if(nextTrack == null)
			{
				nextTrack = predictNextTrack();
				predictedTrack = null;
			}
			return nextTrack;
		}

		public Track requestPreviousTrack()
		{
			if(lastPlayedList.getSize() == 0)
				return currentTrack;
			
			Track previous = lastPlayedList.getElementAt(lastPlayedList.getSize() - 1);
			
			try
			{
				lastPlayedList.remove(lastPlayedList.getSize() - 1);
				playList.add(0, currentTrack);
			}
			catch (ListException e)
			{
				e.printStackTrace();
			}
			
			return previous;
		}
	
		public void playCompleted()
		{
			// TODO Auto-generated method stub
		}
		
		public void reportProblem(PlayerException e, Track track)
		{
			track.problem = e.problem;
			JOptionPane.showMessageDialog(null, "Fehler beim Abspielen:\n" + track.name + "\n\n" + e.getMessage(), "PartyDJ", JOptionPane.ERROR_MESSAGE);
		}

		public void trackDurationCalculated(Track track, double duration)
		{
			if(duration > 0 && track.problem != Problem.NONE)
			{
				track.problem = Problem.NONE;
				try
				{
					data.updateTrack(track, TrackElement.PROBLEM);
				}
				catch (ListException e){}
			}
			
			if(track.duration != duration)
			{
				track.duration = duration;
				try
				{
					data.updateTrack(track, TrackElement.DURATION);
				}
				catch (ListException e){}
			}
			
		}

		//--- PlayStateListener
		public void currentTrackChanged(Track playedLast, Track playingCurrent, Reason reason)
		{
			currentTrack = playingCurrent;
			
			if(playingCurrent != null)
			{
				try
				{
					data.writeSetting("Playing", playingCurrent.path);
				}
				catch (SettingException e){}
				
				if(playingCurrent.duration == 0)
					player.getDuration();
				
				if(playingCurrent.duration > 0 && playingCurrent.problem != Problem.NONE)
				{
					playingCurrent.problem = Problem.NONE;
					try
					{
						data.updateTrack(playingCurrent, TrackElement.PROBLEM);
					}
					catch (ListException e){}
				}
			}
			
			if(playedLast != null && reason != Reason.RECEIVED_BACKWARD)
			{
				try
				{
					while(lastPlayedList.getSize() > 100)
						lastPlayedList.remove(0);
					lastPlayedList.add(lastPlayedList.getSize(), playedLast);
				}
				catch (ListException e)
				{
					e.printStackTrace();
				}
			}
		}

		public void playStateChanged(boolean playState){}
		public void volumeChanged(int volume)
		{
			try
			{
				data.writeSetting("Volume", Integer.toString(volume));
			}
			catch (SettingException e){}
		}
	}
	
	class ClientWindowListener extends WindowAdapter
	{
		Frame window;
		ClientWindowListener(Frame window)
		{
			this.window = window;
		}
		
		public void windowClosing(WindowEvent arg0)
		{
			unregisterWindow(window);
		}
	}
	
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

