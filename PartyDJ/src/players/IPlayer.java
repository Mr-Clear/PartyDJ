package players;

import basics.PlayerContact;
import common.Track;
import java.io.File;

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
	
	/**Ermittelt ob ein Track abgespielt werden kann. 
	 * @param track Track der überprüft wird.
	 * @return True, wenn der Track abgespielt werden kann.*/
	boolean checkPlayable(Track track);
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
	/** Gibt die Dauer des Tracks in der angegebenen Datei zurück.
	 * @param file Datei.
	 * @return Dauer des Tracks in der angegebenen Datei in Sekunden.
	 * @throws PlayerException */
	double getDuration(File file) throws PlayerException;
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
	
	/** Setzt die Position im Track auf die angegebene Position.
	 * @param seconds neue Position in Sekunden.
	 */
	void setPosition(double seconds);

	/**
	 * Setzt die Position im Track suf die angegebene Position.
	 * @param seconds Seconds neue Position in Sekunden.
	 * @param autoPlay	Ob nach setzen der Zeit automatisch abgespielt werden soll oder nicht.
	 */
	void setPosition(double seconds, boolean autoPlay);
	
	/** Setzt die Lautstärke des Players.
	 *  @param volume Lautstärke in Prozent.
	 */
	void setVolume(int volume);
	
	/** Setzt den Contact.
	 * 
	 *  Der Contact empfängt die Ereignisse, die der Player auslöst. 
	 * 
	 *  @param contact Ansprechpartner
	 */
	void setContact(PlayerContact contact);
	
	void addPlayStateListener(PlayStateListener listener);
	
	void removePlayStateListener(PlayStateListener listener);
}
