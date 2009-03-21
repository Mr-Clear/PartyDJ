package players;

import common.Track;
import basics.PlayerContact;

public interface IPlayer
{
	/** L�dt den angegebenen Track, ohne ihn abzuspielen */
	void load(Track track);
	/** Spielt das aktuelle Lied von Anfang an.*/
	void start();
	/** Spielt den angegebenen Track von Anfang an.*/
	void start(Track track);
	/** Stopt den Player und spult zum Anfang des Liedes.*/
	void stop();
	/** Startet den player an der aktuellen Position.*/
	void play();
	/** Spielt den n�chsten Track.*/
	void playNext();
	/** Spielt den vorherigen Track.*/
	void playPrevious();
	/** Stopt den Player.*/
	void pause();
	/** F�hrt je nach Zustand Stop() oder Play() aus.*/
	void playPause();
	/** Blendet an der aktuellen Position langsam ein.*/
	void fadeIn();
	/** Blendet an der aktuellen Position langsam aus.*/
	void fadeOut();
	/** F�hrt je nach Zustand FadeIn() oder FadeOut() aus.*/
	void fadeInOut();
	/** Gibt s�mtliche Ressourcen frei */
	void dispose();
	
	/** Gibt die Dauer des aktuellen Tracks in Sekunden zur�ck.*/
	double getDuration();
	/** Gibt die Dauer des angegebenen Tracks in Sekunden zur�ck.
	 * @throws PlayerException */
	double getDuration(Track track) throws PlayerException;
	/** Gibt die Dauer des Tracks in der angegebenen Datei in Sekunden zur�ck.
	 * @throws PlayerException */
	double getDuration(String filePath) throws PlayerException;
	/** Gibt den aktuellen Track zur�ck.*/
	Track getCurrentTrack();
	/** Gibt die aktuelle Position im Track in Sekunden zur�ck.*/
	double getPosition();
	/** Gibt true zur�ck wenn der Player l�uft.*/
	boolean getPlayState();
	/** Gibt die Lautst�rke in Prozent zur�ck.*/
	int getVolume();
	
	/** Setzt die Position im Track suf die angegebene Position.
	 * @param Seconds Neue Position in Sekunden.
	 */
	void setPosition(double Seconds);
	/** Setzt die Lautst�rke des Players.
	 *  @param Volume Lautst�rke in Prozent.
	 */
	void setVolume(int Volume);
	
	/** Setzt den Contact.
	 * 
	 *  Der Contact empf�ngt die Ereignisse, die der Player ausl�st. 
	 * 
	 *  @param Contact Ansprechpartner
	 */
	void setContact(PlayerContact Contact);
	
	void addPlayStateListener(PlayStateListener listener);
	
	void removePlayStateListener(PlayStateListener listener);
}
