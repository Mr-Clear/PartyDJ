package simplePlayer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.media.*;
import common.*;


/**Einfacher Player.
 * 
 * Verwendet JMF.
 * Keine Lautst�rken�nderung.
 * Kein umblenden.
 * 
 * @author Eraser
 */
public class SimplePlayer implements IPlayer
{
	boolean status;
	int volume;
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
		changeState(false);
	}

	public void play()
	{
		if(p != null)
			p.start();
		changeState(true);
		
	}
	
	public void playNext() throws PlayerException
	{
		start(contact.RequestNextTrack());
	}

	public void playPrevious() throws PlayerException
	{
	
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
			Volume = 0;
		else if(Volume > 100)
			Volume = 100;
		
		volume = Volume;
		
		// TODO Auto-generated method stub
	}

	public void start()
	{
		setPosition(0);
		play();
	}

	public void start(String fileName) throws PlayerException
	{
		if(p != null)
			p.close();
			
		p = getPlayer(fileName);
	
		p.addControllerListener(new ControllerListener()
								{
									public void controllerUpdate(ControllerEvent e)
									{
										if(e instanceof EndOfMediaEvent)
										{
											if(contact != null)
												try
												{
													String next = contact.RequestNextTrack();
													if(next == null)
														contact.PlayCompleted();
													else
														start(next);							
												}
												catch (PlayerException ex)
												{
													contact.ProceedError(ex);
												}
											((Player)e.getSource()).stop();
											((Player)e.getSource()).deallocate();
										}
									}
								});
		p.start();

		changeState(true);
	}

	public void stop()
	{
		if(p != null)
			p.stop();
		changeState(false);
	}

	public double getDuration()
	{
		if(p != null)
			return p.getDuration().getSeconds();
		else
			return 0;
	}

	public double getDuration(String FileName) throws PlayerException
	{
		return getPlayer(FileName).getDuration().getSeconds();
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
			throw new PlayerException("NoPlayerException:\n" + e.getMessage());
		}
		catch (CannotRealizeException e)
		{
			throw new PlayerException("CannotRealizeException:\n" + e.getMessage());
		}
		catch (MalformedURLException e)
		{
			throw new PlayerException("Ung�ltiger Dateiname:\n" + e.getMessage());
		}
		catch (IOException e)
		{
			throw new PlayerException("Fehler beim Lesen von Audio-Datei:\n" + e.getMessage());
		}
	}
	
	private void changeState(boolean newStatus)
	{
		if(newStatus != status)
		{
			status = newStatus;
			if(contact != null)
				contact.StateChanged(status);
		}
	}
}