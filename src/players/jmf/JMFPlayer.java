package players.jmf;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

import javax.media.*;
import players.IPlayer;
import players.PlayStateListener;
import players.PlayerException;
import players.PlayStateListener.Reason;
import basics.Controller;
import basics.PlayerContact;
import common.Track;
import common.Track.Problem;
import data.IData;
import data.SettingException;


/**
 * Einfacher Player.
 * 
 * Verwendet JMF.
 * Keine Lautstärkenänderung.
 * Kein umblenden.
 * 
 * @author Eraser
 */
public class JMFPlayer implements IPlayer
{
	private IData data = Controller.getInstance().getData(); 
	public int volume;
	
	PlayerContact contact;
	
	private final Set<PlayStateListener> playStateListener = new HashSet<PlayStateListener>();
	private boolean status;
	private Track currentTrack;
	
	Player player;
	
	public JMFPlayer(PlayerContact playerContact)
	{
		contact = playerContact;
		try
		{
			volume = Integer.parseInt(data.readSetting("PlayerVolume", "100"));
		}
		catch (NumberFormatException e)
		{
			volume = 100;
			e.printStackTrace();
		}
		catch (SettingException e)
		{
			volume = 100;
			e.printStackTrace();
		}
	}
	
	public void fadeIn()
	{
		play();
	}

	public void fadeInOut()
	{
		if(status)
			fadeOut();
		else
			fadeIn();
	}

	public void fadeOut()
	{
		pause();
	}

	public void pause()
	{
		if(player != null)
			player.stop();
		
		changeState(false);
	}

	public void play()
	{
		if(player != null)
			player.start();
		
		changeState(true);
		
	}
	
	public void playNext()
	{
		start(contact.requestNextTrack(), Reason.RECEIVED_FORWARD);
	}

	public void playPrevious()
	{
		start(contact.requestPreviousTrack(), Reason.RECEIVED_BACKWARD);
	}

	public void playPause()
	{
		if(status)
			pause();
		else
			play();
	}

	public void setPosition(double seconds)
	{
		if(seconds < 0)
			seconds = 0;
		else if(seconds > getDuration())
			seconds = getDuration();
		
		player.setMediaTime(new Time(seconds));
	}

	public void setVolume(int volume)
	{
		if(volume < 0)
			volume = 0;
		
		else if(volume > 100)
			volume = 100;
		
		this.volume = volume;

		try
		{
			data.writeSetting("PlayerVolume", String.valueOf(volume));
		}
		catch (SettingException e){}
		
		if(status)
			player.getGainControl().setLevel(volume/100f);
		
		//gui.ClassicWindow.setVolume(volume);
	}

	public void start()
	{
		setPosition(0);
		play();
	}
	
	public void start(Track track)
	{
		start(track, Reason.RECEIVED_NEW_TRACK);
	}

	private void start(Track track, Reason reason)
	{
		if(track == null)
			return;
		
		if(player != null)
			player.close();
		
		try
		{
			player = getPlayer(track.path);
		
			player.getGainControl().setLevel(volume / 100f);
		}
		catch (PlayerException e)
		{
			contact.reportProblem(e, track);
			return;
		}
	
		player.addControllerListener(new ControllerListener()
								{
									public void controllerUpdate(ControllerEvent e)
									{
										if(e instanceof EndOfMediaEvent)
										{
											if(contact != null)
											{
												Track next = contact.requestNextTrack();
												if(next == null)
													contact.playCompleted();
												else
													start(next, Reason.END_OF_TRACK);
											}
											((Player)e.getSource()).stop();
											((Player)e.getSource()).deallocate();
										}
									}
								});
		player.start();
		
		if(currentTrack != track)
		{
			Track oldTrack = currentTrack;
			currentTrack = track;
			for(PlayStateListener listener : playStateListener)
				listener.currentTrackChanged(oldTrack, currentTrack, reason);
		}

		changeState(true);
	}

	public void stop()
	{
		if(player != null)
			player.stop();
		changeState(false);
		 player.deallocate();
	}

	public double getDuration()
	{
		if(player != null)
		{
			double duration = player.getDuration().getSeconds();
			contact.trackDurationCalculated(currentTrack, duration);
			return duration;
		}
		return 0;
	}
	
	public double getDuration(Track track) throws PlayerException
	{
		double duration = getDuration(track.path);
		contact.trackDurationCalculated(currentTrack, duration);
		return duration;
	}
	
	public double getDuration(String filePath) throws PlayerException
	{
		Player p = getPlayer(filePath);
		double d = p.getDuration().getSeconds();
		p.stop();
		p.deallocate();
		return d;
	}

	public Track getCurrentTrack()
	{
		//TODO
		if(player != null)
			return null;
		return null;
	}

	public double getPosition()
	{
		if(player != null)
			return player.getMediaTime().getSeconds();
		return 0;
	}

	public int getVolume()
	{
		return volume;
	}

	public boolean getPlayState()
	{
		return status;
	}

	public void dispose()
	{
		if(player != null)
		{
			player.stop();
			player.deallocate();
		}
	}

	public void setContact(PlayerContact Contact)
	{
		contact = Contact;
	}
	
	private Player getPlayer(String FileName) throws PlayerException
	{
		try
		{
			Player newPlayer = Manager.createRealizedPlayer(new File(FileName).toURI().toURL());
			if(newPlayer == null)
				throw new NoPlayerException("Player ist null.");
			return newPlayer;
		}
		catch (NoPlayerException e)
		{
			throw new PlayerException(Problem.CANT_PLAY, "Dateityp nicht unterstützt.", e);
		}
		catch (CannotRealizeException e)
		{
			throw new PlayerException(Problem.CANT_PLAY, "Kann Datei nicht wiedergeben.", e);
		}
		catch (MalformedURLException e)
		{
			throw new PlayerException(Problem.FILE_NOT_FOUND, "Ungültiger Dateiname.", e);
		}
		catch (IOException e)
		{
			throw new PlayerException(Problem.FILE_NOT_FOUND, "Fehler beim Lesen der Audio-Datei.", e);
		}
	}
	
	private void changeState(boolean newStatus)
	{
		if(newStatus != status)
		{
			status = newStatus;
			
			for(PlayStateListener listener: playStateListener)
			{
				listener.playStateChanged(status);
			}
		}
	}
	
	public void addPlayStateListener(PlayStateListener listener)
	{
		playStateListener.add(listener);
	}
	public void removePlayStateListener(PlayStateListener listener)
	{
		playStateListener.remove(listener);
	}

	public void load(Track track)
	{
		try
		{
			currentTrack = track;
			player = getPlayer(track.path);
		}
		catch (PlayerException e)
		{
			e.printStackTrace();
			return;
		}
	}
}
