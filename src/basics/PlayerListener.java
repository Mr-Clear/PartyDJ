package basics;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;
import lists.DbClientListModel;
import lists.EditableListModel;
import lists.ListException;
import lists.ListProvider;
import players.PlayStateListener;
import players.PlayerException;
import common.Track;
import common.Track.Problem;
import common.Track.TrackElement;
import data.IData;
import data.SettingException;

class PlayerListener implements PlayerContact, PlayStateListener
{
	private Track predictedTrack;
	private IData data = Controller.getInstance().getData();
	private ListProvider listProvider;
	private EditableListModel playList;
	private DbClientListModel lastPlayedList;
	private Track currentTrack;
	
	PlayerListener()
	{
		try
		{
			listProvider = new ListProvider();
			playList = listProvider.getDbList("Wunschliste");				
			lastPlayedList = listProvider.getDbList("LastPlayed");
		}
		catch (ListException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized Track predictNextTrack()
	{
		try
		{
			IData data = Controller.getInstance().getData();
			List<String> listNames = Controller.getInstance().getData().getLists();
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
				listMap.put(listMap.size(), "maschterlist");
			
			int choice = random.nextInt(listMap.size());
			String nextList = listMap.get(choice);
			
			if(nextList.equalsIgnoreCase("maschterlist"))
				predictedTrack = listProvider.getMasterList().getElementAt(random.nextInt(listProvider.getMasterList().getSize()));
			else
			{
				DbClientListModel chosenList = listProvider.getDbList(nextList);
				predictedTrack = chosenList.getElementAt(random.nextInt(chosenList.getSize()));
			}
			
			DbClientListModel lastPlayed = listProvider.getDbList("LastPlayed");
			for(int i = lastPlayed.getSize() - 1; i >= lastPlayed.getSize() - ignoreCount(listNames); i--)
			{
				System.out.println("i:  "  + i+ "  predictedTrack:  " + predictedTrack + "  Track:  " + lastPlayed.getElementAt(i));
				if(lastPlayed.getElementAt(i).equals(predictedTrack))
					return predictNextTrack();
				System.out.println();
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
			boolean allNull = true;
			for(String a : names)
			{
				int prior = data.getListPriority(a);
				int size = listProvider.getDbList(a).getSize();
				if(prior > 0)
				{
					allNull = false;
					if(size < min)
						min = size;
				}
			}
			if(allNull)
			{
				for(String a : names)
				{
					int size = listProvider.getDbList(a).getSize();
					if(size < min)
						min = size;
				}
			}
			
			
			return min * 2;
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
		if(playList != null)
		{
			synchronized(playList)
			{

				if(playList.getSize() > 0)
				{
					nextTrack = playList.getElementAt(0);
					try
					{
						playList.remove(0);
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
		if(lastPlayedList.getSize() == 0)
			return currentTrack;
		
		Track previous = lastPlayedList.getElementAt(lastPlayedList.getSize() - 1);
		
		try
		{
			lastPlayedList.remove(lastPlayedList.getSize() - 1);
			playList.add(0, currentTrack);
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
				while(lastPlayedList.getSize() > 100)
					lastPlayedList.remove(0);
				lastPlayedList.add(lastPlayedList.getSize(), playedLast);
			}
			catch (ListException e)
			{
				e.printStackTrace();
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
