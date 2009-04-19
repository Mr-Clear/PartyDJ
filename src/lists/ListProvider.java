package lists;

import java.util.HashMap;
import java.util.Map;
import common.Track;
import basics.Controller;

public class ListProvider
{
	private final Map<Integer, Track> masterList;
	
	private DbMasterListModel masterListModel;
	private Map<String, DbClientListModel> dbClientListModels = new HashMap<String, DbClientListModel>();
	
	public ListProvider() throws ListException
	{
		assert Controller.getInstance() != null : "Controller nicht geladen!";
		masterList = Controller.getInstance().getData().readMasterList();
		masterListModel = new DbMasterListModel();
	}
	
	public DbMasterListModel getMasterList()
	{
		return masterListModel;
	}
	
	public Track getTrackByIndex(int index)
	{
		return masterList.get(index);
	}
	
	public DbClientListModel getDbList(String listName) throws ListException
	{
		if(dbClientListModels.containsKey(listName))
		{
			return dbClientListModels.get(listName);
		}
		else
		{
			DbClientListModel lm = new DbClientListModel(listName);
			dbClientListModels.put(listName, lm);
			return lm;
		}
	}
}
