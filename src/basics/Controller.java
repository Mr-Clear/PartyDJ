package basics;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import gui.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import simplePlayer.SimplePlayer;
import common.*;

import lists.EditableListModel;
import lists.ListProvider;
import data.*;
import data.derby.DerbyDB;

public class Controller
{
	public static Controller instance = null;
	public IData data = null;
	public ListProvider listProvider;
	public IPlayer player;
	
	private final HashSet<PlayStateListener> playStateListener = new HashSet<PlayStateListener>();
	private Track currentTrack;
	private EditableListModel playList;
	
	private Stack<Track> trackUpdateStack = new ArrayStack<Track>();
	Timer trackUpdateTimer; 
	
	private boolean loadFinished = false;

	JFrame window;
	
	public Controller() throws Exception
	{
		SplashWindow splash = new SplashWindow(); 
		
		if(instance == null)
			instance = this;
		else
			throw new Exception("Es darf nur ein Controller erstellt werden!");
		
	     Runtime run = Runtime.getRuntime();
	     run.addShutdownHook(new Thread()
	     {
		     public void run()
		     {
		    	 closePartyDJ();
		     }
	     });

		
		//Datenbank verbinden
		splash.setInfo("Verbinde zur Datenbank");
		try
		{
			data = new DerbyDB(Functions.getFolder() + System.getProperty("file.separator") + "DataBase");
		}
		catch (OpenDbException e)
		{
			System.err.println("Keine Verbindung zur Datenbank möglich:");
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Keine Verbindung zur Datenbank möglich!\n\n" + e.getMessage(), "PartyDJ", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		splash.setInfo("Lade Listen");
		listProvider = new ListProvider();
		
		splash.setInfo("Lade Player");
		player = new SimplePlayer(new PlayerListener());
		
		splash.setInfo("Lade Fenster");
		//window = new ClassicWindow();
		window = new TestWindow();
		
		splash.setInfo("PartyDJ bereit :)");
		data.writeSetting("LastLoadTime", Long.toString(splash.getElapsedTime()));
		splash.close();
		loadFinished = true;
	}
	
	public void addPlayStateListener(PlayStateListener listener)
	{
		playStateListener.add(listener);
	}
	public void removeMasterListListener(PlayStateListener listener)
	{
		playStateListener.remove(listener);
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
	
	public void pushTrackToUpdate(Track track)
	{
		trackUpdateStack.push(track);
		
		if(trackUpdateTimer == null)
		{
			trackUpdateTimer = new Timer();
			trackUpdateTimer.schedule(new TrackUpdateTask(trackUpdateStack), 0, 1); 
		}
	}

	public void closePartyDJ()
	{
		try
		{
			if(data != null)
				data.close();
		}
		catch (ListException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		try
		{
			new basics.Controller();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}		
	}
	
	class PlayerListener implements PlayerContact
	{

		public void playCompleted()
		{
			// TODO Auto-generated method stub
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

		public void trackChanged(Track track)
		{
			if(currentTrack != track)
			{
				Track oldTrack = currentTrack;
				currentTrack = track;
				for(PlayStateListener listener : playStateListener)
					listener.currentTrackChanged(oldTrack, currentTrack);
			}
		}
		public void stateChanged(boolean Status)
		{
			// TODO Auto-generated method stub
		}
		
		public void reportProblem(PlayerException e, Track track)
		{
			JOptionPane.showMessageDialog(null, "Fehler beim Abspielen:\n" + track.name + "\n\n" + e.getMessage(), "PartyDJ", JOptionPane.ERROR_MESSAGE);
		}
	}
}

class TrackUpdateTask extends TimerTask 
{
	final Stack<Track> trackUpdateStack;
	final Controller controller = Controller.instance;
	public TrackUpdateTask(Stack<Track> trackUpdateStack)
	{
		this.trackUpdateStack = trackUpdateStack;
	}
	
	public void run() 
	{
		try
		{
			Track track = null;
			synchronized(trackUpdateStack)
			{
				while(true)
				{
					if(trackUpdateStack.empty())
					{
						track = null;
						break;
					}
					track = trackUpdateStack.pop();
	
					if(track.duration == 0 && track.problem == Track.Problem.NONE)
					{
						break;
					}
				}
			}
			
			if(track == null)
			{
				controller.trackUpdateTimer = null;
				this.cancel();
			}
			else
			{
				if(track.duration == 0)
				{
					try
					{
						track.duration = controller.player.getDuration(track);
						try
						{
							controller.data.updateTrack(track, Track.TrackElement.DURATION);
						}
						catch (ListException e)
						{}
					}
					catch (PlayerException e)
					{
						e.printStackTrace();
						track.problem = e.problem;
						try
						{
							controller.data.updateTrack(track, Track.TrackElement.PROBLEM);
						}
						catch (ListException e1)
						{}
					}
				}
				System.out.println("Controller updated duration: " + track.path);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}

interface Stack<E> 
{
	public boolean empty();
	public void push(E elt);
	public E pop();
}

class ArrayStack<E> implements Stack<E> 
{
	private List<E> list;
	public ArrayStack() 
	{ 
		list = new ArrayList<E>();
	}
	public boolean empty()
	{
		return list.size() == 0;
	}
	public void push(E e)
	{
		synchronized(list)
		{
			list.add(e);
		}
	} 
	public E pop()
	{
		synchronized(list)
		{
			return list.remove(list.size()-1);
		}
	}
	public String toString() 
	{
		return "stack"+list.toString();
	}
}

