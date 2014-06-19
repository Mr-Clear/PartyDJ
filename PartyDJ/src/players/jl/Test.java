package players.jl;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer.Info;



public class Test
{

	public static void main(String[] args) 
	{
		Info[] infos = AudioSystem.getMixerInfo();
		for(Info info : infos)
		{
			System.out.println(SoundAudioDevice.test(info) + "\t" + info.getName());
		}
	}
}
