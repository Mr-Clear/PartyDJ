package players;

import common.Track;

/**
 * Empf�ngt �nderungen des Abspielzustandes.
 * <br>Alle Funktionen sind mit leeren R�mpfen implementiert.
 * 
 * @author Eraser
 */
public abstract class PlayStateAdapter implements PlayStateListener
{
	public void currentTrackChanged(Track playedLast, Track playingCurrent, Reason reason){}
	public void playStateChanged(boolean playState){}
	public void volumeChanged(int volume){}
}
