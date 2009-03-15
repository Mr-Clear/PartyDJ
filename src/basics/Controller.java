package basics;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.Stack;
import gui.*;
import gui.settings.SettingWindow;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import players.JLPlayer;
import players.IPlayer;
import players.PlayerException;
import common.*;
import common.Track.TrackElement;

import lists.EditableListModel;
import lists.ListException;
import lists.ListProvider;
import data.*;
import data.derby.DerbyDB;

public class Controller
{
	public final String version = "3.0.1";
	
	private static Controller instance;
	private IData data;
	private ListProvider listProvider;
	private IPlayer player;
	private Track currentTrack;
	private EditableListModel playList;
	
	private Thread closeListenTread;
	private Runtime runtime = Runtime.getRuntime();
	
	private final Set<CloseListener> closeListener = new HashSet<CloseListener>();
	private final Set<Frame> windows = new HashSet<Frame>();
	
	private Stack<Track> trackUpdateStack = new Stack<Track>();
	Timer trackUpdateTimer; 
	
	private boolean loadFinished = false;
	
	private Controller(String[] args)
	{
		SplashWindow splash = new SplashWindow(); 
		
		if(instance == null)
			instance = this;
		else
			throw new RuntimeException("Es darf nur ein Controller erstellt werden!");
		
		closeListenTread = new Thread(){
					public void run()
					{
						closePartyDJ();
					}};
		runtime.addShutdownHook(closeListenTread);
		
		String dbPath = Functions.getFolder() + System.getProperty("file.separator") + "DataBase";
		
		int lastParam = 0;
		for(String arg : args)
		{
			String argl = arg.toLowerCase();
			if(arg.charAt(0) == '-')
			{
				if(argl.equals("-dbpath"))
					lastParam = 1;
				else
					lastParam = 0;
			}
			else
			{
				switch(lastParam)
				{
				case 1:
					dbPath = arg;
				}
				lastParam = 0;
			}
		}
		
		//Datenbank verbinden
		splash.setInfo("Verbinde zur Datenbank");
		try
		{
			data = new DerbyDB(dbPath);
		}
		catch (OpenDbException e)
		{
			System.err.println("Keine Verbindung zur Datenbank möglich:");
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Keine Verbindung zur Datenbank möglich!\n\n" + e.getMessage(), "PartyDJ", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		splash.setInfo("Lade Player");
		player = new JLPlayer(new PlayerListener());
		
		splash.setInfo("Lade Listen");
		try
		{
			listProvider = new ListProvider();
		}
		catch (ListException e)
		{
			System.err.println("Listen konnten nicht geladen werden:");
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Listen konnten nicht geladen werden!\n\n" + e.getMessage(), "PartyDJ", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		splash.setInfo("Lade Fenster");
		registerWindow(new ClassicWindow());
		//registerWindow(new TestWindow());
		registerWindow(new SettingWindow());
		
		splash.setInfo("PartyDJ bereit :)");
		try
		{
			getData().writeSetting("LastLoadTime", Long.toString(splash.getElapsedTime()));
		}
		catch (SettingException e){}
		splash.close();
		loadFinished = true;
	}
	

	/**
	 * @return Die Instanz des Controllers.
	 */
	public static Controller getInstance()
	{
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
		windows.remove(window);
		window.dispose();
		if(windows.isEmpty())
			closePartyDJ();
	}
	
	public void addCloseListener(CloseListener listener)
	{
		closeListener.add(listener);
	}
	public void removeCloseListener(CloseListener listener)
	{
		closeListener.remove(listener);
	}

	public void closePartyDJ()
	{
		runtime.removeShutdownHook(closeListenTread);
		synchronized(closeListener)
		{
			for(CloseListener listener : closeListener)
				listener.closing();
		}
		System.exit(0);
	}

	private class PlayerListener implements PlayerContact
	{
		public Track predictNextTrack()
		{
			// TODO Auto-generated method stub
			return null;
		}
		
		public Track requestNextTrack()
		{
			Track nextTrack = null;
			if(playList != null)
			{
				synchronized(playList)
				{
					if(playList != null)
					{
						if(playList.getSize() > 0);
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
			}
			// TODO Auto-generated method stub
			return nextTrack;
		}

		public Track requestPreviousTrack()
		{
			// TODO Auto-generated method stub
			return null;
		}

		// TODO
		public void trackChanged(Track track)
		{
			if(track.duration != getPlayer().getDuration())
			{
				track.duration = getPlayer().getDuration();
				try
				{
					getData().updateTrack(track, TrackElement.DURATION);
				}
				catch (ListException e)
				{}
			}
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
		}		
	}
}

