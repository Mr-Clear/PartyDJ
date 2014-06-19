package players.jl;

import basics.CloseListener;
import basics.Controller;
import basics.PlayerContact;
import common.Track;
import common.Track.Problem;
import data.SettingException;
import gui.settings.SettingNode;
import players.IPlayer;
import players.PlayStateListener;
import players.PlayerException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.JavaLayerException;

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

	private final Set<PlayStateListener> playStateListener = new HashSet<>();
	private boolean status;
	private Track currentTrack;
	/** Position im Pause Zustand */
	private double tempPosition;

	AdvancedPlayer p;

	final private Controller controller = Controller.getInstance();

	public JLPlayer(final PlayerContact playerContact)
	{
		contact = playerContact;
		Controller.getInstance().addCloseListener(new MyCloseListener());
		Controller.addSettingNode(new SettingNode("Player", players.jl.Settings.class), Controller.getInstance().getSetingTree());

		try
		{
			volume = Integer.parseInt(basics.Controller.getInstance().getData().readSetting("PlayerVolume", "100"));
		}
		catch (final NumberFormatException e)
		{
			volume = 100;
			e.printStackTrace();
		}
		catch (final SettingException e)
		{
			volume = 100;
			e.printStackTrace();
		}
	}

	@Override
	public void dispose() { /* not to implement */ }

	@Override
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
			catch (final PlayerException e1)
			{
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void fadeInOut()
	{

		if(status)
			fadeOut();
		else
			fadeIn();
	}

	@Override
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

	@Override
	public boolean checkPlayable(Track track)
	{
		
		try(FileInputStream fis = new FileInputStream(new File(track.getPath())))
		{
			final Bitstream bs = new Bitstream(fis);

			try
			{
				for(int i = 0; i < 2; i++)
					bs.readFrame();
			}
			catch (final BitstreamException e)
			{
				return false;
			}
		}
		catch (final IOException impossible)
		{
			return false;
		}
		return true;
	}

	@Override
	public double getDuration()
	{
		try
		{
			return getDuration(currentTrack);
		}
		catch (final PlayerException e) { /* ignored */ }
		return 0;
	}

	@Override
	public double getDuration(final Track track) throws PlayerException
	{
		if(track == null)
			return 0;
		final double d = getDuration(track.getPath());
		contact.trackDurationCalculated(track, d);
		return d;
	}


	@Override
	public double getDuration(final File file) throws PlayerException
	{
		return AdvancedPlayer.getDuration(file.getAbsolutePath());
	}

	@Override
	public double getDuration(final String filePath) throws PlayerException
	{
		return AdvancedPlayer.getDuration(filePath);
	}

	@Override
	public Track getCurrentTrack()
	{
		return currentTrack;
	}

	@Override
	public boolean getPlayState()
	{
		return status;
	}

	@Override
	public double getPosition()
	{
		if(p != null)
			return p.getPosition();
		return 0;
	}

	@Override
	public int getVolume()
	{
		return volume;
	}

	@Override
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

	@Override
	public void play()
	{
		try
		{
			start(currentTrack, tempPosition);
		}
		catch (final PlayerException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void playNext()
	{
		final Track track = contact.requestNextTrack();
		try
		{
			start(track, 0);
		}
		catch (final PlayerException e)
		{
			contact.reportProblem(e, track);
			playNext();
			return;
		}
		currentTrackChanged(track, players.PlayStateListener.Reason.RECEIVED_FORWARD);
	}


	@Override
	public void playPrevious()
	{
		final Track track = contact.requestPreviousTrack();
		try
		{
			if(track != null)
				start(track, 0);
		}
		catch (final PlayerException e)
		{
			contact.reportProblem(e, track);
		}
		currentTrackChanged(track, players.PlayStateListener.Reason.RECEIVED_BACKWARD);
	}

	@Override
	public void playPause()
	{
		if(status)
			p.pause();
		else
		{
			p.play();
		}
	}

	@Override
	public void addPlayStateListener(final PlayStateListener listener)
	{
		synchronized(playStateListener)
		{
			playStateListener.add(listener);
		}
	}

	@Override
	public void removePlayStateListener(final PlayStateListener listener)
	{
		synchronized(playStateListener)
		{
			playStateListener.remove(listener);
		}
	}

	@Override
	public void setContact(final PlayerContact contact)
	{
		this.contact = contact;
	}

	@Override
	public synchronized void setPosition(final double seconds)
	{
		setPosition(seconds, true);
	}

	@Override
	public synchronized void setPosition(final double seconds, final boolean autoFadeIn)
	{
		p.sendMessage = false;
		p.fadeDuration = 300;
		p.fadeOut();
		tempPosition = seconds;
		if(autoFadeIn)
		{
			p = null;
			fadeIn();
		}
	}

	@Override
	public void setVolume(int volume)
	{
		if(volume > 100)
			volume = 100;
		if(volume < 0)
			volume = 0;

		this.volume = volume;

		synchronized(playStateListener)
		{
			for(final PlayStateListener listener : playStateListener)
			{
				try
				{
					listener.volumeChanged(this.volume);
				}
				catch (Exception e)
				{
					controller.logError(Controller.NORMAL_ERROR, listener, e, "Fehler in Plugin");
				}
			}
		}
	}

	@Override
	public void start()
	{
		try
		{
			start(currentTrack, 0);
		}
		catch (final PlayerException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void load(final Track track) throws PlayerException
	{
		if(track == null)
			return;

		close();

		p = getPlayer(track.getPath());

		currentTrackChanged(track, players.PlayStateListener.Reason.TRACK_LOADED);
	}

	@Override
	public void start(final Track track) throws PlayerException
	{
		try
		{
			start(track, 0);
		}
		catch (final PlayerException e)
		{
			contact.reportProblem(e, track);
			return;
		}
	}

	private synchronized void start(final Track track, final double position) throws PlayerException
	{
		if(track != null)
		{
			try
			{
				load(track);
				p.play(position);
				changeState(true);
			}
			catch (final PlayerException e1)
			{
				throw e1;
			}
			currentTrackChanged(track, players.PlayStateListener.Reason.RECEIVED_NEW_TRACK);
		}
	}

	@Override
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
		catch (final JavaLayerException e)
		{
			throw new PlayerException(Problem.CANT_PLAY, e);
		}
		return p;
	}

	private void changeState(final boolean newStatus)
	{
		if(newStatus != status)
		{
			status = newStatus;

			synchronized(playStateListener)
			{
				for(final PlayStateListener listener : playStateListener)
				{
					try
					{
						listener.playStateChanged(status);
					}
					catch (Exception e)
					{
						controller.logError(Controller.NORMAL_ERROR, listener, e, "Fehler in Plugin");
					}
				}
			}
		}
	}

	public static float volumeToDB(final int vol)
	{
		return (float)(Math.log((vol + 1) * 172.17390699942) / Math.log(101 * 172.17390699942) * 86 - 80);
	}


	@Override
	public void playbackFinished(final AdvancedPlayer source, final Reason reason)
	{
		if(source == p)
			if(reason == Reason.END_OF_TRACK)
			{
				final Track track = contact.requestNextTrack();
				if(track != null)
					try
					{
						start(track, 0);
					}
					catch (final PlayerException e)
					{
						contact.reportProblem(e, track);
					}
				currentTrackChanged(track, players.PlayStateListener.Reason.END_OF_TRACK);
			}
			else
				changeState(false);
	}

	private void currentTrackChanged(final Track track, final players.PlayStateListener.Reason reason)
	{
		//if(currentTrack != track)
		{
			final Track oldTrack = currentTrack;
			currentTrack = track;
			synchronized(playStateListener)
			{
				for(final PlayStateListener listener : playStateListener)
				{
					try
					{
						listener.currentTrackChanged(oldTrack, currentTrack, reason);
					}
					catch (Exception e)
					{
						controller.logError(Controller.NORMAL_ERROR, listener, e, "Fehler in Plugin");
					}
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

	class MyCloseListener implements CloseListener
	{
		@Override
		public void closing()
		{
			if(p != null)
			{
				final double startTime = System.currentTimeMillis();
				p.fadeDuration = 500;
				p.fadeOut();
				while(status && System.currentTimeMillis() < startTime + 500)
				{
					try
					{
						Thread.sleep(100);
					}
					catch (final InterruptedException e) { /* ignored */ }
				}
			}
		}
	}
}
