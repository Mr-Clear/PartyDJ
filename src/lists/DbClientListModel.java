package lists;

import java.util.Map;
import common.Track;
import data.IData;
import data.SortOrder;
import basics.Controller;

public class DbClientListModel extends LightClientListModel
{
	private final Controller controller = Controller.getInstance();
	private final IData data = controller.getData();
	private String listName;
	private final Map<Integer, Track> masterList = controller.getListProvider().masterList;
	
	protected DbClientListModel(String listName) throws ListException
	{
		super(Controller.getInstance().getData().readList(listName, null, SortOrder.POSITION));
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
