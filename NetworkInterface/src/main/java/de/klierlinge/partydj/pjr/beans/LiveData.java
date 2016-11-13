package de.klierlinge.partydj.pjr.beans;

public class LiveData extends Message
{
    public final Track track;
    public final boolean playing;
    public final double position;

    public LiveData()
    {
		this.track = null;
		this.playing = false;
		this.position = 0;
    }
    
    public LiveData(Track track, boolean playing, double position)
	{
		this.track = track;
		this.playing = playing;
		this.position = position;
	}

	@Override
    public MessageType getType()
    {
        return MessageType.LiveData;
    }
}
