package lists;

import java.util.ArrayList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import basics.Controller;
import common.Track;
import data.SortOrder;

/**
 * Gibt Suchergebnise aus der Hauptliste aus.
 * <br>Die Suchparameter können im Konstruktor übergeben werden und nachträglich geändert werden.
 * 
 * @author Eraser
 * 
 * @see BasicListModel
 */
public class SearchListModel extends BasicListModel
{
	private String actualSearchString = "";
	private SortOrder actualSortOrder;
	private String actualDbList;
	
	/**Liste ist anfangs leer*/
	public SearchListModel()
	{
		super(new ArrayList<Track>());
	}
	
	/**Liste ist bereits gefüllt
	 * 
	 * @param searchString String nach dem gesucht wird.
	 * @param sortOrder Sortierreihenfolge in der das Ergebnis ausgegeben wird.
	 * @param dbList Liste die durchsucht wird. Bei null wird die Hauptliste durchsucht.
	 * @throws ListException
	 */
	public SearchListModel(String searchString, SortOrder sortOrder, String dbList) throws ListException
	{
		super(new ArrayList<Track>());
		search(searchString, sortOrder, dbList);
	}

	
	/**Startet eine Suche in der Hauptliste und gibt das Ergebnis in der Liste aus.
	 * 
	 * @param searchString String nach dem gesucht wird.
	 * @return Anzahl der gefundenen Tracks.
	 * @throws ListException 
	 */
	public int search(String searchString) throws ListException
	{
		return search(searchString, SortOrder.DEFAULT, null);
	}
	
	/**Startet eine Suche in der angegebenen Liste und gibt das Ergebnis mit der angegebenen Sortierreihenfolge in der Liste aus.
	 * 
	 * @param searchString String nach dem gesucht wird.
	 * @param sortOrder Sortierreihenfolge in der das Ergebnis ausgegeben wird.
	 * @param dbList Liste die durchsucht wird. Bei null wird die Hauptliste durchsucht.
	 * @return Anzahl der gefundenen Tracks.
	 * @throws ListException 
	 */
	public synchronized int search(String searchString, SortOrder sortOrder, String dbList) throws ListException
	{
		this.actualSearchString = searchString;
		this.actualSortOrder = sortOrder;
		this.actualDbList = dbList;
		return search();
	}
	
	private int search() throws ListException
	{
		int maxSize = getSize();
		if(actualSearchString == null || actualSearchString.length() > 0)
		{
			list = Controller.getInstance().getData().readList(actualDbList, actualSearchString, actualSortOrder);
			
			if(list.size() > maxSize)
			maxSize = list.size();
		}
		else
		{
			list = new ArrayList<Track>();
		}
		
		for(ListDataListener listener : dataListener)
			listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, maxSize));
		
		return list.size();
	}

	@Override
	public void trackAdded(Track track)
	{
		try
		{
			search();
		}
		catch (ListException e)
		{
			System.err.println("ListException in SearchListModel.trackAdded.");
			e.printStackTrace();
		}
	}
	@Override
	public void trackDeleted(Track track)
	{
		trackAdded(track);
	}

	@Override
	public int getIndex(Track track)
	{
		for(int i = 0; i < getSize(); i++)
		{
			if(this.getElementAt(i).equals(track))
				return i;
		}
		return -1;
	}
}
