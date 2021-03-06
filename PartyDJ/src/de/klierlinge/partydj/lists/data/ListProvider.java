package de.klierlinge.partydj.lists.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.common.Track;
import de.klierlinge.partydj.data.IData;
import de.klierlinge.partydj.lists.ListException;

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
	private static final Logger log = LoggerFactory.getLogger(ListProvider.class);
	protected static final Controller controller = Controller.getInstance();
	protected static final IData data = controller.getData();
	
	protected DbMasterListModel masterListModel;
	protected Map<String, DbClientListModel> dbClientListModels = new HashMap<>();
	
	public ListProvider() throws ListException
	{
		masterListModel = new DbMasterListModel();
		new Thread()
		{
			@Override
			public void run()
			{
				for(final Track track : masterListModel.getList())
					if(track.getDuration() == 0)
						controller.pushTrackToUpdate(track);
			}
		}.start();
	}
	
	/**Gibt die Hauptliste zurück.
	 * 
	 * @return ListModel der Hauptliste.
	 */
	public DbMasterListModel getMasterList()
	{
		return masterListModel;
	}
	
	/**Gibt eine Liste mit dem angegebenen Namen zurück.
	 * Wenn keine Liste mit diesem Namen existiert, wird sie erstellt.
	 * 
	 * @param listName Name der Liste.
	 * @return ListModel zu der Liste mit dem angegebenen Namen.
	 * @throws ListException  
	 */
	public DbClientListModel getDbList(final String listName) throws ListException
	{
		if(dbClientListModels.containsKey(listName))
		{
			return dbClientListModels.get(listName);
		}
		final DbClientListModel lm = new DbClientListModel(listName);
		dbClientListModels.put(listName, lm);
		return lm;
	}
	
	/**Berechnet die Spielwahrscheinlichkeit einer Liste im Vergleich zu allen Anderen Listen.
	 * 
	 * @param list  Liste von der die Spielwahrscheinlichkeit berechnet werden soll.
	 * @return		Spielwahrscheinlichkeit in Prozent
	 */
	public static double listPlayPossibility(final String list)
	{
		try
		{
			final List<String> lists = data.getLists();
			int sum = Integer.parseInt(data.readSetting("MasterListPriority", "1"));
			double val = 0;
			
			for(final String name : lists)
			{
				sum += data.getListPriority(name);
				if(name.equalsIgnoreCase(list))
					val = data.getListPriority(name);
			}
			return (val / sum) * 100;
		}
		catch(final ListException e)
		{
			log.error("Fehler bei Zugriff auf Datenbank.", e);
			return 0;
		}
	}
}
