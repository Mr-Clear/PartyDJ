package _test;
import common.*;

import simplePlayer.SimplePlayer;


public class PlayerTest implements PlayerContact
{
	IPlayer player;
	String[] files = new String[] {"E:/Temp/WAV/Duke3D/001_2RIDE06.wav",
								   "E:/Temp/WAV/Duke3D/002_AHMUCH03.wav",
								   "E:/Temp/WAV/Duke3D/003_AMESS06.wav",
								   "E:/Temp/WAV/Duke3D/004_BLOWIT01.wav",
								   "E:/Temp/WAV/Duke3D/005_BOOBY04.wav",
								   "E:/Temp/WAV/Duke3D/006_BORN01.wav",
								   "E:/Temp/WAV/Duke3D/007_CHEW05.wav",
								   "E:/Temp/WAV/Duke3D/008_COMEON02.wav",};
	
	int pos = 0;
	
	public PlayerTest()
	{
		player = new SimplePlayer(this);
		try
		{
			player.start(files[0]);
		}
		catch (PlayerException e)
		{
			e.printStackTrace();
			player.dispose();
		}
	}
	
	public String RequestNextTrack()
	{
		if(++pos >= files.length)
			return null;
		else
			return files[pos];
	}
	
	public String RequestPreviousTrack()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void StateChanged(boolean Status)
	{
		// TODO Auto-generated method stub		
	}

	public void ProceedError(PlayerException e)
	{
		e.printStackTrace();
		player.dispose();
		System.exit(1);
	}

	public void PlayCompleted()
	{
		System.exit(1);
	}


}
