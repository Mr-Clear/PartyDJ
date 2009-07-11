package lists.data;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import lists.BasicListModel;
import lists.EditableListModel;
import lists.ListException;
import basics.Controller;
import common.*;

/**
 * Einfache konkrete Implementierung von BasicListModel.
 * <p>Der Inhalt wird nicht persistent gespeichert. Er geht nach beenden des PartyDJ verloren. 
 * 
 * @author Eraser
 *
 * @see BasicListModel
 * @see EditableListModel
 */
public class LightClientListModel extends BasicListModel implements EditableListModel
{
	//private final Map<Integer, Track> masterList = Controller.getInstance().getListProvider().masterList;
	ListProvider listProvider = Controller.getInstance().getListProvider();
	
	public LightClientListModel()
	{
		super(new ArrayList<Track>());
	}

	public LightClientListModel(List<DbTrack> list)
	{
		super(new ArrayList<Track>(list));
	}
	
	/**Fügt einen Track zu der Liste hinzu.
	 * 
	 * @param track 
	 * @throws ListException Kann von abgeleiteten Klasse geworfen werden.
	 */
	public void add(DbTrack track) throws ListException
	{
		list.add(track);
		for(ListDataListener listener : dataListener)
			listener.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, list.size(), list.size()));
	}
	
	@Override
	public void add(Track track) throws ListException
	{
		if(track instanceof DbTrack)
			add((DbTrack)track);
		else
			add(new DbTrack(track));
	}

	@Override
	public void add(int index, Track track) throws ListException
	{
		if(track instanceof DbTrack)
			add(index, (DbTrack)track);
		else
			add(index, new DbTrack(track));
	}
	
	/**
	 * @throws ListException  
	 */
	protected void add(int trackIndex) throws ListException
	{
		list.add(listProvider.getTrackByIndex(trackIndex));
	}

	public void add(int index, DbTrack track) throws ListException
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
		add(index, listProvider.getTrackByIndex(trackIndex));
	}

	public void remove(int index) throws ListException
	{
		if(index < 0 || index >= getSize())
			return;		////Wenn der Index außerhalb der Liste ist, nichts machen.
		list.remove(index);
		for(ListDataListener listener : dataListener)
			listener.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index));
	}

	public void move(int oldIndex, int newIndex) throws ListException
	{
		if(oldIndex < 0 || oldIndex >= getSize() || newIndex < 0 || newIndex > getSize() || oldIndex == newIndex)
			return;		//Wenn der Index außerhalb der Liste ist, oder Indices gleich, nichts machen.
		
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
			add(toAdd, (DbTrack)list.get(oldIndex));
			remove(toRemove);
		}
	}

	public void swap(int indexA, int indexB) throws ListException
	{
		Track A = list.get(indexA);
		Track B = list.get(indexB);
		
		list.set(indexA, B);
		list.set(indexB, A);
		
		for(ListDataListener listener : dataListener)
			listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, indexA, indexB));

	}
	
	@Override
	public void trackAdded(DbTrack track)	{} // Erst interesant, wenn der Track in diese Liste eingefügt wird. 
	
	@Override
	public void trackDeleted(DbTrack track)
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
							listener.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, track.getIndex(), track.getIndex()));
					}
					catch (ListException e)
					{
						System.err.println("ListException in LightClientListModel.trackDeleted.");
						e.printStackTrace();
					}
				}
			}
		}	
	}
}
