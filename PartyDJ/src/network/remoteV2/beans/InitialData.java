package network.remoteV2.beans;

import java.util.List;
import java.util.Map;
import flexjson.JSON;

/** Contains all current data of the PDJ. */
public class InitialData extends Message
{
    public final Map<String, String> settings;
    public final List<Track> tracks;
    public final Map<String, List<Integer>> lists;

    public InitialData()
    {
        this.settings = null;
        this.tracks = null;
        lists = null;
    }

    public InitialData(final Map<String, String> settings, final List<Track> tracks, final Map<String, List<Integer>> lists)
    {
        this.settings = settings;
        this.tracks = tracks;
        this.lists = lists;
    }
    
    @JSON
    public Map<String, String> getSettings()
    {
        return settings;
    }

    @JSON
    public List<Track> getTracks()
    {
        return tracks;
    }

    @JSON
    public Map<String, List<Integer>> getLists()
    {
        return lists;
    }

    @Override
    public Child getType()
    {
        return Child.InitialData;
    }

}
