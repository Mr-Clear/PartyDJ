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
	Track requestNextTrack();
	
	/**Der Player den previous-Befehl und will das vorherige lied Spielen.
	 * 
	 * @return Dateiname des n�chsten Liedes.
	 */
	Track requestPreviousTrack();
	
	/**Der aktuelle Track hat sich ge�ndert
	 * 
	 * @param track Der jetzt aktuelle Track.
	 */
	void trackChanged(Track track);
	
	/**Der Player �ndert seinen Status.
	 * 
	 * @param Status neuer Status.
	 */
	void stateChanged(boolean Status);
	
	/**Der Player hat einen Fehler abgefangen, bei dem Versuch das n�chste Lied zu spielen.
	 * 
	 * @param e Ausl�sende Exception.
	 */
	void proceedError(PlayerException e);
	
	/**Der Player ist fertig mit dem abspielen.*/
	void playCompleted();
}
