package players;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import basics.PlayStateListener;
import basics.PlayerContact;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import common.Track;
import data.SettingException;


/**Einfacher Player.
 * 
 * Kein umblenden.
 * 
 * @author Sam
 */

public class JLPlayer implements IPlayer
{
	public int volume;
		
		PlayerContact contact;
		
		private final Set<PlayStateListener> playStateListener = new HashSet<PlayStateListener>();
		private boolean status;
		private Track currentTrack;
		private int frame;
		private PlayerThread startThread;
		private FileInputStream fis = null;
		
		AdvancedPlayer p;
		
		public JLPlayer(PlayerContact playerContact)
		{
			System.out.println("Konstruktor");
			contact = playerContact;
			try
			{
				volume = Integer.parseInt(basics.Controller.instance.data.readSetting("PlayerVolume", "100"));
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
			p.stop();
		
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
		p.stop();
	}

	public double getDuration()
	{
		try
		{
			return getDuration(currentTrack.path);
		}
		catch (PlayerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public double getDuration(Track track) throws PlayerException
	{
		return getDuration(track.path);
	}

	public double getDuration(String filePath) throws PlayerException
	{
		Bitstream bs = null;
		float duration = 0;
		
		try
		{
			bs = new Bitstream(new FileInputStream(filePath));
		}
		catch (FileNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try
		{
			while(bs.readFrame() != null)
			{
				try
				{
					duration += bs.readFrame().ms_per_frame();
				}
				catch (BitstreamException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				bs.closeFrame();
			}
		}
		catch (BitstreamException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try
		{
			bs.close();
		}
		catch (BitstreamException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return (duration / 1000);
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
		System.out.println(startThread.getState());
		return frame / 38;
	}

	public int getVolume()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public void pause()
	{
		p.stop();
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
			p.stop();
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

	public void setPosition(double seconds)
	{
		//pbe.setFrame((int)(seconds * 38));
	}

	public void setVolume(int Volume)
	{
		// TODO Auto-generated method stub
		
	}

	public void start()
	{
		play();
		//pbe.setFrame(0);
	}

	public void start(Track track)
	{
		startThread = new PlayerThread(track);
		startThread.start();
	}

	public void stop()
	{
		p.stop();
		p.close();
	}
	
	private AdvancedPlayer getPlayer(final String fileName)
	{
		if(p != null)
		{
			p.close();
			try
			{
				fis.close();
			}
			catch (IOException e2)
			{
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}
		
		try
		{
			fis = new FileInputStream(fileName);
		}
		catch (FileNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try
		{
			p = new AdvancedPlayer(fis);
		}
		catch (JavaLayerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		p.setPlayBackListener(new myPlaybackListener());
		
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

	class myPlaybackListener extends PlaybackListener 
	{
		public void playbackStarted(PlaybackEvent evt)
		{
			changeState(true);
			frame = evt.getFrame();
		}
		
		public void playbackFinished(PlaybackEvent evt)
		{
			changeState(false);
			if(contact != null)
			{
				Track next = contact.requestNextTrack();
				if(next == null)
					contact.playCompleted();
				else
					start(next);
			}
			evt.getSource().close();
		}
	}
	
	class PlayerThread extends Thread
	{
		Track track;
		
		public PlayerThread(Track track)
		{
			super();
			System.out.println("thread");
			this.track = track;
		}
		
		public void run()
		{
			System.out.println("run");
			if(track == null)
				return;
			
			if(p != null)
				p.close();
			
			getPlayer(track.path);
			
			if(currentTrack != track)
			{
				Track oldTrack = currentTrack;
				currentTrack = track;
				for(PlayStateListener listener : playStateListener)
					listener.currentTrackChanged(oldTrack, currentTrack);
			}
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
	

}
