package players;

import common.Track;
import basics.PlayerContact;

/**
 * Schnittstelle zu einem Player, der die Tracks wiedergibt.
 * 
 * @author Eraser
 */
public interface IPlayer
{
	/** Lädt den angegebenen Track, ohne ihn abzuspielen 
	 * @param track Track der geladen wird.
	 * @throws PlayerException */
	void load(Track track) throws PlayerException;
	/** Spielt das aktuelle Lied von Anfang an.*/
	void start();
	/** Spielt den angegebenen Track von Anfang an.
	 * @param track Track der gestartet wird.
	 * @throws PlayerException */
	void start(Track track) throws PlayerException;
	/** Stopt den Player und spult zum Anfang des Liedes.*/
	void stop();
	/** Startet den player an der aktuellen Position.*/
	void play();
	/** Spielt den nächsten Track.*/
	void playNext();
	/** Spielt den vorherigen Track.*/
	void playPrevious();
	/** Stopt den Player.*/
	void pause();
	/** Führt je nach Zustand Stop() oder Play() aus.*/
	void playPause();
	/** Blendet an der aktuellen Position langsam ein.*/
	void fadeIn();
	/** Blendet an der aktuellen Position langsam aus.*/
	void fadeOut();
	/** Führt je nach Zustand FadeIn() oder FadeOut() aus.*/
	void fadeInOut();
	/** Gibt sämtliche Ressourcen frei */
	void dispose();
	
	/** Gibt die Dauer des aktuellen Tracks zurück.
	 * @return Dauer des aktuellen Tracks in Sekunden.*/
	double getDuration();
	/** Gibt die Dauer des angegebenen Tracks zurück.
	 * @param track Track dessen Dauer ermittelt werden soll.
	 * @return Dauer des angegebenen Tracks in Sekunden.
	 * @throws PlayerException */
	double getDuration(Track track) throws PlayerException;
	/** Gibt die Dauer des Tracks in der angegebenen Datei zurück.
	 * @param filePath Dateipfad.
	 * @return Dauer des Tracks in der angegebenen Datei in Sekunden.
	 * @throws PlayerException */
	double getDuration(String filePath) throws PlayerException;
	/** Gibt den aktuellen Track zurück.
	 * @return Aktueller Track.*/
	Track getCurrentTrack();
	/** Gibt die aktuelle Position im Track zurück.
	 * @return aktuelle Position im Track in Sekunden.*/
	double getPosition();
	/** Gibt zurück, ob der Player läuft.
	 * @return True, wenn der Player läuft.*/
	boolean getPlayState();
	/** Gibt die Lautstärke zurück.
	 * @return Aktuelle Lautstärke in Prozent,*/
	int getVolume();
	
	/** Setzt die Position im Track suf die angegebene Position.
	 * @param Seconds Neue Position in Sekunden.
	 */
	void setPosition(double Seconds);
	/** Setzt die Lautstärke des Players.
	 *  @param Volume Lautstärke in Prozent.
	 */
	void setVolume(int Volume);
	
	/** Setzt den Contact.
	 * 
	 *  Der Contact empfängt die Ereignisse, die der Player auslöst. 
	 * 
	 *  @param Contact Ansprechpartner
	 */
	void setContact(PlayerContact Contact);
	
	void addPlayStateListener(PlayStateListener listener);
	
	void removePlayStateListener(PlayStateListener listener);
}
