package de.klierlinge.partydj.pjr.beans;

import flexjson.JSON;

public class TrackList extends Message
{
	public final String name;
	public final int[] tracks;
	
	public TrackList(final String name, final int[] tracks)
	{
		this.name = name;
		this.tracks = tracks;
	}
	
	/* Required for flexjson. */
	public TrackList()
	{
		name = null;
		tracks = null;
	}

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
	public MessageType getType()
	{
		return Message.MessageType.TrackList;
	}
}
