package data;

import common.Track;
import java.util.List;
import java.util.Map;
import lists.ListException;
import lists.data.DbTrack;

/**
 * Schnittstelle zur Datenbank-Anbindung.
 * <p>Ermöglicht Zugriff auf Einstellungen und Listen.
 * 
 * @author Eraser
 */
public interface IData
{
	/**Speichert eine Einstellung.
	 * 
	 * @param name Name der Einstellung.
	 * @param value Wert der Einstellung.
	 * @throws SettingException
	 */
	void writeSetting(String name, String value) throws SettingException;
	/**Liest eine Einstellung.
	 * Gibt null zurück, wenn die Einstellung nicht vorhanden ist.
	 * 
	 * @param name Name der Einstellung.
	 * @return Wert der Einstellung.
	 * @throws SettingException
	 */
	String readSetting(String name) throws SettingException;
	/**Liest eine Einstellung.
	 * 
	 * @param name Name der Einstellung.
	 * @param defaultValue Standardwert, der zurückgegeben wird, wenn Einstellung nicht vorhanden ist.
	 * @return Wert der Einstellung.
	 * @throws SettingException
	 */
	String readSetting(String name, String defaultValue) throws SettingException;
	
	/**Gibt alle gespeicherten Einstellungen zurück.
	 * 
	 * @return Map mit allen gespeicherten Einstellungen.
	 * @throws SettingException 
	 */
	Map<String, String> readAllSettings() throws SettingException;
	
	/**Fügt einen SettingListener hinzu, der Änderungen an den Einstellungen empfängt.
	 * 
	 * @param listener Der SettingListener der hinzugefügt wird.
	 */
	void addSettingListener(SettingListener listener);
	/**Entfernt einen Settinglistener.
	 * 
	 * @param listener Der SettingListener der entfernt wird.
	 */
	void removeSettingListener(SettingListener listener);
		
	/**Liest eine in der Datenbank gespeicherte Liste ein.
	 * 
	 * @param listName Name der Liste. null: Liest die Hauptliste.
	 * @param searchString Suchbedingung. null: Keine Einschränkung.
	 * @param order Sortierreihenfolge.
	 * @return Die Liste vom Typ List<Track>.
	 * @throws ListException
	 */
	List<? extends DbTrack> readList(String listName, String searchString, data.SortOrder order) throws ListException;
	
	/**Prüft ob eine Datei in der Datenbank ist.
	 * 
	 * @param trackPath Pfad zu der Datei.
	 * @return true, wenn die Datei in der Datenbank ist.
	 * @throws ListException
	 */
	boolean isInDb(String trackPath) throws ListException;
	
	/**Fügt einen Track zur Hauptliste hinzu.
	 * 
	 * @param track Der einzufügende Track.
	 * @param eventsFollowing Gibt an ob weitere, gleichartige Ereignisse folgen werden.
	 * @return Den hinzugefügten Track.
	 * @throws ListException
	 */
	DbTrack addTrack(Track track, boolean eventsFollowing) throws ListException;

	/**Speichert die Änderung an einem Bestimmten Wert im Track in der Datenbank
	 * 
	 * @param track Der Track der geupdated werden soll.
	 * @param element Das Element das geupdatet werden soll.
	 * @param eventsFollowing Gibt an ob weitere, gleichartige Ereignisse folgen werden.
	 * @throws ListException
	 */
	void updateTrack(DbTrack track, DbTrack.TrackElement element, boolean eventsFollowing) throws ListException;
	/**Löscht einen Track aus der Hauptliste.
	 * 
	 * @param track Der zu löschende Track.
	 * @param eventsFollowing Gibt an ob weitere, gleichartige Ereignisse folgen werden.
	 * @throws ListException
	 */
	void deleteTrack(DbTrack track, boolean eventsFollowing) throws ListException;
	
	/**Gibt den DbTrack mit dem angegebenen Pfad zurück.
	 * 
	 * @param path Pfad zur Datei, die der Track enthält.
	 * @param autoCreate Wenn true, wird ein neuer Track erstellt wenn noch keiner mit diesem Pfad vorhanden ist.
	 * @return Der Track mit dem angegebenen Pfad. null, wenn dieser Track nicht existiert und autoCreate false ist.
	 * @throws ListException Wenn autoCreate = true, kann eine Exception beim Erstellen des Tracks auftreten.
	 */
	DbTrack getTrack(String path, boolean autoCreate) throws ListException;
	
