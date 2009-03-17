package players.jl;


import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
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

	static private final String DEVICE_CLASS_NAME = "javazoom.jl.player.JavaSoundAudioDevice";
	
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
		catch (Exception ex)
		{
			throw new JavaLayerException("unable to create JavaSound device: "+ex);
		}
		catch (LinkageError ex)
		{
			throw new JavaLayerException("unable to create JavaSound device: "+ex);
		}
	}
	
	protected SoundAudioDevice createAudioDeviceImpl()
		throws JavaLayerException
	{
		ClassLoader loader = getClass().getClassLoader();
		try
		{
			SoundAudioDevice dev = (SoundAudioDevice)instantiate(loader, "players.jl.SoundAudioDevice");
			return dev;
		}
		catch (Exception ex)
		{
			throw new JavaLayerException("Cannot create JavaSound device", ex);
		}
		catch (LinkageError ex)
		{
			throw new JavaLayerException("Cannot create JavaSound device", ex);
		}
		
	}
	
	public void testAudioDevice() throws JavaLayerException
	{
		SoundAudioDevice dev = createAudioDeviceImpl();
		dev.test();
	}
}
