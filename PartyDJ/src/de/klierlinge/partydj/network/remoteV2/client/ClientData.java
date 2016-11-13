package de.klierlinge.partydj.network.remoteV2.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import de.klierlinge.partydj.common.Track;
import de.klierlinge.partydj.data.IData;
import de.klierlinge.partydj.data.ListListener;
import de.klierlinge.partydj.data.SettingException;
import de.klierlinge.partydj.data.SettingListener;
import de.klierlinge.partydj.data.SortOrder;
import de.klierlinge.partydj.lists.ListException;
import de.klierlinge.partydj.lists.data.DbTrack;
import de.klierlinge.partydj.lists.data.DbTrack.TrackElement;
import de.klierlinge.partydj.network.remoteV2.beans.InitialData;
import de.klierlinge.partydj.network.remoteV2.beans.Setting;
import java.util.Set;

public class ClientData implements IData
{
    private final Client client;
    private final Map<String, String> settings = new HashMap<>();
    private final Set<SettingListener> settingListeners = new HashSet<>();
    private final Set<ListListener> listListeners = new HashSet<>();
    private final List<RemoteTrack> tracks = new ArrayList<>();
    private final Map<String, RemoteTrack> trackMap = new HashMap<>();
    private final Map<String, List<RemoteTrack>> lists = new HashMap<>();
    
    public ClientData(final Client client)
    {
        this.client = client;
    }
    
    void initialData(final InitialData initialData)
    {
        settings.putAll(initialData.settings);
        for(final Entry<String, String> entry : initialData.settings.entrySet())
        {
            noitfySettingListeners(entry.getKey(), entry.getValue());
        }
        
        int trackIndex = 0;
        for(final de.klierlinge.partydj.network.remoteV2.beans.Track track : initialData.tracks)
        {
            final RemoteTrack remoteTrack = new RemoteTrack(this, trackIndex, track);
            tracks.add(remoteTrack);
            trackMap.put(remoteTrack.getPath(), remoteTrack);
            notifyTrackAdded(remoteTrack, true);
            trackIndex++;
        }
        notifyTrackAdded(null, false);
        
        System.out.println(initialData.lists.size());
        for(final Entry<String, List<Integer>> list : initialData.lists.entrySet())
        {
            System.out.println(list.getKey());
            final List<RemoteTrack> newList = new ArrayList<>(list.getValue().size());
            lists.put(list.getKey(), newList);
            synchronized(listListeners)
            {
                for(final ListListener listener : listListeners)
                {
                    listener.listAdded(list.getKey());
                }
            }
            
            for(final int trackId : list.getValue())
            {
                newList.add(tracks.get(trackId));
                System.out.println(list.getKey() + ": " + tracks.get(trackId));
                synchronized(listListeners)
                {
                    for(final ListListener listener : listListeners)
                    {
                        listener.trackInserted(list.getKey(), trackId, tracks.get(trackId), true);
                    }
                }
            }
            synchronized(listListeners)
            {
                for(final ListListener listener : listListeners)
                {
                    listener.trackInserted(null, 0, null, false);
                }
            }
        }
    }
    
    void connectionClosed()
    {
        settings.clear();
        for(int i = tracks.size() - 1; i >= 0; i--)
        {
            final RemoteTrack track = tracks.get(i);
            tracks.remove(i);
            synchronized(listListeners)
            {
                for(final ListListener listener : listListeners)
                {
                    listener.trackDeleted(track, true);
                }
            }
        }
        tracks.clear();
        trackMap.clear();
        synchronized(listListeners)
        {
            for(final ListListener listener : listListeners)
            {
                listener.trackDeleted(null, false);
            }
        }
        

        for(final Entry<String, List<RemoteTrack>> list : lists.entrySet())
        {
            for(int i = list.getValue().size() - 1; i >= 0; i--)
                synchronized(listListeners)
                {
                    for(final ListListener listener : listListeners)
                    {
                        listener.trackRemoved(list.getKey(), i, true);
                    }
                }

            synchronized(listListeners)
            {
                for(final ListListener listener : listListeners)
                {
                    listener.trackRemoved(null, 0, false);
                    listener.listRemoved(list.getKey());
                }
            }
        }
        lists.clear();
    }

