package de.klierlinge.partydj.lists.data;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.common.Track;
import de.klierlinge.partydj.data.ListAdapter;
import de.klierlinge.partydj.data.SortOrder;
import de.klierlinge.partydj.lists.BasicListModel;
import de.klierlinge.partydj.lists.ListException;

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
	private static final Logger log = LoggerFactory.getLogger(SearchListModel.class);
	private String actualSearchString = "";
	private SortOrder actualSortOrder;
	private String actualDbList;
	
	private final Timer updateTimer = new Timer(3000, new ActionListener()
	{
		@Override public void actionPerformed(final ActionEvent e)
		{
			try
			{
				search();
			}
			catch(final ListException e1)
			{
				log.error("Update der Suchliste fehlgeschlagen.", e1);
			}
		}
	});
	
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
	public SearchListModel(final String searchString, final SortOrder sortOrder, final String dbList) throws ListException
	{
		super(new ArrayList<Track>());
		search(searchString, sortOrder, dbList);
	}
	
	protected void init()
	{
		Controller.getInstance().getData().addListListener(new TracksListener());
		updateTimer.setRepeats(false);
	}

	
	/**Startet eine Suche in der Hauptliste und gibt das Ergebnis in der Liste aus.
	 * 
	 * @param searchString String nach dem gesucht wird.
	 * @return Anzahl der gefundenen Tracks.
	 * @throws ListException 
	 */
	public int search(final String searchString) throws ListException
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
	public synchronized int search(final String searchString, final SortOrder sortOrder, final String dbList) throws ListException
	{
		this.actualSearchString = searchString;
		this.actualSortOrder = sortOrder;
		this.actualDbList = dbList;
		return search();
	}
	
	private int search() throws ListException
	{
		int maxSize = getSize();
		int listSize;
		synchronized(list)
		{
			if(actualSearchString == null || actualSearchString.length() > 0)
			{
				list = new ArrayList<>(Controller.getInstance().getData().readList(actualDbList, actualSearchString, actualSortOrder));
				listSize = list.size();
				if(listSize > maxSize)
					maxSize = list.size();
			}
			else
			{
				list = new ArrayList<>();
				listSize = 0;
			}
		}
		
		if(SwingUtilities.isEventDispatchThread())
		{
			for(final ListDataListener listener : dataListener)
				listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, maxSize));
		}
		else
		{
			final int finalMaxSize = maxSize;
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override public void run()
				{
					for(final ListDataListener listener : dataListener)
						listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, finalMaxSize));
				}
			});
		}
		
		return listSize;
	}

	@Override
	public int getIndex(final Track track)
	{
		for(int i = 0; i < getSize(); i++)
		{
			if(this.getElementAt(i).equals(track))
				return i;
		}
		return -1;
	}
	
    protected class TracksListener extends ListAdapter
    {
    	@Override
    	public void trackAdded(final DbTrack track, final boolean eventsFollowing)
    	{
    		try
    		{
    			if(!eventsFollowing)
    			{
    				updateTimer.stop();
    				search();
    			}
    			else
    			{
    				updateTimer.restart();
    			}
    		}
    		catch (final ListException e)
    		{
    			log.error("Suchen fehlgeschlagen.", e);
    		}
    	}
    	@Override
    	public void trackDeleted(final DbTrack track, final boolean eventsFollowing)
    	{
    		trackAdded(track, eventsFollowing);
    	}
    }
}
