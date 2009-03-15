package players;

import common.Track;

/**Empf�ngt �nderungen des Abspielzustandes. 
 * 
 * @author Eraser
 */
public interface PlayStateListener
{
	/**Der aktuell gespielte Track wurde ge�ndert.
	 * 
	 * @param playedLast Zuletzt gespielter Track.
	 * @param playingCurrent Track der jetzt gespielt wird.
	 */
	void currentTrackChanged (Track playedLast, Track playingCurrent);
	
	
	/**Der Player wurde gestartet oder gestoppt.
	 * 
	 * @param playState Spielt noch oder nicht.
	 */
	void playStateChanged (boolean playState);
	
	/**Die Lautst�rke wurde ge�ndert.
	 * 
	 * @param volume Neue Lautst�rke zwischen 0 und 100.
	 */
	void volumeChanged (int volume);
}