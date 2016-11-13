package de.klierlinge.partydj.players.jl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.sound.sampled.FloatControl;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.common.Track.Problem;
import de.klierlinge.partydj.players.PlayStateAdapter;
import de.klierlinge.partydj.players.PlayStateListener;
import de.klierlinge.partydj.players.PlayerException;
import de.klierlinge.partydj.players.jl.PlaybackListener.Reason;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;

/**
 * Player zum Wiedergeben eines Tracks.
 * 
 * @author Sam
 * @author Eraser
 */
public class AdvancedPlayer
{
	private FileInputStream fis;
	private final Bitstream bitStream;
	private final Decoder decoder;
	private final SoundAudioDevice audio;
	private final JLPlayer jlPlayer;
	private boolean paused = false;
	private static String durationPath;
	private static double staticDuration;
	private static final double FRAME_DURATION = 0.02612245;
	private double position;
	private int volume;
	private int audioVolume;
	private boolean fadeOut;
	private long fadeStartTime;

	long fadeDuration = 1000;
	/** Wenn false, sendet der Player kein playbackFinished */
	boolean sendMessage = true;
	private final PlayStateListener listener = new PlayStateAdapter()
	{
		@Override
		public void volumeChanged(final int vol)
		{
			volume = vol;
		}
	};

	/**
	 * Creates a new Player instance.
	 * @throws PlayerException
	 */
	AdvancedPlayer(final String path, final int vol, final JLPlayer jlPlayer) throws JavaLayerException, PlayerException
	{
		this.jlPlayer = jlPlayer;
		try
		{
			fis = new FileInputStream(path);
		}
		catch (final FileNotFoundException e)
		{
			throw new PlayerException(Problem.FILE_NOT_FOUND, e);
		}

		jlPlayer.addPlayStateListener(listener);

		bitStream = new Bitstream(fis);

		decoder = new Decoder();

		audio = (SoundAudioDevice) FactoryRegistry.systemRegistry().createAudioDevice();
		audio.open(decoder);
		volume = vol;
	}

	public boolean play(final double start)
	{
		if (!skip(start))
			return false;

		return play();
	}

	public boolean play()
	{
		new PlayerThread().start();
		return true;
	}

	public void pause()
	{
		paused = true;
	}

