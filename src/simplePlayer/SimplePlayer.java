package simplePlayer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.media.*;
import common.*;
import common.Track;
import common.Track.Problem;


/**Einfacher Player.
 * 
 * Verwendet JMF.
 * Keine Lautstärkenänderung.
 * Kein umblenden.
 * 
 * @author Eraser
 */
public class SimplePlayer implements IPlayer
{
	boolean status;
	public int volume;
	PlayerContact contact;
	
	Player p;
	
	public SimplePlayer(PlayerContact playerContact)
	{
		contact = playerContact;
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
		if(p != null)
			p.stop();
		
		gui.ClassicWindow.refreshTimer.stop();
		changeState(false);
	}

	public void play()
	{
		if(p != null)
			p.start();
		
		gui.ClassicWindow.refreshTimer.start();
		changeState(true);
		
	}
	
	public void playNext()
	{
		start(contact.requestNextTrack());
	}

	public void playPrevious()
	{
		start(contact.requestPreviousTrack());
	}

	public void playPause()
	{
		if(status)
			pause();
		else
			play();
	}

	public void setPosition(double Seconds)
	{
		if(Seconds < 0)
			Seconds = 0;
		else if(Seconds > getDuration())
			Seconds = getDuration();
		
		p.setMediaTime(new Time(Seconds));
	}

	public void setVolume(int Volume)
	{
		if(Volume < 0)
		{
			if(status)
				p.getGainControl().setMute(true);
		}
		
		else if(Volume > 100)
			Volume = 100;
		
		volume = Volume;

		p.getGainControl().setLevel(volume/100f);
	}

	public void start()
	{
		setPosition(0);
		play();
		gui.ClassicWindow.refreshTimer.start();
	}

	public void start(Track track)
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
			return;
		}
	
		p.addControllerListener(new ControllerListener()
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
													start(next);
											}
											((Player)e.getSource()).stop();
											((Player)e.getSource()).deallocate();
										}
									}
								});
		p.start();
		gui.ClassicWindow.refreshTimer.start();
		
		contact.trackChanged(track);

		changeState(true);
	}

	public void stop()
	{
		if(p != null)
			p.stop();
		gui.ClassicWindow.refreshTimer.stop();
		changeState(false);
	}

	public double getDuration()
	{
		if(p != null)
			return p.getDuration().getSeconds();
		else
			return 0;
	}
	
	public double getDuration(Track track) throws PlayerException
	{
		return getDuration(track.path);
	}
	
	public double getDuration(String filePath) throws PlayerException
	{
		Player p = getPlayer(filePath);
		double d = p.getDuration().getSeconds();
		p.stop();
		p.deallocate();
		return d;
	}

	public String getFileName()
	{
		if(p != null)
			return "kA";
		else
			return null;
	}

	public double getPosition()
	{
		if(p != null)
			return p.getMediaTime().getSeconds();
		else
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
		if(p != null)
		{
			p.stop();
			p.deallocate();
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
			return Manager.createRealizedPlayer(new File(FileName).toURI().toURL());
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
			if(contact != null)
				contact.stateChanged(status);
		}
	}
}
