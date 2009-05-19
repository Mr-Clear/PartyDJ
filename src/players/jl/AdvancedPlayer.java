package players.jl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import javax.sound.sampled.FloatControl;
import basics.Controller;
import common.Track.Problem;
import players.PlayStateAdapter;
import players.PlayStateListener;
import players.PlayerException;
import players.jl.PlaybackListener.Reason;

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
	private final AdvancedPlayer me = this;
	private FileInputStream fis;
	private Bitstream bitStream;
	private Decoder decoder;
	private SoundAudioDevice audio;
	private JLPlayer jlPlayer;
	private boolean paused = false;
	private static String durationPath;
	private static double staticDuration;
	private static final double frameDuration = 0.02612245;
	private double position;
	private int volume;
	private int audioVolume;
	private boolean fadeOut;
	private long fadeStartTime;
	int fadeSpeed = 1;
	
	long fadeDuration = 1000;
	/** Wenn false, sendet der Player kein playbackFinished */
	boolean sendMessage = true;
	private final PlayStateListener listener = new PlayStateAdapter(){
		public void volumeChanged(int vol)
		{
			volume = vol;
		}};
		
	//---Debug übersprigen!
	private OutputStreamWriter osw;
	private String path;

	/**
	 * Creates a new Player instance.
	 * @throws PlayerException 
	 */
	AdvancedPlayer(String path, int vol, JLPlayer jlPlayer) throws JavaLayerException, PlayerException
	{
		this.path = path;
		this.jlPlayer = jlPlayer;
		try
		{
			fis = new FileInputStream(path);
		}
		catch (FileNotFoundException e)
		{
			throw new PlayerException(Problem.FILE_NOT_FOUND, e);
		}
		
		jlPlayer.addPlayStateListener(listener);
		
		bitStream = new Bitstream(fis);

		audio = (SoundAudioDevice)FactoryRegistry.systemRegistry().createAudioDevice();
		audio.open(decoder = new Decoder());
		volume = vol;
		

		//---Debug
		try
		{
			osw = new OutputStreamWriter(new FileOutputStream(System.getProperty("user.home") + "\\Desktop" + "\\debugRAW.txt", true));
		}
		catch (FileNotFoundException e1)
		{
			e1.printStackTrace();
		}
		//---Debug Ende
	}
	
	public boolean play(double start) throws JavaLayerException
	{
		if(!skip(start))
			return false;
		
		return play();
	}
	
	public boolean play() throws JavaLayerException
	{
		new PlayerThread().start();
		return true;
	}
	
	public void pause()
	{
		paused = true;
	}

	/**
	 * Closes this player. Any audio currently playing is stopped
	 * immediately.
	 * @throws IOException 
	 */
	public synchronized void close()
	{
		try
		{
			fis.close();
		}
		catch (IOException e){}
		if (audio != null)
		{
			audio.close();
			//audio = null;
		}
		if(bitStream != null)
		{
			try
			{
				bitStream.close();
			}
			catch (BitstreamException ex){ex.printStackTrace();}
		}
	}

	/**
	 * Decodes a single frame.
	 *
	 * @return false if there are no more frames to decode, true otherwise.
	 */
	protected boolean decodeFrame() throws JavaLayerException
	{
		if (audio == null) 
		{
			pdi("AUDIO DEVICE NULL");
			return false;
		}
		
		Header h = null;
		try
		{
			h = bitStream.readFrame();
		}
		catch(BitstreamException e)
		{
			return false;
		}
		
		if (h == null) 
		{
			pdi("BITSTREAM READ FRAME ENDE/FAILED" );
			return false;
		}
		
		SampleBuffer output = (SampleBuffer)decoder.decodeFrame(h, bitStream);

		synchronized (this)
		{
			if(audio != null)
			{
				audio.write(output.getBuffer(), 0, output.getBufferLength());
			}
		}

		bitStream.closeFrame();

		return true;
	}

	/**
	 * skips over a single frame
	 * @return false	if there are no more frames to decode, true otherwise.
	 */
	protected boolean skipFrame() throws JavaLayerException
	{
		Header h = bitStream.readFrame();
		
		if (h == null) 
			return false;
		
		bitStream.closeFrame();
		return true;
	}
	
	public double getPosition()
	{
		return position;
	}
	
	public static double getDuration(String filePath) throws PlayerException
	{
		if(durationPath != null && durationPath.equals(filePath))
		{
			return staticDuration;
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
		staticDuration = calcDuration;
		
		return staticDuration;
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
	
	public void setAudioVolume(double volume)
	{
		if(audio == null || audio.getSourceDataLine() == null)
			return;
		FloatControl gainControl = (FloatControl)audio.getSourceDataLine().getControl(FloatControl.Type.MASTER_GAIN);
		float max = gainControl.getMaximum();
		float min = gainControl.getMinimum();
		double factor = 228;
		factor = 1;
		float dB = (float)(Math.log((volume * factor + 1)) / Math.log(101 * factor) * (max - min) + min);
		//System.out.println("Volume: " + volume + "; Factor: " + dB + "dB = " + Math.round(Math.pow(10, dB / 10) * 100) + "%");
		gainControl.setValue(dB);
		audioVolume = (int)volume;		
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
		public synchronized void run()
		{
			this.setName("AdvancedPlayer Thread");
			paused = false;
			boolean ftd = true;
			
			pdi("Track:  " + new java.io.File(path).getName());
			if(Math.abs(staticDuration - Controller.getInstance().getPlayer().getDuration()) > 0.001)
				pdi("StaticDuration:  " + staticDuration + "    SavedDuration:  " + Controller.getInstance().getPlayer().getDuration());
			
			while (ftd)
			{
				if(audio == null || !audio.isOpen())
					break;
				
				double fadeElapsed = System.currentTimeMillis() - fadeStartTime;
				if(fadeElapsed < fadeDuration)
				{
					double progress = fadeElapsed / fadeDuration;
					if(fadeOut)
						setAudioVolume(volume * (1 - progress));
					else
						setAudioVolume(volume * (progress));
				}
				else if((fadeOut?0:volume) != audioVolume)
				{
					if(fadeOut)
						paused = true;
					else
						setAudioVolume(volume);
				}
				
				if(paused)
				{

					pdi("\n" + "Paused!!!");
					break;
				}
				
				try
				{
					ftd = decodeFrame();
				}
				catch (JavaLayerException e)
				{
					// TODO Auto-generated catch block
					Controller.getInstance().logError(Controller.REGULAR_ERROR, AdvancedPlayer.this, e, "Fehler bei 'ftd = decodeFrame();'");
					e.printStackTrace();
				}
				
				position += frameDuration;
				if(!ftd)
					pdi("Position:   " + position );
			}
			
			if (audio != null)
			{
				pdi("Track end after:  " + position);
				pdi("Successfull playback?   " + (position >= Controller.getInstance().getPlayer().getDuration()) + "\n");
				audio.flush();
				audio.close();
				close();
			}
			
			jlPlayer.removePlayStateListener(listener);
			
			if(sendMessage)
			{
				if(paused)
					jlPlayer.playbackFinished(me, Reason.RECEIVED_STOP);
				else
					jlPlayer.playbackFinished(me, Reason.END_OF_TRACK);
			}
		}
	}
	
	/** Prints Debug-Info */
	void pdi(String text)
	{
		try
		{
			osw.write(text + "\n");
			osw.flush();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
	}
}