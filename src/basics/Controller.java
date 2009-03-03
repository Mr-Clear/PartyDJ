package basics;
import java.util.HashSet;
import gui.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import simplePlayer.SimplePlayer;
import common.*;

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
			// TODO Debuginfo entfernen!
			System.err.println("Keine Verbindung zur Datenbank möglich:");
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Keine Verbindung zur Datenbank möglich!\n\nLäuft schon eine Instanz?\nRichtige Derby Verion?", "PartyDJ", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		splash.setInfo("Lade Listen");
		listProvider = new ListProvider();
		
		splash.setInfo("Lade Player");
		player = new SimplePlayer(new PlayerListener());
		
		splash.setInfo("Lade Fenster");
		window = new ClassicWindow();
		//window = new TestWindow();
		
		splash.setInfo("PartyDJ bereit :)");
		data.writeSetting("LastLoadTime", Long.toString(splash.getElapsedTime()));
		splash.close();
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
	
	public boolean playTrack(Track track)
	{
		Track oldTrack = currentTrack;
		currentTrack = track;
		for(PlayStateListener listener : playStateListener)
			listener.currentTrackChanged(oldTrack, currentTrack);
		return false;
	}
	
	public void closePartyDJ()
	{
		try
		{
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

		public void PlayCompleted()
		{
			// TODO Auto-generated method stub
		}

		public void ProceedError(PlayerException e)
		{
			// TODO Auto-generated method stub
		}

		public String RequestNextTrack()
		{
			// TODO Auto-generated method stub
			return null;
		}

		public String RequestPreviousTrack()
		{
			// TODO Auto-generated method stub
			return null;
		}

		public void StateChanged(boolean Status)
		{
			// TODO Auto-generated method stub
		}
	}
}
