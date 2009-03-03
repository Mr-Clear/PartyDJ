package data;

import common.Track;

/**Empfängt Änderungen in der Hauptliste.
 * 
 * @author Eraser
 */
public interface MasterListListener
{
	/**Ein Track wurde hinzugefügt.
	 * 
	 * @param track Hunzugefügter Track.
	 */
	void trackAdded (Track track);
	/**Ein Track wurde bearbeitet.
	 * 
	 * @param track Bearbeiteter Track.
	 */
	void trackChanged (Track track);
	/**Ein Track wurde gelöscht.
	 * 
	 * @param track Gelöschter Track.
	 */
	void trackDeleted (Track track);
}
