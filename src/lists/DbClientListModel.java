package lists;

import common.DbTrack;
import data.IData;
import data.SortOrder;
import basics.Controller;

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
	private final Controller controller = Controller.getInstance();
	private final IData data = controller.getData();
	private String listName;
	//private final Map<Integer, Track> masterList = controller.getListProvider().masterList;
	
	protected DbClientListModel(String listName) throws ListException
	{
		super(Controller.getInstance().getData().readList(listName, null, SortOrder.POSITION));
		this.listName = listName;
	}
	
	@Override
	public void add(DbTrack track) throws ListException
	{
		data.insertTrack(listName, track);
		super.add(track);
		
	}
	
	@Override
	protected void add(int trackIndex) throws ListException
	{
		add(controller.getListProvider().getTrackByIndex(trackIndex));
	}

	@Override
	public void add(int index, DbTrack track) throws ListException
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
	
	@Override
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
	
	@Override
	public void swap(int indexA, int indexB) throws ListException
	{
		super.swap(indexA, indexB);
		data.swapTrack(listName, indexA, indexB);
	}
}
