package de.klierlinge.partydj.pjr.beans;

public class Track extends Message
{
    public final String name;
    public final String info;
    public final double duration;
    public final long size;
    public final boolean problem;
    
    public Track()
	{
		this.name = null;
		this.info = null;
		this.duration = 0;
		this.size = 0;
		this.problem = true;
	}

	public Track(String name, String info, double duration, long size, boolean problem)
	{
		this.name = name;
		this.info = info;
		this.duration = duration;
		this.size = size;
		this.problem = problem;
	}
    
	@Override
    public MessageType getType()
    {
        return MessageType.Track;
    }
}
