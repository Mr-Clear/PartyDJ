package players.jl;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer.Info;



public class Test
{

	public static void main(final String[] args) 
	{
		final Info[] infos = AudioSystem.getMixerInfo();
		for(final Info info : infos)
		{
			System.out.println(SoundAudioDevice.test(info) + "\t" + info.getName());
		}
	}
}