	/**Gibt den DbTrack mit dem angegebenen Index zurück.
	 * 
	 * @param index Index des Tracks.
	 * @return Der Track mit dem angegebenen Index. null, wenn dieser Track nicht existiert.
	 */
	DbTrack getTrack(int index);
	
	/**Fügt einen MasterListListener hinzu, der Änderungen an der Hauptliste empfängt.
	 * 
	 * @param listener Der MasterListListener der hinzugefügt wird.
	 */
	void addListListener(ListListener listener);
	/**Entfernt einen MasterListListener.
	 * 
	 * @param listener Der MasterListListener der entfernt wird.
	 */
	void removeListListener(ListListener listener);

	/**Fügt neue Liste ein.
	 * 
	 * @param listName Name der Liste.
	 * @throws ListException
	 */
	void addList(String listName) throws ListException;
	/**Fügt neue Liste ein.
	 * 
	 * @param listName Name der Liste.
	 * @param description Beschreibung der Liste.
	 * @throws ListException
	 */
	void addList(String listName, String description) throws ListException;
	/**Löscht eine bestehende Liste.
	 * 
	 * @param listName Name der Liste.
	 * @throws ListException
	 */
	void removeList(String listName) throws ListException;
	/**Fragt die Priorität einer Liste ab.
	 * 
	 * @param listName Name der Liste.
	 * @return Priorität der Liste.
	 * @throws ListException
	 */
	int getListPriority(String listName) throws ListException;
	/**Setzt die Priorität einer Liste.
	 * 
	 * @param listName Name der Liste.
	 * @param priority Priorität die die Liste haben soll.
	 * @throws ListException
	 */
	void setListPriority(String listName, int priority) throws ListException;
	/**Fragt die Beschreibung einer Liste ab.
	 * 
	 * @param listName Name der Liste.
	 * @return Beschreibung der Liste.
	 * @throws ListException
	 */
	String getListDescription(String listName) throws ListException;
	/**Setzt die Beschreibung einer Liste.
	 * 
	 * @param listName Name der Liste.
	 * @param description Beschreibung die die Liste haben soll.
	 * @throws ListException
	 */
	void setListDescription(String listName, String description) throws ListException;
	/**Ändert den Namen einer Liste.
	 * 
	 * @param oldName Bisheriger Name der Liste. 
	 * @param newName Neuer Name der Liste.
	 * @throws ListException
	 */
	void renameList(String oldName, String newName) throws ListException;
	
	/**Gibt alle Client-Listen in der Datenbank zurück.
	 * 
	 * @return Ein String-Array mit den Namen der Listen.
	 * @throws ListException
	 */
	List<String> getLists() throws ListException;
	
	/**Fügt einen Track in eine Liste ein.
	 * 
	 * @param listName Name der Liste.
	 * @param track Track der eingefügt wird.
	 * @param eventsFollowing Gibt an ob weitere, gleichartige Ereignisse folgen werden.
	 * @throws ListException
	 */
	void insertTrack(String listName, DbTrack track, boolean eventsFollowing) throws ListException;
	/**Fügt einen Track in eine Liste an der angegebenen Stelle ein.
	 * 
	 * @param listName Name der Liste.
	 * @param track Track der eingefügt wird.
	 * @param trackPosition Position des Tracks.
	 * @param eventsFollowing Gibt an ob weitere, gleichartige Ereignisse folgen werden.
	 * @throws ListException
	 */
	void insertTrackAt(String listName, DbTrack track, int trackPosition, boolean eventsFollowing) throws ListException;
	/**Entfernt einen Track aus der Liste.
	 * 
	 * @param listName Name der Liste.
	 * @param trackPosition Position des Tracks in der Liste.
	 * @param eventsFollowing Gibt an ob weitere, gleichartige Ereignisse folgen werden.
	 * @throws ListException
	 */
	void removeTrack(String listName, int trackPosition, boolean eventsFollowing) throws ListException;
	
	/**Vertauscht die Position von zwei Tracks.
	 * 
	 * @param listName Liste in der die Tracks vertauscht werden.
	 * @param positionA Position des einen Tracks.
	 * @param positionB Position des anderen Tracks.
	 * @param eventsFollowing Gibt an ob weitere, gleichartige Ereignisse folgen werden.
	 * @throws ListException
	 */
	void swapTrack(String listName, int positionA, int positionB, boolean eventsFollowing) throws ListException;
	
	/** Gibt den Speicherort der Datenbank zurück.
	 * 
	 * @return Speicherort der Datenbank.
	 */
	String getDbPath();
	
	/**Speichert ungesicherte Daten und schließt alle Verbindungen.
	 * @throws ListException 
	 */
	void close() throws ListException;
}
