package players.jl;

import basics.Controller;
import data.IData;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.SourceDataLine;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDeviceBase;

/**
 * The <code>JavaSoundAudioDevice</code> implements an audio
 * device by using the JavaSound API.
 *
 * @since 0.0.8
 * @author Mat McGowan
 */
public class SoundAudioDevice extends AudioDeviceBase
{
	IData pdjData = Controller.getInstance().getData();
	
	private SourceDataLine	source = null;

	private AudioFormat		format = null;

	private byte[]			byteBuf = new byte[4096];

	protected void setAudioFormat(final AudioFormat fmt0)
	{
		format = fmt0;
	}

	protected AudioFormat getAudioFormat()
	{
		if (format == null)
		{
			final Decoder decoder = getDecoder();
			format = new AudioFormat(decoder.getOutputFrequency(),
								  16,
								  decoder.getOutputChannels(),
								  true,
								  false);
		}
		return format;
	}

	protected DataLine.Info getSourceLineInfo()
	{
		//DataLine.Info info = new DataLine.Info(SourceDataLine.class, getAudioFormat(), 4000);
		final DataLine.Info info = new DataLine.Info(SourceDataLine.class, getAudioFormat());
		return info;
	}

	public void open(final AudioFormat fmt) throws JavaLayerException
	{
		if (!isOpen())
		{
			setAudioFormat(fmt);
			openImpl();
			setOpen(true);
		}
	}

	@Override
	protected void openImpl() throws JavaLayerException { /* not to implement */ }


	// createSource fix.
	protected void createSource(final String mixerName) throws JavaLayerException
    {
        Throwable t = null;
        try
        {
			final Line line = getLine(mixerName);
            if (line instanceof SourceDataLine)
            {
         		source = (SourceDataLine)line;
                //source.open(fmt, millisecondsToBytes(fmt, 2000));
				source.open(format);
				
                final FloatControl gainControl = (FloatControl)source.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-80.0f);
                
                /*
                if (source.isControlSupported(FloatControl.Type.MASTER_GAIN))
                {
					FloatControl c = (FloatControl)source.getControl(FloatControl.Type.MASTER_GAIN);
                    c.setValue(c.getMaximum());
                }*/
                source.start();
                

            }
        }
        catch (final RuntimeException ex)
	    {
        	t = ex;
	    }
	    catch (final LinkageError ex)
	    {
	        t = ex;
	    }
	    catch (final LineUnavailableException ex)
	    {
	        t = ex;
	    }
		if (source == null) throw new JavaLayerException("cannot obtain source audio line", t);
    }
	
	protected Line getLine(final String mixerName) throws LineUnavailableException
	{
		if(mixerName != null)
			for(final Info info : AudioSystem.getMixerInfo())
				if(mixerName.equals(info.getName()))
					return AudioSystem.getMixer(info).getLine(getSourceLineInfo());
		return AudioSystem.getLine(getSourceLineInfo());
	}
	
	public SourceDataLine getSourceDataLine()
	{
		return source;
	}

	public static int millisecondsToBytes(final AudioFormat fmt, final int time)
	{
		return (int)(time * (fmt.getSampleRate() * fmt.getChannels() * fmt.getSampleSizeInBits()) / 8000.0);
	}

	@Override
	protected void closeImpl()
	{
		if (source != null)
		{
			source.close();
		}
	}

	@Override
	protected void writeImpl(final short[] samples, final int offs, final int len)
		throws JavaLayerException
	{
		if (source == null)
			createSource(pdjData.readSetting("JLPlayer.Mixer"));

		final byte[] b = toByteArray(samples, offs, len);
		source.write(b, 0, len * 2);
	}

	protected byte[] getByteArray(final int length)
	{
		if (byteBuf.length < length)
		{
			byteBuf = new byte[length + 1024];
		}
		return byteBuf;
	}

	protected byte[] toByteArray(final short[] samples, int offs, int len)
	{
		final byte[] b = getByteArray(len * 2);
		int idx = 0;
		short s;
		while (len-- > 0)
		{
			s = samples[offs++];
			b[idx++] = (byte)s;
			b[idx++] = (byte)(s >>> 8);
		}
		return b;
	}

	@Override
	protected void flushImpl()
	{
		if (source != null)
		{
			source.drain();
		}
	}

	@Override
	public int getPosition()
	{
		int pos = 0;
		if (source != null)
		{
			pos = (int)(source.getMicrosecondPosition() / 1000);
		}
		return pos;
	}

	/**
	 * Runs a short test by playing a short silent sound.
	 * @throws JavaLayerException 
	 */
	public void test()
		throws JavaLayerException
	{
		try
		{
			open(new AudioFormat(22050, 16, 1, true, false));
			final short[] data = new short[22050 / 10];
			write(data, 0, data.length);
			flush();
			close();
		}
		catch (final RuntimeException ex)
		{
			throw new JavaLayerException("Device test failed: " + ex);
		}

	}
	
	public static boolean test(final Info mixerInfo) 
	{
		try(Mixer mixer = AudioSystem.getMixer(mixerInfo); Line line = mixer.getLine(new DataLine.Info(SourceDataLine.class, new AudioFormat(22050, 16, 1, true, false))))
		{
			line.open();
			line.close();
			return true;
		}
		catch (final RuntimeException | LineUnavailableException ex)
		{
			return false;
		}
	}

}
