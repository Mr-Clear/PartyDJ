package lists;

import java.util.HashMap;
import java.util.Map;
import basics.Controller;
import common.Track;

public class ListProvider
{
	public final Map<Integer, Track> masterList;
	
	private DbMasterListModel masterListModel;
	private Map<String, DbClientListModel> dbClientListModels = new HashMap<String, DbClientListModel>();
	
	public ListProvider() throws ListException
	{
		assert Controller.getInstance() != null : "Controller nicht geladen!";
		masterList = Controller.getInstance().getData().readMasterList();
		masterListModel = new DbMasterListModel();
	}
	
	public DbMasterListModel getMasterList() throws ListException
	{
		return masterListModel;
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
