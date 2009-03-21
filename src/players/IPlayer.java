package players;

import common.Track;
import basics.PlayerContact;

public interface IPlayer
{
	/** Lädt den angegebenen Track, ohne ihn abzuspielen */
	void load(Track track);
	/** Spielt das aktuelle Lied von Anfang an.*/
	void start();
	/** Spielt den angegebenen Track von Anfang an.*/
	void start(Track track);
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
	
	/** Gibt die Dauer des aktuellen Tracks in Sekunden zurück.*/
	double getDuration();
	/** Gibt die Dauer des angegebenen Tracks in Sekunden zurück.
	 * @throws PlayerException */
	double getDuration(Track track) throws PlayerException;
	/** Gibt die Dauer des Tracks in der angegebenen Datei in Sekunden zurück.
	 * @throws PlayerException */
	double getDuration(String filePath) throws PlayerException;
	/** Gibt den aktuellen Track zurück.*/
	Track getCurrentTrack();
	/** Gibt die aktuelle Position im Track in Sekunden zurück.*/
	double getPosition();
	/** Gibt true zurück wenn der Player läuft.*/
	boolean getPlayState();
	/** Gibt die Lautstärke in Prozent zurück.*/
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
