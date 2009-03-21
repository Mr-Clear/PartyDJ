package players.jl;

import java.util.HashSet;
import java.util.Set;
import players.IPlayer;
import players.PlayStateListener;
import players.PlayerException;
import basics.PlayerContact;
import javazoom.jl.decoder.JavaLayerException;
import common.Track;
import common.Track.Problem;
import data.SettingException;


/**Einfacher Player.
 * 
 * Kein umblenden.
 * 
 * @author Sam
 */

public class JLPlayer implements IPlayer, PlaybackListener
{
	private int volume;
	
	PlayerContact contact;
	
	private final Set<PlayStateListener> playStateListener = new HashSet<PlayStateListener>();
	private boolean status;
	private Track currentTrack;
	
	AdvancedPlayer p;
	
	public JLPlayer(PlayerContact playerContact)
	{
		contact = playerContact;
		
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
		
	public void addPlayStateListener(PlayStateListener listener)
	{
		playStateListener.add(listener);
	}

	public void dispose(){}

	public void fadeIn()
	{
		if(p != null)
			p.fadeIn();
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
		if(p != null)
			p.fadeOut();
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
		else
			return 0;
	}

	public int getVolume()
	{
		return volume;
	}

	public void pause()
	{
		p.pause();
		changeState(false);
	}

	public void play()
	{
		if(p != null)
		{
			try
			{
				p.play();
			}
			catch (JavaLayerException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		changeState(true);
		
	}

	public void playNext()
	{
		Track track = contact.requestNextTrack();
		start(track, 0);
		currentTrackChanged(track, players.PlayStateListener.Reason.RECEIVED_FORWARD);
	}
	

	public void playPrevious()
	{
		Track track = contact.requestPreviousTrack();
		start(track, 0);
		currentTrackChanged(track, players.PlayStateListener.Reason.RECEIVED_BACKWARD);
	}

	public void playPause()
	{
		if(status)
			p.pause();
		else
		{
			try
			{
				p.play();
			}
			catch (JavaLayerException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	public void removePlayStateListener(PlayStateListener listener)
	{
		playStateListener.remove(listener);
	}

	public void setContact(PlayerContact Contact)
	{
		contact = Contact;
	}

	public synchronized void setPosition(double seconds)
	{
		start(currentTrack, seconds);
	}

	public void setVolume(int volume)
	{
		this.volume = volume;
		
		for(PlayStateListener listener : playStateListener)
			listener.volumeChanged(this.volume);
	}

	public void start()
	{
		start(currentTrack);
	}
	
	public void load(Track track)
	{
		if(track == null)
			return;
		
		close();
		
		try
		{
			p = getPlayer(track.path);
		}
		catch (PlayerException e)
		{
			contact.reportProblem(e, track);
		}
		currentTrackChanged(track, players.PlayStateListener.Reason.RECEIVED_NEW_TRACK);
	}

	public void start(Track track)
	{
		start(track, 0);
		currentTrackChanged(track, players.PlayStateListener.Reason.RECEIVED_NEW_TRACK);
	}
	
	private void start(Track track, double position)
	{
		load(track);
		
		try
		{
			p.play(position);
		}
		catch (JavaLayerException e)
		{
			contact.reportProblem(new PlayerException(Problem.CANT_PLAY, e), track);
		}
		
		changeState(true);
	}

	public void stop()
	{
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
			
			for(PlayStateListener listener: playStateListener)
			{
				listener.playStateChanged(status);
			}
		}
	}
	
	public static float volumeToDB(int vol)
	{
		return (float)(Math.log((vol + 1) * 172.17390699942) / Math.log(101 * 172.17390699942) * 86 - 80);
	}
	

	public void playbackFinished(AdvancedPlayer source, Reason reason)
	{
		if(reason == Reason.END_OF_TRACK)
		{
			Track track = contact.requestNextTrack();
			if(track != null)
				start(track, 0);
			currentTrackChanged(track, players.PlayStateListener.Reason.END_OF_TRACK);
		}
	}
	
	private void currentTrackChanged(Track track, players.PlayStateListener.Reason reason)
	{
		if(currentTrack != track)
		{
			Track oldTrack = currentTrack;
			currentTrack = track;
			for(PlayStateListener listener : playStateListener)
				listener.currentTrackChanged(oldTrack, currentTrack, reason);
		}
	}
	
	private void close()
	{
		if(p != null)
		{
			p.close();
			p = null;
		}
	}
}
