package lists.data;

import basics.Controller;
import common.Track;
import data.IData;
import data.ListAdapter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import lists.BasicListModel;
import lists.EditableListModel;
import lists.ListException;

/**
 * Einfache konkrete Implementierung von BasicListModel.
 * <p>
 * Der Inhalt wird nicht persistent gespeichert. Er geht nach beenden des
 * PartyDJ verloren.
 *
 * @author Eraser
 *
 * @see BasicListModel
 * @see EditableListModel
 */
public class LightClientListModel extends BasicListModel implements EditableListModel
{
    protected static final IData data = Controller.getInstance().getData();
    protected String name;

    public LightClientListModel(final String name)
    {
        super(new ArrayList<Track>());
        this.name = name;
        Controller.getInstance().getData().addListListener(new TracksListener());
    }

    public LightClientListModel(final String name, final List<? extends DbTrack> list)
    {
        super(new ArrayList<Track>(list));
        this.name = name;
        Controller.getInstance().getData().addListListener(new TracksListener());
    }

//    /**
//     * Fügt einen Track zu der Liste hinzu.
//     *
//     * @param track
//     * @param eventsFollowing
//     *            Gibt an ob weitere, gleichartige Ereignisse folgen werden.
//     * @throws ListException
//     *             Kann von abgeleiteten Klasse geworfen werden.
//     */
//    public void add(final DbTrack track, final boolean eventsFollowing) throws ListException
//    {
//        if(track != null)
//            list.add(track);
//        for(final ListDataListener listener : dataListener)
//        {
//            try
//            {
//                listener.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, list.size(), list.size()));
//            }
//            catch(Exception e)
//            {
//                controller.logError(Controller.NORMAL_ERROR, listener, e, "Fehler in Plugin");
//            }
//        }
//    }

    @Override
    public void add(final Track track, final boolean eventsFollowing) throws ListException
    {
        if(track != null)
        {
//            if(track instanceof DbTrack)
//                add((DbTrack)track, eventsFollowing);
//            else
                data.addTrack(track, eventsFollowing);
        }
        else
            add((DbTrack)null, eventsFollowing);
    }

    @Override
    public void add(final int index, final Track track, final boolean eventsFollowing) throws ListException
    {
        if(track instanceof DbTrack)
            add(index, (DbTrack)track, eventsFollowing);
        else
            data.addTrack(track, eventsFollowing);
    }

    /**
     * @param eventsFollowing
     *            Gibt an ob weitere, gleichartige Ereignisse folgen werden.
     * @throws ListException
     */
    protected void add(final int trackIndex, final boolean eventsFollowing) throws ListException
    {
        list.add(data.getTrack(trackIndex));
    }

    public void add(int index, final DbTrack track, final boolean eventsFollowing) throws ListException
    {
        if(index < 0)
            index = 0;
        if(index >= getSize())
        {
            add(track, eventsFollowing); // Wenn der Index ausserhalb der Liste
                                         // ist, Track am Ende einfügen.
            return;
        }
        if(track != null)
            list.add(index, track);
        for(final ListDataListener listener : dataListener)
        {
            try
            {
                listener.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index));
            }
            catch(final Exception e)
            {
                controller.logError(Controller.NORMAL_ERROR, listener, e, "Fehler in Plugin");
            }
        }
    }

    protected void add(final int index, final int trackIndex, final boolean eventsFollowing) throws ListException
    {
        add(index, data.getTrack(trackIndex), eventsFollowing);
    }

    @Override
    public void remove(final int index, final boolean eventsFollowing) throws ListException
    {
        if(index < 0 || index >= getSize())
            return; // //Wenn der Index außerhalb der Liste ist, nichts machen.
        list.remove(index);
        for(final ListDataListener listener : dataListener)
        {
            try
            {
                listener.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index));
            }
            catch(final Exception e)
            {
                controller.logError(Controller.NORMAL_ERROR, listener, e, "Fehler in Plugin");
            }
        }
    }

    @Override
    public void move(final int oldIndex, final int newIndex, final boolean eventsFollowing) throws ListException
    {
        if(oldIndex < 0 || oldIndex >= getSize() || newIndex < 0 || newIndex > getSize() || oldIndex == newIndex)
            return; // Wenn ein Index außerhalb der Liste ist, oder Indices
                    // gleich, nichts machen.

        int toAdd;
        int toRemove;
        if(oldIndex < newIndex)
        {
            toAdd = newIndex + 1;
            toRemove = oldIndex;
        }
        else
        {
            toAdd = newIndex;
            toRemove = oldIndex + 1;
        }

        synchronized(this)
        {
            add(toAdd, (DbTrack)list.get(oldIndex), eventsFollowing);
            remove(toRemove, eventsFollowing);
        }
    }

    @Override
    public void swap(final int indexA, final int indexB, final boolean eventsFollowing) throws ListException
    {
        if(indexA < 0 || indexA >= getSize() || indexB < 0 || indexB > getSize() || indexA == indexB)
            return; // Wenn ein Index außerhalb der Liste ist, oder Indices
                    // gleich, nichts machen.

        final Track a = list.get(indexA);
        final Track b = list.get(indexB);

        list.set(indexA, b);
        list.set(indexB, a);

        for(final ListDataListener listener : dataListener)
        {
            try
            {
                listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, indexA, indexB));
            }
            catch(final Exception e)
            {
                controller.logError(Controller.NORMAL_ERROR, listener, e, "Fehler in Plugin");
            }
        }

    }

    protected class TracksListener extends ListAdapter
    {
        @Override
        public void trackInserted(final String listName, final int position, final DbTrack track, final boolean eventsFollowing)
        {
            if(name.equals(listName))
            {
                synchronized(list)
                {
                    if(position >= 0)
                        list.add(position, track);

                    if(!eventsFollowing)
                    {
                        synchronized(dataListener)
                        {
                            final int min = position >= 0 ? position : 0;
                            final int max = position >= 0 ? position : list.size() - 1;
                            for(final ListDataListener listener : dataListener)
                            {
                                try
                                {
                                    listener.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, min, max));
                                }
                                catch(final Exception e)
                                {
                                    controller.logError(Controller.NORMAL_ERROR, listener, e, "Fehler in Plugin");
                                }
                            }
                        }
                    }
                }
            }
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
                            try
                            {
                                LightClientListModel.this.remove(i--, eventsFollowing); // Gelöschten
                                                                                        // Index
                                                                                        // zurück
                                                                                        // gehen.
                                if(eventsFollowing)
                                {
                                    synchronized(dataListener)
                                    {
                                        for(final ListDataListener listener : dataListener)
                                        {
                                            try
                                            {
                                                listener.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, track.getIndex(), track
                                                        .getIndex()));
                                            }
                                            catch(final Exception e)
                                            {
                                                controller.logError(Controller.NORMAL_ERROR, listener, e, "Fehler in Plugin");
                                            }
                                        }
                                    }
                                }
                            }
                            catch(final ListException e)
                            {
                                System.err.println("ListException in LightClientListModel.trackDeleted.");
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
}
