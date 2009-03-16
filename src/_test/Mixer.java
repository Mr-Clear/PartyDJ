package _test;

import javax.sound.sampled.AudioSystem;
//import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;

public class Mixer
{
	public static void main(String[] args)
	{
		Info[] mInfos = AudioSystem.getMixerInfo();
		for(Info info : mInfos)
			System.out.println(info.getName());
	}
}
