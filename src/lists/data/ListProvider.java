package lists.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lists.ListException;
import data.IData;
import basics.Controller;

/**
 * Gibt Zugriff auf die Listen des PartyDJ.
 * 
 * @author Eraser
 * 
 * @see DbMasterListModel
 * @see DbClientListModel
 */
public class ListProvider
{
	protected final Map<Integer, DbTrack> masterList;
	protected final Controller controller = Controller.getInstance();
	protected final IData data = controller.getData();
	
	protected DbMasterListModel masterListModel;
	protected Map<String, DbClientListModel> dbClientListModels = new HashMap<String, DbClientListModel>();
	
	public ListProvider() throws ListException
	{
		masterList = data.readMasterList();
		masterListModel = new DbMasterListModel();
		
		for(DbTrack track : masterList.values())
			if(track.getDuration() == 0 && track.getProblem() == DbTrack.Problem.NONE)
				controller.pushTrackToUpdate(track);
	}
	
	public DbMasterListModel getMasterList()
	{
		return masterListModel;
	}
	
	public DbTrack getTrackByIndex(int index)
	{
		return masterList.get(index);
	}
	
	public DbClientListModel getDbList(String listName) throws ListException
	{
		if(dbClientListModels.containsKey(listName))
		{
			return dbClientListModels.get(listName);
		}
		DbClientListModel lm = new DbClientListModel(listName);
		dbClientListModels.put(listName, lm);
		return lm;
	}
	
	/**
	 * Synchonisiert einen Track mit der Hauptliste.
	 * <p>Wenn der Track in der Hauptliste existiert wird der Einrag aus der Hauptliste zurück gegeben.
	 * <p>Wenn der Track noch nicht in der Hauptliste steht, wird er hinzu gefügt.
	 * 
	 * @param track Track der synchronisiert werden soll.
	 * @return Synchronisierter Track.
	 * @throws ListException 
	 */
	public DbTrack assignTrack(DbTrack track) throws ListException
	{
		if(masterList.containsKey(track) && track == masterList.get(track))
			return track;
		for(DbTrack t : masterList.values())
		{
			if(track.equals(t))
				return t;
		}
		data.addTrack(track);
		controller.pushTrackToUpdate(track);
		return track;
	}
	
	/**Berechnet die Spielwahrscheinlichkeit einer Liste im Vergleich zu allen Anderen Listen.
	 * 
	 * @param list  Liste von der die Spielwahrscheinlichkeit berechnet werden soll.
	 * @return		Spielwahrscheinlichkeit in Prozent
	 */
	public double listPlayPossibility(String list)
	{
		try
		{
			List<String> lists = data.getLists();
			int sum = Integer.parseInt(data.readSetting("MasterListPriority", "1"));
			double val = 0;
			
			for(String name : lists)
			{
				sum += data.getListPriority(name);
				if(name.equalsIgnoreCase(list))
					val = data.getListPriority(name);
			}
			return (val / sum) * 100;
			
		}
		catch (ListException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
}
