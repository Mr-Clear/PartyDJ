package lists.data;

import basics.Controller;
import common.Track;
import data.IData;
import data.ListAdapter;
import data.SortOrder;
import lists.BasicListModel;
import lists.ListException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.Timer;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * ListModel das die Hauptliste ausgibt.
 *
 * @author Eraser
 *
 * @see BasicListModel
 * @see data.IData
 */
public class DbMasterListModel extends BasicListModel
{
	protected SortOrder sortOrder = SortOrder.NAME;
	protected final IData data = Controller.getInstance().getData();

	protected DbMasterListModel() throws ListException
	{
		super();
		setSortOrder(SortOrder.arrayIndexToSortOrder(Integer.parseInt(data.readSetting("MasterListSortOrder", Integer.toString(SortOrder.sortOrderToArrayIndex(SortOrder.DEFAULT))))));

		Controller.getInstance().getData().addListListener(new TracksListener());
	}

	/**
	 * Setzt die Sortierreihenfolge und sortiert die Liste neu.
	 *
	 * @param sortOrder Neue Sortierreihenvolge.
	 * @throws ListException Wenn auf die Liste in der Datenbank nicht zugegriffen werden kann.
	 */
	public void setSortOrder(SortOrder sortOrder) throws ListException
	{
		this.sortOrder = sortOrder;
		synchronized(list)
		{
			final int oldSize = list.size();
			list = new ArrayList<Track>(Controller.getInstance().getData().readList(null, null, sortOrder));
			data.writeSetting("MasterListSortOrder", Integer.toString(sortOrder.toArrayIndex()));

			for(final ListDataListener listener : dataListener)
			{
				try
				{
					listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, Math.max(oldSize, list.size())));
				}
				catch (Exception e)
				{
					controller.logError(Controller.NORMAL_ERROR, listener, e, "Fehler in Plugin: " + dataListener);
				}
			}
		}
	}

	/**
	 *  Gibt die aktuelle Sortierreihenvolge zurück.
	 *
	 * @return Die aktuelle Sortierreihenvolge.
	 * */
	public SortOrder getSortOrder()
	{
		return sortOrder;
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
    	protected final Timer trackAddedUpdateTimer;
    	protected final Timer trackDeletedUpdateTimer;
    	protected final Timer trackChangedUpdateTimer;

    	public TracksListener()
    	{
    		trackAddedUpdateTimer = new Timer(1000, new ActionListener()
    		{
    			@Override public void actionPerformed(ActionEvent e)
    			{
    				trackAddedUpdate(false);
    			}
    		});
    		trackAddedUpdateTimer.setRepeats(false);

    		trackDeletedUpdateTimer = new Timer(1000, new ActionListener()
    		{
    			@Override public void actionPerformed(ActionEvent e)
    			{
    				trackDeletedUpdate(false);
    			}
    		});
    		trackDeletedUpdateTimer.setRepeats(false);

    		trackChangedUpdateTimer = new Timer(1000, new ActionListener()
    		{
    			@Override public void actionPerformed(ActionEvent e)
    			{
    				trackChangedUpdate(false);
    			}
    		});
    		trackChangedUpdateTimer.setRepeats(false);
    	}

    	@Override
    	public void trackAdded(final DbTrack track, boolean eventsFollowing)
    	{
    		if(track != null)
    			list.add(track);
    		trackAddedUpdate(eventsFollowing);
    	}

    	@Override
    	public void trackDeleted(final DbTrack track, boolean eventsFollowing)
    	{
    		if(track != null)
    		{
	    		synchronized(list)
	    		{
	    			for(int i = 0; i < list.size(); i++)
	    			{
	    				if(list.get(i) == track)
	    				{
	    					list.remove(i--); // Gelöschten Index zurück gehen.
	    				}
	    			}
	    		}
    		}
    		trackDeletedUpdate(eventsFollowing);
    	}

    	@Override
		public void trackChanged(final DbTrack newTrack, final common.Track oldTrack, final boolean eventsFollowing)
    	{
    		//if(!newTrack.getName().equals(oldTrack.getName()) || newTrack.getDuration() != oldTrack.getDuration())
    		{
    			trackChangedUpdate(eventsFollowing);
    		}
    	}

    	protected void trackAddedUpdate(boolean wait)
    	{
    		if(wait)
    		{
    			trackChangedUpdateTimer.restart();
    		}
    		else
    		{
    			trackChangedUpdateTimer.stop();
    			int pos = list.size() - 1;
    			if(pos < 0) pos = 0;
        		for(final ListDataListener listener : dataListener)
    			{
    				try
	    			{
	        			listener.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, pos, pos));
    				}
    				catch (Exception e)
    				{
    					controller.logError(Controller.NORMAL_ERROR, listener, e, "Fehler in Plugin: " + dataListener);
    				}
    			}
    		}
    	}

    	protected void trackDeletedUpdate(boolean wait)
    	{
    		if(wait)
    		{
    			trackChangedUpdateTimer.restart();

    		}
    		else
    		{
    			trackChangedUpdateTimer.stop();
    			for(final ListDataListener listener : dataListener)
    			{
    				try
    				{
    					listener.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, 0, list.size() - 1));
    				}
    				catch (Exception e)
    				{
    					controller.logError(Controller.NORMAL_ERROR, listener, e, "Fehler in Plugin: " + dataListener);
    				}
    			}
    		}
    	}

    	protected void trackChangedUpdate(boolean wait)
    	{
    		if(wait)
    		{
    			trackChangedUpdateTimer.restart();
    		}
    		else
    		{
    			trackChangedUpdateTimer.stop();
				for(final ListDataListener listener : dataListener)
    			{
    				try
    				{
    					listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, list.size() - 1));
    				}
    				catch (Exception e)
    				{
    					controller.logError(Controller.NORMAL_ERROR, listener, e, "Fehler in Plugin: " + dataListener);
    				}
    			}
    		}
    	}
    }
}
