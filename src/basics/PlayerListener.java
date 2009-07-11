package basics;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import lists.ListException;
import lists.data.DbClientListModel;
import lists.data.DbTrack;
import players.PlayStateListener;
import players.PlayerException;
import common.Track;
import common.Track.Problem;
import data.IData;
import data.SettingException;

class PlayerListener implements PlayerContact, PlayStateListener
{
	Controller controller = Controller.getInstance();
	private IData data = controller.getData();
	
	@Override
	public synchronized Track predictNextTrack()
	{
		Track predictedTrack = null;
		try
		{
			List<String> listNames = controller.getData().getLists();
			HashMap<Integer, String> listMap = new HashMap<Integer, String>();
			Random random = new Random();
			
			for(String a : listNames)
			{
				for(int i = 0; i < data.getListPriority(a); i++)
				{
					if(a.equalsIgnoreCase("lastplayed"))
						break;
					listMap.put(i, a);
				}
			}
			for(int i = 0; i < Integer.parseInt(data.readSetting("MasterListPriority", "1")); i++)
				listMap.put(listMap.size(), "masterlist");
			
			int choice = random.nextInt(listMap.size());
			String nextList = listMap.get(choice);
			
			if(nextList.equalsIgnoreCase("masterlist"))
				predictedTrack = controller.listProvider.getMasterList().getElementAt(random.nextInt(controller.listProvider.getMasterList().getSize()));
			else
			{
				DbClientListModel chosenList = controller.listProvider.getDbList(nextList);
				if(chosenList.getSize() > 0)
					predictedTrack = chosenList.getElementAt(random.nextInt(chosenList.getSize()));
				else
					return predictNextTrack();
			}
			
			DbClientListModel lastPlayed = controller.listProvider.getDbList("LastPlayed");
			for(int i = lastPlayed.getSize() - 1; i >= lastPlayed.getSize() - ignoreCount(listNames); i--)
			{
				if(i == -1)
					break;
				if(lastPlayed.getElementAt(i).equals(predictedTrack))
					return predictNextTrack();
			}
		}
		catch (ListException e)
		{
			controller.logError(Controller.NORMAL_ERROR, this, e, "predictNextTrack fehlgeschlagen");
		}
		return predictedTrack;
	}
	
	//TODO Neu machen...
	protected int ignoreCount(List<String> names)
	{
		try
		{
			int min = Integer.MAX_VALUE;
			boolean allNull = true;
			for(String a : names)
			{
				int prior = data.getListPriority(a);
				int size = controller.listProvider.getDbList(a).getSize();
				if(prior > 0 || a.equalsIgnoreCase("lastPlayed"))
				{
					allNull = false;
					if(size < min)
					{
						min = size;
					}
				}
			}
			if(allNull)
			{
				for(String a : names)
				{
					int size = controller.listProvider.getDbList(a).getSize();
					if(size < min)
						min = size;
				}
			}
			
			return (int) (min * 0.8);
		}
		catch(ListException le)
		{
			// TODO Auto-generated catch block
			le.printStackTrace();
		}
		return 0;
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
						controller.playList.remove(0);
					}
					catch (ListException e)
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
			controller.lastPlayedList.remove(controller.lastPlayedList.getSize() - 1);
			controller.lastPlayedList.remove(controller.lastPlayedList.getSize() - 1);
			if(controller.getPlayer().getCurrentTrack() instanceof DbTrack)
				controller.playList.add(0, controller.getPlayer().getCurrentTrack());
		}
		catch (ListException e)
		{
			e.printStackTrace();
		}
		
		return previous;
	}

	@Override
	public void playCompleted()
	{}
	
	@Override
	public void reportProblem(final PlayerException e, final Track track)
	{
		track.setProblem(e.problem);
		controller.logError(Controller.REGULAR_ERROR, this, e, "Fehler beim Abspielen");
		controller.getPlayer().playNext();
	}

	@Override
	public void trackDurationCalculated(Track track, double duration)
	{
		if(duration > 0)
		{
			track.setProblem(Problem.NONE);
		}
		
		track.setDuration(duration);
	}

	//--- PlayStateListener
	@Override
	public void currentTrackChanged(Track playedLast, Track playingCurrent, Reason reason)
	{
		if(reason == Reason.RECEIVED_NEW_TRACK)
		{			
			if(playingCurrent != null)
			{
				try
				{
					data.writeSetting("Playing", playingCurrent.getPath());
				}
				catch (SettingException e){}
				
				if(playingCurrent.getDuration() == 0)
					controller.player.getDuration();
				
				if(playingCurrent.getDuration() > 0)
					playingCurrent.setProblem(Problem.NONE);
			}
			
			if(playedLast != null && reason != Reason.RECEIVED_BACKWARD)
			{
				try
				{
					while(controller.lastPlayedList.getSize() > 100)
						controller.lastPlayedList.remove(0);
					if(playingCurrent instanceof DbTrack)
					{
						controller.lastPlayedList.add(controller.lastPlayedList.getSize(), playingCurrent);
					}
				}
				catch (ListException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void playStateChanged(boolean playState){}
	@Override
	public void volumeChanged(int volume)
	{
		try
		{
			data.writeSetting("Volume", Integer.toString(volume));
		}
		catch (SettingException e){}
	}
}
