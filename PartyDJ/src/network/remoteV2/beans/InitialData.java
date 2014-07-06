package network.remoteV2.beans;

import flexjson.JSON;

import java.util.List;
import java.util.Map;

/** Contains all current data of the PDJ. */
public class InitialData extends Message
{
    public final Map<String, String> settings;
    public final List<Track> tracks;

    public InitialData()
    {
        this.settings = null;
        this.tracks = null;
    }

    public InitialData(Map<String, String> settings, List<Track> tracks)
    {
        this.settings = settings;
        this.tracks = tracks;
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

    @Override
    public Child getType()
    {
        return Child.InitialData;
    }

}
