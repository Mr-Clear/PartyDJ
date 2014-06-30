package network.remoteV2.json;

import flexjson.JSON;

public class TrackList implements Message
{
	public TrackList(String name, int[] tracks)
	{
		this.name = name;
		this.tracks = tracks;
	}
	
	/* Needed for flexjson. */
	public TrackList()
	{
		name = null;
		tracks = null;
	}
	
	public final String name;
	public final int[] tracks;

	@JSON
	public String getName()
	{
		return name;
	}
	
	@JSON
	public int[] getTracks()
	{
		return tracks;
	}
	
	@Override
	public String toString()
	{
		return name + " (" + tracks.length + ")";
	}

	@Override
	public String getType()
	{
		return "TrackList";
	}
}
