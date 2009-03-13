package basics;

import common.Track;

/**Empfängt Änderungen des Abspielzustandes. 
 * 
 * @author Eraser
 */
public interface PlayStateListener
{
	/**Der aktuell gespielte Track wurde geändert.
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
}
