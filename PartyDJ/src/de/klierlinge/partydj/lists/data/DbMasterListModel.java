package de.klierlinge.partydj.lists.data;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.Timer;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.common.Track;
import de.klierlinge.partydj.data.IData;
import de.klierlinge.partydj.data.ListAdapter;
import de.klierlinge.partydj.data.SortOrder;
import de.klierlinge.partydj.lists.BasicListModel;
import de.klierlinge.partydj.lists.ListException;

/**
 * ListModel das die Hauptliste ausgibt.
 *
 * @author Eraser
 *
 * @see BasicListModel
 * @see de.klierlinge.partydj.data.IData
 */
public class DbMasterListModel extends BasicListModel
{
	private static final Logger log = LoggerFactory.getLogger(DbMasterListModel.class);
    protected SortOrder sortOrder = SortOrder.NAME;
    protected final IData data = Controller.getInstance().getData();

    protected DbMasterListModel() throws ListException
    {
        super();
        setSortOrder(SortOrder.arrayIndexToSortOrder(Integer.parseInt(data.readSetting("MasterListSortOrder",
                Integer.toString(SortOrder.sortOrderToArrayIndex(SortOrder.DEFAULT))))));

        Controller.getInstance().getData().addListListener(new TracksListener());
    }

    /**
     * Setzt die Sortierreihenfolge und sortiert die Liste neu.
     *
     * @param sortOrder
     *            Neue Sortierreihenvolge.
     * @throws ListException
     *             Wenn auf die Liste in der Datenbank nicht zugegriffen werden
     *             kann.
     */
    public void setSortOrder(final SortOrder sortOrder) throws ListException
    {
        this.sortOrder = sortOrder;
        synchronized(list)
        {
            final int oldSize = list.size();
            list = new ArrayList<>(Controller.getInstance().getData().readList(null, null, sortOrder));
            data.writeSetting("MasterListSortOrder", Integer.toString(sortOrder.toArrayIndex()));

            for(final ListDataListener listener : dataListener)
            {
                try
                {
                    listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, Math.max(oldSize, list.size())));
                }
                catch(final Exception e)
                {
                	log.error("Failed to notify ListDataListener: " + listener, e);
                }
            }
        }
    }

    /**
     * Gibt die aktuelle Sortierreihenvolge zur??ck.
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
                @Override
                public void actionPerformed(final ActionEvent e)
                {
                    trackAddedUpdate(false);
                }
            });
            trackAddedUpdateTimer.setRepeats(false);

            trackDeletedUpdateTimer = new Timer(1000, new ActionListener()
            {
                @Override
                public void actionPerformed(final ActionEvent e)
                {
                    trackDeletedUpdate(false);
                }
            });
            trackDeletedUpdateTimer.setRepeats(false);

            trackChangedUpdateTimer = new Timer(1000, new ActionListener()
            {
                @Override
                public void actionPerformed(final ActionEvent e)
                {
                    trackChangedUpdate(false);
                }
            });
            trackChangedUpdateTimer.setRepeats(false);
        }

        @Override
        public void trackAdded(final DbTrack track, final boolean eventsFollowing)
        {
            if(track != null)
                list.add(track);
            trackAddedUpdate(eventsFollowing);
        }

        @Override
        public void trackDeleted(final DbTrack track, final boolean eventsFollowing)
        {
            if(track != null)
            {
                synchronized(list)
                {
                    for(int i = 0; i < list.size(); i++)
                    {
                        if(list.get(i) == track)
                        {
                            list.remove(i--); // Gel??schten Index zur??ck gehen.
                        }
                    }
                }
            }
            trackDeletedUpdate(eventsFollowing);
        }

        @Override
        public void trackChanged(final DbTrack newTrack, final de.klierlinge.partydj.common.Track oldTrack, final boolean eventsFollowing)
        {
            // if(!newTrack.getName().equals(oldTrack.getName()) ||
            // newTrack.getDuration() != oldTrack.getDuration())
            {
                trackChangedUpdate(eventsFollowing);
            }
        }

        protected void trackAddedUpdate(final boolean wait)
        {
            if(wait)
            {
                trackChangedUpdateTimer.restart();
            }
            else
            {
                trackChangedUpdateTimer.stop();
                int pos = list.size() - 1;
                if(pos < 0)
                    pos = 0;
                for(final ListDataListener listener : dataListener)
                {
                    try
                    {
                        listener.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, pos, pos));
                    }
                    catch(final Exception e)
                    {
                    	log.error("Failed to notify ListDataListener: " + listener, e);
                    }
                }
            }
        }

        protected void trackDeletedUpdate(final boolean wait)
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
                        listener.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, 0, Math.max(0, list.size() - 1)));
                    }
                    catch(final Exception e)
                    {
                    	log.error("Failed to notify ListDataListener: " + listener, e);
                    }
                }
            }
        }

        protected void trackChangedUpdate(final boolean wait)
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
                    catch(final Exception e)
                    {
                    	log.error("Failed to notify ListDataListener: " + listener, e);
                    }
                }
            }
        }
    }
}
