package de.klierlinge.partydj.network.remoteV2.client;

import java.io.File;
import de.klierlinge.partydj.basics.PlayerContact;
import de.klierlinge.partydj.common.Track;
import de.klierlinge.partydj.players.IPlayer;
import de.klierlinge.partydj.players.PlayStateListener;
import de.klierlinge.partydj.players.PlayerException;

public class ClientPlayer implements IPlayer
{

    @Override
    public void load(final Track track) throws PlayerException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void start()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void start(final Track track) throws PlayerException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void stop()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void play()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void playNext()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void playPrevious()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void pause()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void playPause()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void fadeIn()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void fadeOut()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void fadeInOut()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean checkPlayable(final Track track)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public double getDuration()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getDuration(final Track track) throws PlayerException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getDuration(final String filePath) throws PlayerException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getDuration(final File file) throws PlayerException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Track getCurrentTrack()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double getPosition()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean getPlayState()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getVolume()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setPosition(final double seconds)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setPosition(final double seconds, final boolean autoPlay)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setVolume(final int volume)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setContact(final PlayerContact contact)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void addPlayStateListener(final PlayStateListener listener)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void removePlayStateListener(final PlayStateListener listener)
    {
        // TODO Auto-generated method stub

    }

}
