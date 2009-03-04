package lists;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import common.*;
import data.MasterListListener;

import basics.Controller;

public class DbMasterListModel implements TrackListModel, MasterListListener, PlayStateListener
{
	private final List<Track> list;
	private final Set<ListDataListener> dataListener = new HashSet<ListDataListener>();
	
	protected DbMasterListModel() throws ListException 
	{
		assert Controller.instance != null : "Controller nicht geladen!";
		list = Controller.instance.data.readList(null, null, common.SortOrder.NAME);
		
		Controller.instance.addPlayStateListener(this);
		Controller.instance.data.addMasterListListener(this);
	}
	
	public int getSize()
	{
		return list.size();
	}
	
	public Track getElementAt(int index)
	{
		return list.get(index);
	}

	public void addListDataListener(ListDataListener listener)
	{
		dataListener.add(listener);
	}
	
	public void removeListDataListener(ListDataListener listener)
	{
		dataListener.remove(listener);
	}

	public void trackAdded(Track track)
	{
		list.add(track);
		for(ListDataListener listener : dataListener)
			listener.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, list.size() - 1, list.size() - 1));
	}

	public void trackChanged(Track track)
	{
		synchronized(list)
		{
			for(int i = 0; i < list.size(); i++)
			{
				if(list.get(i) == track)
				{
					for(ListDataListener listener : dataListener)
						listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, i, i));
				}
			}
		}
	}

	public void trackDeleted(Track track)
	{
		synchronized(list)
		{
			for(int i = 0; i < list.size(); i++)
			{
				if(list.get(i) == track)
				{
					list.remove(i--); // Gelöschten Index zurück gehen.
					for(ListDataListener listener : dataListener)
						listener.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, track.index, track.index));
				}
			}
		}	
	}

	public void currentTrackChanged(Track playedLast, Track playingCurrent)
	{
		synchronized(list)
		{
			int last = -1;
			int current = -1;
			if (playedLast != null)
				last = list.indexOf(playedLast);
			if (playingCurrent != null)
				last = list.indexOf(playingCurrent);

			for(ListDataListener listener : dataListener)
			{
				if (last >= 0)
					listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, last, last));
				if (last >= 0)
					listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, current, current));
			}
		}
	}
}
