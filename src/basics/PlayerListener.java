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
	private Track predictedTrack = null;
	Controller controller = Controller.getInstance();
	private IData data = controller.getData();
	private Track currentTrack;
	
	public synchronized Track predictNextTrack()
	{
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
				predictedTrack = chosenList.getElementAt(random.nextInt(chosenList.getSize()));
			}
			
			DbClientListModel lastPlayed = controller.listProvider.getDbList("LastPlayed");
			for(int i = lastPlayed.getSize() - 1; i >= lastPlayed.getSize() - ignoreCount(listNames); i--)
			{
				if(lastPlayed.getElementAt(i).equals(predictedTrack))
					return predictNextTrack();
			}
		}
		catch (ListException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return predictedTrack;
	}
	
	public int ignoreCount(List<String> names)
	{
		try
		{
			int min = Integer.MAX_VALUE;
			int ignoreCount = 0;
			String minList = "";
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
						minList = a;
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
			
			if(controller.listProvider.listPlayPossibility(minList) / 100 > 0)
				ignoreCount = (int)(min / (controller.listProvider.listPlayPossibility(minList) / 100));
			return (int)(ignoreCount * 0.8);
		}
		catch(ListException le)
		{
			// TODO Auto-generated catch block
			le.printStackTrace();
		}
		return 0;
	}
	
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
						e.printStackTrace();
						//TODO
					}
				}
			}
		}
		if(nextTrack == null)
		{
			nextTrack = predictNextTrack();
			predictedTrack = null;
		}
		
		return nextTrack;
	}

	public Track requestPreviousTrack()
	{
		if(controller.lastPlayedList.getSize() == 0)
			return currentTrack;
		
		Track previous = controller.lastPlayedList.getElementAt(controller.lastPlayedList.getSize() - 2);
		
		try
		{
			controller.lastPlayedList.remove(controller.lastPlayedList.getSize() - 1);
			controller.lastPlayedList.remove(controller.lastPlayedList.getSize() - 1);
			controller.playList.add(0, currentTrack);
		}
		catch (ListException e)
		{
			e.printStackTrace();
		}
		
		return previous;
	}

	public void playCompleted()
	{
		// TODO Auto-generated method stub
	}
	
	public void reportProblem(PlayerException e, Track track)
	{
		track.problem = e.problem;
		JOptionPane.showMessageDialog(null, "Fehler beim Abspielen:\n" + track.name + "\n\n" + e.getMessage(), "PartyDJ", JOptionPane.ERROR_MESSAGE);
	}

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
	public void currentTrackChanged(Track playedLast, Track playingCurrent, Reason reason)
	{
		if(reason == Reason.RECEIVED_NEW_TRACK)
		{
			currentTrack = playingCurrent;
			
			if(playingCurrent != null)
			{
				try
				{
					data.writeSetting("Playing", playingCurrent.path);
				}
				catch (SettingException e){}
				
				if(playingCurrent.duration == 0)
					Controller.getInstance().player.getDuration();
				
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

	public void playStateChanged(boolean playState){}
	public void volumeChanged(int volume)
	{
		try
		{
			data.writeSetting("Volume", Integer.toString(volume));
		}
		catch (SettingException e){}
	}
}
