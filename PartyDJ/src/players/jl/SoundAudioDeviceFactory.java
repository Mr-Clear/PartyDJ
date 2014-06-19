package players.jl;


import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDeviceFactory;

/**
 * This class is responsible for creating instances of the
 * SoundAudioDevice. The audio device implementation is loaded
 * and tested dynamically as not all systems will have support
 * for JavaSound, or they may have the incorrect version. 
 */
public class SoundAudioDeviceFactory extends AudioDeviceFactory
{
	private boolean tested = false;
	
	@Override
	public synchronized SoundAudioDevice createAudioDevice()
		throws JavaLayerException
	{
		
		if (!tested)
		{			
			testAudioDevice();
			tested = true;
		}
		
		try
		{			
			return createAudioDeviceImpl();
		}
		catch (final Exception ex)
		{
			throw new JavaLayerException("unable to create JavaSound device: " + ex);
		}
		catch (final LinkageError ex)
		{
			throw new JavaLayerException("unable to create JavaSound device: " + ex);
		}
	}
	
	protected SoundAudioDevice createAudioDeviceImpl()
		throws JavaLayerException
	{
		final ClassLoader loader = getClass().getClassLoader();
		try
		{
			final SoundAudioDevice dev = (SoundAudioDevice)instantiate(loader, "players.jl.SoundAudioDevice");
			return dev;
		}
		catch (final Exception ex)
		{
			throw new JavaLayerException("Cannot create JavaSound device", ex);
		}
		catch (final LinkageError ex)
		{
			throw new JavaLayerException("Cannot create JavaSound device", ex);
		}
		
	}
	
	public void testAudioDevice() throws JavaLayerException
	{
		final SoundAudioDevice dev = createAudioDeviceImpl();
		dev.test();
	}
}
