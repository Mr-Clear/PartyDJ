package common;

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
}
