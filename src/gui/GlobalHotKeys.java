package gui;

import java.util.HashMap;
import lists.ListException;
import lists.data.DbTrack;
import players.IPlayer;
import basics.Controller;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitypeConstants;

public class GlobalHotKeys implements IntellitypeListener, HotkeyListener
{
	private static final GlobalHotKeys instance = new GlobalHotKeys();
	private static HashMap<Integer, String> actionKeys = new HashMap<Integer, String>();
	private boolean stopped;
	
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
		case JIntellitypeConstants.APPCOMMAND_MEDIA_NEXTTRACK:		player.playNext();
																	break;
		case JIntellitypeConstants.APPCOMMAND_MEDIA_PREVIOUSTRACK:	player.playPrevious();
																	break;
		case JIntellitypeConstants.APPCOMMAND_MEDIA_PLAY_PAUSE:		player.fadeInOut();
																	break;
		case JIntellitypeConstants.APPCOMMAND_MEDIA_STOP:			player.stop();
																	break;
//																	JIntellitypeConstants.APPCOMMAND_LAUNCH_MEDIA_SELECT
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
		{
			if(stopped)
				player.start();
			else
				player.fadeInOut();
			stopped = false;
		}
		else if(action.equalsIgnoreCase("STOP"))
		{
			player.stop();
			stopped = true;
		}
		else if(action.equalsIgnoreCase("NEXT"))
			player.playNext();
		else if(action.equalsIgnoreCase("PREVIOUS"))
			player.playPrevious();
		else if(action.equalsIgnoreCase("VOLUME_UP"))
			player.setVolume(player.getVolume() + 10);
		else if(action.equalsIgnoreCase("VOLUME_DOWN"))
			player.setVolume(player.getVolume() - 10);
		else if(action.equalsIgnoreCase("SET_ON_PLAYLIST"))
		{
			if(player.getCurrentTrack() instanceof DbTrack)
			{
				DbTrack dbTrack = (DbTrack)player.getCurrentTrack();
				try
				{
					lists.data.DbClientListModel wishList = Controller.getInstance().getListProvider().getDbList("Playlist");
					if(wishList.getIndex(dbTrack) == -1)
						wishList.add(dbTrack);
				}
				catch (ListException e)
				{
					Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Fehler bei kopieren des Tracks in die Playlist.");
				}
			}
		}
	}

}
