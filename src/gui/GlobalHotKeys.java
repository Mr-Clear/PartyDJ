package gui;

import java.util.HashMap;
import players.IPlayer;
import basics.Controller;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;

public class GlobalHotKeys implements IntellitypeListener, HotkeyListener
{
	private static final GlobalHotKeys instance = new GlobalHotKeys();
	private static HashMap<Integer, String> actionKeys = new HashMap<Integer, String>();
	
	static
	{
		actionKeys.put(JIntellitype.APPCOMMAND_VOLUME_UP, "volumeup");
		actionKeys.put(JIntellitype.APPCOMMAND_VOLUME_DOWN, "volumedown");
		actionKeys.put(JIntellitype.APPCOMMAND_MEDIA_NEXTTRACK, "next");
		actionKeys.put(JIntellitype.APPCOMMAND_MEDIA_PREVIOUSTRACK, "prev");
		actionKeys.put(JIntellitype.APPCOMMAND_MEDIA_PLAY_PAUSE, "play");
	}
	
	public static GlobalHotKeys getInstance()
	{
		return instance;
	}
	
	public void setKeyAction(int id, String action)
	{
		actionKeys.put(id, action);
	}
	
	@Override
	public void onIntellitype(int id)
	{
		IPlayer player = Controller.getInstance().getPlayer();
		switch(id)
		{
		case JIntellitype.APPCOMMAND_MEDIA_NEXTTRACK:		player.playNext();
															break;
		case JIntellitype.APPCOMMAND_MEDIA_PREVIOUSTRACK:	player.playPrevious();
															break;
		case JIntellitype.APPCOMMAND_MEDIA_PLAY_PAUSE:		player.fadeInOut();
															break;
		case JIntellitype.APPCOMMAND_MEDIA_STOP:			player.stop();
															break;
		}
	}

	@Override
	public void onHotKey(int id)
	{
		IPlayer player = Controller.getInstance().getPlayer();
		
		String action = actionKeys.get(id);
		
		if(action == null)
			return;
		
		if(action.equalsIgnoreCase("PLAY_PAUSE"))
			player.fadeInOut();
		else if(action.equalsIgnoreCase("NEXT"))
			player.playNext();
		else if(action.equalsIgnoreCase("PREVIOUS"))
			player.playPrevious();
		else if(action.equalsIgnoreCase("VOLUME_UP"))
			player.setVolume(player.getVolume() + 10);
		else if(action.equalsIgnoreCase("VOLUME_DOWN"))
			player.setVolume(player.getVolume() - 10);
	}

}
