package basics;

import common.Track;
import common.Track.Problem;
import data.IData;
import data.SettingException;
import lists.ListException;
import lists.TrackListModel;
import lists.data.DbClientListModel;
import lists.data.DbTrack;
import players.PlayStateListener;
import players.PlayerException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerListener implements PlayerContact, PlayStateListener
{
	Controller controller = Controller.getInstance();
	private final IData data = controller.getData();
	
	@Override
	public synchronized Track predictNextTrack()
	{
		if(controller.listProvider.getMasterList().getSize() <= 0)
			return null;
		
		Track predictedTrack = null;
		try
		{
			final List<String> listNames = controller.getData().getLists();
			final ArrayList<TrackListModel> listList = new ArrayList<>();
			final Random random = new Random();
			
			for(final String listName : listNames)
			{
				if(!listName.equalsIgnoreCase("lastplayed"))
					for(int i = 0; i < data.getListPriority(listName); i++)
					{
						final TrackListModel list = controller.listProvider.getDbList(listName);
						if(list.getSize() > 0)
							listList.add(list);
					}
			}
			for(int i = 0; i < Integer.parseInt(data.readSetting("MasterListPriority", "1")); i++)
				listList.add(controller.listProvider.getMasterList());
			
			final TrackListModel nextList;
			if(listList.size() > 0)
				nextList = listList.get(random.nextInt(listList.size()));
			else
				nextList = controller.listProvider.getMasterList();
			
			predictedTrack = nextList.getElementAt(random.nextInt(nextList.getSize()));
			
			// Prüfen ob Lied vor kurzem gespielt wurde.
			final DbClientListModel lastPlayed = controller.listProvider.getDbList("LastPlayed");
			int indexOnLastPlayed = lastPlayed.getIndex(predictedTrack);
			if(indexOnLastPlayed >= lastPlayed.getSize() - ignoreCount(listNames))
				return predictNextTrack();
		}
		catch (final ListException e)
		{
			controller.logError(Controller.NORMAL_ERROR, this, e, "predictNextTrack fehlgeschlagen");
		}
		return predictedTrack;
	}
	
	/**
	 * Mit dieser Methode wird festgestellt, nach wie viel Liedern sich ein Lied
	 * wiederholen darf.
	 * 
	 * @param listNames	Liste mit Namen der Listen, für die ein ignoreCount erstellt
	 * werden soll.
	 * @return
	 * @throws ListException
	 */
	protected int ignoreCount(final List<String> listNames) throws ListException
	{
		int min = Integer.MAX_VALUE;
		for(final String listName : listNames)
		{
			final int priority = data.getListPriority(listName);
			final int size = controller.listProvider.getDbList(listName).getSize();
			
			//Es werden nur Listen mit einer Priorität > 0 berücksichtigt.
			if(priority > 0 || listName.equalsIgnoreCase("lastPlayed"))
			{
				if(size < min)
					min = size;
			}
			final int masterListSize = controller.listProvider.getMasterList().getSize();			
			min = masterListSize < min ? masterListSize : min;
		}
		
		if(min < 30)
			return min;
		return (int) (min * 0.8);
	}
	
	@Override
	public synchronized Track requestNextTrack()
	{
		Track nextTrack = null;
		if(controller.playList != null)
		{
			synchronized(controller.playList)
			{
				if(controller.playList.getSize() > 0)
				{
					nextTrack = controller.playList.getElementAt(0);
					try
					{
						controller.playList.remove(0, false);
					}
					catch (final ListException e)
					{
						controller.logError(Controller.NORMAL_ERROR, this, e, "requestNextTrack fehlgeschlagen");
					}
				}
			}
		}
		if(nextTrack == null)
		{
			nextTrack = predictNextTrack();
		}
		
		return nextTrack;
	}

	@Override
	public synchronized Track requestPreviousTrack()
	{
		if(controller.lastPlayedList.getSize() == 0)
			return controller.getPlayer().getCurrentTrack();
		
		Track previous;
		if(controller.lastPlayedList.getSize() >= 2)
			previous = controller.lastPlayedList.getElementAt(controller.lastPlayedList.getSize() - 2);
		else if(controller.lastPlayedList.getSize() != 0)
			previous = controller.lastPlayedList.getElementAt(0);
		else
			return null;
		
		try
		{
			controller.lastPlayedList.remove(controller.lastPlayedList.getSize() - 1, true);
			controller.lastPlayedList.remove(controller.lastPlayedList.getSize() - 1, false);
			if(controller.getPlayer().getCurrentTrack() instanceof DbTrack)
				controller.playList.add(0, controller.getPlayer().getCurrentTrack(), false);
		}
		catch (final ListException e)
		{
			e.printStackTrace();
		}
		
		return previous;
	}

	@Override
	public void playCompleted() { /* not to implement */ }
	
	@Override
	public void reportProblem(final PlayerException e, final Track track)
	{
		track.setProblem(e.getProblem());
		controller.logError(Controller.REGULAR_ERROR, this, e, "Fehler beim Abspielen von " + track.toString());
		controller.getPlayer().playNext();
	}

	@Override
	public void trackDurationCalculated(final Track track, final double duration)
	{
		if(duration > 0)
		{
			track.setProblem(Problem.NONE);
		}
		
		track.setDuration(duration);
	}

	//--- PlayStateListener
	@Override
	public void currentTrackChanged(final Track playedLast, final Track playingCurrent, final Reason reason)
	{
		if(reason == Reason.RECEIVED_NEW_TRACK)
		{			
			if(playingCurrent != null)
			{
				try
				{
					data.writeSetting("Playing", playingCurrent.getPath());
				}
				catch (final SettingException ignored) { /* ignore */ }
				
				if(playingCurrent.getDuration() == 0)
					controller.getPlayer().getDuration();
				
				if(playingCurrent.getDuration() > 0)
					playingCurrent.setProblem(Problem.NONE);
			}
			
			if(playedLast != null && reason != Reason.RECEIVED_BACKWARD)
			{
				try
				{
					while(controller.lastPlayedList.getSize() > 100)
						controller.lastPlayedList.remove(0, false);
					if(playingCurrent instanceof DbTrack)
					{
						controller.lastPlayedList.add(controller.lastPlayedList.getSize(), playingCurrent, false);
					}
				}
				catch (final ListException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void playStateChanged(final boolean playState) { /* not to implement */ }
	@Override
	public void volumeChanged(final int volume)
	{
		try
		{
			data.writeSetting("Volume", Integer.toString(volume));
		}
		catch (final SettingException ignored) { /* ignore */ }
	}
}
