package players.jl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import players.IPlayer;
import players.PlayStateListener;
import players.PlayerException;
import basics.PlayerContact;
import javazoom.jl.decoder.JavaLayerException;
import common.Track;
import common.Track.Problem;
import data.MasterListListener;
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
	private PlayerThread startThread;
	private FileInputStream fis = null;
	
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

	public void fadeInOut()
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

	public void fadeOut()
	{
		p.pause();
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
		double d = getDuration(track.path);
		contact.trackDurationCalculated(track, d);
		return d;
	}

	public double getDuration(String filePath) throws PlayerException
	{
		return AdvancedPlayer.getDuration(filePath);
	}

	public String getFileName()
	{
		return fis.toString();
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
		start(contact.requestNextTrack());
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

	public void playPrevious()
	{
		start(contact.requestPreviousTrack());
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
		
		if(getPlayState())
			p.setGlobalVolume(volume);
		
		for(PlayStateListener listener : playStateListener)
			listener.volumeChanged(this.volume);
	}

	public void start()
	{
		start(currentTrack);
	}

	public void start(Track track)
	{
		start(track, 0);
		if(currentTrack != track)
		{
			Track oldTrack = currentTrack;
			currentTrack = track;
			for(PlayStateListener listener : playStateListener)
				listener.currentTrackChanged(oldTrack, currentTrack);
		}
	}
	
	private void start(Track track, double position)
	{
		startThread = new PlayerThread(track, position);
		startThread.start();
		changeState(true);
	}

	public void stop()
	{
		p.close();
		p = null;
	}
	
	private AdvancedPlayer getPlayer(final String fileName) throws PlayerException
	{
		if(p != null)
		{
			p.close();
			try
			{
				fis.close();
			}
			catch (IOException e2){}
		}
		
		try
		{
			fis = new FileInputStream(fileName);
		}
		catch (FileNotFoundException e)
		{
			throw new PlayerException(Problem.FILE_NOT_FOUND, e);
		}
		
		try
		{
			p = new AdvancedPlayer(fis, volume);
			for(PlayStateListener listener : playStateListener)
				listener.volumeChanged(volume);
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
	
	class PlayerThread extends Thread
	{
		Track track;
		double start;
		
		public PlayerThread(Track track, double start)
		{
			this.track = track;
			this.start = start;
		}
		
		public void run()
		{
			if(track == null)
				return;
			
			if(p != null)
				p.close();
			
			try
			{
				p = getPlayer(track.path);
			}
			catch (PlayerException e)
			{
				contact.reportProblem(e, track);
			}
			

			try
			{
				p.play(start);
			}
			catch (JavaLayerException e)
			{
				contact.reportProblem(new PlayerException(Problem.CANT_PLAY, e), track);
			}
		}
	}

	public void playbackFinished(AdvancedPlayer source, int reason)
	{
		// TODO Auto-generated method stub
		
	}

}
