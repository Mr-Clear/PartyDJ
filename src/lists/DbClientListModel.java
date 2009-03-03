package lists;

import java.util.HashMap;
import common.ListException;
import common.Track;
import data.IData;
import basics.Controller;

public class DbClientListModel extends LightClientListModel
{
	private String listName;
	private final IData data = Controller.instance.data;
	private final HashMap<Integer, Track> masterList = Controller.instance.listProvider.masterList;
	
	protected DbClientListModel(String listName) throws ListException
	{
		super(Controller.instance.data.readList(listName, null, common.SortOrder.POSITION));
		this.listName = listName;
	}
	
	public void add(Track track) throws ListException
	{
		try
		{
			data.insertTrack(listName, track);
		}
		catch (ListException e)
		{
			throw e;
		}
		super.add(track);
		
	}
	
	protected void add(int trackIndex) throws ListException
	{
		add(masterList.get(trackIndex));
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
		try
		{
			data.insertTrackAt(listName, track, index);
		}
		catch (ListException e)
		{
			throw e;
		}
		super.add(index, track);
	}
	
	public void remove(int index) throws ListException
	{
		if(index < 0 || index >= getSize())
			return;		//Wenn der Index ausserhalb der Liste ist, nichts machen.
		try
		{
			data.removeTrack(listName, index);
		}
		catch (ListException e)
		{
			throw e;
		}
		super.remove(index);
	}
}
