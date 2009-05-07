package lists;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import common.*;

import basics.Controller;

/**
 * ListModel das die Hauptliste ausgibt.
 * 
 * @author Eraser
 *
 * @see BasicListModel
 * @see data.IData
 */
public class DbMasterListModel extends BasicListModel
{
	protected DbMasterListModel() throws ListException 
	{
		super(Controller.getInstance().getData().readList(null, null, data.SortOrder.NAME));
	}

	public void trackAdded(Track track)
	{
		list.add(track);
		for(ListDataListener listener : dataListener)
			listener.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, list.size() - 1, list.size() - 1));
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
	
	public int getIndex(Track track)
	{
		for(int i = 0; i < getSize(); i++)
		{
			if(this.getElementAt(i).equals(track))
				return i;
		}
		return -1;
	}
	
	// Verschieben zum ListProvider
	public Track getTrackByPath(String path)
	{
		for(Track track : list)
		{
			if(track.path.equals(path))
				return track;
		}
		return null;
	}
}
