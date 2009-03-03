package common;

/**Erm�glicht es dem Player Daten an den PartyDJ zu senden.
 * 
 * @author Eraser
 *
 */
public interface PlayerContact
{
	/**Der Player ist am Ende eines Liedes, oder bekommt den next-Befehl und will das n�chste lied Spielen.
	 * 
	 * @return Dateiname des n�chsten Liedes.
	 */
	String RequestNextTrack();
	
	/**Der Player den previous-Befehl und will das vorherige lied Spielen.
	 * 
	 * @return Dateiname des n�chsten Liedes.
	 */
	String RequestPreviousTrack();
	
	/**Der Player �ndert seinen Status.
	 * 
	 * @param Status neuer Status.
	 */
	void StateChanged(boolean Status);
	
	/**Der Player hat einen Fehler abgefangen, bei dem Versuch das n�chste Lied zu spielen.
	 * 
	 * @param e Ausl�sende Exception.
	 */
	void ProceedError(PlayerException e);
	
	/**Der Player ist fertig mit dem abspielen.*/
	void PlayCompleted();
}
