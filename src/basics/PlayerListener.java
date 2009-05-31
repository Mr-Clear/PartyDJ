package basics;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;
import lists.DbClientListModel;
import lists.ListException;
import players.PlayStateListener;
import players.PlayerException;
import common.Track;
import common.Track.Problem;
import common.Track.TrackElement;
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
			IData data = controller.getData();
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
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	public void reportProblem(final PlayerException e, final Track track)
	{
		track.problem = e.problem;
		Thread t = new Thread(){
			public void run()
			{
				JOptionPane.showMessageDialog(null, "Fehler beim Abspielen:\n" + track.name + "\n\n" + e.getMessage(), "PartyDJ", JOptionPane.ERROR_MESSAGE);
			}
		};
		t.start();
	}

	@Override
	public void trackDurationCalculated(Track track, double duration)
	{
		if(duration > 0 && track.problem != Problem.NONE)
		{
			track.problem = Problem.NONE;
			try
			{
				data.updateTrack(track, TrackElement.PROBLEM);
			}
			catch (ListException e){}
		}
		
		if(track.duration != duration)
		{
			track.duration = duration;
			try
			{
				data.updateTrack(track, TrackElement.DURATION);
			}
			catch (ListException e){}
		}
		
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
					data.writeSetting("Playing", playingCurrent.path);
				}
				catch (SettingException e){}
				
				if(playingCurrent.duration == 0)
					controller.player.getDuration();
				
				if(playingCurrent.duration > 0 && playingCurrent.problem != Problem.NONE)
				{
					playingCurrent.problem = Problem.NONE;
					try
					{
						data.updateTrack(playingCurrent, TrackElement.PROBLEM);
					}
					catch (ListException e){}
				}
			}
			
			if(playedLast != null && reason != Reason.RECEIVED_BACKWARD)
			{
				try
				{
					while(controller.lastPlayedList.getSize() > 100)
						controller.lastPlayedList.remove(0);
					controller.lastPlayedList.add(controller.lastPlayedList.getSize(), playingCurrent);
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
