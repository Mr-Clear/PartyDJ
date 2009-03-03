package lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import basics.Controller;
import common.*;
import data.MasterListListener;

public class LightClientListModel implements EditableListModel, MasterListListener, PlayStateListener
{
	private final ArrayList<Track> list;
	private final HashMap<Integer, Track> masterList = Controller.instance.listProvider.masterList;
	private final HashSet<ListDataListener> dataListener = new HashSet<ListDataListener>();
	
	public LightClientListModel()
	{
		assert Controller.instance != null : "Controller nicht geladen!";
		list = new ArrayList<Track>();
		initialise();
	}
	
	protected LightClientListModel(ArrayList<Track> list)
	{
		assert Controller.instance != null : "Controller nicht geladen!";
		this.list = list;
		initialise();
	}
	
	private void initialise()
	{
		Controller.instance.addPlayStateListener(this);
		Controller.instance.data.addMasterListListener(this);
	}
	
	public int getSize()
	{
		return list.size();
	}
	
	public Object getElementAt(int index)
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
	
	public void add(Track track) throws ListException
	{
		list.add(track);
		for(ListDataListener listener : dataListener)
			listener.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, list.size(), list.size()));
	}
	
	protected void add(int trackIndex) throws ListException
	{
		list.add(masterList.get(trackIndex));
	}

	public void add(int index, Track track) throws ListException
	{
		if(index < 0)
			index = 0;
		if(index >= getSize())
		{
			add(track);	//Wenn der Index ausserhalb der Liste ist, Track am Ende einfügen.
			return;
		}
		list.add(index, track);
		for(ListDataListener listener : dataListener)
			listener.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index));
	}
	
	protected void add(int index, int trackIndex) throws ListException
	{
		add(index, masterList.get(trackIndex));
	}

	public void remove(int index) throws ListException
	{
		if(index < 0 || index >= getSize())
			return;		////Wenn der Index ausserhalb der Liste ist, nichts machen.
		list.remove(index);
		for(ListDataListener listener : dataListener)
			listener.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index));
	}

	public void move(int oldIndex, int newIndex) throws ListException
	{
		
		if(oldIndex < 0 || oldIndex >= getSize() || newIndex < 0 || newIndex > getSize() || oldIndex == newIndex)
			return;		//Wenn der Index ausserhalb der Liste ist, oder Indizes gleich, nichts machen.
		
		int toAdd;
		int toRemove;
		if(oldIndex < newIndex)
		{
			toAdd = newIndex + 1;
			toRemove = oldIndex;
		}
		else
		{
			toAdd = newIndex;
			toRemove = oldIndex + 1;
		}

		synchronized(this)
		{
			add(toAdd, list.get(oldIndex));
			remove(toRemove);
		}
	}

	public void trackAdded(Track track)	{} // Mir wurscht ;)

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
					try
					{
						this.remove(i--); // Gelöschten Index zurück gehen.
						for(ListDataListener listener : dataListener)
							listener.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, track.index, track.index));
					}
					catch (ListException e)
					{
						//TODO
						e.printStackTrace();
					}
				}
			}
		}	
	}

	public void currentTrackChanged(Track playedLast, Track playingCurrent)
	{
		synchronized(list)
		{
			for(int i = 0; i < list.size(); i++)
			{
				if((playedLast != null && list.get(i) == playedLast) || (playingCurrent != null && list.get(i) == playingCurrent))
				{
					for(ListDataListener listener : dataListener)
						listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, i, i));
				}
			}
		}
	}
}
