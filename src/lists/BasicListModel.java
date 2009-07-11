package lists;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import common.Track;
import players.PlayStateListener;
import basics.Controller;
//import common.Track;
import data.ListAdapter;

/**
 * Stellt die grundlegenden Funktion einer TrackList dar.
 * 
 * @author Eraser
 *
 * @see TrackListModel
 */
public class BasicListModel extends ListAdapter implements TrackListModel, PlayStateListener
{
	private Controller controller;
	protected final Set<ListDataListener> dataListener = new HashSet<ListDataListener>();
	protected List<Track> list;
	
	public BasicListModel()
	{
		this(new ArrayList<Track>());
	}
	
	public BasicListModel(List<Track> list)
	{
		this.list = list;
		try
		{
			controller = Controller.getInstance();
			controller.getPlayer().addPlayStateListener(this);
			controller.getData().addListListener(this);
		}
		catch(Error e)
		{
			System.out.println(e);
			controller = null;
		}
	}
	
	public int getSize()
	{
		return list.size();
	}
	
	public Track getElementAt(int index)
	{
		synchronized(list)
		{
			return list.get(index);
		}
	}
	
	public void addListDataListener(ListDataListener listener)
	{
		dataListener.add(listener);
	}
	
	public void removeListDataListener(ListDataListener listener)
	{
		dataListener.remove(listener);
	}
	
	public void currentTrackChanged(Track playedLast, Track playingCurrent, Reason reason)
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
	
	public int getIndex(Track track)
	{
		for(int i = 0; i < getSize(); i++)
		{
			if(this.getElementAt(i).equals(track))
				return i;
		}
		return -1;
	}
	
	public void playStateChanged(boolean playState){}
		
	public void volumeChanged(int volume){}

	@Override
	public List<Track> getList()
	{
		return java.util.Collections.unmodifiableList(list);
	}
	
	@Override
	public Track[] getValues()
	{
		Track[] ret = new Track[list.size()];
		return list.toArray(ret);
	}
}
