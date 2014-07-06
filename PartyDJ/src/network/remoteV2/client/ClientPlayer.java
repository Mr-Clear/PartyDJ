package network.remoteV2.client;

import basics.PlayerContact;

import common.Track;

import players.IPlayer;
import players.PlayStateListener;
import players.PlayerException;

import java.io.File;

public class ClientPlayer implements IPlayer
{

    @Override
    public void load(Track track) throws PlayerException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void start()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void start(Track track) throws PlayerException
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
    public boolean checkPlayable(Track track)
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
    public double getDuration(Track track) throws PlayerException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getDuration(String filePath) throws PlayerException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getDuration(File file) throws PlayerException
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
    public void setPosition(double seconds)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setPosition(double seconds, boolean autoPlay)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setVolume(int volume)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setContact(PlayerContact contact)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void addPlayStateListener(PlayStateListener listener)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void removePlayStateListener(PlayStateListener listener)
    {
        // TODO Auto-generated method stub

    }

}
