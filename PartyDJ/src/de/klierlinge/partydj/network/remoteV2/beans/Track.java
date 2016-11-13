package de.klierlinge.partydj.network.remoteV2.beans;

import de.klierlinge.partydj.common.Track.Problem;

public class Track extends Message
{
    public final String name;
    public final String info;
    public final double duration;
    public final long size;
    public final Problem problem;
    
    public Track()
    {
        this.name = null;
        this.info = null;
        this.duration = 0;
        this.size = 0;
        this.problem = null;
    }

    public Track(final de.klierlinge.partydj.common.Track track)
    {
        this.name = track.getName();
        this.info = track.getInfo();
        this.duration = track.getDuration();
        this.size = track.getSize();
        this.problem = track.getProblem();
    }

    @Override
    public Child getType()
    {
        return Child.Track;
    }
}
