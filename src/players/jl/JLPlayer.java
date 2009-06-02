package players.jl;

import java.util.HashSet;
import java.util.Set;
import players.IPlayer;
import players.PlayStateListener;
import players.PlayerException;
import basics.CloseListener;
import basics.Controller;
import basics.PlayerContact;
import javazoom.jl.decoder.JavaLayerException;
import common.Track;
import common.Track.Problem;
import data.SettingException;

//TODO Umblenden

/**
 * Einfacher Player.
 * <p>Größtenteils übernommen von Java Zoom JLayer.
 * 
 * @author Sam, Eraser
 * 
 * @see IPlayer
 */

public class JLPlayer implements IPlayer, PlaybackListener
{
	private int volume;
	
	PlayerContact contact;
	
	private final Set<PlayStateListener> playStateListener = new HashSet<PlayStateListener>();
	private boolean status;
	private Track currentTrack;
	/** Position im Pause Zustand */
	private double tempPosition;
	
	AdvancedPlayer p;
	
	public JLPlayer(PlayerContact playerContact)
	{
		contact = playerContact;
		Controller.getInstance().addCloseListener(new closeListener());
		
		try
		{
			volume = Integer.parseInt(basics.Controller.getInstance().getData().readSetting("PlayerVolume", "100"));
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
		
	public void dispose(){}

	public void fadeIn()
	{
		if(currentTrack != null)
		{
			try
			{
				load(currentTrack);
				p.fadeIn();
				p.fadeDuration = 1000;
				p.play(tempPosition);
				
				changeState(true);
			}
			catch (PlayerException e1)
			{
				e1.printStackTrace();
			}
		}
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
		tempPosition = getPosition();
		if(p != null)
		{
			p.fadeDuration = 1000;
			p.fadeOut();
			changeState(false);
		}
	}

	public double getDuration()
	{
		try
		{
			return getDuration(currentTrack);
		}
		catch (PlayerException e){}
		return 0;
	}

	public double getDuration(Track track) throws PlayerException
	{
		if(track == null)
			return 0;
		double d = getDuration(track.path);
		contact.trackDurationCalculated(track, d);
		return d;
	}

	public double getDuration(String filePath) throws PlayerException
	{
		return AdvancedPlayer.getDuration(filePath);
	}

	public Track getCurrentTrack()
	{
		return currentTrack;
	}

	public boolean getPlayState()
	{
		return status;
	}

	public double getPosition()
	{
		if(p != null)
			return p.getPosition();
		return 0;
	}

	public int getVolume()
	{
		return volume;
	}

	public void pause()
	{
		tempPosition = getPosition();
		if(p != null)
		{
			tempPosition = p.getPosition();
			p.pause();
		}
		changeState(false);
	}

	public void play()
	{
		try
		{
			start(currentTrack, tempPosition);
		}
		catch (PlayerException e)
		{
			e.printStackTrace();
		}
	}

	public void playNext()
	{
		Track track = contact.requestNextTrack();
		try
		{
			start(track, 0);
		}
		catch (PlayerException e)
		{
			contact.reportProblem(e, track);
			playNext();
			return;
		}
		currentTrackChanged(track, players.PlayStateListener.Reason.RECEIVED_FORWARD);
	}
	

	public void playPrevious()
	{
		Track track = contact.requestPreviousTrack();
		try
		{
			if(track != null)
				start(track, 0);
		}
		catch (PlayerException e)
		{
			contact.reportProblem(e, track);
		}
		currentTrackChanged(track, players.PlayStateListener.Reason.RECEIVED_BACKWARD);
	}

	public void playPause()
	{
		if(status)
			p.pause();
		else
		{
			p.play();
		}
	}

	public void addPlayStateListener(PlayStateListener listener)
	{		
		synchronized(playStateListener)
		{
			playStateListener.add(listener);
		}
	}
	
	public void removePlayStateListener(PlayStateListener listener)
	{
		synchronized(playStateListener)
		{
			playStateListener.remove(listener);
		}
	}

	public void setContact(PlayerContact Contact)
	{
		contact = Contact;
	}

	public synchronized void setPosition(double seconds)
	{
		p.sendMessage = false;
		p.fadeDuration = 300;
		p.fadeOut();
		p = null;
		tempPosition = seconds;
		fadeIn();
	}

	public void setVolume(int volume)
	{
		if(volume > 100)
			volume = 100;
		if(volume < 0)
			volume = 0;
		
		this.volume = volume;
		
		synchronized(playStateListener)
		{
			for(PlayStateListener listener : playStateListener)
			{
				listener.volumeChanged(this.volume);
			}
		}
	}

	public void start()
	{
		try
		{
			start(currentTrack, 0);
		}
		catch (PlayerException e)
		{
			e.printStackTrace();
		}
	}
	
	public void load(Track track) throws PlayerException
	{
		if(track == null)
			return;
		
		close();
		
		p = getPlayer(track.path);

		currentTrackChanged(track, players.PlayStateListener.Reason.TRACK_LOADED);
	}

	public void start(Track track) throws PlayerException
	{
		try
		{
			start(track, 0);
		}
		catch (PlayerException e)
		{
			contact.reportProblem(e, track);
			return;
		}
	}
	
	private synchronized void start(Track track, double position) throws PlayerException
	{
		if(track != null)
		{
			try
			{
				load(track);
				p.play(position);
				changeState(true);
			}
			catch (PlayerException e1)
			{
				throw e1;
			}
			currentTrackChanged(track, players.PlayStateListener.Reason.RECEIVED_NEW_TRACK);
		}
	}

	public void stop()
	{
		tempPosition = 0;
		close();
	}
	
	private AdvancedPlayer getPlayer(final String fileName) throws PlayerException
	{
		if(p != null)
		{
			p.close();
		}

		try
		{
			p = new AdvancedPlayer(fileName, volume, this);
		}
		catch (JavaLayerException e)
		{
			throw new PlayerException(Problem.CANT_PLAY, e);
		}
		return p;
	}
	
	private void changeState(boolean newStatus)
	{
		if(newStatus != status)
		{
			status = newStatus;
			
			synchronized(playStateListener)
			{
				for(PlayStateListener listener: playStateListener)
				{
					listener.playStateChanged(status);
				}
			}
		}
	}
	
	public static float volumeToDB(int vol)
	{
		return (float)(Math.log((vol + 1) * 172.17390699942) / Math.log(101 * 172.17390699942) * 86 - 80);
	}
	

	public void playbackFinished(AdvancedPlayer source, Reason reason)
	{
		if(source == p)
			if(reason == Reason.END_OF_TRACK)
			{
				Track track = contact.requestNextTrack();
				if(track != null)
					try
					{
						start(track, 0);
					}
					catch (PlayerException e)
					{
						contact.reportProblem(e, track);
					}
				currentTrackChanged(track, players.PlayStateListener.Reason.END_OF_TRACK);
			}
			else
				changeState(false);
	}
	
	private void currentTrackChanged(Track track, players.PlayStateListener.Reason reason)
	{
		//if(currentTrack != track)
		{
			Track oldTrack = currentTrack;
			currentTrack = track;
			synchronized(playStateListener)
			{
				for(PlayStateListener listener : playStateListener)
				{
					listener.currentTrackChanged(oldTrack, currentTrack, reason);
				}
			}
		}
	}
	
	private void close()
	{
		if(p != null)
		{
			p.close();
			p = null;
		}
		changeState(false);
	}
	
	class closeListener implements CloseListener
	{
		public void closing()
		{
			if(p != null)
			{
				double startTime = System.currentTimeMillis();
				p.fadeDuration = 500;
				p.fadeOut();
				while(status && System.currentTimeMillis() < startTime + 500)
				{
					try
					{
						Thread.sleep(100);
					}
					catch (InterruptedException e)
					{}
				}
			}			
		}
	}
}
