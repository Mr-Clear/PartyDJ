package data;

import java.util.List;
import java.util.Map;
import lists.ListException;
import common.Track;

public interface IData
{
	/**Speichert eine Einstellung.
	 * 
	 * @param name Name der Einstellung.
	 * @param value Wert der Einstellung.
	 * @throws ListException
	 */
	void writeSetting(String name, String value) throws SettingException;
	/**Liest eine Einstellung.
	 * Gibt null zurück, wenn die Einstellung nicht vorhanden ist.
	 * 
	 * @param name Name der Einstellung.
	 * @return Wert der Einstellung.
	 * @throws ListException
	 */
	String readSetting(String name) throws SettingException;
	/**Liest eine Einstellung.
	 * 
	 * @param name Name der Einstellung.
	 * @param defaultValue Standardwert, der zurückgegeben wird, wenn Einstellung nicht vorhanden ist.
	 * @return Wert der Einstellung.
	 * @throws ListException
	 */
	String readSetting(String name, String defaultValue) throws SettingException;
	
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
	
	/**Gibt die Hauptliste aus der Datenbank zurück.
	 * 
	 * @return Die Liste vom Typ java.util.Map<Integer, Track>.
	 * @throws ListException
	 */
	Map<Integer, Track> readMasterList() throws ListException;
	
	/**Liest eine in der Datenbank gespeicherte Liste ein.
	 * 
	 * @param listName Name der Liste. null: Liest die Hauptliste.
	 * @param searchString Suchbedingung. null: Keine Einschränkung.
	 * @param order Sortierreihenfolge.
	 * @return Die Liste vom Typ List<Track>.
	 * @throws ListException
	 */
	List<Track> readList(String listName, String searchString, data.SortOrder order) throws ListException;
	
	/**Fügt einen Track zur Hauptliste hinzu.
	 * 
	 * @param track Der einzufügende Track.
	 * @return Der Index des Tracks.
	 * @throws ListException
	 */
	int addTrack(Track track) throws ListException;
	/**Speichert die Änderungen im Track in der Datenbank
	 * 
	 * @param track Der Track der geupdated werden soll.
	 * @throws ListException
	 */
	void updateTrack(Track track) throws ListException;
	/**Speichert die Änderung an einem Bestimten Wert im Track in der Datenbank
	 * 
	 * @param track Der Track der geupdated werden soll.
	 * @param element Das Element das geupdatet werden soll.
	 * @throws ListException
	 */
	void updateTrack(Track track, Track.TrackElement element) throws ListException;
	/**Löscht einen Track aus der Hauptliste.
	 * 
	 * @param track Der zu löschende Track.
	 * @throws ListException
	 */
	void deleteTrack(Track track) throws ListException;
	
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
	 * @param index Index des Tracks in der Hauptliste.
	 * @throws ListException
	 */
	void insertTrack(String listName, Track track) throws ListException;
	/**Fügt einen Track in eine Liste an der angegebenen Stelle ein.
	 * 
	 * @param listName Name der Liste.
	 * @param track Track der eingefügt wird.
	 * @param trackPosition Position des Tracks.
	 * @throws ListException
	 */
	void insertTrackAt(String listName, Track track, int trackPosition) throws ListException;
	/**Entfernt einen Track aus der Liste.
	 * 
	 * @param listName Name der Liste.
	 * @param track Track der eingefügt wird.
	 * @param position Position des Tracks in der Liste.
	 * @throws ListException
	 */
	void removeTrack(String listName, int trackPosition) throws ListException;
	
	/**Vertauscht die Position von zwei Tracks.
	 * 
	 * @param listName Liste in der die Tracks vertauscht werden.
	 * @param positionA Position des einen Tracks.
	 * @param positionB Position des anderen Tracks.
	 * @throws ListException
	 */
	void swapTrack(String listName, int positionA, int positionB) throws ListException;
	
	/**Speichert ungesicherte Daten und schließt alle Verbindungen.
	 */
	void close() throws ListException;
}
