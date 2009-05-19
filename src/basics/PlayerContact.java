package basics;

import players.PlayerException;
import common.Track;

/**
 * Ermöglicht es dem Player Daten an den PartyDJ zu senden.
 * 
 * @author Eraser
 *
 */
public interface PlayerContact
{
	/**Der Player ist am Ende eines Tracks, oder bekommt den next-Befehl und will den nächsten Track spielen.
	 * Der Aufruf teilt dem Contact auch mit, dass der nächste Track gespielt wird.
	 * 
	 * @return nächster Track.
	 */
	Track requestNextTrack();
	
	/**Der Player möchte wissen welchen Track er als nächstes spielen soll, um ihn im vorraus zu laden.
	 * 
	 * @return nächster Track.
	 */
	Track predictNextTrack();
	
	/**Der Player hat den previous-Befehl bekommen und den vorherigen Track spielen.
	 * Der Aufruf teilt dem Contact auch mit, dass der letzte Track gespielt wird.
	 * 
	 * @return letzter Track.
	 */
	Track requestPreviousTrack();

	
	/**Der Player ist fertig mit dem abspielen.*/
	void playCompleted();
	
	/**Der Player kann einen Track nicht abspielen.
	 * 
	 * @param e Auslösende Exception.
	 * @param track Betreffender Track.
	 */
	void reportProblem(PlayerException e, Track track);
	
	/**Der Player hat die Dauer eines Liedes berechnet.
	 * 
	 * @param track Track dessen Dauer berechnet wurde.
	 * @param duration Dauer des Tracks.
	 */
	void trackDurationCalculated(Track track, double duration);
}
