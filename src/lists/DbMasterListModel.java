package lists;

import java.util.ArrayList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import common.DbTrack;
import common.Track;
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
		super(new ArrayList<Track>(Controller.getInstance().getData().readList(null, null, data.SortOrder.NAME)));
	}

	@Override
	public void trackAdded(DbTrack track)
	{
		list.add(track);
		for(ListDataListener listener : dataListener)
			listener.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, list.size() - 1, list.size() - 1));
	}

	@Override
	public void trackDeleted(DbTrack track)
	{
		synchronized(list)
		{
			for(int i = 0; i < list.size(); i++)
			{
				if(list.get(i) == track)
				{
					list.remove(i--); // Gelöschten Index zurück gehen.
					for(ListDataListener listener : dataListener)
						listener.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, track.getIndex(), track.getIndex()));
				}
			}
		}	
	}


	@Override
	public int getIndex(Track track)
	{
		for(int i = 0; i < getSize(); i++)
		{
			if(this.getElementAt(i).equals(track))
				return i;
		}
		return -1;
	}
	
	//TODO Verschieben zum ListProvider
	public DbTrack getTrackByPath(String path)
	{
		for(Track track : list)
			if(track.getPath().equals(path))
				return (DbTrack)track;
		return null;
	}
}
