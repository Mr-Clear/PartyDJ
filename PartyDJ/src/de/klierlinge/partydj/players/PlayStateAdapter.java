package de.klierlinge.partydj.players;

import de.klierlinge.partydj.common.Track;

/**
 * Empfängt Änderungen des Abspielzustandes.
 * <br>Alle Funktionen sind mit leeren Rümpfen implementiert.
 * 
 * @author Eraser
 */
public abstract class PlayStateAdapter implements PlayStateListener
{
	@Override
	public void currentTrackChanged(final Track playedLast, final Track playingCurrent, final Reason reason) { /* Empty default implementation. */ }
	@Override
	public void playStateChanged(final boolean playState) { /* Empty default implementation. */ }
	@Override
	public void volumeChanged(final int volume) { /* Empty default implementation. */ }
}
