package lists;

import java.util.ArrayList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import basics.Controller;
import common.ListException;
import common.SortOrder;
import common.Track;

public class SearchListModel extends BasicListModel
{
	private String searchString;
	
	public SearchListModel()
	{
		super(new ArrayList<Track>());
	}
	
	/**Startet eine Suche, und gibt das Ergebnis in der Liste aus.
	 * 
	 * @param searchString String nach dem gesucht wird.
	 * @return Anzahl der gefundenen Tracks.
	 * @throws ListException 
	 */
	public int search(String searchString) throws ListException
	{
		int maxSize = getSize();
		this.searchString = searchString;
		
		list = Controller.instance.data.readList(null, searchString, SortOrder.DEFAULT);
		
		if(list.size() > maxSize)
		maxSize = list.size();
		
		for(ListDataListener listener : dataListener)
			listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, maxSize));
		
		return list.size();
	}

	public void trackAdded(Track track)
	{
		try
		{
			search(searchString);
		}
		catch (ListException e)
		{
			System.err.println("ListException in SearchListModel.trackAdded.");
			e.printStackTrace();
		}
	}
	public void trackDeleted(Track track)
	{
		try
		{
			search(searchString);
		}
		catch (ListException e)
		{
			System.err.println("ListException in SearchListModel.trackDeleted.");
			e.printStackTrace();
		}
	}
}
