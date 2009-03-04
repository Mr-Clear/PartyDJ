package lists;

import java.util.HashMap;
import java.util.Map;
import javax.swing.ListModel;
import basics.Controller;
import common.ListException;
import common.Track;

public class ListProvider
{
	public final Map<Integer, Track> masterList;
	
	private DbMasterListModel masterListModel;
	private Map<String, DbClientListModel> dbClientListModels = new HashMap<String, DbClientListModel>();
	
	public ListProvider() throws ListException
	{
		assert Controller.instance != null : "Controller nicht geladen!";
		masterList = Controller.instance.data.getMasterList();
		masterListModel = new DbMasterListModel();
	}
	
	public ListModel getMasterList() throws ListException
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
