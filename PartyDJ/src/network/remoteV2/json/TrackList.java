package network.remoteV2.json;

public class TrackList implements Message
{
	public TrackList(String name, int[] tracks)
	{
		this.name = name;
		this.tracks = tracks;
	}
	
	/* Needed for Jackson. */
	@SuppressWarnings("unused")
	private TrackList()
	{
		name = null;
		tracks = null;
	}
	
	private final String name;
	private final int[] tracks;
	
	public String getName()
	{
		return name;
	}

	public int[] getTracks()
	{
		return tracks;
	}
}
