package players;

import common.Track;

public abstract class PlayStateAdapter implements PlayStateListener
{
	public void currentTrackChanged(Track playedLast, Track playingCurrent, Reason reason){}
	public void playStateChanged(boolean playState){}
	public void volumeChanged(int volume){}
}
