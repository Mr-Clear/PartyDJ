package network.remoteV2.client;

import common.Track;

import data.IData;
import data.ListListener;
import data.SettingException;
import data.SettingListener;
import data.SortOrder;

import lists.ListException;
import lists.data.DbTrack;
import lists.data.DbTrack.TrackElement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import network.remoteV2.RemoteTrack;
import network.remoteV2.beans.InitialData;
import network.remoteV2.beans.Setting;

public class ClientData implements IData
{
    private final Client client;
    private Map<String, String> settings = new HashMap<>();
    private Set<SettingListener> settingListeners = new HashSet<>();
    private Set<ListListener> listListeners = new HashSet<>();
    private final List<RemoteTrack> tracks = new ArrayList<>();
    
    public ClientData(Client client)
    {
        this.client = client;
    }
    
    void initialDate(InitialData initialData)
    {
        settings.putAll(initialData.settings);
        for(Entry<String, String> entry : initialData.settings.entrySet())
        {
            noitfySettingListeners(entry.getKey(), entry.getValue());
        }
        
        int trackIndex = 0;
        for(network.remoteV2.beans.Track track : initialData.tracks)
        {
            RemoteTrack remoteTrack = new RemoteTrack(this, trackIndex, track);
            tracks.add(remoteTrack);
            notifyTrackAdded(remoteTrack, true);
            trackIndex++;
        }
        notifyTrackAdded(null, false);
    }
    
    void connectionClosed()
    {
        settings.clear();
        for(int i = tracks.size() - 1; i >= 0; i--)
        {
            RemoteTrack track = tracks.get(i);
            tracks.remove(i);
            synchronized(listListeners)
            {
                for(ListListener listener : listListeners)
                {
                    listener.trackDeleted(track, true);
                }
            }
        }
        tracks.clear();
        synchronized(listListeners)
        {
            for(ListListener listener : listListeners)
            {
                listener.trackDeleted(null, false);
            }
        }
        
        // TODO: Notify listeners
    }

    @Override
    public void writeSetting(String name, String value) throws SettingException
    {
        if(value != settings.get(name))
            try
            {
                client.send(new Setting(name, value));
            }
            catch(IOException e)
            {
                throw new SettingException(e);
            }
    }
    
    @Override
    public String readSetting(String name) throws SettingException
    {
        return readSetting(name, null);
    }

    @Override
    public String readSetting(String name, String defaultValue) throws SettingException
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
    public void addSettingListener(SettingListener listener)
    {
        synchronized(settingListeners)
        {
            settingListeners.add(listener);
        }
    }

    @Override
    public void removeSettingListener(SettingListener listener)
    {
        synchronized(settingListeners)
        {
            settingListeners.remove(listener);
        }
    }
    
    void updateSetting(Setting setting)
    {
        settings.put(setting.name, setting.value);
        noitfySettingListeners(setting.name, setting.value);
    }
    
    private void noitfySettingListeners(String name, String value)
    {
        synchronized(settingListeners)
        {
            for(SettingListener listener : settingListeners)
            {
                listener.settingChanged(name, value);
            }
        }
    }

    @Override
    public List<DbTrack> readList(String listName, String searchString, SortOrder order) throws ListException
    {
        // TODO Auto-generated method stub
        return new ArrayList<>();
    }

    @Override
    public boolean isInDb(String trackPath) throws ListException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public DbTrack addTrack(Track track, boolean eventsFollowing) throws ListException
    {
        // TODO Auto-generated method stub
        return new DbTrack(this, track)
        {
            private static final long serialVersionUID = 1L;
        };
    }

    @Override
    public void updateTrack(DbTrack track, TrackElement element, boolean eventsFollowing) throws ListException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteTrack(DbTrack track, boolean eventsFollowing) throws ListException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public DbTrack getTrack(String path, boolean autoCreate) throws ListException
    {
        // TODO Auto-generated method stub
        if(autoCreate)
            return new DbTrack(this, new Track(path, false))
            {
                private static final long serialVersionUID = 1L;
            };
        return null;
    }

    @Override
    public DbTrack getTrack(int index)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addListListener(ListListener listener)
    {
        synchronized(listListeners)
        {
            listListeners.add(listener);
        }
    }

    @Override
    public void removeListListener(ListListener listener)
    {
        synchronized(listListeners)
        {
            listListeners.add(listener);
        }
    }
    
    private void notifyTrackAdded(DbTrack track, boolean eventsFollowing)
    {
        synchronized(listListeners)
        {
            for(ListListener listener : listListeners)
            {
                listener.trackAdded(track, eventsFollowing);
            }
        }
    }

    @Override
    public void addList(String listName) throws ListException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void addList(String listName, String description) throws ListException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeList(String listName) throws ListException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public int getListPriority(String listName) throws ListException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setListPriority(String listName, int priority) throws ListException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public String getListDescription(String listName) throws ListException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setListDescription(String listName, String description) throws ListException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void renameList(String oldName, String newName) throws ListException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public List<String> getLists() throws ListException
    {
        // TODO Auto-generated method stub
        return new ArrayList<>();
    }

    @Override
    public void insertTrack(String listName, DbTrack track, boolean eventsFollowing) throws ListException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void insertTrackAt(String listName, DbTrack track, int trackPosition, boolean eventsFollowing) throws ListException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeTrack(String listName, int trackPosition, boolean eventsFollowing) throws ListException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void swapTrack(String listName, int positionA, int positionB, boolean eventsFollowing) throws ListException
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