    @Override
    public void writeSetting(final String name, final String value) throws SettingException
    {
        if(value != settings.get(name))
            try
            {
                client.send(new Setting(name, value));
            }
            catch(final IOException e)
            {
                throw new SettingException(e);
            }
    }
    
    @Override
    public String readSetting(final String name) throws SettingException
    {
        return readSetting(name, null);
    }

    @Override
    public String readSetting(final String name, final String defaultValue) throws SettingException
    {
        if(settings.containsKey(name))
            return settings.get(name);
        return defaultValue;
    }
    
    @Override
    public Map<String, String> readAllSettings() throws SettingException
    {
        return Collections.unmodifiableMap(settings);
    }

    @Override
    public void addSettingListener(final SettingListener listener)
    {
        synchronized(settingListeners)
        {
            settingListeners.add(listener);
        }
    }

    @Override
    public void removeSettingListener(final SettingListener listener)
    {
        synchronized(settingListeners)
        {
            settingListeners.remove(listener);
        }
    }
    
    void updateSetting(final Setting setting)
    {
        settings.put(setting.name, setting.value);
        noitfySettingListeners(setting.name, setting.value);
    }
    
    private void noitfySettingListeners(final String name, final String value)
    {
        synchronized(settingListeners)
        {
            for(final SettingListener listener : settingListeners)
            {
                listener.settingChanged(name, value);
            }
        }
    }

    @Override
    public List<? extends DbTrack> readList(final String listName, final String searchString, final SortOrder order) throws ListException
    {
        if(!lists.containsKey(listName))
            return new ArrayList<>();
        return Collections.unmodifiableList(lists.get(listName));
    }

    @Override
    public boolean isInDb(final String trackPath) throws ListException
    {
        return trackMap.containsKey(trackPath);
    }

    @Override
    public DbTrack addTrack(final Track track, final boolean eventsFollowing) throws ListException
    {
        // TODO Auto-generated method stub
        return new DbTrack(this, track)
        {
            private static final long serialVersionUID = 1L;
        };
    }

    @Override
    public void updateTrack(final DbTrack track, final TrackElement element, final boolean eventsFollowing) throws ListException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteTrack(final DbTrack track, final boolean eventsFollowing) throws ListException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public DbTrack getTrack(final String path, final boolean autoCreate) throws ListException
    {
        if(trackMap.containsKey(path))
            return trackMap.get(path);
        if(autoCreate)
            return new DbTrack(this, new Track(path, false))
            {
                private static final long serialVersionUID = 1L;
            };
        return null;
    }

    @Override
    public DbTrack getTrack(final int index)
    {
        return tracks.get(index);
    }

    @Override
    public void addListListener(final ListListener listener)
    {
        synchronized(listListeners)
        {
            listListeners.add(listener);
        }
    }

    @Override
    public void removeListListener(final ListListener listener)
    {
        synchronized(listListeners)
        {
            listListeners.add(listener);
        }
    }
    
    private void notifyTrackAdded(final DbTrack track, final boolean eventsFollowing)
    {
        synchronized(listListeners)
        {
            for(final ListListener listener : listListeners)
            {
                listener.trackAdded(track, eventsFollowing);
            }
        }
    }

    @Override
    public void addList(final String listName) throws ListException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void addList(final String listName, final String description) throws ListException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeList(final String listName) throws ListException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public int getListPriority(final String listName) throws ListException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setListPriority(final String listName, final int priority) throws ListException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public String getListDescription(final String listName) throws ListException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setListDescription(final String listName, final String description) throws ListException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void renameList(final String oldName, final String newName) throws ListException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public List<String> getLists() throws ListException
    {
        return new ArrayList<>(lists.keySet());
    }

    @Override
    public void insertTrack(final String listName, final DbTrack track, final boolean eventsFollowing) throws ListException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void insertTrackAt(final String listName, final DbTrack track, final int trackPosition, final boolean eventsFollowing) throws ListException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeTrack(final String listName, final int trackPosition, final boolean eventsFollowing) throws ListException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void swapTrack(final String listName, final int positionA, final int positionB, final boolean eventsFollowing) throws ListException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public String getDbPath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void close() throws ListException
    {
        // TODO Auto-generated method stub

    }
}
