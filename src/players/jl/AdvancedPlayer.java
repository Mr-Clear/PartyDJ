package players.jl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.sound.sampled.FloatControl;
import common.Track.Problem;
import players.PlayerException;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;
import javazoom.jl.player.AudioDevice;

/**
 * Simple MP3-Player with advanced Features
 * 
 * @author Sam
 */
public class AdvancedPlayer
{
	/** The MPEG audio bitstream.*/
	private Bitstream bitStream;
	/** The MPEG audio decoder. */
	private Decoder decoder;
	/** The AudioDevice the audio samples are written to. */
	private SoundAudioDevice audio;
	
	private boolean paused = false;

	private static String durationPath;
	private static double duration;
	
	private static final double frameDuration = 0.02612245;
	
	private double position;
	
	int count = 0;

	/**
	 * Creates a new Player instance.
	 */
	public AdvancedPlayer(InputStream stream) throws JavaLayerException
	{
		bitStream = new Bitstream(stream);
		audio = (SoundAudioDevice)FactoryRegistry.systemRegistry().createAudioDevice();
		audio.open(decoder = new Decoder());
	}
	
	/**
	 * Plays a range of MPEG audio frames
	 * @param start	The first frame to play
	 * @param end		The last frame to play
	 * @return true if the last frame was played, or false if there are more frames.
	 */
	public boolean play(double start) throws JavaLayerException
	{
		if(!skip(start))
			return false;
		
		return play();
	}
	
	public boolean play() throws JavaLayerException
	{
		paused = false;
		boolean ftd = true;
		
		while (ftd)
		{
			ftd = decodeFrame();
			position += frameDuration;
			if(paused)
			{
				return true;
			}
		}
		
		AudioDevice out = audio;
		if (out != null)
		{
			out.flush();
			
			close();
		}
		return true;
	}
	
	public void pause()
	{
		paused = true;
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
				bitStream.close();
			}
			catch (BitstreamException ex){}
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

			Header h = bitStream.readFrame();
			if (h == null) 
				return false;
			
			SampleBuffer output = (SampleBuffer)decoder.decodeFrame(h, bitStream);

			synchronized (this)
			{
				out = audio;
				if(out != null)
				{
					out.write(output.getBuffer(), 0, output.getBufferLength());
				}
			}

			bitStream.closeFrame();
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
		count++;
		Header h = bitStream.readFrame();
		
		if (h == null) 
			return false;
		
		bitStream.closeFrame();
		return true;
	}
	
	/**
	 * 
	 * @return Returns the position in seconds
	 */
	public double getPosition()
	{
		return position;
	}
	
	/**
	 * 
	 * @param filePath	Path of the track being played
	 * @return	The duration of the track in seconds
	 * @throws PlayerException
	 */
	public static double getDuration(String filePath) throws PlayerException
	{
		if(durationPath != null)
		{
			if(durationPath.equals(filePath))
			{
				return duration;
			}	
		}
		
		Bitstream bs = null;
		float calcDuration = 0;
		
		try
		{
			bs = new Bitstream(new FileInputStream(filePath));
		}
		catch (FileNotFoundException e)
		{
			throw new PlayerException(Problem.FILE_NOT_FOUND, e);
		}
		
		try
		{
			while(bs.readFrame() != null)
			{
				double spf = bs.readFrame().ms_per_frame() / 1000;
				calcDuration += spf;
				bs.closeFrame();
			}
		}
		catch (BitstreamException e)
		{
			throw new PlayerException(Problem.CANT_PLAY, e);
		}
		
		try
		{
			bs.close();
		}
		catch (BitstreamException e){}
		
		durationPath = filePath;
		duration = calcDuration;
		
		return (duration);
	}
	
	//TODO JEDE MENGE
	private boolean skip(double newPosition)
	{
		double position = this.position;
		this.position = newPosition;
		while(position < newPosition)
		{
			Header header = null;
			try
			{
				header = bitStream.readFrame();
				position += frameDuration;
				
			}
			catch (BitstreamException e)
			{
				return false;
			}
			
			if(header != null)
				bitStream.closeFrame();
		}
		this.position = position;
		return true;
	}
	
	public void setGlobalVolume(int volume)
	{
		FloatControl gainControl = (FloatControl) audio.getSourceDataLine().getControl(FloatControl.Type.MASTER_GAIN);
		float dB = (float)((Math.log(volume + 1)/Math.log(101)) * 86 - 80);
		gainControl.setValue(dB);
		
		System.out.println("dB:   " + dB);
	}
}