	/**
	 * Closes this player. Any audio currently playing is stopped immediately.
	 */
	public synchronized void close()
	{
		try
		{
			fis.close();
		}
		catch (final IOException e)
		{ /* Wenns nicht geht, gehts nicht. */ }
		if (audio != null)
		{
			audio.close();
			//audio = null;
		}
		if (bitStream != null)
		{
			try
			{
				bitStream.close();
			}
			catch (final BitstreamException ex)
			{
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Decodes a single frame.
	 *
	 * @return false if there are no more frames to decode, true otherwise.
	 */
	protected boolean decodeFrame() throws JavaLayerException
	{
		//		XXX Überspringen?
		//		if (audio == null) 
		//			return false;

		Header h = null;
		try
		{
			h = bitStream.readFrame();
		}
		catch (final BitstreamException e)
		{
			return false;
		}

		if (h == null)
			return false;

		final SampleBuffer output = (SampleBuffer) decoder.decodeFrame(h, bitStream);

		synchronized (this)
		{
			if (audio != null)
			{
				audio.write(output.getBuffer(), 0, output.getBufferLength());
			}
		}

		bitStream.closeFrame();

		return true;
	}

	/**
	 * skips over a single frame
	 * @return false if there are no more frames to decode, true otherwise.
	 */
	protected boolean skipFrame() throws JavaLayerException
	{
		final Header h = bitStream.readFrame();

		if (h == null)
			return false;

		bitStream.closeFrame();
		return true;
	}

	public double getPosition()
	{
		return position;
	}

	@SuppressWarnings("resource")
	public static double getDuration(final String filePath) throws PlayerException
	{
		if (durationPath != null && durationPath.equals(filePath))
		{
			return staticDuration;
		}

		Bitstream bs = null;
		float calcDuration = 0;

		try
		{
			bs = new Bitstream(new FileInputStream(filePath));
		}
		catch (final FileNotFoundException e)
		{
			throw new PlayerException(Problem.FILE_NOT_FOUND, e);
		}

		try
		{
			while (bs.readFrame() != null)
			{
				final double spf = bs.readFrame().ms_per_frame() / 1000;
				calcDuration += spf;
				bs.closeFrame();
			}
		}
		catch (final BitstreamException e)
		{
			throw new PlayerException(Problem.CANT_PLAY, e);
		}

		try
		{
			bs.close();
		}
		catch (final BitstreamException e)
		{ /* ignore */ }

		durationPath = filePath;
		staticDuration = calcDuration;

		return staticDuration;
	}

	//TODO JEDE MENGE
	private boolean skip(final double newPosition)
	{
		double oldPosition = position;
		position = newPosition;
		while (oldPosition < newPosition)
		{
			Header header = null;
			try
			{
				header = bitStream.readFrame();
				oldPosition += FRAME_DURATION;

			}
			catch (final BitstreamException e)
			{
				return false;
			}

			if (header != null)
				bitStream.closeFrame();
		}
		position = oldPosition;
		return true;
	}

	public void setAudioVolume(final double volume)
	{
		if (audio == null || audio.getSourceDataLine() == null)
			return;
		final FloatControl gainControl = (FloatControl) audio.getSourceDataLine().getControl(FloatControl.Type.MASTER_GAIN);
		final float max = gainControl.getMaximum();
		final float min = gainControl.getMinimum();
		double factor = 228;
		factor = 1;
		final float dB = (float) (Math.log((volume * factor + 1)) / Math.log(101 * factor) * (max - min) + min);
		//System.out.println("Volume: " + volume + "; Factor: " + dB + "dB = " + Math.round(Math.pow(10, dB / 10) * 100) + "%");
		gainControl.setValue(dB);
		audioVolume = (int) volume;
	}

	public void fadeOut()
	{
		fadeStartTime = System.currentTimeMillis();
		fadeOut = true;
	}

	public void fadeIn()
	{
		fadeStartTime = System.currentTimeMillis();
		fadeOut = false;
	}

	class PlayerThread extends Thread
	{
		public PlayerThread()
		{
			setName("AdvancedPlayer Thread");
			setUncaughtExceptionHandler(new UncaughtExceptionHandler()
			{
				@Override
				public void uncaughtException(final Thread t, final Throwable e)
				{
					closeThread(Reason.ERROR);
				}
			});
		}

		@Override
		public synchronized void run()
		{
			paused = false;
			boolean ftd = true;

			while (ftd)
			{
				if (audio == null) //XXX Überspringen ??? || !audio.isOpen())
					break;

				final double fadeElapsed = System.currentTimeMillis() - fadeStartTime;
				if (fadeElapsed < fadeDuration)
				{
					final double progress = fadeElapsed / fadeDuration;
					if (fadeOut)
						setAudioVolume(volume * (1 - progress));
					else
						setAudioVolume(volume * (progress));
				}
				else if ((fadeOut ? 0 : volume) != audioVolume)
				{
					if (fadeOut)
						paused = true;
					else
						setAudioVolume(volume);
				}

				if (paused)
					break;

				try
				{
					ftd = decodeFrame();
				}
				catch (final JavaLayerException e)
				{
					Controller.getInstance().logError(Controller.REGULAR_ERROR, AdvancedPlayer.this, e, "Fehler bei 'ftd = decodeFrame();'");
				}

				position += FRAME_DURATION;
			}

			closeThread(paused ? Reason.RECEIVED_STOP : Reason.END_OF_TRACK);
		}

		private void closeThread(final Reason reason)
		{
			if (audio != null)
			{
				audio.flush();
				audio.close();
				close();
			}

			jlPlayer.removePlayStateListener(listener);

			if (sendMessage)
				jlPlayer.playbackFinished(AdvancedPlayer.this, reason);
		}
	}
}