package lists;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import basics.Controller;
import basics.PlayStateListener;
import common.Track;
import data.MasterListListener;

/**Stellt die grundlegenden Funktionen einer TrackList dar.
 * 
 * @author Eraser
 *
 */
abstract class BasicListModel implements TrackListModel, PlayStateListener, MasterListListener
{
	private final Controller controller = Controller.getInstance();
	protected final Set<ListDataListener> dataListener = new HashSet<ListDataListener>();
	protected List<Track> list;
	
	public BasicListModel(List<Track> list)
	{
		assert Controller.getInstance() != null : "Controller nicht geladen!";
		this.list = list;
		controller.getPlayer().addPlayStateListener(this);
		controller.getData().addMasterListListener(this);
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
	public void playStateChanged(boolean playState){}
	
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

}
