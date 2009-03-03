package data;

import common.ListException;
import common.SettingException;
import common.Track;

public interface IData
{
	/**Speichert eine Einstellung.
	 * 
	 * @param Name Name der Einstellung.
	 * @param Value Wert der Einstellung.
	 * @throws ListException
	 */
	void writeSetting(String name, String value) throws SettingException;
	/**Liest eine Einstellung.
	 * Gibt null zurück, wenn die Einstellung nicht vorhanden ist.
	 * 
	 * @param Name Name der Einstellung.
	 * @return Wert der Einstellung.
	 * @throws ListException
	 */
	String readSetting(String name) throws SettingException;
	/**Liest eine Einstellung.
	 * 
	 * @param Name Name der Einstellung.
	 * @param Default Standartwert, der zurückgegeben wird, wenn Einstellung nicht vorhanden ist.
	 * @return Wert der Einstellung.
	 * @throws ListException
	 */
	String readSetting(String name, String defaultValue) throws SettingException;
	
	/**Fügt einen SettingListener hinzu, der Änderungen an den Winstellungen empfängt.
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
	 * @return Die Liste vom Typ java.util.ArrayList<common.Track>.
	 * @throws ListException
	 */
	java.util.HashMap<Integer, Track> getMasterList() throws ListException;
	
	/**Liest eine in der Datenbank gespeicherte Liste ein.
	 * 
	 * @param listName Name der Liste. null: Liest die Hauptliste.
	 * @param searchString Suchbedingung. null: Keine Einschränkung.
	 * @param order Sortierreihnevolge.
	 * @return Die Liste vom Typ java.util.ArrayList<common.Track>.
	 * @throws ListException
	 */
	java.util.ArrayList<Track> readList(String listName, String searchString, common.SortOrder order) throws ListException;
	
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
	void addMasterListListener(MasterListListener listener);
	/**Entfernt einen MasterListListener.
	 * 
	 * @param listener Der MasterListListener der entfernt wird.
	 */
	void removeMasterListListener(MasterListListener listener);

	/**Fügt neue Liste ein.
	 * 
	 * @param ListName Name der Liste.
	 * @throws ListException
	 */
	void addList(String listName) throws ListException;
	/**Löscht eine bestehende Liste.
	 * 
	 * @param ListName Name der Liste.
	 * @throws ListException
	 */
	void removeList(String listName) throws ListException;
	
	/**Fügt einen Track in eine Liste ein.
	 * 
	 * @param ListName Name der Liste.
	 * @param Index Index des Tracks in der Hauptliste.
	 * @throws ListException
	 */
	void insertTrack(String listName, Track track) throws ListException;
	/**Fügt einen Track in eine Liste an der angegebenen Stelle ein.
	 * 
	 * @param ListName Name der Liste.
	 * @param Track Track der eingefügt wird.
	 * @param trackPosition Postition des Tracks.
	 * @throws ListException
	 */
	void insertTrackAt(String listName, Track track, int trackPosition) throws ListException;
	/**Entfernt einen Track aus der Liste.
	 * 
	 * @param ListName Name der Liste.
	 * @param Track Track der eingefügt wird.
	 * @param Position Position des Tracks in der Liste.
	 * @throws ListException
	 */
	void removeTrack(String listName, int trackPosition) throws ListException;
	
	/**Speichert ungesicherte Daten und schließt alle Verbindungen.
	 */
	void close() throws ListException;
}
