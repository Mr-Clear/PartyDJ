package de.klierlinge.partydj.lists.data;

import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.common.Track;
import de.klierlinge.partydj.data.IData;
import de.klierlinge.partydj.data.SortOrder;
import de.klierlinge.partydj.lists.ListException;

/**
 * Erweitert das LightClientListModel um dauerhafte Speicherung des Inhalts.
 * <p>Jede Änderung wird sofort in der Datenbank gespeichert.
 * <br>Das macht dieses ListModel deutlich langsamer als das LightClientListModel.
 * <p>Kann nur von ListProvider erstellt werden, um sicher zu stellen dass zu jeder Liste
 * nur eine Instanz läuft.
 * 
 * @author Eraser
 *
 * @see ListProvider
 * @see LightClientListModel
 * @see IData
 */
public class DbClientListModel extends LightClientListModel
{
	private final String listName;
	
	protected DbClientListModel(final String listName) throws ListException
	{
		super(listName, Controller.getInstance().getData().readList(listName, null, SortOrder.POSITION));
		this.listName = listName;
	}
	
	//@Override
	public void add(final DbTrack track, final boolean eventsFollowing) throws ListException
	{		
		data.insertTrack(listName, track, eventsFollowing);
		super.add(track, eventsFollowing);
	}
	
	@Override
	public void add(final Track track, final boolean eventsFollowing) throws ListException
	{
		if(track == null)
		{
			super.add(null, eventsFollowing);
			return;
		}
		final DbTrack dbTrack;
		if(track instanceof DbTrack)
			dbTrack = (DbTrack)track;
		else
			dbTrack = data.getTrack(track.getPath(), true); 
		data.insertTrack(listName, dbTrack, eventsFollowing);
		super.add(dbTrack, eventsFollowing);
	}
	
	@Override
	protected void add(final int trackIndex, final boolean eventsFollowing) throws ListException
	{
		add(data.getTrack(trackIndex), eventsFollowing);
	}

	@Override
	public void add(int index, final DbTrack track, final boolean eventsFollowing) throws ListException
	{
		if(index < 0)
			index = 0;
		if(index >= getSize())
		{
			add(track, eventsFollowing);	//Wenn der Index ausserhalb der Liste ist, Track am Ende einfügen.
			return;
		}
		try
		{
			data.insertTrackAt(listName, track, index, eventsFollowing);
		}
		catch (final ListException e)
		{
			throw e;
		}
		super.add(index, track, eventsFollowing);
	}
	
	@Override
	public void remove(final int index, final boolean eventsFollowing) throws ListException
	{
		if(index < 0 || index >= getSize())
			return;		//Wenn der Index ausserhalb der Liste ist, nichts machen.
		try
		{
			data.removeTrack(listName, index, eventsFollowing);
		}
		catch (final ListException e)
		{
			throw e;
		}
		super.remove(index, eventsFollowing);
	}
	
	@Override
	public void swap(final int indexA, final int indexB, final boolean eventsFollowing) throws ListException
	{
		super.swap(indexA, indexB, eventsFollowing);
		data.swapTrack(listName, indexA, indexB, eventsFollowing);
	}
}
