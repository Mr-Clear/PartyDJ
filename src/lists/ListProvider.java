package lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import common.Track;
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
	private final Map<Integer, Track> masterList;
	private final IData data = Controller.getInstance().getData();
	
	private DbMasterListModel masterListModel;
	private Map<String, DbClientListModel> dbClientListModels = new HashMap<String, DbClientListModel>();
	
	public ListProvider() throws ListException
	{
		assert Controller.getInstance() != null : "Controller nicht geladen!";
		masterList = data.readMasterList();
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
	
	/**
	 * Synchonisiert einen Track mit der Hauptliste.
	 * <p>Wenn der Track in der Hauptliste existiert wird der Einrag aus der Hauptliste zurück gegeben.
	 * <p>Wenn der Track noch nicht in der Hauptliste steht, wird er hinzu gefügt.
	 * 
	 * @param track Track der synchronisiert werden soll.
	 * @return Synchronisierter Track.
	 */
	public Track assignTrack(Track track)
	{
		if(masterList.containsKey(track) && track == masterList.get(track))
			return track;
		for(Track t : masterList.values())
		{
			if(track.equals(t))
				return t;
		}
		try
		{
			data.addTrack(track);
			return track;
		}
		catch (ListException e)
		{
			//TODO null is nix gut. 
			return null;
		}		
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
			IData data = Controller.getInstance().getData();
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
