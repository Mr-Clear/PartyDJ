package common;

/**Ermöglicht es dem Player Daten an den PartyDJ zu senden.
 * 
 * @author Eraser
 *
 */
public interface PlayerContact
{
	/**Der Player ist am Ende eines Liedes, oder bekommt den next-Befehl und will das nächste lied Spielen.
	 * 
	 * @return Dateiname des nächsten Liedes.
	 */
	Track requestNextTrack();
	
	/**Der Player den previous-Befehl und will das vorherige lied Spielen.
	 * 
	 * @return Dateiname des nächsten Liedes.
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
}
