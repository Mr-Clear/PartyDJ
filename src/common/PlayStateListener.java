package common;

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
}
