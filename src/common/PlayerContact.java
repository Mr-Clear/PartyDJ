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
	String RequestNextTrack();
	
	/**Der Player den previous-Befehl und will das vorherige lied Spielen.
	 * 
	 * @return Dateiname des nächsten Liedes.
	 */
	String RequestPreviousTrack();
	
	/**Der Player ändert seinen Status.
	 * 
	 * @param Status neuer Status.
	 */
	void StateChanged(boolean Status);
	
	/**Der Player hat einen Fehler abgefangen, bei dem Versuch das nächste Lied zu spielen.
	 * 
	 * @param e Auslösende Exception.
	 */
	void ProceedError(PlayerException e);
	
	/**Der Player ist fertig mit dem abspielen.*/
	void PlayCompleted();
}
