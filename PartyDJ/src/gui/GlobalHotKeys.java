package gui;

import basics.Controller;
import players.IPlayer;
import java.util.HashMap;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitypeConstants;
import lists.ListException;
import lists.data.DbTrack;

public class GlobalHotKeys implements IntellitypeListener, HotkeyListener
{
	private static final GlobalHotKeys INSTANCE = new GlobalHotKeys();
	private static HashMap<Integer, String> actionKeys = new HashMap<>();
	private boolean stopped;
	
	public static GlobalHotKeys getInstance()
	{
		return INSTANCE;
	}
	
	public static void setKeyAction(final int id, final String action)
	{
		actionKeys.put(id, action);
	}
	
	@Override
	public void onIntellitype(final int id)
	{
		final IPlayer player = Controller.getInstance().getPlayer();
		switch(id)
		{
		case JIntellitypeConstants.APPCOMMAND_MEDIA_NEXTTRACK:
			player.playNext();
			break;
		case JIntellitypeConstants.APPCOMMAND_MEDIA_PREVIOUSTRACK:
			player.playPrevious();
			break;
		case JIntellitypeConstants.APPCOMMAND_MEDIA_PLAY_PAUSE:
			player.fadeInOut();
			break;
		case JIntellitypeConstants.APPCOMMAND_MEDIA_STOP:
			player.stop();
			break;
		//JIntellitypeConstants.APPCOMMAND_LAUNCH_MEDIA_SELECT
		}
	}

	@Override
	public void onHotKey(final int id)
	{
		final IPlayer player = Controller.getInstance().getPlayer();
		
		final String action = actionKeys.get(id);
		
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
				final DbTrack dbTrack = (DbTrack)player.getCurrentTrack();
				try
				{
					final lists.data.DbClientListModel wishList = Controller.getInstance().getListProvider().getDbList("Playlist");
					if(wishList.getIndex(dbTrack) == -1)
						wishList.add(dbTrack, false);
				}
				catch (final ListException e)
				{
					Controller.getInstance().logError(Controller.NORMAL_ERROR, this, e, "Fehler bei kopieren des Tracks in die Playlist.");
				}
			}
		}
	}

}
