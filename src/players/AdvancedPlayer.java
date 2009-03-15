package players;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.JavaSoundAudioDevice;

/**
 * Simple MP3-Player with advanced Features
 * 
 * @author Sam
 */
public class AdvancedPlayer
{
	/** The MPEG audio bitstream.*/
	private Bitstream bitstream;
	/** The MPEG audio decoder. */
	private Decoder decoder;
	/** The AudioDevice the audio samples are written to. */
	private JavaSoundAudioDevice audio;
	/** Listener for the playback process */
	private PlaybackListener listener;
	/**Will be initialised with the path of the playing Track when getDuration gets called*/
	private String durationPath;
	/**Duration of the song being played*/
	private double duration;

	/**
	 * Creates a new Player instance.
	 */
	public AdvancedPlayer(InputStream stream) throws JavaLayerException
	{
		bitstream = new Bitstream(stream);
		audio = (JavaSoundAudioDevice) FactoryRegistry.systemRegistry().createAudioDevice();
		audio.open(decoder = new Decoder());
	}
	
	public void play() throws JavaLayerException
	{
		play(Integer.MAX_VALUE);
	}

	/**
	 * Plays a number of MPEG audio frames.
	 *
	 * @param	frames	The number of frames to play.
	 * @return	true if the last frame was played, or false if there are
	 *			more frames.
	 */
	public boolean play(int frames) throws JavaLayerException
	{
		boolean ret = true;
		
		if(listener != null) 
			listener.playbackStarted(createEvent(PlaybackEvent.STARTED));

		while (frames-- > 0 && ret)
		{
			ret = decodeFrame();
		}
			
		AudioDevice out = audio;
		if (out != null)
		{
			out.flush();
			
			synchronized (this)
			{
				close();
			}
			
			if(listener != null) 
				listener.playbackFinished(createEvent(out, PlaybackEvent.STOPPED));
		}
		return ret;
	}

	/**
	 * Closes this player. Any audio currently playing is stopped
	 * immediately.
	 */
	public synchronized void close()
	{
		AudioDevice out = audio;
		if (out != null)
		{
			audio = null;
			out.close();
			try
			{
				bitstream.close();
			}
			catch (BitstreamException ex)
			{
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Decodes a single frame.
	 *
	 * @return true if there are no more frames to decode, false otherwise.
	 */
	protected boolean decodeFrame() throws JavaLayerException
	{
		try
		{
			AudioDevice out = audio;
			if (out == null) 
				return false;

			Header h = bitstream.readFrame();
			if (h == null) 
				return false;

			SampleBuffer output = (SampleBuffer) decoder.decodeFrame(h, bitstream);

			synchronized (this)
			{
				out = audio;
				if(out != null)
				{
					out.write(output.getBuffer(), 0, output.getBufferLength());
				}
			}

			bitstream.closeFrame();
		}
		catch (RuntimeException ex)
		{
			throw new JavaLayerException("Exception decoding audio frame", ex);
		}
		return true;
	}

	/**
	 * skips over a single frame
	 * @return false	if there are no more frames to decode, true otherwise.
	 */
	protected boolean skipFrame() throws JavaLayerException
	{
		Header h = bitstream.readFrame();
		
		if (h == null) 
			return false;
		
		bitstream.closeFrame();
		return true;
	}

	/**
	 * Plays a range of MPEG audio frames
	 * @param start	The first frame to play
	 * @param end		The last frame to play
	 * @return true if the last frame was played, or false if there are more frames.
	 */
	public boolean play(final int start, final int end) throws JavaLayerException
	{
		boolean ret = true;
		int offset = start;
		
		while (offset-- > 0 && ret) 
			ret = skipFrame();
		
		return play(end - start);
	}

	/**
	 * Constructs a PlaybackEvent
	 */
	private PlaybackEvent createEvent(int id)
	{
		return createEvent(audio, id);
	}

	/**
	 * Constructs a PlaybackEvent
	 */
	private PlaybackEvent createEvent(AudioDevice dev, int id)
	{
		return new PlaybackEvent(this, id, dev.getPosition());
	}

	/**
	 * sets the PlaybackListener
	 */
	public void setPlayBackListener(PlaybackListener listener)
	{
		this.listener = listener;
	}

	/**
	 * gets the PlaybackListener
	 */
	public PlaybackListener getPlayBackListener()
	{
		return listener;
	}

	/**
	 * closes the player and notifies PlaybackListene
	 */
	public void stop()
	{
		listener.playbackFinished(createEvent(PlaybackEvent.STOPPED));
		close();
	}
	
	public int getPostion()
	{
		if(audio != null)
			return audio.getPosition();
		
		return 0;
	}
	
	public double getDuration(String filePath) throws PlayerException
	{
		if(durationPath != null)
		{
			if(durationPath.equals(filePath))
			{
				return this.duration / 1000;
			}	
		}
		
		
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
		durationPath = filePath;
		this.duration = duration;
		return (duration / 1000);
	}
}