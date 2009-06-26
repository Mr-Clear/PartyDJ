package lists;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import players.PlayStateListener;
import basics.Controller;
import common.Track;
import data.ListAdapter;

/**
 * Stellt die grundlegenden Funktion einer TrackList dar.
 * 
 * @author Eraser
 *
 * @see TrackListModel
 */
abstract class BasicListModel extends ListAdapter implements TrackListModel, PlayStateListener
{
	private final Controller controller = Controller.getInstance();
	protected final Set<ListDataListener> dataListener = new HashSet<ListDataListener>();
	protected List<Track> list;
	
	public BasicListModel(List<Track> list)
	{
		assert Controller.getInstance() != null : "Controller nicht geladen!";
		this.list = list;
		controller.getPlayer().addPlayStateListener(this);
		controller.getData().addListListener(this);
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
	public void playStateChanged(boolean playState){}
	
	@Override
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
	
	public void volumeChanged(int volume){}
}
